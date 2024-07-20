package tfar.elixirsmps2.elixir;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;
import tfar.elixirsmps2.init.ModMobEffects;
import tfar.elixirsmps2.mixin.LivingEntityAccess;

import java.util.Iterator;
import java.util.List;

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
        if (player.hasEffect(ModMobEffects.STUNNED))return;
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

    protected static void addTempMobEffect(LivingEntity living, MobEffect mobEffect, int amplifier, int time) {
        living.addEffect(new MobEffectInstance(mobEffect,time,amplifier,false,false,true));
    }

    protected void notifyAbilityHit(ServerPlayer affected,int level) {
        affected.sendSystemMessage(Component.literal("You were hit by "+name+" EP "+level));
    }

    protected void playSound(ServerPlayer player) {
        player.playNotifySound(ElixirSMPS2.ABILITY_USED, SoundSource.PLAYERS,1,1);
    }

    protected static List<Player> getNearbyPlayers(ServerPlayer player) {
       return player.serverLevel().getNearbyPlayers(TargetingConditions.DEFAULT,player,player.getBoundingBox().inflate(6));
    }

    protected static Player getNearestPlayer(ServerPlayer player) {
        return player.serverLevel().getNearestPlayer(TargetingConditions.DEFAULT.copy().ignoreLineOfSight(),player);
    }

    protected static void removeSomeEffects(Player player, MobEffectCategory category) {
        Iterator<MobEffectInstance> iterator = player.getActiveEffectsMap().values().iterator();

        while (iterator.hasNext()) {
            MobEffectInstance effect = iterator.next();
            if (effect.getEffect().getCategory() == category) {
                // if(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.living.MobEffectEvent.Remove(this, effect))) continue;
                ((LivingEntityAccess) player).$onEffectRemoved(effect);
                iterator.remove();
            }
        }
    }

    protected static void removeNonElixirPositiveEffects(Player player) {
        PlayerDuck playerDuck = PlayerDuck.of(player);
        Elixir elixir = playerDuck.getElixir();
        Iterator<MobEffectInstance> iterator = player.getActiveEffectsMap().values().iterator();

        while (iterator.hasNext()) {
            MobEffectInstance effect = iterator.next();
            if (effect.getEffect().getCategory() == MobEffectCategory.BENEFICIAL && (elixir == null || elixir.good != effect.getEffect())) {
                // if(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.living.MobEffectEvent.Remove(this, effect))) continue;
                ((LivingEntityAccess) player).$onEffectRemoved(effect);
                iterator.remove();
            }
        }
    }

    protected static void push(Player player, Vec3 dir) {
        player.setDeltaMovement(player.getDeltaMovement().add(dir));
        player.hurtMarked = true;
    }

    protected static void addAttributeSafely(Player player, Attribute attribute, AttributeModifier modifier) {
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if (attributeInstance == null) return;
        if (attributeInstance.getModifier(modifier.getId()) != null) {
            attributeInstance.removeModifier(modifier.getId());
        }
        attributeInstance.addPermanentModifier(modifier);
    }
}
