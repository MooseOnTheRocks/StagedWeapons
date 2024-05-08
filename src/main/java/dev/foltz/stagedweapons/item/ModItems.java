package dev.foltz.stagedweapons.item;

import dev.foltz.stagedweapons.StagedWeapons;
import dev.foltz.stagedweapons.entity.ModEntities;
import dev.foltz.stagedweapons.item.ammo.*;
import dev.foltz.stagedweapons.item.gun.*;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ModItems {
    public static final Map<Identifier, Item> ALL_ITEMS = new HashMap<>();
    public static final Map<Identifier, ItemGroup> ALL_ITEM_GROUPS = new HashMap<>();

    // == Ammo Items
    // -- Pistol ammo
    public static final Identifier AMMO_PISTOL = registerItem("ammo/pistol", AmmoItem.builder()
            .entityType(Registries.ENTITY_TYPE.get(ModEntities.BULLET_PISTOL_ENTITY_TYPE))
            .bulletSpeed(1.5f)
            .build());

    public static final Identifier AMMO_PISTOL_DRAGON_BREATH = registerItem("ammo/pistol_dragon_breath", AmmoItem.builder()
            .entityType(ModEntities.BULLET_PISTOL_DRAGON_BREATH_ENTITY_TYPE)
            .bulletSpeed(1.3f)
            .build());

    // -- Shotgun ammo
    public static final Identifier AMMO_SHOTGUN = registerItem("ammo/shotgun", AmmoItem.builder()
            .entityType(ModEntities.BULLET_SHOTGUN_ENTITY_TYPE)
            .bulletSpeed(1.1f)
            .bulletDivergence(15f)
            .bulletCount(stack -> Random.create().nextBetween(5, 7))
            .build());

    public static final Identifier AMMO_SHOTGUN_BOUNCY = registerItem("ammo/shotgun_bouncy", AmmoItem.builder()
            .entityType(ModEntities.BULLET_SHOTGUN_BOUNCY_ENTITY_TYPE)
            .bulletSpeed(1f)
            .bulletDivergence(10f)
            .bulletCount(stack -> Random.create().nextBetween(5, 7))
            .build());

    public static final Identifier AMMO_SHOTGUN_DRAGON_BREATH = registerItem("ammo/shotgun_dragon_breath", AmmoItem.builder()
            .entityType(ModEntities.BULLET_SHOTGUN_DRAGON_BREATH_ENTITY_TYPE)
            .bulletSpeed(1f)
            .bulletDivergence(10f)
            .bulletCount(stack -> Random.create().nextBetween(5, 7))
            .build());

    // == Flamethrower ammo
    public static final Identifier AMMO_FLAMETHROWER = registerItem("ammo/flamethrower", AmmoItem.builder()
            .entityType(ModEntities.BULLET_FLAMETHROWER_ENTITY_TYPE)
            .bulletSpeed(1f)
            .bulletDivergence(25f)
            .bulletCount(stack -> Random.create().nextBetween(6, 10))
            .build());

    // == Ammo Types
    public static final AmmoType AMMO_TYPE_PISTOL = new AmmoType(Identifier.of(StagedWeapons.MODID, "ammo_type_pistol"), AMMO_PISTOL);
    public static final AmmoType AMMO_TYPE_SHOTGUN = new AmmoType(Identifier.of(StagedWeapons.MODID, "ammo_type_shotgun"), AMMO_SHOTGUN);
    public static final AmmoType AMMO_TYPE_FLAMETHROWER = new AmmoType(Identifier.of(StagedWeapons.MODID, "ammo_type_flamethrower"), AMMO_FLAMETHROWER);

    // == Gun Items
    public static final Identifier EOKA = registerItem("gun/eoka", new EokaItem());
    public static final Identifier FLINTLOCK_PISTOL = registerItem("gun/flintlock_pistol", new FlintlockPistolItem());
    public static final Identifier REVOLVER = registerItem("gun/revolver", new RevolverItem());
    public static final Identifier PUMP_SHOTGUN = registerItem("gun/pump_shotgun", new PumpShotgunItem());
    public static final Identifier FLAMETHROWER = registerItem("gun/flamethrower", new FlamethrowerItem());

    // == Item Groups
    public static final Identifier MAIN_GROUP = registerItemGroup("main_group", List.of(
        EOKA,
        FLINTLOCK_PISTOL,
        REVOLVER,
        PUMP_SHOTGUN,
        FLAMETHROWER,
        AMMO_PISTOL,
        AMMO_PISTOL_DRAGON_BREATH,
        AMMO_SHOTGUN,
        AMMO_SHOTGUN_BOUNCY,
        AMMO_SHOTGUN_DRAGON_BREATH,
        AMMO_FLAMETHROWER
    ), () -> new ItemStack(Registries.ITEM.get(REVOLVER)));

    public static Identifier registerItem(String name, Item item) {
        Identifier id = Identifier.of(StagedWeapons.MODID, name + (item instanceof CompositeItem ? "/default" : ""));
        ALL_ITEMS.put(id, item);
        return id;
    }

    public static void registerAllItems() {
        ALL_ITEMS.forEach((id, type) -> Registry.register(Registries.ITEM, id, type));
    }

    public static Identifier registerItemGroup(String name, List<Identifier> items, Supplier<ItemStack> icon) {
        Identifier id = Identifier.of(StagedWeapons.MODID, name);
        ItemGroup group = FabricItemGroup.builder()
            .icon(icon)
            .entries((displayContext, entries) -> items.stream().map(Registries.ITEM::get).map(ItemStack::new).forEach(entries::add))
            .displayName(Text.translatable("itemGroup." + StagedWeapons.MODID + "." + name))
            .build();
        ALL_ITEM_GROUPS.put(id, group);
        return id;
    }

    public static void registerAllItemGroups() {
        ALL_ITEM_GROUPS.forEach((id, type) -> Registry.register(Registries.ITEM_GROUP, id, type));
    }
}
