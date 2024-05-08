package dev.foltz.stagedweapons.entity.ai.goal;

import dev.foltz.stagedweapons.entity.BulletEntity;
import dev.foltz.stagedweapons.item.ammo.IAmmoItem;
import dev.foltz.stagedweapons.item.gun.GunItem;
import dev.foltz.stagedweapons.item.gun.GunItemStageUtil;
import dev.foltz.stagedweapons.stage.StagedItemView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.EnumSet;

public class GunAttackGoal extends Goal {
    private final MobEntity actor;
    private int attackInterval;
    private float speed;

    private final float squaredRange;
    private boolean movingToLeft;
    private boolean backward;
    private int cooldown = -1;
    private int targetSeeingTicker = 0;
    private int combatTicks = -1;

    public GunAttackGoal(MobEntity actor, int attackInterval, float speed, float range) {
        this.actor = actor;
        this.attackInterval = attackInterval;
        this.speed = speed;
        this.squaredRange = range * range;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    protected ItemStack getHeldGun() {
        var stack = actor.getMainHandStack();
        if (stack.getItem() instanceof GunItem<?>) {
            return stack;
        }
        stack = actor.getOffHandStack();
        if (stack.getItem() instanceof GunItem<?>) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canStart() {
        return actor.getTarget() != null && !getHeldGun().isEmpty();
    }

    @Override
    public void start() {
        super.start();
        actor.setAttacking(true);
    }

    @Override
    public boolean shouldContinue() {
        return canStart() || !actor.getNavigation().isIdle() && !getHeldGun().isEmpty();
    }

    @Override
    public void stop() {
        super.stop();
        actor.setAttacking(false);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = actor.getTarget();
        if (livingEntity == null) {
            return;
        }
        double squaredDistance = actor.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        boolean hasLineOfSight = actor.getVisibilityCache().canSee(livingEntity);
        boolean bl2 = this.targetSeeingTicker > 0;
        if (hasLineOfSight != bl2) {
            this.targetSeeingTicker = 0;
        }
        this.targetSeeingTicker = hasLineOfSight ? ++this.targetSeeingTicker : --this.targetSeeingTicker;
        if (squaredDistance > (double) this.squaredRange || this.targetSeeingTicker < 20) {
            actor.getNavigation().startMovingTo(livingEntity, this.speed);
            this.combatTicks = -1;
        }
        else {
            actor.getNavigation().stop();
            ++this.combatTicks;
        }

        if (this.combatTicks >= 20) {
            if ((double) actor.getRandom().nextFloat() < 0.3) {
                this.movingToLeft = !this.movingToLeft;
            }
            if ((double) this.actor.getRandom().nextFloat() < 0.3) {
                this.backward = !this.backward;
            }
            this.combatTicks = 0;
        }

        if (this.combatTicks > -1) {
            if (squaredDistance > (double)(this.squaredRange * 0.75f)) {
                this.backward = false;
            }
            else if (squaredDistance < (double)(this.squaredRange * 0.25f)) {
                this.backward = true;
            }
            actor.getMoveControl().strafeTo(this.backward ? -0.5f : 0.5f, this.movingToLeft ? 0.5f : -0.5f);
            Entity entity = actor.getControllingVehicle();
            if (entity instanceof MobEntity mobEntity) {
                mobEntity.lookAtEntity(livingEntity, 30.0f, 30.0f);
            }
            actor.lookAtEntity(livingEntity, 30.0f, 30.0f);
        }
        else {
            actor.getLookControl().lookAt(livingEntity, 30.0f, 30.0f);
        }

        if (actor.isUsingItem()) {
            int i;
            if (!hasLineOfSight && this.targetSeeingTicker < -60) {
                actor.clearActiveItem();
            }
            else if (hasLineOfSight && (i = actor.getItemUseTime()) >= 20) {
                actor.clearActiveItem();
//                ((RangedAttackMob) this.actor).shootAt(livingEntity, BowItem.getPullProgress(i));
                var stack = getHeldGun();
                if (stack.getItem() instanceof GunItem<?> gunItem) {
                    ItemStack bulletStack = gunItem.getDefaultAmmoStack();
                    if (bulletStack.getItem() instanceof IAmmoItem ammoItem) {
                        gunItem.playSoundShoot(actor);
                        ammoItem.createBulletEntities(actor, stack, bulletStack).forEach(entity -> {
//                            double d = livingEntity.getX() - entity.getX();
//                            double e = livingEntity.getBodyY(0.3333333333333333) - entity.getY();
//                            double f = livingEntity.getZ() - entity.getZ();
//                            double g = Math.sqrt(d * d + f * f);
                            entity.setVelocity(actor, entity.getPitch(), entity.getYaw(), 0, ammoItem.bulletSpeed(stack), ammoItem.bulletDivergence(stack));
                            actor.getWorld().spawnEntity(entity);
                        });
                    }
                }
                this.cooldown = this.attackInterval;
            }
        }
        else if (--this.cooldown <= 0 && this.targetSeeingTicker >= -60) {
            actor.setCurrentHand(actor.getMainHandStack().getItem() instanceof GunItem<?> ? Hand.MAIN_HAND : Hand.OFF_HAND);
        }
    }
}
