package dev.foltz.stagedweapons.item.gun;

import dev.foltz.stagedweapons.Util;
import dev.foltz.stagedweapons.enchantment.ModEnchantments;
import dev.foltz.stagedweapons.item.ammo.AmmoType;
import dev.foltz.stagedweapons.stage.StagedItem;
import dev.foltz.stagedweapons.stage.StagedItemGraph;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class GunItem<T extends GunItem<?>> extends StagedItem<T> {
    public static final String KEY_AMMO_LIST = "AmmoList";

    public final List<AmmoType> ammoTags;
    public final int ammoCapacity;

    public GunItem(Settings settings, int durability, List<AmmoType> ammoTags, int ammoCapacity, StagedItemGraph<T> stagesGraph) {
        super(settings.maxDamage(durability), stagesGraph);
        this.ammoTags = List.copyOf(ammoTags);
        this.ammoCapacity = ammoCapacity;
    }

    public ItemStack getDefaultAmmoStack() {
        return ammoTags.get(0).getDefaultAmmoStack();
    }

    @Override
    public int getEnchantability() {
        return 1;
    }

    public boolean hasRoomInGun(ItemStack stack) {
        return getAmmoList(stack).size() < ammoCapacity;
    }

    public boolean hasAmmoInGun(ItemStack stack) {
        return !getAmmoList(stack).isEmpty();
    }

    public List<ItemStack> getAmmoList(ItemStack stack) {
        return Util.getNbtItemStackList(stack, KEY_AMMO_LIST).orElse(List.of());
    }

    public List<ItemStack> getCompactAmmoList(ItemStack stack) {
        var ammoList = getAmmoList(stack);
        var compactList = new ArrayList<ItemStack>();
        for (var item : ammoList) {
            if (compactList.isEmpty() || !ItemStack.canCombine(item, compactList.get(compactList.size() - 1))) {
                compactList.add(item);
            }
            else {
                var newItem = compactList.get(compactList.size() - 1);
                newItem = newItem.copyWithCount(newItem.getCount() + 1);
                compactList.set(compactList.size() - 1, newItem);
            }
        }
        return compactList;
    }

    public void setAmmoList(ItemStack stack, List<ItemStack> stacks) {
        Util.setNbtItemStackList(stack, KEY_AMMO_LIST, stacks);
    }

    public void reloadOneBullet(ItemStack stack, ItemStack bullet) {
        if (!hasRoomInGun(stack)) {
            return;
        }
        var newList = new ArrayList<>(getAmmoList(stack));
        newList.add(bullet);
        setAmmoList(stack, newList);
    }

    public void reloadOneBulletFromInventory(ItemStack stack, Entity entity) {
        var bulletStack = findFirstAmmoStackInInventory(entity);
        if (!bulletStack.isEmpty()) {
            reloadOneBullet(stack, bulletStack.split(1));
        }
    }

     public void reloadWholeClip(ItemStack stack, List<ItemStack> bullets) {
        setAmmoList(stack, bullets);
     }

     public void reloadWholeClipFromInventory(ItemStack stack, Entity entity) {
        var bulletStack = findFirstAmmoStackInInventory(entity);
        while (!bulletStack.isEmpty() && hasRoomInGun(stack)) {
            reloadOneBullet(stack, bulletStack.split(1));
            bulletStack = findFirstAmmoStackInInventory(entity);
        }
     }

     public ItemStack popNextBullet(ItemStack stack) {
        var ammoList = new ArrayList<>(getAmmoList(stack));
        if (ammoList.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack bullet = ammoList.remove(0);
        setAmmoList(stack, ammoList);
        return bullet;
     }

     public boolean hasAmmoInInventory(Entity entity) {
        return !findFirstAmmoStackInInventory(entity).isEmpty();
     }

     public ItemStack findFirstAmmoStackInInventory(Entity entity) {
        for (var tag : ammoTags) {
            if (entity instanceof PlayerEntity player) {
                ItemStack stack = player.getOffHandStack();
                if (stack.isIn(tag.tagKey)) {
                    return stack;
                }
                else {
                    for (int slot = 0; slot < player.getInventory().main.size(); slot++) {
                        stack = player.getInventory().getStack(slot);
                        if (stack.isIn(tag.tagKey)) {
                            return stack;
                        }
                    }
                }
            }
        }
        return ItemStack.EMPTY;
     }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        var ammoList = getAmmoList(stack);
        tooltip.add(Text.of("Ammo List [" + ammoList.size() + "/" + ammoCapacity + "]:"));
        var compactAmmoList = getCompactAmmoList(stack);
        for (var item : compactAmmoList) {
            tooltip.add(MutableText.of(Text.of("  - ").getContent()).append(item.getName()));
        }
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        defaultedList.addAll(getCompactAmmoList(stack));
        return Optional.of(new BundleTooltipData(defaultedList, (int) MathHelper.map(getAmmoList(stack).size(), 0, ammoCapacity, 0, 64)));
    }

    public int getMaxAimingTicks(ItemStack stack) {
        return GunItemStageUtil.durationWithQuickAim(Util.ticksFromSeconds(1.5f), 0.25f).apply(stack);
    }

    public float getAimingZoomModifier() {
        return 0.15f;
    }

    public boolean isBroken(ItemStack stack) {
        return stack.getDamage() >= stack.getMaxDamage();
    }

    public void playSoundReload(Entity entity) {
        entity.getWorld().playSound(null, entity.getX(), entity.getEyeY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.75f, 0.5f);
    }

    public void playSoundCancel(Entity entity) {
        entity.getWorld().playSound(null, entity.getX(), entity.getEyeY(), entity.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.PLAYERS, 0.3f, 1.0f);
    }

    public void playSoundShoot(Entity entity) {
        entity.getWorld().playSound(null, entity.getX(), entity.getEyeY(), entity.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.5f, 2.5f);
    }

    public void playSoundReadyTick(Entity entity) {
        entity.getWorld().playSound(null, entity.getX(), entity.getEyeY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.75f, 2.0f);
    }

    public void playSoundReadyDone(Entity entity) {
        entity.getWorld().playSound(null, entity.getX(), entity.getEyeY(), entity.getZ(), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 0.75f, 0.5f);
    }
}
