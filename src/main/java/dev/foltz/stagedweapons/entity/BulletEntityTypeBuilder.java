package dev.foltz.stagedweapons.entity;

import dev.foltz.stagedweapons.StagedWeapons;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BulletEntityTypeBuilder {
    private float speed;
    private float damage;
    private float gravity;
    private float drag;
    private EntityDimensions dimensions;
    private boolean isOnFire;
    private boolean isBouncy;
    private int lifetime;
    private Identifier texturePath;

    private BulletEntityTypeBuilder() {
        this.speed = 1.0f;
        this.damage = 1.0f;
        this.gravity = 0.01f;
        this.drag = 0.0000001f;
        this.isOnFire = false;
        this.isBouncy = false;
        this.lifetime = 200;
        this.dimensions = EntityDimensions.fixed(1.0f, 1.0f);
        this.texturePath = Identifier.of(StagedWeapons.MODID, "textures/entity/bullet/bronze.png");
    }

    public static BulletEntityTypeBuilder create() {
        return new BulletEntityTypeBuilder();
    }

    public BulletEntityTypeBuilder speed(float speed) {
        this.speed = speed;
        return this;
    }

    public BulletEntityTypeBuilder damage(float damage) {
        this.damage = damage;
        return this;
    }

    public BulletEntityTypeBuilder gravity(float gravity) {
        this.gravity = gravity;
        return this;
    }

    public BulletEntityTypeBuilder drag(float drag) {
        this.drag = drag;
        return this;
    }

    public BulletEntityTypeBuilder isOnFire(boolean onFire) {
        this.isOnFire = onFire;
        return this;
    }

    public BulletEntityTypeBuilder isBouncy(boolean bouncy) {
        this.isBouncy = bouncy;
        return this;
    }

    public BulletEntityTypeBuilder dimensions(EntityDimensions dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public BulletEntityTypeBuilder lifetime(int lifetime) {
        this.lifetime = lifetime;
        return this;
    }

    public BulletEntityTypeBuilder texturePath(Identifier texturePath) {
        this.texturePath = texturePath;
        return this;
    }

    public EntityType<BulletEntity> build() {
        return FabricEntityTypeBuilder.create().entityFactory(buildEntityFactory()).dimensions(dimensions).build();
    }

    public EntityType.EntityFactory<BulletEntity> buildEntityFactory() {
        return (type, world) -> new BulletEntity(type, world, damage, speed, gravity, drag, isOnFire, isBouncy, lifetime, texturePath);
    }
}
