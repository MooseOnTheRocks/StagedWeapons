package dev.foltz.stagedweapons.networking.client;

import net.minecraft.entity.Entity;

public class ClientEntityState {
    private final Entity entity;
    private boolean isAiming;
    private long startedAimingTime;

    public ClientEntityState(Entity entity) {
        this.entity = entity;
        this.isAiming = false;
    }

    public void setAiming(boolean aiming) {
        System.out.println("Set aiming: " + aiming);
        this.startedAimingTime = entity.getWorld().getTime();
        this.isAiming = aiming;
    }

    public boolean isAiming() {
        return isAiming;
    }

    public long getAimingDuration() {
        return entity.getWorld().getTime() - startedAimingTime;
    }
}
