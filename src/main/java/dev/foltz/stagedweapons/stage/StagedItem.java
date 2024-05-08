package dev.foltz.stagedweapons.stage;

import dev.foltz.stagedweapons.StagedWeapons;
import dev.foltz.stagedweapons.item.CompositeItem;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

import static dev.foltz.stagedweapons.Util.*;

public abstract class StagedItem<T extends IStagedItem<?>> extends CompositeItem implements IStagedItem<T> {
    protected final StagedItemGraph<T> stagesGraph;

    public StagedItem(Settings settings, StagedItemGraph<T> stagesGraph) {
        super(settings);
        this.stagesGraph = stagesGraph;
    }

    @Override
    public StagedItemGraph<T> stagesGraph() {
        return stagesGraph;
    }

    @Override
    public ItemStack getDefaultStack() {
        var stack = super.getDefaultStack();
        setStage(stack, "default");
        resetStageTicks(stack);
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!selected && !world.isClient && getStage(stack).tickWhileUnselected) {
            handleTickInventory(StagedItemView.of(stack, entity));
        }
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return getStage(stack).barColor(stack);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        float p = getStage(stack).barProgress(stack);
        return p < 0 ? 0 : Math.round(13f * p);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return getStage(stack).barProgress(stack) > 0;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
//        tooltip.add(MutableText.of(Text.of("Gun Stage (" + getStageId(stack) + ") = " + stagesGraph.nameFromId(getStageId(stack))).getContent()));
//        if (getMaxAmmoCapacity(stack) > 0) {
//            var ammoList = getAmmoInGun(stack);
//            if (ammoList.isEmpty()) {
//                tooltip.add(MutableText.of(Text.of("Ammo 0/" + getMaxAmmoCapacity(stack)).getContent()).formatted(Formatting.GRAY));
//            }
//            else {
//                tooltip.add(MutableText.of(Text.of("Ammo " + ammoList.size() + "/" + getMaxAmmoCapacity(stack) + ":").getContent()).formatted(Formatting.GRAY));
//                var compactList = getCompactAmmoList(stack);
//                compactList.forEach(a -> tooltip.add(MutableText.of(Text.of("  " + a.getCount() + "x ").getContent()).append(Text.translatable(a.getTranslationKey())).formatted(Formatting.DARK_GRAY)));
//            }
//        }
//
//        if (stack.getDamage() > 0){
//            tooltip.add(MutableText.of(Text.of("Durability: " + (stack.getMaxDamage() - stack.getDamage()) + "/" + stack.getMaxDamage()).getContent()).formatted(Formatting.GRAY, Formatting.BOLD));
//        }
        tooltip.add(MutableText.of(Text.of("Stage: " + getStage(stack).name).getContent()));
    }
}
