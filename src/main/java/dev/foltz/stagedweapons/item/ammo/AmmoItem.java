package dev.foltz.stagedweapons.item.ammo;

import dev.foltz.stagedweapons.item.CompositeItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public abstract class AmmoItem extends CompositeItem implements IAmmoItem {
    public AmmoItem(Settings settings) {
        super(settings);
    }

    public static Builder builder() {
        return Builder.create();
    }

    public static class Builder {
        private EntityType<?> entityType;
        private Function<ItemStack, Float> bulletSpeed = stack -> 1.0f;
        private Function<ItemStack, Float> bulletDivergence = stack -> 1.0f;
        private Function<ItemStack, Integer> bulletCount = stack -> 1;

        protected Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder entityType(Identifier entityTypeIdentifier) {
            return entityType(Registries.ENTITY_TYPE.get(entityTypeIdentifier));
        }

        public Builder entityType(EntityType<?> entityType) {
            this.entityType = entityType;
            return this;
        }

        public Builder bulletSpeed(float bulletSpeed) {
            return bulletSpeed(stack -> bulletSpeed);
        }

        public Builder bulletSpeed(Function<ItemStack, Float> bulletSpeed) {
            this.bulletSpeed = bulletSpeed;
            return this;
        }

        public Builder bulletDivergence(float bulletDivergence) {
            return bulletDivergence(stack -> bulletDivergence);
        }

        public Builder bulletDivergence(Function<ItemStack, Float> bulletDivergence) {
            this.bulletDivergence = bulletDivergence;
            return this;
        }

        public Builder bulletCount(int bulletCount) {
            return bulletCount(stack -> bulletCount);
        }

        public Builder bulletCount(Function<ItemStack, Integer> bulletCount) {
            this.bulletCount = bulletCount;
            return this;
        }

        public AmmoItem build() {
            return new AmmoItem(new FabricItemSettings()) {
                @Override
                public EntityType<?> entityType() {
                    return entityType;
                }

                @Override
                public float bulletSpeed(ItemStack ammoStack) {
                    return bulletSpeed == null ? super.bulletCount(ammoStack) : bulletSpeed.apply(ammoStack);
                }

                @Override
                public float bulletDivergence(ItemStack ammoStack) {
                    return bulletDivergence == null ? super.bulletDivergence(ammoStack) : bulletDivergence.apply(ammoStack);
                }

                @Override
                public int bulletCount(ItemStack ammoStack) {
                    return bulletCount == null ? super.bulletCount(ammoStack) : bulletCount.apply(ammoStack);
                }
            };
        }
    }
}
