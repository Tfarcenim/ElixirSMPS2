package tfar.elixirsmps2.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.elixirsmps2.ElixirSMPS2;

@Mixin(LivingEntity.class)
public class LivingEntityMixinFabric {
    @Inject(method = "actuallyHurt",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;)V"))
    private void onEntityDamagedPost(DamageSource damageSource, float damageAmount, CallbackInfo ci) {
        ElixirSMPS2.afterDamage((LivingEntity)(Object) this,damageSource);
    }

    @ModifyVariable(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), argsOnly = true)
    private float onEntityDamagedPre(float amount,DamageSource source) {
        return ElixirSMPS2.modifyDamage((LivingEntity)(Object) this,source,amount);
    }
}
