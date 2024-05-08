package dev.foltz.stagedweapons.datagen;

import dev.foltz.stagedweapons.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;

public class StagedWeaponsRecipeGenerator extends FabricRecipeProvider {

    public StagedWeaponsRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        // == Pistol Ammo
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Registries.ITEM.get(ModItems.AMMO_PISTOL), 4)
                .pattern("ccc")
                .pattern("cxc")
                .pattern("iIi")
                .input('c', Items.COPPER_INGOT)
                .input('i', Items.IRON_NUGGET)
                .input('x', Items.GUNPOWDER)
                .input('I', Items.IRON_INGOT)
                .criterion(FabricRecipeProvider.hasItem(Items.GUNPOWDER), FabricRecipeProvider.conditionsFromItem(Items.GUNPOWDER))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, Registries.ITEM.get(ModItems.AMMO_PISTOL_DRAGON_BREATH), 8)
                .input(Registries.ITEM.get(ModItems.AMMO_PISTOL), 8)
                .input(Items.BLAZE_POWDER)
                .criterion(FabricRecipeProvider.hasItem(Items.BLAZE_POWDER), FabricRecipeProvider.conditionsFromItem(Items.BLAZE_POWDER))
                .criterion(FabricRecipeProvider.hasItem(Registries.ITEM.get(ModItems.AMMO_PISTOL)), FabricRecipeProvider.conditionsFromItem(Registries.ITEM.get(ModItems.AMMO_PISTOL)))
                .offerTo(exporter);

        // == Shotgun Ammo
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Registries.ITEM.get(ModItems.AMMO_SHOTGUN), 2)
                .pattern("cxc")
                .pattern("cxc")
                .pattern("igi")
                .input('i', Items.IRON_NUGGET)
                .input('x', Items.GUNPOWDER)
                .input('c', Items.COPPER_INGOT)
                .input('g', Items.GOLD_INGOT)
                .criterion(FabricRecipeProvider.hasItem(Items.GUNPOWDER), FabricRecipeProvider.conditionsFromItem(Items.GUNPOWDER))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, Registries.ITEM.get(ModItems.AMMO_SHOTGUN_BOUNCY), 8)
                .input(Registries.ITEM.get(ModItems.AMMO_SHOTGUN), 8)
                .input(Items.SLIME_BALL)
                .criterion(FabricRecipeProvider.hasItem(Items.SLIME_BALL), FabricRecipeProvider.conditionsFromItem(Items.SLIME_BALL))
                .criterion(FabricRecipeProvider.hasItem(Registries.ITEM.get(ModItems.AMMO_SHOTGUN)), FabricRecipeProvider.conditionsFromItem(Registries.ITEM.get(ModItems.AMMO_SHOTGUN)))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, Registries.ITEM.get(ModItems.AMMO_SHOTGUN_DRAGON_BREATH), 8)
                .input(Registries.ITEM.get(ModItems.AMMO_SHOTGUN), 8)
                .input(Items.BLAZE_POWDER)
                .criterion(FabricRecipeProvider.hasItem(Items.BLAZE_POWDER), FabricRecipeProvider.conditionsFromItem(Items.BLAZE_POWDER))
                .criterion(FabricRecipeProvider.hasItem(Registries.ITEM.get(ModItems.AMMO_SHOTGUN)), FabricRecipeProvider.conditionsFromItem(Registries.ITEM.get(ModItems.AMMO_SHOTGUN)))
                .offerTo(exporter);

        // == Flamethrower Ammo
        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, Registries.ITEM.get(ModItems.AMMO_FLAMETHROWER), 8)
                .input(Items.BLAZE_POWDER, 4)
                .input(Items.MAGMA_CREAM)
                .criterion(FabricRecipeProvider.hasItem(Items.BLAZE_POWDER), FabricRecipeProvider.conditionsFromItem(Items.SLIME_BALL))
                .criterion(FabricRecipeProvider.hasItem(Items.MAGMA_CREAM), FabricRecipeProvider.conditionsFromItem(Items.MAGMA_CREAM))
                .offerTo(exporter);
    }
}
