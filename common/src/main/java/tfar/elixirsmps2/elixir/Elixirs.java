package tfar.elixirsmps2.elixir;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Elixirs {

    public static final Map<String,Elixir> ELIXIR_MAP = new HashMap<>();
    public static final List<Elixir> ELIXIR_LIST = new ArrayList<>();


    //Ep -4: Weakness 2
//Ep -3: Weakness 1
//Ep -2: No Effect
//Ep -1: Strength 1
//Ep 0: Strength 2
//Ep 1: Effect enemy with Weakness for 20 Secs. Cooldown. 1 Minute
//Ep 2: Remove Weakness and Slowness from self(Only if you have the effect) Cooldown 2 min.
//Ep 3: Strength 3 for one hit. Cooldown 2 min.
//Ep 4: Remove strength from all enemies. Cooldown 3 Mins.
//Ep 5: Temp disable other peoples Elixirs for 15 secs. Only disables 0 to 5 ep. (Cooldown 5 Mins.)
    public static final StrengthElixir STRENGTH = register(new StrengthElixir("strength",new int[]{0,60 * 20,120 * 20,120 * 20,60 * 20,300 * 20}));

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
