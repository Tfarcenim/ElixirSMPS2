package tfar.elixirsmps2;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import tfar.elixirsmps2.commands.ModCommands;
import tfar.elixirsmps2.elixir.Elixir;
import tfar.elixirsmps2.init.ModItems;
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
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID,path);
    }

    public static void onDeath(LivingEntity living, DamageSource damageSource) {
        if (living instanceof ServerPlayer serverPlayer) {
            ItemStack stack = ModItems.ELIXIR_POINT.getDefaultInstance();
            ItemEntity itemEntity = new ItemEntity(living.level(), living.getX(), living.getY(), living.getZ(), stack);
            itemEntity.setUnlimitedLifetime();
            living.level().addFreshEntity(itemEntity);
            PlayerDuck.of(serverPlayer).addElixirPoints(-1);
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
        if (ElixirSMPS2.ENABLED) {
            PlayerDuck playerDuck = PlayerDuck.of(newPlayer);
            Elixir elixir = playerDuck.getElixir();
            if (elixir != null) {
                elixir.applyPassiveEffects(newPlayer);
            }
        }
    }

    public static void afterDamage(LivingEntity livingEntity, DamageSource source) {
        Entity attacker = source.getEntity();
        if (livingEntity instanceof Player player) {
            PlayerDuck playerDuck = PlayerDuck.of(player);

        }

        if (attacker instanceof Player player) {
            PlayerDuck playerDuck = PlayerDuck.of(player);
            if (playerDuck.isShouldBurnOnHit()) {
                livingEntity.setSecondsOnFire(2);
            }
            playerDuck.getOnNextHit().accept(player);
        }
    }

    public static float modifyDamage(LivingEntity livingEntity, DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (livingEntity instanceof Player player) {
            PlayerDuck playerDuck = PlayerDuck.of(player);
            if (source.is(DamageTypeTags.IS_FIRE)) {
                amount *= playerDuck.getFireDamageMultiplier();
            }
        }
        return amount;
    }

}