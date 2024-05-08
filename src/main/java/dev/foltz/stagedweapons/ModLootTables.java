package dev.foltz.stagedweapons;

import dev.foltz.stagedweapons.item.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

import static net.minecraft.loot.LootTables.*;

public abstract class ModLootTables {
    public static final List<Identifier> ENTITIES_AMMO_PISTOL = List.of(
            EntityType.ZOMBIE.getLootTableId(),
            EntityType.ZOMBIE_VILLAGER.getLootTableId(),
            EntityType.DROWNED.getLootTableId(),
            EntityType.STRAY.getLootTableId(),
            EntityType.SKELETON.getLootTableId(),
            EntityType.PILLAGER.getLootTableId()
    );

    public static final List<Identifier> ENTITIES_AMMO_SHOTGUN = List.of(
            EntityType.ZOMBIE.getLootTableId(),
            EntityType.ZOMBIE_VILLAGER.getLootTableId(),
            EntityType.DROWNED.getLootTableId(),
            EntityType.PILLAGER.getLootTableId()
    );

    public static final List<Identifier> ENTITIES_AMMO_BOUNCY = List.of(
            EntityType.SLIME.getLootTableId()
    );

    public static final List<Identifier> ENTITIES_AMMO_DRAGON_BREATH = List.of(
            EntityType.PIGLIN_BRUTE.getLootTableId(),
            EntityType.PIGLIN.getLootTableId(),
            EntityType.ZOMBIFIED_PIGLIN.getLootTableId(),
            EntityType.WITHER_SKELETON.getLootTableId()
    );

    public static final List<Identifier> CHESTS_AMMO_PISTOL = List.of(
            ABANDONED_MINESHAFT_CHEST,
            SHIPWRECK_SUPPLY_CHEST,
            BURIED_TREASURE_CHEST,
            RUINED_PORTAL_CHEST,
            STRONGHOLD_CORRIDOR_CHEST,
            STRONGHOLD_CROSSING_CHEST,
            WOODLAND_MANSION_CHEST,
            DESERT_PYRAMID_CHEST,
            PILLAGER_OUTPOST_CHEST,
            VILLAGE_TOOLSMITH_CHEST,
            JUNGLE_TEMPLE_CHEST
    );

    public static final List<Identifier> CHESTS_AMMO_SHOTGUN = List.of(
            ABANDONED_MINESHAFT_CHEST,
            SHIPWRECK_SUPPLY_CHEST,
            BURIED_TREASURE_CHEST,
            RUINED_PORTAL_CHEST,
            STRONGHOLD_CORRIDOR_CHEST,
            STRONGHOLD_CROSSING_CHEST,
            WOODLAND_MANSION_CHEST,
            DESERT_PYRAMID_CHEST,
            PILLAGER_OUTPOST_CHEST,
            VILLAGE_TOOLSMITH_CHEST,
            JUNGLE_TEMPLE_CHEST
    );

    public static final List<Identifier> CHESTS_AMMO_BOUNCY = List.of(
            JUNGLE_TEMPLE_CHEST,
            BURIED_TREASURE_CHEST,
            ABANDONED_MINESHAFT_CHEST
    );

    public static final List<Identifier> CHESTS_AMMO_DRAGON_BREATH = List.of(
            RUINED_PORTAL_CHEST,
            NETHER_BRIDGE_CHEST,
            BASTION_BRIDGE_CHEST,
            BASTION_OTHER_CHEST,
            DESERT_PYRAMID_CHEST
    );

    public static final List<Identifier> CHESTS_GUN_REVOLVER = List.of(
            SIMPLE_DUNGEON_CHEST,
            DESERT_PYRAMID_CHEST,
            BURIED_TREASURE_CHEST,
            RUINED_PORTAL_CHEST,
            WOODLAND_MANSION_CHEST,
            VILLAGE_WEAPONSMITH_CHEST
    );

    public static final List<Identifier> CHESTS_GUN_PUMP_SHOTGUN = List.of(
            SIMPLE_DUNGEON_CHEST,
            BURIED_TREASURE_CHEST,
            PILLAGER_OUTPOST_CHEST,
            WOODLAND_MANSION_CHEST,
            VILLAGE_WEAPONSMITH_CHEST,
            DESERT_PYRAMID_CHEST,
            JUNGLE_TEMPLE_CHEST,
            BASTION_TREASURE_CHEST
    );

