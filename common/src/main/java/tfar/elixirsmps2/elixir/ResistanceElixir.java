package tfar.elixirsmps2.elixir;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;
import tfar.elixirsmps2.init.ModMobEffects;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ResistanceElixir extends Elixir {
    public ResistanceElixir(String name, int[] cooldowns, MobEffect good, MobEffect bad) {
        super(name,cooldowns,good,bad);
    }


//Resistance:
//Ep -4: Take 20% more damage.
//Ep -3: Weakness
//Ep -2: Slowness
//Ep -1: No Effect
//Ep 0: Resistance 1
//Ep 1: Receive Resistance 3 for 15 secs.(Cooldown 2 Mins)
//Ep 2: Give weakness 1 to enemy for 20 secs.(Cooldown 2 mins)
//Ep 3: Deal 30% more damage for 15 secs.(Cooldown 4 mins)
//Ep 4: Remove strength from enemy. Disables Strength Elixir for 20 secs.(Cooldown 2 mins.)
//Ep 5: Become immune from all hits for 5 secs.(5 min cooldown.)

    @Override
    public void applyPassiveEffects(Player player) {
        if (!ElixirSMPS2.ENABLED) return;
        int elixirPoints = PlayerDuck.of(player).getElixirPoints();
        if (elixirPoints < 0) {
            player.removeEffect(good);
            if (elixirPoints < -3) {
                addMobEffect(player,MobEffects.MOVEMENT_SLOWDOWN,0);
                addMobEffect(player,MobEffects.WEAKNESS,0);
            } else {
                switch (elixirPoints) {
                    case -3 -> {
                        addMobEffect(player,MobEffects.MOVEMENT_SLOWDOWN,0);
                        addMobEffect(player,MobEffects.WEAKNESS,0);
                    }
                    case -2 -> {
                        addMobEffect(player,MobEffects.MOVEMENT_SLOWDOWN,0);
                    }
                    case -1 -> {
                    }
                }
            }
        } else {
            addMobEffect(player,good, 0);
        }
    }

    @Override
    protected boolean actuallyApplyActiveEffects(ServerPlayer player, int key) {
        switch (key) {
            case 0 -> {
                addMobEffect(player,good,0);
            }
            case 1 -> {
                addTempMobEffect(player,good,2,15 * 20);
            }
            case 2 -> {
                List<Player> nearby = player.serverLevel().getNearbyPlayers(TargetingConditions.DEFAULT,player,player.getBoundingBox().inflate(6));
                for (Player otherPlayer:nearby) {
                    addTempMobEffect(otherPlayer,MobEffects.WEAKNESS,0,20 *20);
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                }
            }
            case 3 -> {
                List<Player> nearby = player.serverLevel().getNearbyPlayers(TargetingConditions.DEFAULT,player,player.getBoundingBox().inflate(6));
                for (Player otherPlayer:nearby) {
                    PlayerDuck otherPlayerDuck = PlayerDuck.of(otherPlayer);
                    otherPlayer.removeEffect(MobEffects.DAMAGE_BOOST);

                    if (otherPlayerDuck.getElixir() == Elixirs.STRENGTH) {
                        Elixirs.STRENGTH.disable(otherPlayer,false);
                        int[] cooldowns = otherPlayerDuck.getCooldowns();
                        for (int i = 0; i < cooldowns.length;i++) {
                            cooldowns[i] = Math.max(cooldowns[i],20 * 20);
                        }
                    }

                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                }
            }
            case 4 -> {
                addTempMobEffect(player,good,1,30 * 20);
            }
            case 5 -> {
                addTempMobEffect(player,good,4,20 * 5);
            }
        }
        return true;
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
        if (!positiveOnly) {
            player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            player.removeEffect(MobEffects.WEAKNESS);
        }
    }
}
