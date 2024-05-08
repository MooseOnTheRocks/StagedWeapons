package dev.foltz.stagedweapons.item.ammo;

import dev.foltz.stagedweapons.entity.BulletEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface IAmmoItem {
    EntityType<?> entityType();

    default float bulletSpeed(ItemStack ammoStack) {
        return 1.0f;
    }

    default float bulletDivergence(ItemStack ammoStack) {
        return 1.0f;
    }

    default int bulletCount(ItemStack ammoStack) {
        return 1;
    }

    default List<BulletEntity> createBulletEntities(Entity entity, ItemStack stack, ItemStack ammo) {
        var bullets = new ArrayList<BulletEntity>();
        int amount = bulletCount(stack);
        for (int i = 0; i < amount; i++) {
            var bullet = (BulletEntity) entityType().create(entity.getWorld());
            bullet.setPos(entity.getX(), entity.getEyeY() - 0.1f, entity.getZ());
            bullet.setVelocity(entity, entity.getPitch(), entity.getYaw(), 0, bulletSpeed(stack), bulletDivergence(stack));
            bullets.add(bullet);
        }
        return List.copyOf(bullets);
    }
}
