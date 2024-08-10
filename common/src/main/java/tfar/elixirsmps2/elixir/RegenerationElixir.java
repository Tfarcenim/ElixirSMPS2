package tfar.elixirsmps2.elixir;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;
import tfar.elixirsmps2.init.ModMobEffects;

import java.util.List;
import java.util.UUID;

public class RegenerationElixir extends Elixir {
    public RegenerationElixir(String name, int[] cooldowns, MobEffect good, MobEffect bad) {
        super(name,cooldowns,good,bad);
    }


//Regeneration:
//Ep -4: 8 hearts.
//Ep -3: Slowness 1
//Ep -2: Weakness 1
//Ep -1: No Effect
//Ep 0: Regeneration 1
//Ep 1: Fully heal to 10 hearts.(Cooldown 3 mins)
//Ep 2: Take 2 hearts from the enemy for 20 secs.(Cooldown 3 mins)(Cannot be stacked)
//Ep 3: Give enemy poison 1 for 20 secs.(3 min cooldown.)
//Ep 4: Regen 2 for 30 secs.(Cooldown 3 mins)
//Ep 5: Max hearts set to 15.

    public static final UUID uuid = UUID.fromString("5eb6dd61-eaf4-44b7-aac6-e1cc979e9980");

    @Override
    public void applyPassiveEffects(Player player) {
        if (!ElixirSMPS2.ENABLED) return;
        int elixirPoints = PlayerDuck.of(player).getElixirPoints();
        if (elixirPoints < 0) {
            player.removeEffect(good);
            if (elixirPoints < -3) {
                addMobEffect(player,MobEffects.MOVEMENT_SLOWDOWN,0);
                addMobEffect(player,MobEffects.WEAKNESS,0);
                addMobEffect(player,ModMobEffects.HEALTH_SINKING,0);
                capHealth(player);
            } else {
                switch (elixirPoints) {
                    case -3 -> {
                        addMobEffect(player,MobEffects.MOVEMENT_SLOWDOWN,0);
                        addMobEffect(player,MobEffects.WEAKNESS,0);
                    }
                    case -2 -> {
                        addMobEffect(player,MobEffects.WEAKNESS,0);
                    }
                    case -1 -> {
                    }
                }
            }
        } else {
            addMobEffect(player,good, 0);
        }
    }

    @Override
    protected boolean actuallyApplyActiveEffects(ServerPlayer user, int key) {
        boolean didSomething = false;
        switch (key) {
            case 0 -> {
                didSomething |= addMobEffect(user,good,0);
            }
            case 1 -> {
                user.heal(20);
                didSomething = true;
            }
            case 2 -> {
                List<Player> nearby = user.serverLevel().getNearbyPlayers(TargetingConditions.DEFAULT, user, user.getBoundingBox().inflate(16));
                for (Player otherPlayer:nearby) {
                    didSomething |= addTempMobEffect(otherPlayer,ModMobEffects.HEALTH_SINKING,0,20 *20);
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                }
            }
            case 3 -> {
                List<Player> nearby = user.serverLevel().getNearbyPlayers(TargetingConditions.DEFAULT, user, user.getBoundingBox().inflate(16));
                for (Player otherPlayer:nearby) {
                    didSomething |= addTempMobEffect(user,MobEffects.POISON,0,20 *20);
                    notifyAbilityHit((ServerPlayer) otherPlayer,key);
                }
            }
            case 4 -> {
                didSomething |= addTempMobEffect(user,good,1,30 * 20);
            }
            case 5 -> {
                didSomething |= addAttributeSafely(user, Attributes.MAX_HEALTH,new AttributeModifier(uuid,"Regen elixir",10, AttributeModifier.Operation.ADDITION));
            }
        }
        return didSomething;
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
        player.removeEffect(good);
        AttributeModifier attributeModifier = player.getAttribute(Attributes.MAX_HEALTH).getModifier(uuid);
        if (attributeModifier!= null && attributeModifier.getAmount() > 0) {
            player.getAttribute(Attributes.MAX_HEALTH).removeModifier(uuid);
            capHealth(player);
        }
    }

    protected static void capHealth(Player player) {
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }
}
