package tfar.elixirsmps2.mobeffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class FreezingEffect extends MobEffect {
    public FreezingEffect(MobEffectCategory $$0, int $$1) {
        super($$0, $$1);
    }

    @Override
    public void addAttributeModifiers(LivingEntity $$0, AttributeMap $$1, int $$2) {
        super.addAttributeModifiers($$0, $$1, $$2);
        $$0.setIsInPowderSnow(true);
    }

    @Override
    public boolean isDurationEffectTick(int $$0, int $$1) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity $$0, int $$1) {
        super.applyEffectTick($$0, $$1);
        $$0.setIsInPowderSnow(true);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity $$0, AttributeMap $$1, int $$2) {
        super.removeAttributeModifiers($$0, $$1, $$2);
        $$0.setIsInPowderSnow(false);
    }
}
