package tfar.elixirsmps2.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.init.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ElixirSMPS2.MOD_ID, existingFileHelper);
    }

    ModelFile.ExistingModelFile GENERATED = getExistingFile(mcLoc("item/generated"));

    @Override
    protected void registerModels() {
        makeOneLayerItem(ModItems.EFFECT_REROLL,new ResourceLocation("item/nether_star"));
        makeOneLayerItem(ModItems.ELIXIR_POINT,new ResourceLocation("item/red_dye"));
    }

    protected void makeOneLayerItem(Item item, ResourceLocation texture) {
        String path = BuiltInRegistries.ITEM.getKey(item).getPath();
        if (existingFileHelper.exists(texture , PackType.CLIENT_RESOURCES, ".png", "textures")) {
            getBuilder(path).parent(GENERATED).texture("layer0", texture);
        } else {
            System.out.println("no texture for " + texture + " found, skipping");
        }
    }

    protected void makeOneLayerItem(Item item) {
        ResourceLocation texture = BuiltInRegistries.ITEM.getKey(item);
        makeOneLayerItem(item, new ResourceLocation(texture.getNamespace(), "item/" + texture.getPath()));
    }

}
