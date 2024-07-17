package tfar.elixirsmps2;

import net.minecraft.world.entity.player.Player;

public interface PlayerDuck {

    int getElixirPoints();
    void setElixirPoints(int elixirPoints);
    default void addElixirPoints(int elixirPoints) {
        setElixirPoints(getElixirPoints() + elixirPoints);
    }

    default void copyTo(Player newPlayer) {
        PlayerDuck newPlayerDuck = of(newPlayer);
        newPlayerDuck.setElixirPoints(getElixirPoints());
    }

    static PlayerDuck of(Player player) {
        return (PlayerDuck) player;
    }

}
