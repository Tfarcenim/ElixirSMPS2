package tfar.elixirsmps2.elixir;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;

import java.util.List;

public class FireResistanceElixir extends Elixir {
    public FireResistanceElixir(String name, int[] cooldowns, MobEffect good, MobEffect bad) {
        super(name,cooldowns,good,bad);
    }


//Fire Resistance:
//Ep -4: Slowness 1
//Ep -3: Increased fire damage
//Ep -2: Decreases Fire Damage
//Ep -1: No Effect
//Ep 0: Fire Resistance
//Ep 1: Light people on fire with every hit
//Ep 2: Dry all water in a 10 block radius.(2 min cooldown)
//Ep 3: Remove fire res from enemy.(Cooldown 3 mins)
//Ep 4: Get regen 1 while in lava for 15 secs.(Cooldown 3 mins)
//Ep 5: Shoots a fireball. Like a ghast fireball.(Cooldown 1 min)


    @Override
    public void applyPassiveEffects(Player player) {
        if (!ElixirSMPS2.ENABLED) return;
        PlayerDuck playerDuck = PlayerDuck.of(player);
        int elixirPoints = playerDuck.getElixirPoints();
        if (elixirPoints < 0) {
            player.removeEffect(good);
            if (elixirPoints < -3) {
                addMobEffect(player,bad,0);
                playerDuck.setFireDamageMultiplier(2);
            } else {
                player.removeEffect(bad);
                switch (elixirPoints) {
                    case -3 -> {
                        playerDuck.setFireDamageMultiplier(2);
                    }
                    case -2 -> {
                        playerDuck.setFireDamageMultiplier(1.5);

                    }
                    case -1 -> {
                        playerDuck.setFireDamageMultiplier(1);
                    }
                }
            }
        } else {
            playerDuck.setFireDamageMultiplier(1);
            player.removeEffect(bad);
            addMobEffect(player,good, 0);
        }
    }

    @Override
    protected boolean actuallyApplyActiveEffects(ServerPlayer user, int key) {
        switch (key) {
            case 0 -> {
                addMobEffect(user,good,0);
            }
            case 1 -> {
                PlayerDuck.of(user).toggleShouldBurnOnHit();
            }
            case 2 -> {
                tryAbsorbWater(user.serverLevel(), user.blockPosition());
            }
            case 3 -> {
                List<Player> nearby = user.serverLevel().getNearbyPlayers(TargetingConditions.DEFAULT, user, user.getBoundingBox().inflate(16));
                for (Player otherPlayer:nearby) {
                    PlayerDuck otherPlayerDuck = PlayerDuck.of(otherPlayer);
                    otherPlayer.removeEffect(good);
                    if (otherPlayerDuck.getElixir() == Elixirs.FIRE_RESISTANCE) {
                        otherPlayerDuck.getCooldowns()[0] = 60 * 20;
                    }
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                }
            }
            case 4 -> {
                if (user.isInLava()) {
                    addTempMobEffect(user, MobEffects.REGENERATION, 0, 15 * 20);
                }
            }
            case 5 -> {
                Vec3 vec3 = user.getLookAngle();
                LargeFireball largeFireball = new LargeFireball(user.serverLevel(), user,vec3.x,vec3.y,vec3.z,1);
                largeFireball.setPos(user.getX(), user.getY()+ user.getEyeHeight(), user.getZ());
                user.level().addFreshEntity(largeFireball);
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
        playerDuck.setShouldBurnOnHit(false);
        player.removeEffect(good);
        if (!positiveOnly) {
            playerDuck.setFireDamageMultiplier(1);
            player.removeEffect(bad);
        }
    }

    /**
     * borrowed from SpongeBlock
     * @param pLevel
     * @param pPos
     */
    protected void tryAbsorbWater(Level pLevel, BlockPos pPos) {
        if (this.removeWaterBreadthFirstSearch(pLevel, pPos)) {
            pLevel.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pPos, Block.getId(Blocks.WATER.defaultBlockState()));
        }
    }

    private static final Direction[] ALL_DIRECTIONS = Direction.values();

    private boolean removeWaterBreadthFirstSearch(Level level, BlockPos pos) {
        return BlockPos.breadthFirstTraversal(pos, 10, 512, (blockPos, posConsumer) -> {
            for (Direction direction : ALL_DIRECTIONS) {
                posConsumer.accept(blockPos.relative(direction));
            }
        }, $$2 -> {
            if ($$2.equals(pos)) {
                return true;
            } else {
                BlockState blockState = level.getBlockState($$2);
                FluidState fluidState = level.getFluidState($$2);
                if (!fluidState.is(FluidTags.WATER)) {
                    return false;
                } else {
                    if (blockState.getBlock() instanceof BucketPickup $$6 && !$$6.pickupBlock(level, $$2, blockState).isEmpty()) {
                        return true;
                    }

                    if (blockState.getBlock() instanceof LiquidBlock) {
                        level.setBlock($$2, Blocks.AIR.defaultBlockState(), 3);
                    } else {
                        if (!blockState.is(Blocks.KELP) && !blockState.is(Blocks.KELP_PLANT) && !blockState.is(Blocks.SEAGRASS) && !blockState.is(Blocks.TALL_SEAGRASS)) {
                            return false;
                        }

                        BlockEntity $$7 = blockState.hasBlockEntity() ? level.getBlockEntity($$2) : null;
                        Block.dropResources(blockState, level, $$2, $$7);
                        level.setBlock($$2, Blocks.AIR.defaultBlockState(), 3);
                    }

                    return true;
                }
            }
        }) > 1;
    }

}
