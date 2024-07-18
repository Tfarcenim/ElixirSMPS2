package tfar.elixirsmps2.mixin;

import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixinFabric {
    @Shadow public abstract RandomSource getRandom();

    @Inject(method = "actuallyHurt",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;)V"))
    private void onEntityDamagedPost(DamageSource damageSource, float damageAmount, CallbackInfo ci) {
        ElixirSMPS2.afterDamage((LivingEntity)(Object) this,damageSource);
    }

    @ModifyVariable(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), argsOnly = true)
    private float onEntityDamagedPre(float amount,DamageSource source) {
        return ElixirSMPS2.modifyDamage((LivingEntity)(Object) this,source,amount);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int decreaseAirSupply(int currentAir) {
        int chance = EnchantmentHelper.getRespiration((LivingEntity) (Object)this);
        int decrease = 1;
        if ((Object)this instanceof Player player) {
            PlayerDuck playerDuck = PlayerDuck.of(player);
            if (playerDuck.getElixirPoints() <= -3) {
                decrease++;
            } else if (playerDuck.getElixirPoints() == -1) {
                chance *=2;
            }
        }


        if (this.getRandom().nextInt(chance + 1) == 0) {
            currentAir = currentAir - decrease;
        }
        return currentAir;
    }

}
