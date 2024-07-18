package tfar.elixirsmps2.init;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import tfar.elixirsmps2.mobeffect.FreezingEffect;
import tfar.elixirsmps2.mobeffect.StunnedEffect;

public class ModMobEffects {

    public static final MobEffect FREEZING = new FreezingEffect(MobEffectCategory.HARMFUL,0xff0000);
    public static final MobEffect INSTANT_MINE = new MobEffect(MobEffectCategory.BENEFICIAL,0xff0000){};
    public static final MobEffect CHEAP_PRICES = new MobEffect(MobEffectCategory.BENEFICIAL,0xff0000){};
    public static final MobEffect STUNNED = new StunnedEffect(MobEffectCategory.HARMFUL,0xff0000)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", -1, AttributeModifier.Operation.MULTIPLY_TOTAL);

}
