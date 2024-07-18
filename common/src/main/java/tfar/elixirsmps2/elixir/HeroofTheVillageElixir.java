package tfar.elixirsmps2.elixir;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;
import tfar.elixirsmps2.init.ModMobEffects;

import java.util.List;

public class HeroofTheVillageElixir extends Elixir {
    public HeroofTheVillageElixir(String name, int[] cooldowns, MobEffect good, MobEffect bad) {
        super(name,cooldowns,good,bad);
    }


//Hero of the Village:
//Ep -4: Higher Increased Villager Prices
//Ep -3: Increased Villager Prices
//Ep -2: No Effect
//Ep -1: Hero of the Village 1
//Ep 0: Hero of the Village 2
//Ep 1: All villager prices drop to 1 item for 15 secs.(Cooldown 5 mins.)
//Ep 2: See villagers through walls for 20 secs.(Cooldown 2 mins)
//Ep 3; Remove all debuffs from self.(Cooldown 2 mins.)
//Ep 4: Hero of the Village 3
//Ep 5: Temp disables enemies Elixirs and Abilities for 20 secs.(5 min cooldown)


    @Override
    public void applyPassiveEffects(Player player) {
        if (!ElixirSMPS2.ENABLED) return;
        int elixirPoints = PlayerDuck.of(player).getElixirPoints();
        if (elixirPoints < 0) {
            player.removeEffect(good);
            if (elixirPoints < -3) {
            } else {
                switch (elixirPoints) {
                    case -3 -> {
                    }
                    case -2 -> {
                    }
                    case -1 -> {
                        addMobEffect(player,good,0);
                    }
                }
            }
        } else {
            addMobEffect(player,good, 1);
        }
    }

    @Override
    protected boolean actuallyApplyActiveEffects(ServerPlayer player, int key) {
        switch (key) {
            case 0 -> {
                addMobEffect(player,good,1);
            }
            case 1 -> {
                addTempMobEffect(player,ModMobEffects.CHEAP_PRICES,0,20 * 15);
            }
            case 2 -> {
                List<Villager> nearby = getNearbyVillagers(player);
                for (Villager villager:nearby) {
                    addTempMobEffect(villager,MobEffects.GLOWING,0,15 * 20);
                }
            }
            case 3 -> {
                removeSomeEffects(player, MobEffectCategory.HARMFUL);
            }
            case 4 -> {
                addMobEffect(player,good,2);
            }
            case 5 -> {
                List<ServerPlayer> allPlayers = player.server.getPlayerList().getPlayers();
                for (ServerPlayer otherPlayer:allPlayers) {
                    if (otherPlayer == player) continue;
                    PlayerDuck otherPlayerDuck = PlayerDuck.of(otherPlayer);
                    Elixir elixir = otherPlayerDuck.getElixir();
                    if (elixir != null) {
                        elixir.disable(player,true);
                        for (int i = 0; i < otherPlayerDuck.getCooldowns().length;i++) {
                            otherPlayerDuck.getCooldowns()[i] = Math.max(20 * 20,otherPlayerDuck.getCooldowns()[i]);
                        }
                        notifyAbilityHit(otherPlayer,key);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onEPChange(ServerPlayer player, int oldEP, int newEP) {
        if (oldEP < -3 && newEP < -3) return;
        if (oldEP > -1 && newEP > -1) return;

        disable(player,false);
        applyPassiveEffects(player);
    }

    @Override
    public void disable(Player player, boolean positiveOnly) {
        PlayerDuck playerDuck = PlayerDuck.of(player);
        player.removeEffect(good);
        if (!positiveOnly) {

        }
    }

    protected static List<Villager> getNearbyVillagers(ServerPlayer player) {
        return player.serverLevel().getNearbyEntities(Villager.class,TargetingConditions.DEFAULT,player,player.getBoundingBox().inflate(64));
    }


}
