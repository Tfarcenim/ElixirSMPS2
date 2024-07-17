package tfar.elixirsmps2.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import tfar.elixirsmps2.init.ModItems;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.EFFECT_REROLL)
                .define('d', Blocks.DIAMOND_BLOCK).define('e',Blocks.EMERALD_BLOCK)
                .define('g',Blocks.GOLD_BLOCK).define('n', Items.NETHER_STAR)
                .pattern("ded")
                .pattern("gng")
                .pattern("ded")
                .unlockedBy(getHasName(Items.NETHER_STAR),has(Items.NETHER_STAR))
                .save(pWriter);
    }
}
