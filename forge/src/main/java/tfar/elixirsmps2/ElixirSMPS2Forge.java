package tfar.elixirsmps2;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import tfar.elixirsmps2.datagen.ModDatagen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mod(ElixirSMPS2.MOD_ID)
public class ElixirSMPS2Forge {

    public static Map<Registry<?>, List<Pair<ResourceLocation, Supplier<?>>>> registerLater = new HashMap<>();

    public ElixirSMPS2Forge() {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::register);
        bus.addListener(ModDatagen::gather);
        // Use Forge to bootstrap the Common mod.
        ElixirSMPS2.init();
    }

    private void register(RegisterEvent e) {
        for (Map.Entry<Registry<?>, List<Pair<ResourceLocation, Supplier<?>>>> entry : registerLater.entrySet()) {
            Registry<?> registry = entry.getKey();
            List<Pair<ResourceLocation, Supplier<?>>> toRegister = entry.getValue();
            for (Pair<ResourceLocation, Supplier<?>> pair : toRegister) {
                e.register((ResourceKey<? extends Registry<Object>>) registry.key(), pair.getLeft(), (Supplier<Object>) pair.getValue());
            }
        }
    }
}