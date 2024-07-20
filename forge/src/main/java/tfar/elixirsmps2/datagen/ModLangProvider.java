package tfar.elixirsmps2.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import org.codehaus.plexus.util.StringUtils;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.init.ModItems;
import tfar.elixirsmps2.init.ModMobEffects;

import java.util.function.Supplier;


public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput output) {
        super(output, ElixirSMPS2.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addDefaultItem(() -> ModItems.ELIXIR_POINT);
        addDefaultItem(() -> ModItems.EFFECT_REROLL);

        addEffect(() -> ModMobEffects.FREEZING,"Freezing");
        addEffect(() -> ModMobEffects.INSTANT_MINE,"Instant Mine");
        addEffect(() -> ModMobEffects.STUNNED,"Stunned");
        addEffect(() -> ModMobEffects.CHEAP_PRICES,"Cheap Prices");
        addEffect(() -> ModMobEffects.HEALTH_SINKING,"Health Sinking");
        addEffect(() -> ModMobEffects.GENERIC_DAMAGE_BOOST,"Generic Damage Boost");
    }

    protected void addDefaultItem(Supplier<? extends Item> supplier) {
        addItem(supplier,getNameFromItem(supplier.get()));
    }

    protected void addDefaultBlock(Supplier<? extends Block> supplier) {
        addBlock(supplier,getNameFromBlock(supplier.get()));
    }

    protected void addDefaultEnchantment(Supplier<? extends Enchantment> supplier) {
        addEnchantment(supplier,getNameFromEnchantment(supplier.get()));
    }

    protected void addDefaultEntityType(Supplier<EntityType<?>> supplier) {
        addEntityType(supplier,getNameFromEntity(supplier.get()));
    }

    public static String getNameFromItem(Item item) {
        return StringUtils.capitaliseAllWords(item.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    public static String getNameFromBlock(Block block) {
        return StringUtils.capitaliseAllWords(block.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    public static String getNameFromEnchantment(Enchantment enchantment) {
        return StringUtils.capitaliseAllWords(enchantment.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    public static String getNameFromEntity(EntityType<?> entity) {
        return StringUtils.capitaliseAllWords(entity.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

}
