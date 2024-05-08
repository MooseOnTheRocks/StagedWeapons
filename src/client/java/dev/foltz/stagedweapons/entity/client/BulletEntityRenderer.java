package dev.foltz.stagedweapons.entity.client;

import dev.foltz.stagedweapons.entity.BulletEntity;
import dev.foltz.stagedweapons.entity.client.model.BulletEntityModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class BulletEntityRenderer extends EntityRenderer<BulletEntity> {
    private final BulletEntityModel model;

    public BulletEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer) {
        super(context);
        this.model = new BulletEntityModel(context.getPart(layer));
    }

    @Override
    public void render(BulletEntity bulletEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {

        matrixStack.push();
        matrixStack.translate(0.0f, 0.0f, 0.0f);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp((float)g, (float)bulletEntity.prevYaw, (float)bulletEntity.getYaw()) - 90.0f));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp((float)g, (float)bulletEntity.prevPitch, (float)bulletEntity.getPitch())));
        this.model.setAngles(bulletEntity, g, 0.0f, -0.1f, 0.0f, 0.0f);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(getTexture(bulletEntity)));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
        super.render(bulletEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(BulletEntity entity) {
        return entity.texturePath;
    }
}
