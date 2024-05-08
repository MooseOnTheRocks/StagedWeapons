package dev.foltz.stagedweapons.enchantment;

import dev.foltz.stagedweapons.StagedWeapons;
import dev.foltz.stagedweapons.item.gun.GunItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public abstract class ModEnchantments {
    private static final Map<Identifier, Enchantment> ALL_ENCHANTMENTS = new HashMap<>();

    public static final Identifier ENCHANTMENT_QUICK_ACTION = registerEnchantment("quick_action", new QuickActionEnchantment());
    public static final Identifier ENCHANTMENT_QUICK_FIRE = registerEnchantment("quick_fire", new QuickFireEnchantment());
    public static final Identifier ENCHANTMENT_QUICK_RELOAD = registerEnchantment("quick_reload", new QuickReloadEnchantment());
    public static final Identifier ENCHANTMENT_QUICK_AIM = registerEnchantment("quick_aim", new QuickAimEnchantment());

    public static Identifier registerEnchantment(String name, Enchantment enchantment) {
        Identifier id = Identifier.of(StagedWeapons.MODID, name);
        ALL_ENCHANTMENTS.put(id, enchantment);
        return id;
    }
    public static void registerAllEnchantments() {
        ALL_ENCHANTMENTS.forEach((id, type) -> Registry.register(Registries.ENCHANTMENT, id, type));
    }
}
