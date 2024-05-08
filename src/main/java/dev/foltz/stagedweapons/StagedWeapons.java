package dev.foltz.stagedweapons;

import dev.foltz.stagedweapons.enchantment.ModEnchantments;
import dev.foltz.stagedweapons.entity.ModEntities;
import dev.foltz.stagedweapons.item.ModItems;
import dev.foltz.stagedweapons.networking.ModNetworking;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.minecraft.loot.LootTables.*;

public class StagedWeapons implements ModInitializer {
	public static final String MODID = "stagedweapons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello, StagedWeapons!");

		ModEntities.registerAllEntityTypes();
		ModItems.registerAllItems();
		ModItems.registerAllItemGroups();
		ModNetworking.registerAllEvents();
		ModLootTables.registerAllLootTables();
		ModEnchantments.registerAllEnchantments();
	}
}