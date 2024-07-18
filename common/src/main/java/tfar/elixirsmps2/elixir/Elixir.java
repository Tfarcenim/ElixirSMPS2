package tfar.elixirsmps2.elixir;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;

public class Elixir {
    private final String name;
    protected final int[] cooldowns;

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




    public Elixir(String name,int[] cooldowns) {
        this.name = name;
        this.cooldowns = cooldowns;
    }

    public String getName() {
        return name;
    }

    public void applyPassiveEffects(Player player) {
    }

    public void disable(Player player,boolean positiveOnly) {

    }

    public final void attemptApplyActiveEffect(ServerPlayer player, int key) {
        if (!ElixirSMPS2.ENABLED)return;
        if (key > 5) return;
        PlayerDuck playerDuck = PlayerDuck.of(player);
        int elixirPoints = playerDuck.getElixirPoints();
        if (elixirPoints < key) return;
        int cooldown = playerDuck.getCooldowns()[key];
        if (cooldown > 0)return;
        boolean shouldCooldown = actuallyApplyActiveEffects(player,key);
        if (shouldCooldown) {
            playerDuck.getCooldowns()[key] = cooldowns[key];
        }
        playSound(player);
    }

    public void onEPChange(ServerPlayer player,int oldEP,int newEP) {

    }

    protected boolean actuallyApplyActiveEffects(ServerPlayer player,int key) {
        return false;
    }

    protected static void addMobEffect(Player player, MobEffect mobEffect, int i) {
        player.addEffect(new MobEffectInstance(mobEffect,MobEffectInstance.INFINITE_DURATION,i,false,false,true));
    }

    protected void notifyAbilityHit(ServerPlayer affected,int level) {
        affected.sendSystemMessage(Component.literal("You were hit by "+name+" EP "+level));
    }

    protected void playSound(ServerPlayer player) {
        player.playNotifySound(ElixirSMPS2.ABILITY_USED, SoundSource.PLAYERS,1,1);
    }

}
