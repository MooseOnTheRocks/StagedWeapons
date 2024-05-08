package dev.foltz.stagedweapons.item.gun;

import dev.foltz.stagedweapons.Util;
import dev.foltz.stagedweapons.item.ModItems;
import dev.foltz.stagedweapons.stage.Stage;
import dev.foltz.stagedweapons.stage.StagedItemGraph;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemStack;

import java.util.List;

import static dev.foltz.stagedweapons.Util.ticksFromSeconds;
import static dev.foltz.stagedweapons.item.gun.GunItemStageUtil.*;

public class FlamethrowerItem extends GunItem<FlamethrowerItem> {
    public static final String STAGE_DEFAULT = "default";
    public static final String STAGE_RELOADING = "reloading";
    public static final String STAGE_DONE_RELOADING = "done_reloading";
    public static final String STAGE_FIRING = "firing";
    public static final String STAGE_BROKEN = "broken";

    public FlamethrowerItem() {
        super(new FabricItemSettings(), 256, List.of(ModItems.AMMO_TYPE_FLAMETHROWER), 64, StagedItemGraph.<FlamethrowerItem>builder()
                .withStage(Stage.<FlamethrowerItem>builder(STAGE_DEFAULT)
                        .barColor(barColorOrange)
                        .barProgress(barProgressLinearAmmoAmount)
                        .onInit(tryShootOrReload(STAGE_FIRING, STAGE_RELOADING))
                        .onPressShoot(tryShootOrReload(STAGE_FIRING, STAGE_RELOADING))
                        .onPressReload(tryReload(STAGE_RELOADING))
                )
                .withStage(Stage.<FlamethrowerItem>builder(STAGE_DONE_RELOADING)
                        .barColor(barColorGreen)
                        .barProgress(barProgressLinearAmmoAmount)
                        .onReleaseReload(STAGE_DEFAULT)
                        .onReleaseShoot(STAGE_DEFAULT)
                        .onUnselected(STAGE_DEFAULT)
                )
                .withStage(Stage.<FlamethrowerItem>builder(STAGE_RELOADING)
                        .duration(durationWithQuickReload(ticksFromSeconds(2.5f), ticksFromSeconds(0.3f)))
                        .barColor(barColorYellow)
                        .barProgress(barProgressLinearAmmoAmount)
                        .onInit(setStageTicksFromAmmo)
                        .onTick(tryReloadOneBullet(STAGE_DONE_RELOADING))
                        .onLastTick(STAGE_DONE_RELOADING)
                        .onReleaseReload(playSoundCancel.then(STAGE_DEFAULT))
                        .onReleaseShoot(playSoundCancel.then(STAGE_DEFAULT))
                        .onUnselected(playSoundCancel.then(STAGE_DEFAULT))
                )
                .withStage(Stage.<FlamethrowerItem>builder(STAGE_FIRING)
                        .tickWhileUnselected()
                        .duration(durationWithQuickFire(ticksFromSeconds(0.2f), ticksFromSeconds(0.05f)))
                        .barColor(barColorRed)
                        .barProgress(barProgressLinearInverseStageTicks)
                        .onInit(tryShootOneBullet.then(ifBroken(STAGE_BROKEN)))
                        .onLastTick(STAGE_DEFAULT)
                )
                .withStage(Stage.<FlamethrowerItem>builder(STAGE_BROKEN)
                        .barColor(barColorRed)
                        .barProgress(barProgressFull)
                )
                .build()
        );
    }

    @Override
    public float getAimingZoomModifier() {
        return 0.15f;
    }

    @Override
    public int getMaxAimingTicks(ItemStack stack) {
        return GunItemStageUtil.durationWithQuickAim(Util.ticksFromSeconds(0.8f), ticksFromSeconds(0.15f)).apply(stack);
    }
}
