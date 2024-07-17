package tfar.elixirsmps2.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.ArrayUtils;

public class ModClient {

    public static void registerKeybinds() {
        ModKeybinds.keyMappingList.forEach(ModClient::registerKeybind);
    }

    public static void registerKeybind(KeyMapping keyMapping) {
        Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings,keyMapping);
    }
}
