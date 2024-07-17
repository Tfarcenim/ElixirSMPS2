package tfar.elixirsmps2;

import net.minecraft.world.entity.player.Player;

public interface PlayerDuck {

    int getElixirPoints();
    void setElixirPoints(int elixirPoints);
    static PlayerDuck of(Player player) {
        return (PlayerDuck) player;
    }

}
