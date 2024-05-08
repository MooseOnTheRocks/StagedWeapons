package dev.foltz.stagedweapons.mixin.client;

import dev.foltz.stagedweapons.item.gun.GunItem;
import dev.foltz.stagedweapons.networking.client.ClientState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Shadow protected abstract void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress);

    @Shadow public abstract void renderItem(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light);

    @Shadow public abstract void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Accessor
    protected abstract ItemStack getMainHand();

    // todo: getUsingItemHandRenderType

    @Inject(
        method="renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
        at=@At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            ordinal = 0),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true)
    public void doRenderNoEquipOffset(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci, float f_, Hand hand, float g_, HeldItemRenderer.HandRenderType handRenderType, float h_, float i_, float j_, float k_) {
        var stack = player.getMainHandStack();
        var entityState = ClientState.getEntityState(player);

        if (entityState.isAiming() && stack.getItem() instanceof GunItem<?> stagedItem) {
            boolean bl = hand == Hand.MAIN_HAND;
            Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
            boolean bl2 = arm == Arm.RIGHT;
            matrices.push();
            this.applyEquipOffset(matrices, bl2 ? Arm.RIGHT : Arm.LEFT, 0);

            float aimTicksLeft = (float) Math.max(0, stagedItem.getMaxAimingTicks(stack) - entityState.getAimingDuration());
            float f = 1.0f - MathHelper.clamp((float) (aimTicksLeft - tickDelta) / (float) stagedItem.getMaxAimingTicks(stack), 0f, 1f);
            // Vertical shake
            float theta = player.getWorld().getTime() + tickDelta;
            float g = MathHelper.sin(theta * 0.1f) * (2 * (f - 0.5f));
            matrices.translate(0, g * 0.008f, 0);

            // Bring further back and closer to the center
            float towardsPlayer = 0.04f;
            float towardsCenter = 0.1f;
            matrices.translate(f * -towardsCenter, 0, f * towardsPlayer);

            this.renderItem(player, stack, bl2 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND, !bl2, matrices, vertexConsumers, light);
            matrices.pop();
            vertexConsumers.draw();
            ci.cancel();
        }
    }
}
