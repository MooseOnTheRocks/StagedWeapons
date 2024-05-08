package dev.foltz.stagedweapons.mixin;

import dev.foltz.stagedweapons.entity.ai.goal.GunAttackGoal;
import dev.foltz.stagedweapons.item.gun.GunItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntity.class)
public abstract class SkeletonGunAttackGoalMixin extends HostileEntity {
    @Shadow @Final private BowAttackGoal<AbstractSkeletonEntity> bowAttackGoal;
    @Shadow @Final private MeleeAttackGoal meleeAttackGoal;
    private final GunAttackGoal gunAttackGoal = new GunAttackGoal((AbstractSkeletonEntity) ((Object) this), 20, 1.0f, 15f);

    protected SkeletonGunAttackGoalMixin(EntityType<? extends AbstractSkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="updateAttackType", at=@At("TAIL"), cancellable = true)
    public void updateAttackTypeForGun(CallbackInfo ci) {
        if (getWorld() != null && !getWorld().isClient) {
            ItemStack itemStack = getStackInHand(getMainHandStack().getItem() instanceof GunItem<?> ? Hand.MAIN_HAND : Hand.OFF_HAND);
            goalSelector.remove(gunAttackGoal);
            if (itemStack.getItem() instanceof GunItem<?> gunItem) {
                goalSelector.remove(bowAttackGoal);
                goalSelector.remove(meleeAttackGoal);
                int i = 20;
                if (getWorld().getDifficulty() != Difficulty.HARD) {
                    i = 40;
                }
                goalSelector.add(4, gunAttackGoal);
                ci.cancel();
            }
        }
    }

    @Override
    protected boolean prefersNewEquipment(ItemStack newStack, ItemStack oldStack) {
        if (newStack.getItem() instanceof GunItem<?>) {
            return this.prefersNewDamageableItem(newStack, oldStack);
        }
        return super.prefersNewEquipment(newStack, oldStack);
    }
}
