package dev.foltz.stagedweapons;

import com.mojang.datafixers.util.Function4;
import dev.foltz.stagedweapons.item.ModItems;
import dev.foltz.stagedweapons.item.gun.*;
import dev.foltz.stagedweapons.stage.IStagedItem;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class ItemModelPredicates {
    private static final List<Integer> IN_HAND_ORDINALS = Stream.of(
            ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
            ModelTransformationMode.FIRST_PERSON_RIGHT_HAND,
            ModelTransformationMode.THIRD_PERSON_LEFT_HAND,
            ModelTransformationMode.THIRD_PERSON_RIGHT_HAND)
        .map(Enum::ordinal).toList();

    public static final Consumer<Item> STACK_COUNT = makePredicate("stack_count", (item, stack, livingEntity, world) -> stack.getCount() / (float) stack.getMaxCount());

    public static final Consumer<? extends IStagedItem<?>> USAGE_TICKS = makePredicate("usage_stage", (stagedItem, stack, livingEntity, world) -> {
        int maxUse = stagedItem.getMaxStageTicks(stack);
        return maxUse == 0 ? 0.0f : stagedItem.getStageTicks(stack) / (float) maxUse;
    });

    public static final Consumer<? extends GunItem<?>> AMMO_STAGE = makePredicate("ammo_stage", (stagedItem, stack, livingEntity, world) -> {
        int maxAmmo = stagedItem.ammoCapacity;
        return maxAmmo == 0 ? 0.0f : stagedItem.getAmmoList(stack).size() / (float) maxAmmo;
    });

    public static final Consumer<? extends Item> IN_GUI = item -> ModelPredicateProviderRegistry.register(item, new Identifier("in_gui"), (stack, world, entity, seed) -> entity != null && IN_HAND_ORDINALS.contains(seed - entity.getId()) ? 0 : 1);

    public static void registerAllItemModelPredicates() {
        // == Guns
        registerItemPredicates(Registries.ITEM.get(ModItems.EOKA),
                stagePredicate("is_reloading", EokaItem.STAGE_RELOADING),
                stagePredicate("is_firing", EokaItem.STAGE_FIRING),
                stagePredicate("is_striking", EokaItem.STAGE_STRIKING),
                stagePredicate("is_broken", EokaItem.STAGE_BROKEN),
                USAGE_TICKS,
                IN_GUI
        );

        registerItemPredicates(Registries.ITEM.get(ModItems.FLINTLOCK_PISTOL),
                stagePredicate("is_reloading", FlintlockPistolItem.STAGE_RELOADING),
                stagePredicate("is_firing", FlintlockPistolItem.STAGE_FIRING),
                stagePredicate("is_cocking", FlintlockPistolItem.STAGE_COCKING),
                stagePredicate("is_cocked", FlintlockPistolItem.STAGE_COCKED),
                stagePredicate("is_broken", FlintlockPistolItem.STAGE_BROKEN),
                USAGE_TICKS,
                IN_GUI
        );

        registerItemPredicates(Registries.ITEM.get(ModItems.REVOLVER),
                stagePredicate("is_reloading", RevolverItem.STAGE_RELOADING),
                stagePredicate("is_firing", RevolverItem.STAGE_FIRING),
                stagePredicate("is_cocking", RevolverItem.STAGE_COCKING),
                stagePredicate("is_cocked", RevolverItem.STAGE_COCKED),
                stagePredicate("is_broken", RevolverItem.STAGE_BROKEN),
                USAGE_TICKS,
                IN_GUI
        );

        registerItemPredicates(Registries.ITEM.get(ModItems.PUMP_SHOTGUN),
                stagePredicate("is_reloading", PumpShotgunItem.STAGE_RELOADING),
                stagePredicate("is_firing", PumpShotgunItem.STAGE_FIRING),
                stagePredicate("is_pumping", PumpShotgunItem.STAGE_PUMPING),
                stagePredicate("is_pumped", PumpShotgunItem.STAGE_PUMPED),
                stagePredicate("is_broken", PumpShotgunItem.STAGE_BROKEN),
                USAGE_TICKS,
                IN_GUI
        );

        registerItemPredicates(Registries.ITEM.get(ModItems.FLAMETHROWER),
                stagePredicate("is_reloading", FlamethrowerItem.STAGE_RELOADING),
                stagePredicate("is_firing", FlamethrowerItem.STAGE_FIRING),
                stagePredicate("is_broken", FlamethrowerItem.STAGE_BROKEN),
                AMMO_STAGE,
                IN_GUI
        );

        // == Ammo
        ModItems.ALL_ITEMS.values().forEach(item -> registerItemPredicates(item, STACK_COUNT));
//        registerItemPredicates(Registries.ITEM.get(ModItems.AMMO_PISTOL), STACK_COUNT);
//        registerItemPredicates(Registries.ITEM.get(ModItems.AMMO_PISTOL_DRAGON_BREATH), STACK_COUNT);
//        registerItemPredicates(Registries.ITEM.get(ModItems.AMMO_SHOTGUN), STACK_COUNT);
//        registerItemPredicates(Registries.ITEM.get(ModItems.AMMO_SHOTGUN_BOUNCY), STACK_COUNT);
//        registerItemPredicates(Registries.ITEM.get(ModItems.AMMO_SHOTGUN_DRAGON_BREATH), STACK_COUNT);
    }

    public static <T extends Item> void registerItemPredicates(T item, Consumer<?>... predicates) {
        for (var predicate : predicates) {
            ((Consumer<T>) predicate).accept(item);
        }
    }

    public static <T extends Item> void registerItemPredicates(T item, List<Consumer<T>> predicates) {
        predicates.forEach(pred -> pred.accept(item));
    }

    public static <T extends Item> Consumer<T> makePredicate(String name, Function4<T, ItemStack, LivingEntity, World, Float> predicate) {
        return item -> ModelPredicateProviderRegistry.register(item, new Identifier(name), (stack, world, entity, seed) -> entity == null ? 0.0f : predicate.apply(item, stack, entity, world));
    }

    public static <T extends Item> Consumer<T> booleanPredicate(String name, Function4<T, ItemStack, LivingEntity, World, Boolean> predicate) {
        return makePredicate(name, (item, stack, entity, world) -> predicate.apply(item, stack, entity, world) ? 1.0f : 0.0f);
    }

    public static <T extends Item & IStagedItem<?>> Consumer<T> stagePredicate(String predName, String... stageName) {
        return booleanPredicate(predName, (item, stack, entity, world) -> List.of(stageName).contains(item.getStageName(stack)));
    }
}
