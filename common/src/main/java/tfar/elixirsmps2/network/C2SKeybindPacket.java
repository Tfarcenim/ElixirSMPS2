package tfar.elixirsmps2.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import tfar.elixirsmps2.PlayerDuck;
import tfar.elixirsmps2.elixir.Elixir;

public class C2SKeybindPacket implements C2SModPacket{

    int ability;

    public C2SKeybindPacket(int ability) {
        this.ability = ability;
    }

    public C2SKeybindPacket(FriendlyByteBuf buf) {
        ability = buf.readInt();
    }

    @Override
    public void handleServer(ServerPlayer player) {
        Elixir elixir = PlayerDuck.of(player).getElixir();
        if (elixir != null) {
            elixir.attemptApplyActiveEffect(player,ability);
        }
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeInt(ability);
    }
}
