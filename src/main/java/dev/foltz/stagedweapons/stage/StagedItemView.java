package dev.foltz.stagedweapons.stage;

import dev.foltz.stagedweapons.networking.ServerEntityState;
import dev.foltz.stagedweapons.networking.ServerState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public record StagedItemView<T extends IStagedItem<?>>(
        String stageName,
        int stageTicks,
        int maxStageTicks,
        T item,
        ItemStack stack,
        Entity entity,
        World world
) {
    public static <T extends IStagedItem<?>> StagedItemView<T> of(ItemStack stack, Entity entity) {
        if (stack.getItem() instanceof IStagedItem<?> stagedItem) {
            var stageId = stagedItem.getStageName(stack);
            var stageTicks = stagedItem.getStageTicks(stack);
            var maxStageTicks = stagedItem.getMaxStageTicks(stack);
            return new StagedItemView<>(stageId, stageTicks, maxStageTicks, (T) stagedItem, stack, entity, entity.getWorld());
        }
        else {
            throw new IllegalStateException("Unable to create StagedItemView of item: " + stack.getItem());
        }
    }

    public StagedItemView<? extends T> withStage(String newStage) {
        return new StagedItemView<>(newStage, item.getStageTicks(stack), item.getMaxStageTicks(stack), item, stack, entity, world);
    }

    public ServerEntityState getEntityState() {
        return ServerState.getEntityState(entity);
    }
}
