package dev.foltz.stagedweapons.item.gun;

import dev.foltz.stagedweapons.item.ModItems;
import dev.foltz.stagedweapons.item.ammo.AmmoType;
import dev.foltz.stagedweapons.stage.Stage;
import dev.foltz.stagedweapons.stage.StagedItemGraph;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemStack;

import java.util.List;

import static dev.foltz.stagedweapons.Util.ticksFromSeconds;
import static dev.foltz.stagedweapons.item.gun.GunItemStageUtil.*;

public class EokaItem extends GunItem<EokaItem> {
    public static final String STAGE_DEFAULT = "default";
    public static final String STAGE_RELOADING = "reloading";
    public static final String STAGE_DONE_STRIKING = "done_striking";
    public static final String STAGE_STRIKING = "striking";
    public static final String STAGE_FIRING = "firing";
    public static final String STAGE_BROKEN = "broken";

    public EokaItem() {
        super(new FabricItemSettings(), 32, List.of(ModItems.AMMO_TYPE_PISTOL), 1, StagedItemGraph.<EokaItem>builder()
                .withStage(Stage.<EokaItem>builder(STAGE_DEFAULT)
                        .barColor(barColorOrange)
                        .barProgress(barProgressLinearAmmoAmount)
                        .onInit(tryShootOrReload(STAGE_STRIKING, STAGE_RELOADING))
                        .onPressShoot(tryShootOrReload(STAGE_STRIKING, STAGE_RELOADING))
                        .onPressReload(tryReload(STAGE_RELOADING))
                )
                .withStage(Stage.<EokaItem>builder(STAGE_RELOADING)
                        .duration(durationWithQuickReload(ticksFromSeconds(1.5f), ticksFromSeconds(0.3f)))
                        .barColor(barColorYellow)
                        .barProgress(barProgressLinearStageTicks)
                        .onTick(tryReloadOneBullet(STAGE_DEFAULT))
                        .onLastTick(STAGE_DONE_STRIKING)
                        .onReleaseReload(STAGE_DEFAULT)
                        .onReleaseShoot(STAGE_DEFAULT)
                        .onUnselected(STAGE_DEFAULT)
                )
                .withStage(Stage.<EokaItem>builder(STAGE_DONE_STRIKING)
                        .barColor(barColorGreen)
                        .barProgress(barProgressLinearAmmoAmount)
                        .onInit(view -> view.getEntityState().isPressingShoot() ? "" : STAGE_DEFAULT)
                        .onReleaseShoot(STAGE_DEFAULT)
                        .onUnselected(STAGE_DEFAULT)
                )
                .withStage(Stage.<EokaItem>builder(STAGE_STRIKING)
                        .duration(durationWithQuickAction(ticksFromSeconds(0.5f), ticksFromSeconds(0.1f)))
                        .barColor(barColorOrange)
                        .barProgress(barProgressLinearAmmoAmount)
                        .onInit(playSoundReadyTick)
                        .onLastTick(ifRandom(0.55f, STAGE_FIRING).then(STAGE_DONE_STRIKING))
                        .onUnselected(STAGE_DEFAULT)
                )
                .withStage(Stage.<EokaItem>builder(STAGE_FIRING)
                        .tickWhileUnselected()
                        .duration(durationWithQuickFire(ticksFromSeconds(1.5f), ticksFromSeconds(0.3f)))
                        .barColor(barColorRed)
                        .barProgress(barProgressLinearInverseStageTicks)
                        .onInit(tryShootOneBullet.then(ifBroken(STAGE_BROKEN)))
                        .onLastTick(STAGE_DEFAULT)
                )
                .withStage(Stage.<EokaItem>builder(STAGE_BROKEN)
                        .barColor(barColorRed)
                        .barProgress(barProgressFull)
                )
                .build());
    }

    @Override
    public float getAimingZoomModifier() {
        return 0.2f;
    }

    @Override
    public int getMaxAimingTicks(ItemStack stack) {
        return GunItemStageUtil.durationWithQuickAim(ticksFromSeconds(0.5f), ticksFromSeconds(0.1f)).apply(stack);
    }
}
