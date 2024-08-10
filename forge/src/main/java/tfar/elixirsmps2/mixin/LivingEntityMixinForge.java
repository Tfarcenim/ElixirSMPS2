package tfar.elixirsmps2.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;
import tfar.elixirsmps2.elixir.Elixir;

import java.util.Iterator;
import java.util.Map;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixinForge extends Entity {
    @Shadow @Final private Map<MobEffect, MobEffectInstance> activeEffects;

    @Shadow protected abstract void onEffectRemoved(MobEffectInstance pEffectInstance);

    @Shadow private boolean effectsDirty;

    public LivingEntityMixinForge(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "curePotionEffects",at = @At("HEAD"),cancellable = true)
    private void blockCuring(ItemStack curativeItem, CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof Player player) {
            if (ElixirSMPS2.ENABLED) {
                Elixir elixir = PlayerDuck.of(player).getElixir();
                if (elixir != null) {
                    cir.setReturnValue(elixirSMPS2$handlePotionRemoval(curativeItem,elixir));
                }
            }
        }
    }

    @Unique
    private boolean elixirSMPS2$handlePotionRemoval(ItemStack curativeItem, Elixir elixir) {
        if (this.level().isClientSide)
            return false;
        boolean ret = false;
        Iterator<MobEffectInstance> itr = this.activeEffects.values().iterator();
        while (itr.hasNext()) {
            MobEffectInstance effect = itr.next();
            if (shouldRemove(effect,curativeItem,elixir)) {
                this.onEffectRemoved(effect);
                itr.remove();
                ret = true;
                this.effectsDirty = true;
            }
        }
        return ret;
    }

    @Unique
    private boolean shouldRemove(MobEffectInstance effect, ItemStack curativeItem, Elixir elixir) {

        if (elixir.grants().contains(effect.getEffect()))return false;

       return effect.isCurativeItem(curativeItem) && !net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.living.MobEffectEvent.Remove((LivingEntity)(Object) this, effect));
    }

}
