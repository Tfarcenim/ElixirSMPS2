package tfar.elixirsmps2.elixir;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;
import tfar.elixirsmps2.init.ModMobEffects;

import java.util.List;

public class LuckElixir extends Elixir {
    public LuckElixir(String name, int[] cooldowns, MobEffect good, MobEffect bad) {
        super(name,cooldowns,good,bad);
    }


//Luck:
//Ep -4: Bad Luck 2
//Ep -3: Bad Luck 1
//Ep -2: No Effect
//Ep -1: Luck 1
//Ep 0: Luck 2
//Ep 1: Randomly give enemy 1 debuff for 15 secs.Cannot be Hunger(Cooldown 3 mins.)
//Ep 2:  Temp disables all enemies positive effects for 20 secs. Does NOT include debuffs and Elixirs.(Cooldown 3 mins.)
//Ep 3: Remove all debuffs from self.(Cooldown 3 mins.)
//Ep 4: Swap effects and abilities with enemy for 20 secs.(Cooldown 5 mins)
//Ep 5: Randomly give self 11-20 hearts. Does not heal fully up. Lasts 30 secs.(Cooldown 5 mins.)

    @Override
    public void applyPassiveEffects(Player player) {
        if (!ElixirSMPS2.ENABLED) return;
        int elixirPoints = PlayerDuck.of(player).getElixirPoints();
        if (elixirPoints < 0) {
            player.removeEffect(good);
            if (elixirPoints < -3) {
                addMobEffect(player,bad,1);
            } else {
                player.removeEffect(bad);
                switch (elixirPoints) {
                    case -3 -> {
                        addMobEffect(player,bad,0);
                    }
                    case -2 -> {
                    }
                    case -1 -> {
                        addMobEffect(player,good,0);
                    }
                }
            }
        } else {
            player.removeEffect(bad);
            addMobEffect(player,good, 1);
        }
    }

    @Override
    protected boolean actuallyApplyActiveEffects(ServerPlayer user, int key) {
        boolean didSomething = false;
        switch (key) {
            case 0 -> {
                addMobEffect(user,good,1);
            }
            case 1 -> {
                List<Player> nearby = user.serverLevel().getNearbyPlayers(TargetingConditions.DEFAULT, user, user.getBoundingBox().inflate(16));
                for (Player otherPlayer:nearby) {
                    didSomething |= giveRandomBadEffect(otherPlayer);
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                }
            }
            case 2 -> {
                List<Player> nearby = user.serverLevel().getNearbyPlayers(TargetingConditions.DEFAULT, user, user.getBoundingBox().inflate(16));
                for (Player otherPlayer:nearby) {
                    removeNonElixirPositiveEffects(otherPlayer);
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                    didSomething = true;
                }
            }
            case 3 -> {
                removeSomeEffects(user,MobEffectCategory.HARMFUL);
                didSomething = true;
            }
            case 4 -> {
                Player otherPlayer = getNearestPlayer(user);
                if (otherPlayer != null) {
                    PlayerDuck playerDuck = PlayerDuck.of(user);
                    PlayerDuck otherPlayerDuck = PlayerDuck.of(otherPlayer);
                    playerDuck.setAlternativeElixir(otherPlayerDuck.getElixir());
                    addTempMobEffect(user, ModMobEffects.USE_ALT_ELIXIR,0,20 * 20);
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                    didSomething = true;
                }
            }
            case 5 -> {
                didSomething |= addTempMobEffect(user,MobEffects.HEALTH_BOOST, user.getRandom().nextInt(5),20 * 30);
            }
        }
        return didSomething;
    }

    @Override
    public void onEPChange(ServerPlayer player, int oldEP, int newEP) {
        if (oldEP < -3 && newEP < -3) return;
        if (oldEP > -1 && newEP > -1) return;

        disable(player,false);
        applyPassiveEffects(player);
    }

    @Override
    public void disable(Player player, boolean positiveOnly) {
        player.removeEffect(good);
        if (!positiveOnly) {
            player.removeEffect(bad);
        }
    }

    protected static boolean giveRandomBadEffect(Player player) {
        List<MobEffect> effects = BuiltInRegistries.MOB_EFFECT.stream().filter(effect -> effect.getCategory() == MobEffectCategory.HARMFUL).filter(effect -> effect != MobEffects.HUNGER).toList();
        MobEffect random = effects.get(player.getRandom().nextInt(effects.size()));
        if (random.isInstantenous()) {
            random.applyInstantenousEffect(null, null, player, 0, 1);
            return true;
        }
        return addTempMobEffect(player,random,0,15 * 20);
    }

}
