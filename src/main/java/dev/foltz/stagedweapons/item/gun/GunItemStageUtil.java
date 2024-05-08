package dev.foltz.stagedweapons.item.gun;

import dev.foltz.stagedweapons.Util;
import dev.foltz.stagedweapons.enchantment.ModEnchantments;
import dev.foltz.stagedweapons.item.ammo.IAmmoItem;
import dev.foltz.stagedweapons.stage.IStagedItem;
import dev.foltz.stagedweapons.stage.StagedItemEventHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.function.Function;

public abstract class GunItemStageUtil {

    public static Function<ItemStack, Integer> durationWithQuickAction(int baseDuration, float levelMultiplier) {
        return stack -> durationWithEnchantmentLevel(baseDuration, levelMultiplier, EnchantmentHelper.getLevel(Registries.ENCHANTMENT.get(ModEnchantments.ENCHANTMENT_QUICK_ACTION), stack));
    }

    public static Function<ItemStack, Integer> durationWithQuickFire(int baseDuration, float levelMultiplier) {
        return stack -> durationWithEnchantmentLevel(baseDuration, levelMultiplier, EnchantmentHelper.getLevel(Registries.ENCHANTMENT.get(ModEnchantments.ENCHANTMENT_QUICK_FIRE), stack));
    }

    public static Function<ItemStack, Integer> durationWithQuickReload(int baseDurationTicks, float levelMultiplier) {
        return stack -> durationWithEnchantmentLevel(baseDurationTicks, levelMultiplier, EnchantmentHelper.getLevel(Registries.ENCHANTMENT.get(ModEnchantments.ENCHANTMENT_QUICK_RELOAD), stack));
    }

    public static Function<ItemStack, Integer> durationWithQuickAim(int baseDurationTicks, float levelMultiplier) {
        return stack -> durationWithEnchantmentLevel(baseDurationTicks, levelMultiplier, EnchantmentHelper.getLevel(Registries.ENCHANTMENT.get(ModEnchantments.ENCHANTMENT_QUICK_AIM), stack));
    }

    public static int durationWithEnchantmentLevel(int baseDurationTicks, float levelMultiplier, int level) {
        return Math.max(1, Math.round(baseDurationTicks - level * levelMultiplier));
    }

    public static Function<ItemStack, Integer> barColorGreen = barColor(Util.GREEN);
    public static Function<ItemStack, Integer> barColorYellow = barColor(Util.YELLOW);
    public static Function<ItemStack, Integer> barColorOrange = barColor(Util.ORANGE);
    public static Function<ItemStack, Integer> barColorRed = barColor(Util.RED);
    public static Function<ItemStack, Integer> barColor(int color) {
        return stack -> color;
    }

    public static Function<ItemStack, Float> barProgressFull = barProgressConstant(1.0f);

    public static Function<ItemStack, Float> barProgressConstant(float percent) {
        return stack -> percent;
    }

