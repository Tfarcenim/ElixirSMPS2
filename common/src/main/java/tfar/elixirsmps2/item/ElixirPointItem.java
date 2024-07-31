package tfar.elixirsmps2.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tfar.elixirsmps2.PlayerDuck;

public class ElixirPointItem extends Item {
    public ElixirPointItem(Properties $$0) {
        super($$0);
    }

    @Override
    public boolean isFoil(ItemStack $$0) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            PlayerDuck playerDuck = PlayerDuck.of(player);
            int points = playerDuck.getElixirPoints();
            if (points < 5) {
                playerDuck.addElixirPoints(1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }

    @Override
    public boolean canBeHurtBy(DamageSource $$0) {
        return $$0.is(DamageTypes.FELL_OUT_OF_WORLD);
    }
}
