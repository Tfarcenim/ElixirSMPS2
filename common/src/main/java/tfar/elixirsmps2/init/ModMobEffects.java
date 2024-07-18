package tfar.elixirsmps2.init;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import tfar.elixirsmps2.FreezingEffect;

public class ModMobEffects {

    public static final MobEffect FREEZING = new FreezingEffect(MobEffectCategory.HARMFUL,0xff0000);
    public static final MobEffect INSTANT_MINE = new MobEffect(MobEffectCategory.BENEFICIAL,0xff0000){};

}
