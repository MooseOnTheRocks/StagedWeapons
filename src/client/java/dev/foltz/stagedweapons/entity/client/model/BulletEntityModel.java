package dev.foltz.stagedweapons.entity.client.model;

import dev.foltz.stagedweapons.entity.BulletEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;

public class BulletEntityModel extends SinglePartEntityModel<BulletEntity> {
    private final ModelPart root;

    public BulletEntityModel(ModelPart root) {
        this.root = root;
    }

    @Override
    public void setAngles(BulletEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}