package dev.foltz.stagedweapons.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.foltz.stagedweapons.item.gun.GunItem;
import dev.foltz.stagedweapons.networking.client.ClientState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AimingZoomMixin {
    @Inject(
        method="getFovMultiplier",
        at=@At("RETURN"),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true)
    public void doAiming(CallbackInfoReturnable<Float> cir, float f) {
        var self = ((AbstractClientPlayerEntity) (Object) this);
        var entityState = ClientState.getEntityState(self);
        ItemStack stack = self.getMainHandStack();
        if (entityState.isAiming() && MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && stack.getItem() instanceof GunItem<?> stagedItem) {
            int aimTimeLeft = (int) Math.max(0, stagedItem.getMaxAimingTicks(stack) - entityState.getAimingDuration() + MinecraftClient.getInstance().getTickDelta());
            float g = 1.0f - (float) aimTimeLeft / (float) stagedItem.getMaxAimingTicks(stack);
            g = g > 1.0f ? 1.0f : (g *= g);
            f *= 1.0f - g * stagedItem.getAimingZoomModifier();
            var ret = MathHelper.lerp((float) MinecraftClient.getInstance().options.getFovEffectScale().getValue().floatValue(), (float)1.0f, (float)f);
            cir.setReturnValue(ret);
        }
    }
}
