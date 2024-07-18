package tfar.elixirsmps2.elixir;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;

import java.util.List;

public class StrengthElixir extends Elixir {
    public StrengthElixir(String name,int[] cooldowns) {
        super(name,cooldowns);
    }


//Ep -4: Weakness 2
//Ep -3: Weakness 1
//Ep -2: No Effect
//Ep -1: Strength 1
//Ep 0: Strength 2
//Ep 1: Effect enemy with Weakness for 20 Secs. Cooldown. 1 Minute
//Ep 2: Remove Weakness and Slowness from self(Only if you have the effect) Cooldown 2 min.
//Ep 3: Strength 3 for one hit. Cooldown 2 min.
//Ep 4: Remove strength from all enemies. Cooldown 3 Mins.
//Ep 5: Temp disable other peoples Elixirs for 15 secs. Only disables 0 to 5 ep. (Cooldown 5 Mins.)


    @Override
    public void applyPassiveEffects(Player player) {
        if (!ElixirSMPS2.ENABLED) return;
        int elixirPoints = PlayerDuck.of(player).getElixirPoints();
        if (elixirPoints < 0) {
            player.removeEffect(MobEffects.DAMAGE_BOOST);
            if (elixirPoints < -3) {
                addMobEffect(player,MobEffects.WEAKNESS,1);
            } else {
                player.removeEffect(MobEffects.WEAKNESS);
                switch (elixirPoints) {
                    case -3 -> {
                        addMobEffect(player,MobEffects.WEAKNESS,0);
                    }
                    case -2 -> {
                    }
                    case -1 -> {
                        addMobEffect(player,MobEffects.DAMAGE_BOOST,0);
                    }
                }
            }
        } else {
            player.removeEffect(MobEffects.WEAKNESS);
            addMobEffect(player,MobEffects.DAMAGE_BOOST, 1);
        }
    }

    @Override
    protected boolean actuallyApplyActiveEffects(ServerPlayer player, int key) {
        switch (key) {
            case 0 -> {
                addMobEffect(player,MobEffects.DAMAGE_BOOST,2);
            }
            case 1 -> {
                List<Player> nearby = player.serverLevel().getNearbyPlayers(TargetingConditions.DEFAULT,player,player.getBoundingBox().inflate(6));
                for (Player otherPlayer:nearby) {
                    otherPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,20 * 20,0));
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                }
            }
            case 2 -> {
                boolean didSomething = player.removeEffect(MobEffects.WEAKNESS);
                if (player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                    didSomething = true;
                }
                return didSomething;
            }
            case 3 -> {
                addMobEffect(player,MobEffects.DAMAGE_BOOST,2);
                PlayerDuck playerDuck = PlayerDuck.of(player);
                playerDuck.setOnNextHit(player1 -> {
                    player1.removeEffect(MobEffects.DAMAGE_BOOST);
                    addMobEffect(player1,MobEffects.DAMAGE_BOOST,1);
                    playerDuck.getCooldowns()[3] = cooldowns[3];
                });
                return false;
            }
            case 4 -> {
                List<ServerPlayer> allPlayers = player.server.getPlayerList().getPlayers();
                for (ServerPlayer otherPlayer:allPlayers) {
                    if (otherPlayer == player) continue;
                    otherPlayer.removeEffect(MobEffects.DAMAGE_BOOST);
                    notifyAbilityHit(otherPlayer,key);
                }
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
                            otherPlayerDuck.getCooldowns()[i] = Math.max(15 * 20,otherPlayerDuck.getCooldowns()[i]);
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
        player.removeEffect(MobEffects.DAMAGE_BOOST);
        playerDuck.setOnNextHit(player1 -> {});
        if (!positiveOnly) {
            player.removeEffect(MobEffects.WEAKNESS);
            player.removeEffect(MobEffects.DAMAGE_BOOST);
        }
    }
}
