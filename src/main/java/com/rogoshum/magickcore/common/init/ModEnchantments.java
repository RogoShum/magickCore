package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.lib.LibEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MagickCore.MOD_ID);
    public static RegistryObject<Enchantment> ELEMENT_DEPRIVATION = ENCHANTMENTS.register(LibEnchantment.ELEMENT_DEPRIVATION, () -> new ElementDeprivationEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));

    public static class ElementDeprivationEnchantment extends Enchantment {
        protected ElementDeprivationEnchantment(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot[] slots) {
            super(rarityIn, typeIn, slots);
        }

        public int getMinCost(int enchantmentLevel) {
            return 10;
        }

        public int getMaxCost(int enchantmentLevel) {
            return 1000;
        }

        public int getMaxLevel() {
            return 3;
        }
    }
}
