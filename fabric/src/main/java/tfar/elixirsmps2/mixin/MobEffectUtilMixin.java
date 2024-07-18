package tfar.elixirsmps2.mixin;

import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.elixirsmps2.init.ModMobEffects;

@Mixin(MobEffectUtil.class)
public class MobEffectUtilMixin {
    @Inject(method = "getDigSpeedAmplification",at = @At("RETURN"),cancellable = true)
    private static void instamine(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (entity.hasEffect(ModMobEffects.INSTANT_MINE)) {
            cir.setReturnValue(1000000000);//can't use max value
        }
    }

}
