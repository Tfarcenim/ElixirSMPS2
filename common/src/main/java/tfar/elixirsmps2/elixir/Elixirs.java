package tfar.elixirsmps2.elixir;

import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Elixirs {

    public static final Map<String,Elixir> ELIXIR_MAP = new HashMap<>();
    public static final List<Elixir> ELIXIR_LIST = new ArrayList<>();



    public static final StrengthElixir STRENGTH = register(new StrengthElixir("strength",new int[]{0,60 * 20,120 * 20,120 * 20,180 * 20,300 * 20}, MobEffects.DAMAGE_BOOST,MobEffects.WEAKNESS));
    public static final SpeedElixir SPEED = register(new SpeedElixir("speed",new int[]{0,120 * 20,(15 + 120) * 20,120 * 20,120 * 20,300 * 20}, MobEffects.MOVEMENT_SPEED,MobEffects.MOVEMENT_SLOWDOWN));
    public static final FireResistanceElixir FIRE_RESISTANCE = register(new FireResistanceElixir("fire_resistance",new int[]{0,0,120 * 20,180 * 20,180 * 20,60 * 20}, MobEffects.FIRE_RESISTANCE,MobEffects.MOVEMENT_SLOWDOWN));

    public static <E extends Elixir> E register(E elixir) {
        ELIXIR_MAP.put(elixir.getName(),elixir);
        ELIXIR_LIST.add(elixir);
        return elixir;
    }

    public static Elixir getRandom(RandomSource random) {
        int i = random.nextInt(ELIXIR_LIST.size());
        return ELIXIR_LIST.get(i);
    }

}