    public static Function<ItemStack, Float> barProgressLinearStageTicks = GunItemStageUtil::barProgressLinearStageTicks;
    public static float barProgressLinearStageTicks(ItemStack stack) {
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

    public static Function<ItemStack, Float> barProgressLinearInverseStageTicks = GunItemStageUtil::barProgressLinearInverseStageTicks;
    public static float barProgressLinearInverseStageTicks(ItemStack stack) {
        return 1.0f - barProgressLinearStageTicks(stack);
    }

    public static Function<ItemStack, Float> barProgressLinearAmmoAmount = GunItemStageUtil::barProgressLinearAmmoAmount;
    public static float barProgressLinearAmmoAmount(ItemStack stack) {
        if (!(stack.getItem() instanceof GunItem<?> gunItem)) {
            return 0f;
        }

        int maxAmmo = gunItem.ammoCapacity;
        if (maxAmmo <= 0) {
            return 0f;
        }

        return (float) gunItem.getAmmoList(stack).size() / (float) maxAmmo;
    }

    public static <T extends GunItem<?>> StagedItemEventHandler<T> tryShoot(String stageShoot) {
        return view -> {
            boolean canShoot = view.item().hasAmmoInGun(view.stack());
            return canShoot ? stageShoot : "";
        };
    }

    public static <T extends GunItem<?>> StagedItemEventHandler<T> tryReloadIfEmpty(String stageReload) {
        return view -> {
            if (!view.item().hasAmmoInGun(view.stack())) {
                return tryReload(stageReload).handleEvent(view);
            }
            return "";
        };
    }

    public static <T extends GunItem<?>> StagedItemEventHandler<T> tryReload(String stageReload) {
        return view -> {
            boolean canReload = view.item().hasRoomInGun(view.stack()) && view.item().hasAmmoInInventory(view.entity());
            return canReload ? stageReload : "";
        };
    }

    public static <T extends GunItem<?>> StagedItemEventHandler<T> tryShootOrReload(String stageShoot, String stageReload) {
        return view -> {
            boolean isPressingShoot = view.getEntityState().isPressingShoot();
            boolean isPressingReload = view.getEntityState().isPressingReload();
            boolean canShoot = view.item().hasAmmoInGun(view.stack());
            boolean canReload = view.item().hasRoomInGun(view.stack()) && view.item().hasAmmoInInventory(view.entity());

            if (isPressingShoot) {
                if (canShoot) {
                    return stageShoot;
                }
                else if (canReload) {
                    return stageReload;
                }
            }
            else if (isPressingReload && canReload) {
                return stageReload;
            }
            return "";
        };
    }

    public static StagedItemEventHandler<GunItem<?>> tryReloadWholeClip = tryReloadWholeClip();
    public static <T extends GunItem<?>> StagedItemEventHandler<T> tryReloadWholeClip() {
        return view -> {
            view.item().playSoundReload(view.entity());
            view.item().reloadWholeClipFromInventory(view.stack(), view.entity());
            return "";
        };
    }

    public static <T extends GunItem<?>> StagedItemEventHandler<T> tryReloadOneBullet(String stageCancel) {
        return view -> {
            int usageTicks = view.item().getStageTicks(view.stack());
            int maxStageTicks = view.item().getMaxStageTicks(view.stack());
            int loadingAmmoStage = (int) (view.item().ammoCapacity * (usageTicks / (float) maxStageTicks));

            ItemStack bulletStack = view.item().findFirstAmmoStackInInventory(view.entity());

            // No available bullets to load.
            if (bulletStack.isEmpty()) {
                view.item().playSoundCancel(view.entity());
                return stageCancel;
            }

            // Loading final bullet.
            if (usageTicks >= maxStageTicks) {
                while ((int) (view.item().ammoCapacity * (usageTicks / (float) maxStageTicks)) > view.item().getAmmoList(view.stack()).size() && !bulletStack.isEmpty()) {
                    view.item().reloadOneBullet(view.stack(), bulletStack.split(1));
                    view.item().playSoundReload(view.entity());
                }
            }
            // Loading a bullet.
            else if (loadingAmmoStage > view.item().getAmmoList(view.stack()).size()) {
                while ((int) (view.item().ammoCapacity * (usageTicks / (float) maxStageTicks)) > view.item().getAmmoList(view.stack()).size() && !bulletStack.isEmpty()) {
                    view.item().reloadOneBullet(view.stack(), bulletStack.split(1));
                    view.item().playSoundReload(view.entity());
                }
            }

            return "";
        };
    }

    public static final StagedItemEventHandler<GunItem<?>> tryShootOneBullet = tryShootOneBullet();
    public static <T extends GunItem<?>> StagedItemEventHandler<T> tryShootOneBullet() {
        return view -> {
            ItemStack bulletStack = view.item().popNextBullet(view.stack());
            World world = view.world();
            if (bulletStack.getItem() instanceof IAmmoItem ammoItem) {
                view.item().playSoundShoot(view.entity());
                ammoItem.createBulletEntities(view.entity(), view.stack(), bulletStack).forEach(world::spawnEntity);
            }

            if (view.entity() instanceof PlayerEntity player) {
                if (!(world.isClient || player.getAbilities().creativeMode) && view.stack().isDamageable()) {
                    view.stack().damage(1, world.getRandom(), (ServerPlayerEntity) player);
                    if (view.item().isBroken(view.stack())) {
                        player.incrementStat(Stats.BROKEN.getOrCreateStat(view.item()));
                        player.sendToolBreakStatus(Hand.MAIN_HAND);
                    }
                }
                player.incrementStat(Stats.USED.getOrCreateStat(view.item()));
            }

            return "";
        };
    }

    public static StagedItemEventHandler<GunItem<?>> setStageTicksFromAmmo = setStageTicksFromAmmo();
    public static <T extends GunItem<?>> StagedItemEventHandler<T> setStageTicksFromAmmo() {
        return view -> {
            int ammoAmount = view.item().getAmmoList(view.stack()).size();
            int ammoCapacity = view.item().ammoCapacity;
            int usageStage = (int) MathHelper.map(ammoAmount, 0, ammoCapacity, 0, view.item().getMaxStageTicks(view.stack()));
            view.item().setStageTicks(view.stack(), usageStage);
            return "";
        };
    }

    public static <T extends GunItem<?>> StagedItemEventHandler<T> ifRandom(float chance, String stage) {
        return view -> view.world().getRandom().nextFloat() <= chance ? stage : "";
    }

    public static <T extends GunItem<?>> StagedItemEventHandler<T> ifBroken(String stageBroken) {
        return view -> view.item().isBroken(view.stack()) ? stageBroken : "";
    }

    public static <T extends GunItem<?>> StagedItemEventHandler<T> playSound(SoundEvent soundEvent, float volume, float pitch) {
        return view -> {
            var entity = view.entity();
            view.world().playSound(null, entity.getX(), entity.getEyeY(), entity.getZ(), soundEvent, SoundCategory.PLAYERS, volume, pitch);
            return "";
        };
    }

    public static StagedItemEventHandler<GunItem<?>> playSoundReload = playSoundReload();
    public static <T extends GunItem<?>> StagedItemEventHandler<T> playSoundReload() {
        return view -> {
            view.item().playSoundReload(view.entity());
            return "";
        };
    }

    public static StagedItemEventHandler<GunItem<?>> playSoundCancel = playSoundCancel();
    public static <T extends GunItem<?>> StagedItemEventHandler<T> playSoundCancel() {
        return view -> {
            view.item().playSoundCancel(view.entity());
            return "";
        };
    }

    public static StagedItemEventHandler<GunItem<?>> playSoundShoot = playSoundShoot();
    public static <T extends GunItem<?>> StagedItemEventHandler<T> playSoundShoot() {
        return view -> {
            view.item().playSoundShoot(view.entity());
            return "";
        };
    }

    public static StagedItemEventHandler<GunItem<?>> playSoundReadyTick = playSoundReadyTick();
    public static <T extends GunItem<?>> StagedItemEventHandler<T> playSoundReadyTick() {
        return view -> {
            view.item().playSoundReadyTick(view.entity());
            return "";
        };
    }

    public static StagedItemEventHandler<GunItem<?>> playSoundReadyDone = playSoundReadyDone();
    public static <T extends GunItem<?>> StagedItemEventHandler<T> playSoundReadyDone() {
        return view -> {
            view.item().playSoundReadyDone(view.entity());
            return "";
        };
    }
}
