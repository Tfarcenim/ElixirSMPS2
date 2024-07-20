package tfar.elixirsmps2.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.MixinEnvironment;
import tfar.elixirsmps2.network.ClientHandler;
import tfar.elixirsmps2.network.PacketHandler;
import tfar.elixirsmps2.network.S2CModPacket;

import java.util.function.Function;

public class ModClientFabric implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> ModClient.clientTick());
        ModClient.registerKeybinds();
    }

    public static <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
            ClientPlayNetworking.registerGlobalReceiver(PacketHandler.packet(packetLocation), new ClientHandler<>(reader));
    }
}
