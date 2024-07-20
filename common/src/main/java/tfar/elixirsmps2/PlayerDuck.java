package tfar.elixirsmps2;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import tfar.elixirsmps2.elixir.Elixir;
import tfar.elixirsmps2.network.S2CCooldownPacket;
import tfar.elixirsmps2.platform.Services;

import java.util.function.Consumer;

public interface PlayerDuck {

    int getElixirPoints();
    void setElixirPoints(int elixirPoints);
    void setElixirPointsNoUpdate(int elixirPoints);
    default void addElixirPoints(int elixirPoints) {
        setElixirPoints(getElixirPoints() + elixirPoints);
    }

    Elixir getElixir();
    void setElixir(Elixir elixir);

    Elixir getAlternativeElixir();
    void setAlternativeElixir(Elixir elixir);

    int[] getCooldowns();
    Consumer<Player> getOnNextHit();
    void setOnNextHit(Consumer<Player> onNextHit);

    boolean isShouldBurnOnHit();
    void setShouldBurnOnHit(boolean shouldBurnOnHit);
    default void toggleShouldBurnOnHit() {
        setShouldBurnOnHit(!isShouldBurnOnHit());
    }

    double getFireDamageMultiplier();
    void setFireDamageMultiplier(double multiplier);

    default void copyTo(Player newPlayer) {
        PlayerDuck newPlayerDuck = of(newPlayer);
        newPlayerDuck.setElixirPointsNoUpdate(getElixirPoints());
        Elixir elixir = getElixir();
        newPlayerDuck.setElixir(elixir);
    }

    default void tickServer() {
        boolean clientDirty = false;
        int[] cooldowns = getCooldowns();
        for (int i = 0; i < cooldowns.length;i++) {
            if (cooldowns[i] > 0) {
                cooldowns[i]--;
                clientDirty = true;
            }
        }
        if (clientDirty) {
            Services.PLATFORM.sendToClient(new S2CCooldownPacket(cooldowns),(ServerPlayer) this);
        }
    }

    static PlayerDuck of(Player player) {
        return (PlayerDuck) player;
    }

}
