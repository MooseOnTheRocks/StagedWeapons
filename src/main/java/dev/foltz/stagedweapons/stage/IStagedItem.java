package dev.foltz.stagedweapons.stage;

import dev.foltz.stagedweapons.Util;
import net.minecraft.item.ItemStack;

public interface IStagedItem<T extends IStagedItem<?>> {
    String KEY_STAGE = "StageName";
    String KEY_STAGE_TICKS = "StageTicks";

    StagedItemGraph<T> stagesGraph();

    default void updateStageGraph(String newStage, StagedItemView<? extends T> view) {
        if (!newStage.isEmpty()) {
//            System.out.println("Updating stage to: " + newStage);
            setStage(view.stack(), newStage);
            resetStageTicks(view.stack());
            handleInit(StagedItemView.of(view.stack(), view.entity()));
        }
    }

    default void handleInit(StagedItemView<? extends T> view) {
        updateStageGraph(stagesGraph().getStage(view.stageName()).handleInit(view), view);
    }

    default void handleTick(StagedItemView<T> view) {
        String newStage = stagesGraph().getStage(view.stageName()).handleTick(view);
        var stageTicks = getStageTicks(view.stack());
        if (!newStage.isEmpty()) {
            updateStageGraph(newStage, view);
        }
        else if (view.maxStageTicks() > 0) {
            if (stageTicks >= view.maxStageTicks()) {
                handleLastTick(view);
            }
            else {
                setStageTicks(view.stack(), stageTicks + 1);
            }
        }
    }

    default void handleLastTick(StagedItemView<T> view) {
        updateStageGraph(stagesGraph().getStage(view.stageName()).handleLastTick(view), view);
    }

    default void handleTickInventory(StagedItemView<T> view) {
        handleTick(view);
    }

    default void handlePressShoot(StagedItemView<T> view) {
        updateStageGraph(stagesGraph().getStage(view.stageName()).handlePressShoot(view), view);
    }

    default void handleReleaseShoot(StagedItemView<T> view) {
        updateStageGraph(stagesGraph().getStage(view.stageName()).handleReleaseShoot(view), view);
    }

    default void handlePressReload(StagedItemView<T> view) {
        updateStageGraph(stagesGraph().getStage(view.stageName()).handlePressReload(view), view);
    }

    default void handleReleaseReload(StagedItemView<T> view) {
        updateStageGraph(stagesGraph().getStage(view.stageName()).handleReleaseReload(view), view);
    }

    default void handleUnselected(StagedItemView<T> view) {
        updateStageGraph(stagesGraph().getStage(view.stageName()).handleUnselected(view), view);
    }

    default void setStage(ItemStack stack, String stageName) {
        Util.setNbtString(stack, KEY_STAGE, stageName);
    }

    default String getStageName(ItemStack stack) {
        return Util.getNbtString(stack, KEY_STAGE).orElse("default");
    }

    default Stage<T> getStage(ItemStack stack) {
        return stagesGraph().getStage(getStageName(stack));
    }

    default void setStageTicks(ItemStack stack, int ticks) {
        Util.setNbtInt(stack, KEY_STAGE_TICKS, ticks);
    }

    default int getStageTicks(ItemStack stack) {
        return Util.getNbtInt(stack, KEY_STAGE_TICKS).orElse(0);
    }

    default int getMaxStageTicks(ItemStack stack) {
        return getStage(stack).duration(stack);
    }

    default void resetStageTicks(ItemStack stack) {
        setStageTicks(stack, 0);
    }
}
