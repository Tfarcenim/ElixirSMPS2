package tfar.elixirsmps2.mobeffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import tfar.elixirsmps2.PlayerDuck;
import tfar.elixirsmps2.elixir.Elixir;

public class UseAltEffect extends MobEffect {
    public UseAltEffect(MobEffectCategory $$0, int $$1) {
        super($$0, $$1);
    }

    @Override
    public void addAttributeModifiers(LivingEntity living, AttributeMap $$1, int $$2) {
        super.addAttributeModifiers(living, $$1, $$2);
        if (living instanceof Player player) {
            PlayerDuck playerDuck = PlayerDuck.of(player);
            Elixir elixir = playerDuck.getElixir();
            if (elixir != null) {
                Elixir alt_elixir = playerDuck.getAlternativeElixir();
                elixir.disable(player,false);
                alt_elixir.applyPassiveEffects(player);
            }
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity living, AttributeMap $$1, int $$2) {
        super.removeAttributeModifiers(living, $$1, $$2);
        if (living instanceof Player player) {
            PlayerDuck playerDuck = PlayerDuck.of(player);
            Elixir elixir = playerDuck.getElixir();
            if (elixir != null) {
                Elixir alt_elixir = playerDuck.getAlternativeElixir();
                alt_elixir.disable(player,false);
                elixir.applyPassiveEffects(player);
            }
        }
    }
}
