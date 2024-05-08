package dev.foltz.stagedweapons.entity;

import dev.foltz.stagedweapons.StagedWeapons;
import dev.foltz.stagedweapons.Util;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public abstract class ModEntities {
    private static final Map<Identifier, EntityType<?>> ALL_ENTITY_TYPES = new HashMap<>();

    public static final RegistryKey<DamageType> BULLET_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(StagedWeapons.MODID, "bullet"));

    // == Pistol Bullets
    public static final Identifier BULLET_PISTOL_ENTITY_TYPE = registerEntityType("bullet_pistol", BulletEntityTypeBuilder.create()
            .damage(8f)
            .speed(1.5f)
            .gravity(0.000025f)
            .dimensions(EntityDimensions.fixed(0.15f, 0.15f))
            .texturePath(Identifier.of(StagedWeapons.MODID, "textures/entity/bullet/bronze.png"))
            .build());

    public static final Identifier BULLET_PISTOL_DRAGON_BREATH_ENTITY_TYPE = registerEntityType("bullet_dragon_breath_pistol", BulletEntityTypeBuilder.create()
            .damage(10f)
            .speed(1.3f)
            .gravity(0.0005f)
            .isOnFire(true)
            .dimensions(EntityDimensions.fixed(0.15f, 0.15f))
            .texturePath(Identifier.of(StagedWeapons.MODID, "textures/entity/bullet/bronze_dragon_breath.png"))
            .build());

    // == Shotgun Bullets
    public static final Identifier BULLET_SHOTGUN_ENTITY_TYPE = registerEntityType("bullet_shotgun", BulletEntityTypeBuilder.create()
            .damage(5f)
            .speed(1.1f)
            .drag(0.00001f)
            .gravity(0.01f)
            .dimensions(EntityDimensions.fixed(0.2f, 0.2f))
            .texturePath(Identifier.of(StagedWeapons.MODID, "textures/entity/bullet/lead.png"))
            .build());

    public static final Identifier BULLET_SHOTGUN_BOUNCY_ENTITY_TYPE = registerEntityType("bullet_bouncy_shotgun", BulletEntityTypeBuilder.create()
            .damage(5f)
            .speed(1.2f)
            .drag(0.00001f)
            .gravity(0.04f)
            .isBouncy(true)
            .dimensions(EntityDimensions.fixed(0.2f, 0.2f))
            .texturePath(Identifier.of(StagedWeapons.MODID, "textures/entity/bullet/lead_bouncy.png"))
            .build());

    public static final Identifier BULLET_SHOTGUN_DRAGON_BREATH_ENTITY_TYPE = registerEntityType("bullet_dragon_breath_shotgun", BulletEntityTypeBuilder.create()
            .damage(6f)
            .speed(0.9f)
            .drag(0.00001f)
            .gravity(0.02f)
            .isOnFire(true)
            .dimensions(EntityDimensions.fixed(0.2f, 0.2f))
            .texturePath(Identifier.of(StagedWeapons.MODID, "textures/entity/bullet/lead_dragon_breath.png"))
            .build());

    // == Flamethrower "Bullets"
    public static final Identifier BULLET_FLAMETHROWER_ENTITY_TYPE = registerEntityType("bullet_flamethrower", BulletEntityTypeBuilder.create()
            .damage(0.5f)
            .speed(1.0f)
            .drag(0.025f)
            .gravity(0.00001f)
            .lifetime(Util.ticksFromSeconds(1.5f))
            .isOnFire(true)
            .dimensions(EntityDimensions.fixed(0.1f, 0.1f))
            .texturePath(Identifier.of(StagedWeapons.MODID, "textures/entity/bullet/flamethrower.png"))
            .build());

    public static Identifier registerEntityType(String name, EntityType<?> entityType) {
        Identifier id = Identifier.of(StagedWeapons.MODID, name);
        ALL_ENTITY_TYPES.put(id, entityType);
        return id;
    }

    public static void registerAllEntityTypes() {
        ALL_ENTITY_TYPES.forEach((id, type) -> Registry.register(Registries.ENTITY_TYPE, id, type));
    }
}
