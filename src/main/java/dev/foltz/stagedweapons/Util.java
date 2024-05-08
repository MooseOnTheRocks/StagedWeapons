package dev.foltz.stagedweapons;

import dev.foltz.stagedweapons.stage.IStagedItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Optional;

public class Util {
    public static final int TICKS_PER_SECOND = 20;
    public static final int GREEN = MathHelper.hsvToRgb(0.33f, 1, 1);
    public static final int YELLOW = MathHelper.hsvToRgb(1.0f, 1, 1);
    public static final int ORANGE = MathHelper.hsvToRgb(0.1f, 1, 1);
    public static final int RED = MathHelper.hsvToRgb(0.0f, 1, 1);

    public static int ticksFromSeconds(float seconds) {
        return (int) Math.ceil(TICKS_PER_SECOND * seconds);
    }

    public static void setNbtString(ItemStack stack, String key, String value) {
        stack.getOrCreateNbt().putString(key, value);
    }

    public static Optional<String> getNbtString(ItemStack stack, String key) {
        var nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(key, NbtElement.STRING_TYPE)) {
            return Optional.empty();
        }
        return Optional.of(nbt.getString(key));
    }

    public static void setNbtInt(ItemStack stack, String key, int value) {
        stack.getOrCreateNbt().putInt(key, value);
    }

    public static Optional<Integer> getNbtInt(ItemStack stack, String key) {
        var nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(key, NbtElement.INT_TYPE)) {
            return Optional.empty();
        }
        return Optional.of(nbt.getInt(key));
    }

    public static void setNbtItemStackList(ItemStack stack, String key, List<ItemStack> items) {
        var nbt = stack.getOrCreateNbt();
        NbtList list = new NbtList();
        for (var item : items) {
            var compound = new NbtCompound();
            item.writeNbt(compound);
            list.add(compound);
        }
        nbt.put(key, list);
    }

    public static Optional<List<ItemStack>> getNbtItemStackList(ItemStack stack, String key) {
        var nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(key, NbtElement.LIST_TYPE)) {
            return Optional.empty();
        }
        return Optional.of(nbt.getList(key, NbtElement.COMPOUND_TYPE)
            .stream()
            .map(e -> (NbtCompound) e)
            .map(ItemStack::fromNbt)
            .toList());
    }

    public static float linearTicks(ItemStack stack) {
        if (!(stack.getItem() instanceof IStagedItem<?> stagedItem)) {
            return 0f;
        }

        int maxTicks = stagedItem.getMaxStageTicks(stack);
        if (maxTicks <= 0) {
            return 0f;
        }
        int ticks = stagedItem.getStageTicks(stack);
        return (float) ticks / (float) maxTicks;
    }

    public static float linearInverseTicks(ItemStack stack) {
        return 1.0f - linearTicks(stack);
    }
}
