package dev.foltz.stagedweapons.networking;

import dev.foltz.stagedweapons.stage.IStagedItem;
import dev.foltz.stagedweapons.stage.StagedItemView;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class ServerEntityState {
    private final Entity entity;
    private boolean isPressingShoot = false;
    private boolean isPressingReload = false;
    private boolean isPressingAim = false;
    private ItemStack lastHeldItemStack = ItemStack.EMPTY;

    public ServerEntityState(Entity entity) {
        this.entity = entity;
    }

    public static void onPressShoot(Entity entity) {
        ServerState.getEntityState(entity).setPressingShoot(true);
        var stack = entity.getHandItems().iterator().next();
        var item = stack.getItem();
        if (item instanceof IStagedItem<?> stagedItem) {
            stagedItem.handlePressShoot(StagedItemView.of(stack, entity));
        }
    }

    public static void onReleaseShoot(Entity entity) {
        ServerState.getEntityState(entity).setPressingShoot(false);
        var stack = entity.getHandItems().iterator().next();
        var item = stack.getItem();
        if (item instanceof IStagedItem<?> stagedItem) {
            stagedItem.handleReleaseShoot(StagedItemView.of(stack, entity));
        }
    }

    public static void onPressReload(Entity entity) {
        ServerState.getEntityState(entity).setPressingReload(true);
        var stack = entity.getHandItems().iterator().next();
        var item = stack.getItem();
        if (item instanceof IStagedItem<?> stagedItem) {
            stagedItem.handlePressReload(StagedItemView.of(stack, entity));
        }
    }

    public static void onReleaseReload(Entity entity) {
        ServerState.getEntityState(entity).setPressingReload(false);
        var stack = entity.getHandItems().iterator().next();
        var item = stack.getItem();
        if (item instanceof IStagedItem<?> stagedItem) {
            stagedItem.handleReleaseReload(StagedItemView.of(stack, entity));
        }
    }

    public static void onPressAim(Entity entity) {
        ServerState.getEntityState(entity).setPressingAim(true);
    }

    public static void onReleaseAim(Entity entity) {
        ServerState.getEntityState(entity).setPressingAim(false);
    }

    public static void onHeldItemChange(Entity entity) {
        var playerState = ServerState.getEntityState(entity);
        playerState.setPressingShoot(false);
        playerState.setPressingReload(false);
        playerState.setPressingAim(false);
        var stack = playerState.getLastHeldItemStack();
        playerState.setLastHeldItemStack(entity.getHandItems().iterator().next());
        var item = stack.getItem();
        if (item instanceof IStagedItem<?> stagedItem) {
            stagedItem.handleUnselected(StagedItemView.of(stack, entity));
        }
    }

    public static void onTick(Entity entity) {
        var stack = entity.getHandItems().iterator().next();
        var item = stack.getItem();
        if (item instanceof IStagedItem<?> stagedItem) {
            stagedItem.handleTick(StagedItemView.of(stack, entity));
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public void setPressingShoot(boolean pressed) {
        this.isPressingShoot = pressed;
    }

    public boolean isPressingShoot() {
        return isPressingShoot;
    }

    public void setPressingReload(boolean pressed) {
        this.isPressingReload = pressed;
    }

    public boolean isPressingReload() {
        return isPressingReload;
    }

    public void setPressingAim(boolean pressed) {
        this.isPressingAim = pressed;
    }

    public boolean isPressingAim() {
        return isPressingAim;
    }

    public void setLastHeldItemStack(ItemStack stack) {
        this.lastHeldItemStack = stack;
    }

    public ItemStack getLastHeldItemStack() {
        return lastHeldItemStack;
    }
}
