package tfar.elixirsmps2.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccess {

    @Invoker("onEffectRemoved")
    void $onEffectRemoved(MobEffectInstance effect);

}
