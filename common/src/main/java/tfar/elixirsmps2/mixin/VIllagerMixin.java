package tfar.elixirsmps2.mixin;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.elixirsmps2.ElixirSMPS2;

@Mixin(Villager.class)
public class VIllagerMixin {
    @Inject(method = "updateSpecialPrices",at = @At("RETURN"))
    private void onPriceUpdated(Player $$0, CallbackInfo ci) {
        ElixirSMPS2.onPlayerPriceUpdate((Villager)(Object) this,$$0);
    }
}
