package tfar.elixirsmps2.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Field;
import java.util.Locale;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <F> void registerAll(Class<?> clazz, Registry<? super F> registry, Class<F> filter) {
        for (Field field : clazz.getFields()) {
            try {
                Object o = field.get(null);
                if (filter.isInstance(o)) {
                    ResourceLocation key = ElixirSMPS2.id(field.getName().toLowerCase(Locale.ROOT));
                    Registry.register(registry,key,(F)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
