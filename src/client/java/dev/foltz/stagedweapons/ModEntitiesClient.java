package dev.foltz.stagedweapons;

import dev.foltz.stagedweapons.entity.BulletEntity;
import dev.foltz.stagedweapons.entity.ModEntities;
import dev.foltz.stagedweapons.entity.client.BulletEntityRenderer;
import dev.foltz.stagedweapons.entity.client.model.BulletEntityTexturedModels;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public abstract class ModEntitiesClient {
    public static void registerAllEntityRenderers() {
        registerBulletEntityRendererAndModel(
                (EntityType<BulletEntity>) Registries.ENTITY_TYPE.get(ModEntities.BULLET_PISTOL_ENTITY_TYPE),
                new EntityModelLayer(new Identifier(StagedWeapons.MODID, "pistol_bullet"), "main"),
                BulletEntityTexturedModels::pistolBulletTexturedModelData
        );

        registerBulletEntityRendererAndModel(
                (EntityType<BulletEntity>) Registries.ENTITY_TYPE.get(ModEntities.BULLET_PISTOL_DRAGON_BREATH_ENTITY_TYPE),
                new EntityModelLayer(new Identifier(StagedWeapons.MODID, "pistol_dragon_breath_bullet"), "main"),
                BulletEntityTexturedModels::pistolBulletTexturedModelData
        );

        registerBulletEntityRendererAndModel(
                (EntityType<BulletEntity>) Registries.ENTITY_TYPE.get(ModEntities.BULLET_SHOTGUN_ENTITY_TYPE),
                new EntityModelLayer(new Identifier(StagedWeapons.MODID, "shotgun_bullet"), "main"),
                BulletEntityTexturedModels::shotgunBulletTexturedModelData
        );

        registerBulletEntityRendererAndModel(
                (EntityType<BulletEntity>) Registries.ENTITY_TYPE.get(ModEntities.BULLET_SHOTGUN_BOUNCY_ENTITY_TYPE),
                new EntityModelLayer(new Identifier(StagedWeapons.MODID, "shotgun_bouncy_bullet"), "main"),
                BulletEntityTexturedModels::shotgunBulletTexturedModelData
        );

        registerBulletEntityRendererAndModel(
                (EntityType<BulletEntity>) Registries.ENTITY_TYPE.get(ModEntities.BULLET_SHOTGUN_DRAGON_BREATH_ENTITY_TYPE),
                new EntityModelLayer(new Identifier(StagedWeapons.MODID, "shotgun_dragon_breath_bullet"), "main"),
                BulletEntityTexturedModels::shotgunBulletTexturedModelData
        );

        registerBulletEntityRendererAndModel(
                (EntityType<BulletEntity>) Registries.ENTITY_TYPE.get(ModEntities.BULLET_FLAMETHROWER_ENTITY_TYPE),
                new EntityModelLayer(new Identifier(StagedWeapons.MODID, "flamethrower_bullet"), "main"),
                BulletEntityTexturedModels::flamethrowerBulletTexturedModelData
        );
    }

    public static void registerBulletEntityRendererAndModel(EntityType<BulletEntity> entityType, EntityModelLayer entityModelLayer, EntityModelLayerRegistry.TexturedModelDataProvider entityModelDataProvider) {
        registerEntityRendererAndModel(entityType, (context) -> new BulletEntityRenderer(context, entityModelLayer), entityModelLayer, entityModelDataProvider);
    }

    public static <T extends Entity> void registerEntityRendererAndModel(EntityType<T> entityType, EntityRendererFactory<T> entityRendererFactory, EntityModelLayer entityModelLayer, EntityModelLayerRegistry.TexturedModelDataProvider entityModelDataProvider) {
        EntityRendererRegistry.register(entityType, entityRendererFactory);
        EntityModelLayerRegistry.registerModelLayer(entityModelLayer, entityModelDataProvider);
    }
}
