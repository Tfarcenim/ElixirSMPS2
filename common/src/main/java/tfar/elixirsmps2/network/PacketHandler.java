package tfar.elixirsmps2.network;

import net.minecraft.resources.ResourceLocation;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.platform.Services;

import java.util.Locale;

public class PacketHandler {

    public static void registerPackets() {
        Services.PLATFORM.registerServerPacket(C2SKeybindPacket.class,C2SKeybindPacket::new);
        Services.PLATFORM.registerClientPacket(S2CCooldownPacket.class, S2CCooldownPacket::new);

    }

    public static ResourceLocation packet(Class<?> clazz) {
        return ElixirSMPS2.id(clazz.getName().toLowerCase(Locale.ROOT));
    }

}
