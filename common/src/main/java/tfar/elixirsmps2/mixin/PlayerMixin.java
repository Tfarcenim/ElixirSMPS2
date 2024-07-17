package tfar.elixirsmps2.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.elixirsmps2.PlayerDuck;

@Mixin(Player.class)
public class PlayerMixin implements PlayerDuck {

    @Unique
    int elixirPoints;


    @Override
    public int getElixirPoints() {
        return elixirPoints;
    }

    @Override
    public void setElixirPoints(int elixirPoints) {
        this.elixirPoints = elixirPoints;
    }

    @Inject(method = "addAdditionalSaveData",at = @At("RETURN"))
    private void addAdd(CompoundTag tag, CallbackInfo ci) {
        tag.putInt("elixir_points",elixirPoints);
    }

    @Inject(method = "readAdditionalSaveData",at = @At("RETURN"))
    private void readAdd(CompoundTag tag, CallbackInfo ci) {
        elixirPoints = tag.getInt("elixir_points");
    }

}
