package tfar.elixirsmps2.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

public record ClientHandler<MSG extends S2CModPacket>(Function<FriendlyByteBuf, MSG> decodeFunction) implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        MSG decode = decodeFunction.apply(buf);
        client.execute(decode::handleClient);
    }
}
