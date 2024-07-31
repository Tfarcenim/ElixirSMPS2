package tfar.elixirsmps2.mobeffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class HealthSinkingEffect extends MobEffect {
    public HealthSinkingEffect(MobEffectCategory $$0, int $$1) {
        super($$0, $$1);
    }

    @Override
    public void addAttributeModifiers(LivingEntity $$0, AttributeMap $$1, int $$2) {
        super.addAttributeModifiers($$0, $$1, $$2);
        if ($$0.getHealth() > $$0.getMaxHealth()) {
            $$0.setHealth($$0.getMaxHealth());
        }
    }
}
