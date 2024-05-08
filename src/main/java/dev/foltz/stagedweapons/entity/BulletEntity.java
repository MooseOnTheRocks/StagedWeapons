package dev.foltz.stagedweapons.entity;

import dev.foltz.stagedweapons.StagedWeapons;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BulletEntity extends ProjectileEntity {
    public final float bulletDamage;
    public final float bulletSpeed;
    public final float bulletGravity;
    public final float bulletDrag;
    public final boolean bulletIsOnFire;
    public final boolean isBouncy;
    private int lifetime;
    public final Identifier texturePath;

    public BulletEntity(EntityType<? extends ProjectileEntity> entityType, World world, float damage, float speed, float gravity, float drag, boolean isOnFire, boolean isBouncy, int lifetime, Identifier texturePath) {
        super(entityType, world);
        this.bulletDamage = damage;
        this.bulletSpeed = speed;
        this.bulletGravity = gravity;
        this.bulletDrag = drag;
        this.bulletIsOnFire = isOnFire;
        this.isBouncy = isBouncy;
        this.lifetime = lifetime;
        this.texturePath = texturePath;
    }

    @Override
    public boolean isOnFire() {
        return this.bulletIsOnFire;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (!entity.canBeHitByProjectile()) {
            return false;
        }

        Entity owner = getOwner();
        if (owner == null || !owner.isConnectedThroughVehicle(entity) || !owner.equals(entity)) {
            return true;
        }
        else {
            return super.canHit(entity);
        }
    }

    @Override
    public void tick() {
        if (this.lifetime <= 0) {
            this.discard();
            return;
        }

        this.lifetime -= 1;

        super.tick();

        if (!getWorld().isClient) {
            if (isOnFire()) {
                var random = getWorld().getRandom();
                if (random.nextFloat() <= 0.75f) {
                    var x = getPos().x;
                    var y = getPos().y;
                    var z = getPos().z;
                    var s = 0.1f;
                    var dx = s * 0.5f * (random.nextFloat() - 0.5f);
                    var dy = s * 0.5f * (random.nextFloat() - 0.5f);
                    var dz = s * 0.5f * (random.nextFloat() - 0.5f);
                    float speed = random.nextFloat() * 0.2f;
                    ((ServerWorld) getWorld()).spawnParticles(ParticleTypes.SMALL_FLAME, x, y, z, 1, dx, dy, dz, speed);
                }
            }
        }

        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        boolean bl = false;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
            BlockState blockState = this.getWorld().getBlockState(blockPos);
            if (blockState.isOf(Blocks.NETHER_PORTAL)) {
                this.setInNetherPortal(blockPos);
                bl = true;
            }
            else if (blockState.isOf(Blocks.END_GATEWAY)) {
                BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPos);
                if (blockEntity instanceof EndGatewayBlockEntity && EndGatewayBlockEntity.canTeleport(this)) {
                    EndGatewayBlockEntity.tryTeleportingEntity(this.getWorld(), blockPos, blockState, this, (EndGatewayBlockEntity)blockEntity);
                }

                bl = true;
            }
        }

        if (hitResult.getType() != HitResult.Type.MISS && !bl) {
            this.onCollision(hitResult);
        }


        float drag = 1f - bulletDrag;
        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;
        this.updateRotation();
        if (this.isTouchingWater()) {
            for (int i = 0; i < 4; ++i) {
                float g = 0.25f;
                this.getWorld().addParticle(ParticleTypes.BUBBLE, d - vec3d.x * g, e - vec3d.y * g, f - vec3d.z * g, vec3d.x, vec3d.y, vec3d.z);
            }
            drag *= drag;
        }
        this.setVelocity(vec3d.multiply(drag));
        if (!this.hasNoGravity()) {
            Vec3d vel = this.getVelocity();
            float gravStart = 0.75f;
            float grav = vel.length() <= gravStart ? (float) MathHelper.map(vel.length(), gravStart, 0.0f, 0.0f, 1f) : 0f;
            grav *= 0.05f;
            this.setVelocity(vel.x, vel.y - (bulletGravity + grav), vel.z);
        }
        this.setPosition(d, e, f);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!this.getWorld().isClient) {
            var entity = entityHitResult.getEntity();
            if (this.isOnFire()) {
                entity.setFireTicks((int) (20 * 2.5));
            }
            entity.damage(this.getDamageSources().create(ModEntities.BULLET_DAMAGE_TYPE, this, this.getOwner()), bulletDamage);
            this.discard();
        }
    }

    private void setPositionOnBlockHit(BlockHitResult blockHitResult) {
//        BlockPos blockPos = blockHitResult.getBlockPos();
        Vec3d hitPos = blockHitResult.getPos();
        Direction side = blockHitResult.getSide();
        Direction direction = side.getOpposite();
        Vec3d pos = hitPos;
        pos = switch (side) {
            case UP, DOWN -> pos.offset(direction, getBoundingBox().getLengthY() / 2f);
            case NORTH, SOUTH -> pos.offset(direction, getBoundingBox().getLengthX() / 2f);
            case EAST, WEST -> pos.offset(direction, getBoundingBox().getLengthZ() / 2f);
        };
        setPosition(pos);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.getWorld().isClient) {
            if (this.isOnFire()) {
                var firePos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
                if (AbstractFireBlock.canPlaceAt(getWorld(), firePos, blockHitResult.getSide().getOpposite())) {
                    getWorld().setBlockState(firePos, AbstractFireBlock.getState(getWorld(), firePos), Block.NOTIFY_ALL_AND_REDRAW);
                }
            }

            if (this.isBouncy && getVelocity().length() >= 0.2f) {
                var nv = getVelocity();
                nv = switch (blockHitResult.getSide()) {
                    case UP, DOWN -> nv.multiply(1, -1, 1);
                    case NORTH, SOUTH -> nv.multiply(-1, 1, 1);
                    case EAST, WEST -> nv.multiply(1, 1, -1);
                };
                setPositionOnBlockHit(blockHitResult);
                this.setVelocity(nv.multiply(0.85));

                var random = getWorld().getRandom();
                int count = random.nextBetween(2, 5);
                for (int i = 0; i < count; i++) {
                    var direction = nv.normalize();
                    var hitPos = blockHitResult.getPos();
                    var x = hitPos.x;
                    var y = hitPos.y;
                    var z = hitPos.z;
                    var dx = direction.x;
                    var dy = direction.y;
                    var dz = direction.z;
                    float speed = 0.5f;
                    ((ServerWorld) getWorld()).spawnParticles(ParticleTypes.ITEM_SLIME, x, y, z, 1, dx, dy, dz, speed);
                }
            }
            else {
                this.discard();
            }
        }
    }
}
