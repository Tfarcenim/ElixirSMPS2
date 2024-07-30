package tfar.elixirsmps2.elixir;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;

import java.util.List;

public class SpeedElixir extends Elixir {
    public SpeedElixir(String name, int[] cooldowns, MobEffect good, MobEffect bad) {
        super(name,cooldowns,good,bad);
    }


//Ep -4: Slowness 2
//Ep -3: Slowness 1
//Ep -2: No effect
//Ep -1: Speed 1
//Ep 0: Speed 2
//Ep 1: Give Enemy slowness 2 for 15 secs.(2 min cooldown)
//Ep 2: Speed 3 for 15 secs.(2 Min cooldown.)
//Ep 3: Dash 10 blocks forward.(Cooldown 2 Mins)
//Ep 4: Attack speed is decreased for 20 secs. Like how fast haste 2 attack speed is.(Cooldown 2 mins)
//Ep 5: Effect the enemy with Blindness 1 for 15 secs.(Cooldown 5 mins.)


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
    protected boolean actuallyApplyActiveEffects(ServerPlayer player, int key) {
        switch (key) {
            case 0 -> {
                addMobEffect(player,good,2);
            }
            case 1 -> {
                List<Player> nearby = player.serverLevel().getNearbyPlayers(TargetingConditions.DEFAULT,player,player.getBoundingBox().inflate(6));
                for (Player otherPlayer:nearby) {
                    otherPlayer.addEffect(new MobEffectInstance(bad,20 * 15,1));
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                }
            }
            case 2 -> {
                addTempMobEffect(player,good,2,20 * 15);
            }
            case 3 -> {
                player.setDeltaMovement(player.getDeltaMovement().add(player.getLookAngle().scale(2)));
                player.hurtMarked = true;
            }
            case 4 -> {
                addTempMobEffect(player,MobEffects.DIG_SPEED,1,20 * 20);
            }
            case 5 -> {
                List<Player> nearby = player.serverLevel().getNearbyPlayers(TargetingConditions.DEFAULT,player,player.getBoundingBox().inflate(6));
                for (Player otherPlayer:nearby) {
                    otherPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,20 * 15,1));
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                }
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
        player.removeEffect(good);
        player.removeEffect(MobEffects.DIG_SPEED);
        if (!positiveOnly) {
            player.removeEffect(bad);
        }
    }
}
