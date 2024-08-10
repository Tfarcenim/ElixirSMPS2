package tfar.elixirsmps2.elixir;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;
import tfar.elixirsmps2.init.ModMobEffects;

import java.util.List;

public class InvisibilityElixir extends Elixir {
    public InvisibilityElixir(String name, int[] cooldowns, MobEffect good, MobEffect bad) {
        super(name,cooldowns,good,bad);
    }


//Ep -4: Glowing + Slowness 1
//Ep -3: Glowing
//Ep -2: Glowing for 30 secs when hit
//Ep -1: No effect
//Ep 0: Invisibility
//Ep 1: Effect enemy with glowing for 30 secs every hit.
//Ep 2: Scan for enemies in a 20 block range gives glowing for 10 secs.(Cooldown 2 mins)
//Ep 3: Stun the enemy for 5 secs. Stops people from using abilities, Using ender pearls, and moving.(Cooldown 3 mins)
//Ep 4: Give enemy blindness 1 for 15 secs.(3 min cooldown)
//Ep 5: Push enemy 10 blocks away from all sides.(Cooldown 3 mins.)


    @Override
    public void applyPassiveEffects(Player player) {
        if (!ElixirSMPS2.ENABLED) return;
        int elixirPoints = PlayerDuck.of(player).getElixirPoints();
        if (elixirPoints < 0) {
            player.removeEffect(good);
            if (elixirPoints < -3) {
                addMobEffect(player,bad,0);
                addMobEffect(player,MobEffects.MOVEMENT_SLOWDOWN,0);
            } else {
                player.removeEffect(bad);
                player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                switch (elixirPoints) {
                    case -3 -> {
                        addMobEffect(player,bad,0);
                    }
                    case -2 -> {
                    }
                    case -1 -> {
                    }
                }
            }
        } else {
            player.removeEffect(bad);
            addMobEffect(player,good, 0);
        }
    }

    @Override
    protected boolean actuallyApplyActiveEffects(ServerPlayer user, int key) {
        boolean didSomething = false;
        switch (key) {
            case 0 -> {
                if (user.hasEffect(good)) {
                    user.removeEffect(good);
                } else {
                    addMobEffect(user, good, 0);
                }
            }
            case 1 -> {
            }
            case 2 -> {
                List<Player> nearby = user.serverLevel().getNearbyPlayers(TargetingConditions.DEFAULT, user, user.getBoundingBox().inflate(40));
                for (Player otherPlayer:nearby) {
                    didSomething |= otherPlayer.addEffect(new MobEffectInstance(bad,20 * 15,0));
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                }
            }
            case 3 -> {
                List<Player> nearby = user.serverLevel().getNearbyPlayers(TargetingConditions.DEFAULT, user, user.getBoundingBox().inflate(16));
                for (Player otherPlayer:nearby) {
                    didSomething |= otherPlayer.addEffect(new MobEffectInstance(ModMobEffects.STUNNED,20 * 5,0));
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                }
            }
            case 4 -> {
                List<Player> nearby = user.serverLevel().getNearbyPlayers(TargetingConditions.DEFAULT, user, user.getBoundingBox().inflate(16));
                for (Player otherPlayer:nearby) {
                    didSomething |= otherPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,20 * 15,0));
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                }
            }
            case 5 -> {
                List<Player> nearby = user.serverLevel().getNearbyPlayers(TargetingConditions.DEFAULT, user, user.getBoundingBox().inflate(10));
                for (Player otherPlayer:nearby) {
                    Vec3 dir = user.position().subtract(otherPlayer.position()).normalize();
                    push(otherPlayer,dir);
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                    didSomething = true;
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
        player.removeEffect(good);
        if (!positiveOnly) {
            player.removeEffect(bad);
            player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        }
    }
}
