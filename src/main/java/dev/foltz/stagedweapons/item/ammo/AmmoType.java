package dev.foltz.stagedweapons.item.ammo;

import dev.foltz.stagedweapons.StagedWeapons;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class AmmoType {
    public final TagKey<Item> tagKey;
    public final Identifier defaultItem;

    public AmmoType(Identifier identifier, Identifier defaultItem) {
        tagKey = TagKey.of(RegistryKeys.ITEM, identifier);
        this.defaultItem = defaultItem;
    }

    public ItemStack getDefaultAmmoStack() {
        return Registries.ITEM.get(defaultItem).getDefaultStack();
    }
}
