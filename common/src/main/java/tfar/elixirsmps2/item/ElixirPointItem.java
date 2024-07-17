package tfar.elixirsmps2.item;

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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            PlayerDuck playerDuck = PlayerDuck.of(player);
            playerDuck.addElixirPoints(1);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }

    @Override
    public boolean canBeHurtBy(DamageSource $$0) {
        return $$0.is(DamageTypes.FELL_OUT_OF_WORLD);
    }
}
