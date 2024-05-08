package dev.foltz.stagedweapons.entity.client.model;

import net.minecraft.client.model.*;

public abstract class BulletEntityTexturedModels {
    public static TexturedModelData pistolBulletTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("main",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        // Small bullet model
                        .cuboid(-2f, 0.5f, -0.5f, 4f, 1.0f, 1.0f),
                ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    public static TexturedModelData shotgunBulletTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("main",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        // Big bullet model
                        .cuboid(-1f, 1f, -1f, 2.0f, 2.0f, 2.0f),
                ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    public static TexturedModelData flamethrowerBulletTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("main",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        // Big bullet model
                        .cuboid(-0.5f, 0.5f, -0.5f, 1.0f, 1.0f, 1.0f),
                ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }
}
