package tfar.elixirsmps2;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import tfar.elixirsmps2.commands.ModCommands;
import tfar.elixirsmps2.elixir.Elixir;
import tfar.elixirsmps2.elixir.Elixirs;
import tfar.elixirsmps2.init.ModItems;
import tfar.elixirsmps2.init.ModMobEffects;
import tfar.elixirsmps2.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class ElixirSMPS2 {

    public static final String MOD_ID = "elixirsmps2";
    public static final String MOD_NAME = "ElixirSMPS2";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final SoundEvent ABILITY_USED = SoundEvents.SNOWBALL_THROW;

    public static boolean ENABLED = true;

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        Services.PLATFORM.registerAll(ModItems.class, BuiltInRegistries.ITEM, Item.class);
        Services.PLATFORM.registerAll(ModMobEffects.class, BuiltInRegistries.MOB_EFFECT, MobEffect.class);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID,path);
    }

    public static void onDeath(LivingEntity living, DamageSource damageSource) {
        if (!ENABLED)return;
        if (living instanceof ServerPlayer serverPlayer) {
            PlayerDuck playerDuck = PlayerDuck.of(serverPlayer);
            if (playerDuck.getElixirPoints() <-4)return;
            ItemStack stack = ModItems.ELIXIR_POINT.getDefaultInstance();
            ItemEntity itemEntity = new ItemEntity(living.level(), living.getX(), living.getY(), living.getZ(), stack);
            itemEntity.setUnlimitedLifetime();
            living.level().addFreshEntity(itemEntity);
            playerDuck.addElixirPoints(-1);
        }
    }

    public static void onClone(ServerPlayer originalPlayer,ServerPlayer newPlayer,boolean alive) {
        PlayerDuck.of(originalPlayer).copyTo(newPlayer);
    }

    public static void onLogin(ServerPlayer player) {
        if (ENABLED) {
            PlayerDuck playerDuck = PlayerDuck.of(player);
            Elixir elixir = playerDuck.getElixir();
            if (elixir == null) {
                ModCommands.reroll(player);
            }
        }
    }

    public static void onAfterRespawn(ServerPlayer originalPlayer,ServerPlayer newPlayer,boolean alive) {
        if (ENABLED) {
            PlayerDuck playerDuck = PlayerDuck.of(newPlayer);
            Elixir elixir = playerDuck.getElixir();
            if (elixir != null) {
                elixir.applyPassiveEffects(newPlayer);
            }
        }
    }

    public static void afterDamage(LivingEntity target, DamageSource source) {
        if (!ENABLED) return;
        Entity attacker = source.getEntity();
        if (target instanceof Player player) {
            PlayerDuck playerDuck = PlayerDuck.of(player);
            if (playerDuck.getElixir() == Elixirs.INVISIBILITY) {
                if (playerDuck.getElixirPoints() <= -2){
                    player.addEffect(new MobEffectInstance(MobEffects.GLOWING,30 *20,0));
                }
            }
        }

        if (attacker instanceof Player player) {
            PlayerDuck playerDuck = PlayerDuck.of(player);
            if (playerDuck.isShouldBurnOnHit()) {
                target.setSecondsOnFire(2);
            }

            if (playerDuck.getElixir() == Elixirs.INVISIBILITY) {
                if (playerDuck.getElixirPoints() >= 1) {
                    target.addEffect(new MobEffectInstance(MobEffects.GLOWING,30 *20,0));
                }
            }

            playerDuck.getOnNextHit().accept(player);
        }
    }

    public static float modifyDamage(LivingEntity livingEntity, DamageSource source, float amount) {
        if (!ENABLED)return amount;
        Entity attacker = source.getEntity();
        if (livingEntity instanceof Player player) {
            PlayerDuck playerDuck = PlayerDuck.of(player);
            if (source.is(DamageTypeTags.IS_FIRE)) {
                amount *= playerDuck.getFireDamageMultiplier();
            }
            Elixir elixir = playerDuck.getElixir();
            if (source.is(DamageTypes.DROWN)) {
                if (elixir == Elixirs.WATER_BREATHING && playerDuck.getElixirPoints() < -3) {
                    amount *=2;
                }
            }
            if (elixir == Elixirs.RESISTANCE && playerDuck.getElixirPoints() < -3) {
                amount *= 1.2;
            }
        }

        if (attacker instanceof Player player) {
            if (player.hasEffect(ModMobEffects.GENERIC_DAMAGE_BOOST)) {
                amount *= 1+ (player.getEffect(ModMobEffects.GENERIC_DAMAGE_BOOST).getAmplifier() +1) *.1;
            }
        }

        return amount;
    }

    public static void onPlayerPriceUpdate(Villager villager,Player player) {
        if (!ENABLED)return;
        PlayerDuck playerDuck = PlayerDuck.of(player);
        Elixir elixir = playerDuck.getElixir();
        if (elixir == Elixirs.HERO_OF_THE_VILLAGE) {
            int points = playerDuck.getElixirPoints();
            if (points < -3) {
                for (MerchantOffer merchantoffer : villager.getOffers()) {
                    merchantoffer.addToSpecialPriceDiff(merchantoffer.getCostA().getCount());
                }
            } else {
                if (points == -3) {
                    for (MerchantOffer merchantoffer : villager.getOffers()) {
                        merchantoffer.addToSpecialPriceDiff((int) (merchantoffer.getCostA().getCount() * .5));
                    }
                }
                if (player.hasEffect(ModMobEffects.CHEAP_PRICES)) {
                    for (MerchantOffer merchantoffer : villager.getOffers()) {
                        if (merchantoffer.getBaseCostA().is(Items.EMERALD)) {
                            merchantoffer.addToSpecialPriceDiff(-100);
                        }
                    }
                }
            }
        }
    }
}
//Strength Elixir
//Ep -3 is gives weakness 2 instead of weakness 1
//Ep 1 not giving weakness to enemy
//
//Speed Elixir 
//Ep -3 doesnt give slowness 1 it gives slowness 2.
//Ep 1 Doesnt give enemy slowness
//Ep 5 Doesnt give enemy Blindness
//
//Fire Res Elixir
//Ep 3 Does not remove fire res from enemy
//Ep -3 is giving slowness instead of being ep -4
//
//Water Breathing Elixir
//Ep 4 Doesnt give poison to enemy in water(Does give to person using ability)
//Ep 3 Doesnt give enemy freezing but gives person using ability freezing
//
//Haste Elixir
//Ep 4 makes a stone box above player(legs arent covered but covers head) make it so it fully encloses the player
//Ep 3 gives Slowness and mining fatigue but its only meant to be slowness
//
//Hero Of Village
//Ep -3 and -4 are the same.
//Ep -4 is suppose to be more expensive
//
//Invis Elixir
//Ep 5 Pulls enemy towards player instead of pushing away. Its meant to push away.
//Ep 2 is not 20 blocks away its like 10 blocks.
//Ep 1 Does not give enemy glowing every hit
//Ep -3 Gives glowing and Slowness meant to just be glowing
//
//Luck Elixir
//Ep 1 not working at all
//Ep 2 not working at all
//Ep 4 not working at all
//Ep 5 gives 20-30 hearts meant to be 11-20 hearts. Meant to just set self to 11-20 hearts instead of adding them.
//Ep -3 is giving bad luck 2 instead of bad luck 1 
//
//
//
//
//Regen Elixir 
//Ep 2 doesnt remove 2 hearts.(Gives enemy health sinking effect though)
//Ep 3 gives self poison instead of enemy
//Ep -3 sets self to 8 hearts instead of ep -4
//When i gave myself more ep it didnt remove Weakness and slowness
//
//Resistance Elixir
//Ep 4 Does not remove strength or disable strength from enemy.
//Ep 4 and Ep 3 is swapped.(Deal 30% damage still doesnt work)
//Ep -3 takes 20% more damage
//Ep -4 still takes 20% more damage
//
//Other
//Drinking milk removes elixir effects(not meant to)
//Please enchant the elixir reroll and Ep to make it look a lil better.
//When player dies at -4 ep it doesnt ban them nor set them to -5
//
//Sounds:
//When gaining ep - minecraft:block.respawn_anchor.charge 
//When rerolling elixir - minecraft:ui.cartography_table.take_result 
//Ability using can stay snowball throw