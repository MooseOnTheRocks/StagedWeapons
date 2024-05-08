package dev.foltz.stagedweapons.item;

import dev.foltz.stagedweapons.StagedWeapons;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public class CompositeItem extends Item {
    public CompositeItem(Settings settings) {
        super(settings);
    }

    @Override
    protected String getOrCreateTranslationKey() {
        if (this.translationKey == null) {
            Identifier id = Registries.ITEM.getId(this);
            var fullPath = id.getPath().replace('/', '.');
            var parts = fullPath.split("\\.");
            // Strip off trailing .default
            int newLength = parts[parts.length - 1].equals("default") ? parts.length - 1 : parts.length;
            parts = Arrays.copyOf(parts, newLength);
            var joined = String.join(".", parts);
            this.translationKey = "item" + "." + id.getNamespace() + "." + joined;
        }
        return this.translationKey;
    }
}
