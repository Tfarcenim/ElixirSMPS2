package tfar.elixirsmps2.mobeffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

public class StunnedEffect extends MobEffect {

    public StunnedEffect(MobEffectCategory $$0, int $$1) {
        super($$0, $$1);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap $$1, int $$2) {
        super.addAttributeModifiers(entity, $$1, $$2);
    }


    @Override
    public boolean isDurationEffectTick(int $$0, int $$1) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity $$0, int $$1) {
        if ($$0 instanceof Player player) {
            player.getCooldowns().addCooldown(Items.ENDER_PEARL,20);
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity $$0, AttributeMap $$1, int $$2) {
        super.removeAttributeModifiers($$0, $$1, $$2);
    }
}
