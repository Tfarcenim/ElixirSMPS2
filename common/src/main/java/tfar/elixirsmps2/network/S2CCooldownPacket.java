package tfar.elixirsmps2.network;

import net.minecraft.network.FriendlyByteBuf;
import tfar.elixirsmps2.client.ModClient;

public class S2CCooldownPacket implements S2CModPacket {

    public int[] cooldowns;

    public S2CCooldownPacket(int[]cooldowns){
        this.cooldowns = cooldowns;
    }

    public S2CCooldownPacket(FriendlyByteBuf buf) {
        int length = buf.readInt();
        cooldowns = new int[length];
        for (int i = 0;i < length;i++) {
            cooldowns[i] = buf.readInt();
        }
    }

    @Override
    public void handleClient() {
        ModClient.handle(this);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeInt(cooldowns.length);
        for (int cooldown:cooldowns){
            to.writeInt(cooldown);
        }
    }
}
