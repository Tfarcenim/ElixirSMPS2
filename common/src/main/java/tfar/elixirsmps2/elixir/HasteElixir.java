package tfar.elixirsmps2.elixir;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;
import tfar.elixirsmps2.init.ModMobEffects;

import java.util.List;
import java.util.Set;

public class HasteElixir extends Elixir{
    public HasteElixir(String name, int[] cooldowns, MobEffect good, MobEffect bad) {
        super(name,cooldowns,good,bad);
    }


//Haste:
//Ep -4: Mining Fatigue 1
//Ep -3: Slowness 1
//Ep -2: No Effect
//Ep -1: Haste 1
//Ep 0: Haste 2
//Ep 1: Haste 3 for 15 secs.(Cooldown 2 mins)
//Ep 2: Give enemy mining fatigue 1 for 15 secs.(Cooldown
//Ep 3: Makes a 5x5 Stone box around self. (Cooldown 3 mins.)
//Ep 4: Instantly mine all blocks for 10 secs. Does NOT change attack speed(Cooldown 10 mins)
//Ep 5: Remove all positive effects from the enemy.(Cooldown 5 mins)


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
                switch (elixirPoints) {
                    case -3 -> {
                        addMobEffect(player,MobEffects.MOVEMENT_SLOWDOWN,0);
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
            player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            addMobEffect(player,good, 1);
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
                didSomething |= addTempMobEffect(user,good,2,15 * 20);
            }
            case 2 -> {
                List<Player> nearby = getNearbyPlayers(user);
                for (Player otherPlayer:nearby) {
                    didSomething |= addTempMobEffect(otherPlayer,bad,0,15 * 20);
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                }
            }
            case 3 -> {
                BlockPos origin = user.blockPosition();
                for (int y = 0; y < 5;y++) {
                    for (int z = -2; z < 3;z++) {
                        for (int x = -2; x < 3;x++) {
                            BlockPos pos = origin.offset(x,y,z);
                            BlockState state = user.level().getBlockState(pos);
                            if (state.canBeReplaced() && !user.getBoundingBox().intersects(new AABB(pos))) {
                                user.level().setBlock(pos,Blocks.STONE.defaultBlockState(),3);
                            }
                        }
                    }
                }
                didSomething = true;
            }
            case 4 -> {
                didSomething |= addTempMobEffect(user,ModMobEffects.INSTANT_MINE,0,20 * 10);
            }
            case 5 -> {
                List<Player> nearby = getNearbyPlayers(user);
                for (Player otherPlayer:nearby) {
                    removeSomeEffects(otherPlayer, MobEffectCategory.BENEFICIAL);
                    PlayerDuck otherPlayerDuck = PlayerDuck.of(otherPlayer);
                    otherPlayerDuck.getCooldowns()[0] = Math.max(otherPlayerDuck.getCooldowns()[0],15 * 20);
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
        PlayerDuck playerDuck = PlayerDuck.of(player);
        player.removeEffect(good);
        player.removeEffect(MobEffects.CONDUIT_POWER);
        if (!positiveOnly) {
            player.removeEffect(bad);
            player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        }
    }

    @Override
    public Set<MobEffect> grants() {
        Set<MobEffect> grants = super.grants();
        grants.add(MobEffects.MOVEMENT_SLOWDOWN);
        grants.add(MobEffects.CONDUIT_POWER);
        return grants;
    }
}
