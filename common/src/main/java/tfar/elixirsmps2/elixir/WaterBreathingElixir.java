package tfar.elixirsmps2.elixir;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.phys.Vec3;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;
import tfar.elixirsmps2.init.ModMobEffects;

import java.util.List;

public class WaterBreathingElixir extends Elixir {
    public WaterBreathingElixir(String name, int[] cooldowns, MobEffect good, MobEffect bad) {
        super(name,cooldowns,good,bad);
    }


//Water Breathing:
//Ep -4: Increased drowning damage
//Ep -3: Drown Faster
//Ep -2: No Effect
//Ep -1: Drown Slower
//Ep 0: Water Breathing
//Ep 1: Change Water Breathing to Conduit Power.
//Ep 2: Give enemy mining fatigue for 15 secs.(Cooldown 3 mins)
//Ep 3: Give all enemies freezing effect for 10 secs.(Cooldown 2 mins)
//Ep 4: All enemies in water within a 10 block radius will be affected with poison 2 for 15 secs.(Cooldown 3 mins)
//Ep 5: Get regen 1 when in water for 15 secs.(Cooldown 3 mins)


    @Override
    public void applyPassiveEffects(Player player) {
        if (!ElixirSMPS2.ENABLED) return;
        PlayerDuck playerDuck = PlayerDuck.of(player);
        int elixirPoints = playerDuck.getElixirPoints();
        if (elixirPoints < 0) {
            player.removeEffect(good);
        } else {
            addMobEffect(player,good, 0);
            if (elixirPoints > 0) {
                addMobEffect(player,MobEffects.CONDUIT_POWER,0);
            }
        }
    }

    @Override
    protected boolean actuallyApplyActiveEffects(ServerPlayer user, int key) {
        boolean didSomething = false;
        switch (key) {
            case 0 -> {
                didSomething |= addMobEffect(user,good,0);
            }
            case 1 -> {
                didSomething |= addMobEffect(user,MobEffects.CONDUIT_POWER,0);
            }
            case 2 -> {
                List<Player> nearby = getNearbyPlayers(user);
                for (Player otherPlayer:nearby) {
                    didSomething |= addTempMobEffect(otherPlayer,MobEffects.DIG_SLOWDOWN,0,15 * 20);
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                }
            }
            case 3 -> {
                List<ServerPlayer> nearby = user.server.getPlayerList().getPlayers();
                for (ServerPlayer otherPlayer:nearby) {
                    didSomething |= addTempMobEffect(otherPlayer, ModMobEffects.FREEZING,0,15 * 20);
                    notifyAbilityHit(otherPlayer,key);
                }
            }
            case 4 -> {
                List<Player> nearby = user.serverLevel().getNearbyPlayers(TargetingConditions.forCombat().selector(Entity::isInWater), user, user.getBoundingBox().inflate(10));
                for (Player otherPlayer:nearby) {
                    didSomething |= addTempMobEffect(otherPlayer,MobEffects.POISON,0,15 * 20);
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                }
            }
            case 5 -> {
                if (user.isInWater()) {
                    didSomething |= addTempMobEffect(user,MobEffects.REGENERATION,0,15 * 20);
                }
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
        PlayerDuck playerDuck = PlayerDuck.of(player);
        player.removeEffect(good);
        player.removeEffect(MobEffects.CONDUIT_POWER);
        if (!positiveOnly) {

        }
    }
}
