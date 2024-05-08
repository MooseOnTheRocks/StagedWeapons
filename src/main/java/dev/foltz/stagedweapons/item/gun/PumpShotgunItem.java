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

public class PumpShotgunItem extends GunItem<PumpShotgunItem> {
    public static final String STAGE_DEFAULT = "default";
    public static final String STAGE_RELOADING = "reloading";
    public static final String STAGE_PRE_PUMPING = "pre_pumping";
    public static final String STAGE_PUMPING = "pumping";
    public static final String STAGE_PUMPED = "pumped";
    public static final String STAGE_FIRING = "firing";
    public static final String STAGE_BROKEN = "broken";

    public PumpShotgunItem() {
        super(new FabricItemSettings(), 64, List.of(ModItems.AMMO_TYPE_SHOTGUN), 4, StagedItemGraph.<PumpShotgunItem>builder()
                .withStage(Stage.<PumpShotgunItem>builder(STAGE_DEFAULT)
                        .barColor(barColorGreen)
                        .barProgress(barProgressLinearAmmoAmount)
                        .onInit(tryShootOrReload(STAGE_PRE_PUMPING, STAGE_RELOADING))
                        .onPressShoot(tryShootOrReload(STAGE_PRE_PUMPING, STAGE_RELOADING))
                        .onPressReload(tryReload(STAGE_RELOADING))
                )
                .withStage(Stage.<PumpShotgunItem>builder(STAGE_RELOADING)
                        .duration(durationWithQuickReload(ticksFromSeconds(3.0f), ticksFromSeconds(0.3f)))
                        .barColor(barColorYellow)
                        .barProgress(barProgressLinearAmmoAmount)
                        .onInit(setStageTicksFromAmmo)
                        .onTick(tryReloadOneBullet(STAGE_DEFAULT))
                        .onLastTick(STAGE_DEFAULT)
                        .onReleaseReload(playSoundCancel.then(STAGE_DEFAULT))
                        .onReleaseShoot(playSoundCancel.then(STAGE_DEFAULT))
                        .onUnselected(playSoundCancel.then(STAGE_DEFAULT))
                )
                .withStage(Stage.<PumpShotgunItem>builder(STAGE_PRE_PUMPING)
                        .duration(durationWithQuickAction(ticksFromSeconds(0.25f), ticksFromSeconds(0.025f)))
                        .barProgress(barProgressLinearAmmoAmount)
                        .barColor(barColorGreen)
                        .onReleaseShoot(STAGE_DEFAULT)
                        .onPressReload(STAGE_DEFAULT)
                        .onUnselected(STAGE_DEFAULT)
                        .onLastTick(STAGE_PUMPING)
                )
                .withStage(Stage.<PumpShotgunItem>builder(STAGE_PUMPING)
                        .duration(durationWithQuickAction(ticksFromSeconds(0.7f), ticksFromSeconds(0.15f)))
                        .barProgress(barProgressLinearAmmoAmount)
                        .barColor(barColorGreen)
                        .onInit(playSoundReadyTick)
                        .onReleaseShoot(playSoundCancel.then(STAGE_DEFAULT))
                        .onReleaseReload(playSoundCancel.then(STAGE_DEFAULT))
                        .onUnselected(playSoundCancel.then(STAGE_DEFAULT))
                        .onLastTick(STAGE_PUMPED)
                )
                .withStage(Stage.<PumpShotgunItem>builder(STAGE_PUMPED)
                        .barColor(barColorOrange)
                        .barProgress(barProgressLinearAmmoAmount)
                        .onInit(playSoundReadyDone)
                        .onReleaseReload(playSoundCancel.then(STAGE_DEFAULT))
                        .onPressShoot(STAGE_FIRING)
                )
                .withStage(Stage.<PumpShotgunItem>builder(STAGE_FIRING)
                        .tickWhileUnselected()
                        .duration(durationWithQuickFire(ticksFromSeconds(2f), ticksFromSeconds(0.25f)))
                        .barColor(barColorRed)
                        .barProgress(barProgressLinearInverseStageTicks)
                        .onInit(tryShootOneBullet.then(ifBroken(STAGE_BROKEN)))
                        .onLastTick(STAGE_DEFAULT)
                )
                .withStage(Stage.<PumpShotgunItem>builder(STAGE_BROKEN)
                        .barColor(barColorRed)
                        .barProgress(barProgressFull)
                )
                .build()
        );
    }

    @Override
    public float getAimingZoomModifier() {
        return 0.3f;
    }

    @Override
    public int getMaxAimingTicks(ItemStack stack) {
        return GunItemStageUtil.durationWithQuickAim(Util.ticksFromSeconds(1.0f), ticksFromSeconds(0.175f)).apply(stack);
    }
}
