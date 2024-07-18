package tfar.elixirsmps2.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.elixirsmps2.ElixirSMPS2;

@Mixin(Player.class)
public class PlayerMixinFabric {
    @Inject(method = "actuallyHurt",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;)V"))
    private void onEntityDamaged(DamageSource damageSource, float damageAmount, CallbackInfo ci) {
        ElixirSMPS2.afterDamage((LivingEntity)(Object) this,damageSource);
    }
}
