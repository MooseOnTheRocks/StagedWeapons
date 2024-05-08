package dev.foltz.stagedweapons.item.gun;

import dev.foltz.stagedweapons.Util;
import dev.foltz.stagedweapons.enchantment.ModEnchantments;
import dev.foltz.stagedweapons.item.ModItems;
import dev.foltz.stagedweapons.stage.Stage;
import dev.foltz.stagedweapons.stage.StagedItemGraph;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.List;

import static dev.foltz.stagedweapons.Util.ticksFromSeconds;
import static dev.foltz.stagedweapons.item.gun.GunItemStageUtil.*;

public class RevolverItem extends GunItem<RevolverItem> {
    public static final String STAGE_DEFAULT = "default";
    public static final String STAGE_RELOADING = "reloading";
    public static final String STAGE_COCKING = "cocking";
    public static final String STAGE_COCKED = "cocked";
    public static final String STAGE_FIRING = "firing";
    public static final String STAGE_BROKEN = "broken";

    public RevolverItem() {
        super(new FabricItemSettings(), 128, List.of(ModItems.AMMO_TYPE_PISTOL), 6, StagedItemGraph.<RevolverItem>builder()
                .withStage(Stage.<RevolverItem>builder(STAGE_DEFAULT)
                        .barColor(barColorGreen)
                        .barProgress(barProgressLinearAmmoAmount)
                        .onInit(tryShootOrReload(STAGE_COCKING, STAGE_RELOADING))
                        .onPressShoot(tryShootOrReload(STAGE_COCKING, STAGE_RELOADING))
                        .onPressReload(tryReload(STAGE_RELOADING))
                )
                .withStage(Stage.<RevolverItem>builder(STAGE_RELOADING)
                        .duration(durationWithQuickReload(ticksFromSeconds(5f), ticksFromSeconds(1.0f)))
                        .barColor(barColorYellow)
                        .barProgress(barProgressLinearAmmoAmount)
                        .onInit(setStageTicksFromAmmo)
                        .onTick(tryReloadOneBullet(STAGE_DEFAULT))
                        .onLastTick(STAGE_DEFAULT)
                        .onReleaseReload(playSoundCancel.then(STAGE_DEFAULT))
                        .onReleaseShoot(playSoundCancel.then(STAGE_DEFAULT))
                        .onUnselected(playSoundCancel.then(STAGE_DEFAULT))
                )
                .withStage(Stage.<RevolverItem>builder(STAGE_COCKING)
                        .duration(durationWithQuickAction(ticksFromSeconds(0.5f), ticksFromSeconds(0.1f)))
                        .barProgress(barProgressLinearAmmoAmount)
                        .barColor(barColorGreen)
                        .onInit(playSoundReadyTick)
                        .onReleaseShoot(playSoundCancel.then(STAGE_DEFAULT))
                        .onReleaseReload(playSoundCancel.then(STAGE_DEFAULT))
                        .onUnselected(playSoundCancel.then(STAGE_DEFAULT))
                        .onLastTick(STAGE_COCKED)
                )
                .withStage(Stage.<RevolverItem>builder(STAGE_COCKED)
                        .barColor(barColorOrange)
                        .barProgress(barProgressLinearAmmoAmount)
                        .onInit(playSoundReadyDone)
                        .onReleaseReload(playSoundCancel.then(STAGE_DEFAULT))
                        .onPressShoot(STAGE_FIRING)
                )
                .withStage(Stage.<RevolverItem>builder(STAGE_FIRING)
                        .tickWhileUnselected()
                        .duration(durationWithQuickFire(ticksFromSeconds(0.8f), ticksFromSeconds(0.15f)))
                        .barColor(barColorRed)
                        .barProgress(barProgressLinearInverseStageTicks)
                        .onInit(tryShootOneBullet.then(ifBroken(STAGE_BROKEN)))
                        .onLastTick(STAGE_DEFAULT)
                )
                .withStage(Stage.<RevolverItem>builder(STAGE_BROKEN)
                        .barColor(barColorRed)
                        .barProgress(barProgressFull)
                )
                .build()
        );
    }

    @Override
    public float getAimingZoomModifier() {
        return 0.25f;
    }

    @Override
    public int getMaxAimingTicks(ItemStack stack) {
        return GunItemStageUtil.durationWithQuickAim(ticksFromSeconds(0.5f), ticksFromSeconds(0.1f)).apply(stack);
    }
}
