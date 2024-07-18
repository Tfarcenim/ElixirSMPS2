package tfar.elixirsmps2.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.elixirsmps2.PlayerDuck;
import tfar.elixirsmps2.elixir.Elixir;
import tfar.elixirsmps2.elixir.Elixirs;

import java.util.function.Consumer;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerDuck {

    @Shadow public abstract void tick();

    @Unique
    int elixirPoints;
    @Unique Elixir elixir;

    @Unique
    int[] cooldowns = new int[6];
    @Unique
    Consumer<Player> onNextHit = player -> {};
    @Unique
    boolean shouldBurnOnHit;
    @Unique
    double damageMultiplier = 1;

    protected PlayerMixin(EntityType<? extends LivingEntity> $$0, Level $$1) {
        super($$0, $$1);
    }


    @Override
    public int getElixirPoints() {
        return elixirPoints;
    }

    @Override
    public void setElixirPoints(int elixirPoints) {
        int lastElixirPoints = this.elixirPoints;
        this.elixirPoints = elixirPoints;
        if (elixir != null && !level().isClientSide) {
            elixir.onEPChange((ServerPlayer) (Object)this, lastElixirPoints, elixirPoints);
        }
    }

    @Override
    public void setElixirPointsNoUpdate(int elixirPoints) {
        this.elixirPoints = elixirPoints;
    }

    @Override
    public Elixir getElixir() {
        return elixir;
    }

    @Override
    public void setElixir(Elixir elixir) {
        if (this.elixir != null){
            this.elixir.disable((Player) (Object)this,false);
        }
        this.elixir = elixir;
    }

    @Override
    public Consumer<Player> getOnNextHit() {
        return onNextHit;
    }

    @Override
    public void setOnNextHit(Consumer<Player> onNextHit) {
        this.onNextHit = onNextHit;
    }

    @Override
    public boolean isShouldBurnOnHit() {
        return shouldBurnOnHit;
    }

    @Override
    public void setShouldBurnOnHit(boolean shouldBurnOnHit) {
        this.shouldBurnOnHit = shouldBurnOnHit;
    }

    @Override
    public int[] getCooldowns() {
        return cooldowns;
    }

    @Override
    public double getFireDamageMultiplier() {
        return damageMultiplier;
    }

    @Override
    public void setFireDamageMultiplier(double multiplier) {
        damageMultiplier = multiplier;
    }

    @Inject(method = "tick",at = @At("HEAD"))
    private void onPlayerTick(CallbackInfo ci) {
        if (!level().isClientSide) {
            tickServer();
        }
    }

    @Inject(method = "addAdditionalSaveData",at = @At("RETURN"))
    private void addAdd(CompoundTag tag, CallbackInfo ci) {
        tag.putInt("elixir_points",elixirPoints);
        if (elixir != null) {
            tag.putString("elixir",elixir.getName());
        }
    }

    @Inject(method = "readAdditionalSaveData",at = @At("RETURN"))
    private void readAdd(CompoundTag tag, CallbackInfo ci) {
        elixirPoints = tag.getInt("elixir_points");
        if (tag.contains("elixir")) {
            elixir = Elixirs.ELIXIR_MAP.get(tag.getString("elixir"));
        }
    }

}