    public static final List<Identifier> CHESTS_GUN_FLAMETHROWER = List.of(
            BASTION_TREASURE_CHEST,
            BASTION_OTHER_CHEST,
            NETHER_BRIDGE_CHEST,
            RUINED_PORTAL_CHEST
    );

    public static void registerAllLootTables() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (ENTITIES_AMMO_PISTOL.contains(id)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(Registries.ITEM.get(ModItems.AMMO_PISTOL))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 6))))
                        .conditionally(RandomChanceWithLootingLootCondition.builder(0.05f, 0.01f))
                        .rolls(ConstantLootNumberProvider.create(1))
                );
            }

            if (ENTITIES_AMMO_SHOTGUN.contains(id)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(Registries.ITEM.get(ModItems.AMMO_PISTOL))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 6))))
                        .conditionally(RandomChanceWithLootingLootCondition.builder(0.05f, 0.01f))
                        .rolls(ConstantLootNumberProvider.create(1))
                );
            }

            if (ENTITIES_AMMO_BOUNCY.contains(id)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(Registries.ITEM.get(ModItems.AMMO_SHOTGUN_BOUNCY))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 6))))
                        .conditionally(RandomChanceWithLootingLootCondition.builder(0.05f, 0.01f))
                        .rolls(ConstantLootNumberProvider.create(1))
                );
            }

            if (ENTITIES_AMMO_DRAGON_BREATH.contains(id)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(Registries.ITEM.get(ModItems.AMMO_PISTOL_DRAGON_BREATH))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 6))))
                        .conditionally(RandomChanceWithLootingLootCondition.builder(0.05f, 0.01f))
                        .rolls(ConstantLootNumberProvider.create(1))
                );

                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(Registries.ITEM.get(ModItems.AMMO_SHOTGUN_DRAGON_BREATH))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3))))
                        .conditionally(RandomChanceWithLootingLootCondition.builder(0.05f, 0.01f))
                        .rolls(ConstantLootNumberProvider.create(1))
                );
            }

            if (CHESTS_AMMO_PISTOL.contains(id)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(Registries.ITEM.get(ModItems.AMMO_PISTOL))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8, 32))))
                        .conditionally(RandomChanceLootCondition.builder(0.5f))
                        .rolls(ConstantLootNumberProvider.create(1))
                );
            }

            if (CHESTS_AMMO_SHOTGUN.contains(id)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(Registries.ITEM.get(ModItems.AMMO_SHOTGUN))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8, 32))))
                        .conditionally(RandomChanceLootCondition.builder(0.5f))
                        .rolls(ConstantLootNumberProvider.create(1))
                );
            }

            if (CHESTS_AMMO_BOUNCY.contains(id)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(Registries.ITEM.get(ModItems.AMMO_SHOTGUN_BOUNCY))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8, 32))))
                        .conditionally(RandomChanceLootCondition.builder(0.5f))
                        .rolls(ConstantLootNumberProvider.create(1))
                );
            }

            if (CHESTS_AMMO_DRAGON_BREATH.contains(id)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(Registries.ITEM.get(ModItems.AMMO_SHOTGUN_DRAGON_BREATH))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8, 32))))
                        .conditionally(RandomChanceLootCondition.builder(0.5f))
                        .rolls(ConstantLootNumberProvider.create(1))
                );

                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(Registries.ITEM.get(ModItems.AMMO_PISTOL_DRAGON_BREATH))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8, 32))))
                        .conditionally(RandomChanceLootCondition.builder(0.5f))
                        .rolls(ConstantLootNumberProvider.create(1))
                );
            }

            if (CHESTS_GUN_REVOLVER.contains(id)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(Registries.ITEM.get(ModItems.REVOLVER)))
                        .conditionally(RandomChanceLootCondition.builder(0.5f))
                        .rolls(ConstantLootNumberProvider.create(1))
                );
            }

            if (CHESTS_GUN_PUMP_SHOTGUN.contains(id)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(Registries.ITEM.get(ModItems.PUMP_SHOTGUN)))
                        .conditionally(RandomChanceLootCondition.builder(0.5f))
                        .rolls(ConstantLootNumberProvider.create(1))
                );
            }

            if (CHESTS_GUN_FLAMETHROWER.contains(id)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(Registries.ITEM.get(ModItems.FLAMETHROWER)))
                        .conditionally(RandomChanceLootCondition.builder(0.5f))
                        .rolls(ConstantLootNumberProvider.create(1))
                );
            }
        });
    }
}
