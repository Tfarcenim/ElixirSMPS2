package tfar.elixirsmps2.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;
import tfar.elixirsmps2.elixir.Elixir;

import java.util.Iterator;

@Mixin(MilkBucketItem.class)
public class MilkBucketItemMixinFabric {
    @Redirect(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeAllEffects()Z"))
    private boolean preventCuringEverything(LivingEntity instance) {
        if (ElixirSMPS2.ENABLED && instance instanceof Player player) {
            Elixir elixir = PlayerDuck.of(player).getElixir();
            Iterator<MobEffectInstance> iterator = instance.getActiveEffectsMap().values().iterator();

            boolean flag;
            flag = false;
            while (iterator.hasNext()) {
                MobEffectInstance effect = iterator.next();
                if (shouldRemove(effect, new ItemStack(Items.MILK_BUCKET), elixir))
                    instance.onEffectRemoved(effect);
                iterator.remove();
                flag = true;
            }

            return flag;
        } else {
            return instance.removeAllEffects();
        }
    }

    @Unique
    private boolean shouldRemove(MobEffectInstance effect, ItemStack curativeItem, Elixir elixir) {
        if (elixir.grants().contains(effect.getEffect())) return false;
        return true;
    }
}
