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
    protected final MobEffect good;
    protected final MobEffect bad;


    public Elixir(String name, int[] cooldowns, MobEffect good, MobEffect bad) {
        this.name = name;
        this.cooldowns = cooldowns;
        this.good = good;
        this.bad = bad;
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

    protected static void addMobEffect(Player player, MobEffect mobEffect, int amplifier) {
        player.addEffect(new MobEffectInstance(mobEffect,MobEffectInstance.INFINITE_DURATION,amplifier,false,false,true));
    }

    protected static void addTempMobEffect(Player player, MobEffect mobEffect, int amplifier,int time) {
        player.addEffect(new MobEffectInstance(mobEffect,time,amplifier,false,false,true));
    }

    protected void notifyAbilityHit(ServerPlayer affected,int level) {
        affected.sendSystemMessage(Component.literal("You were hit by "+name+" EP "+level));
    }

    protected void playSound(ServerPlayer player) {
        player.playNotifySound(ElixirSMPS2.ABILITY_USED, SoundSource.PLAYERS,1,1);
    }

}
