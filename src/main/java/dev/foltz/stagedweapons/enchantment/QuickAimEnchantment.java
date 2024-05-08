package dev.foltz.stagedweapons.enchantment;

import dev.foltz.stagedweapons.item.gun.GunItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class QuickAimEnchantment extends Enchantment {
    public QuickAimEnchantment() {
        super(Rarity.COMMON, EnchantmentTarget.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public int getMinPower(int level) {
        return 15;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof GunItem<?>;
    }
}
