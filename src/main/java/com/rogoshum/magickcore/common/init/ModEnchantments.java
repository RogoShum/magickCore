package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.lib.LibEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MagickCore.MOD_ID);
    public static RegistryObject<Enchantment> ELEMENT_DEPRIVATION = ENCHANTMENTS.register(LibEnchantment.ELEMENT_DEPRIVATION, () -> new ElementDeprivationEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));

    public static class ElementDeprivationEnchantment extends Enchantment {
        protected ElementDeprivationEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {
            super(rarityIn, typeIn, slots);
        }

        public int getMinEnchantability(int enchantmentLevel) {
            return 10;
        }

        public int getMaxEnchantability(int enchantmentLevel) {
            return 1000;
        }

        public int getMaxLevel() {
            return 3;
        }
    }
}
