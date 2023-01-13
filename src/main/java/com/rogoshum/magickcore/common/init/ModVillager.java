package com.rogoshum.magickcore.common.init;

import com.google.common.collect.ImmutableSet;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.entity.ai.sensor.VillagerHostilesSensor;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.SoundEvent;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModVillager {
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, MagickCore.MOD_ID);
    public static final DeferredRegister<PointOfInterestType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, MagickCore.MOD_ID);
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, MagickCore.MOD_ID);
    public static final RegistryObject<PointOfInterestType> MAGE_POI = POI_TYPES.register("mage",
            () -> new PointOfInterestType("mage", ImmutableSet.copyOf(ModBlocks.MAGICK_CRAFTING.get().getStateDefinition().getPossibleStates()), 1, 1));
    public static final RegistryObject<VillagerProfession> MAGE = VILLAGER_PROFESSIONS.register("mage",
            () -> new VillagerProfession("mage", MAGE_POI.get(), ImmutableSet.of(), ImmutableSet.of(), (SoundEvent)null));

    public static final RegistryObject<SensorType<Sensor<? super LivingEntity>>> VILLAGER_HOSTILES = SENSOR_TYPES.register("villager_hostiles", () -> new SensorType<>(VillagerHostilesSensor::new));
    private static final List<RegistryObject<?>> TradList = new ArrayList<>();

    public static void init() {
        TradList.clear();
        TradList.add(ModEntities.BUBBLE);
        TradList.add(ModEntities.RAY_TRACE);
        TradList.add(ModEntities.CONE);
        TradList.add(ModEntities.SPHERE);
        TradList.add(ModEntities.SQUARE);
        TradList.add(ModEntities.SECTOR);
        TradList.add(ModEntities.ENTITY_CAPTURE);
        TradList.add(ModEntities.LEAF);
        TradList.add(ModEntities.WIND);
        TradList.add(ModEntities.RAY);
        TradList.add(ModEntities.RED_STONE);
        TradList.add(ModEntities.MANA_STAR);
        TradList.add(ModEntities.MANA_LASER);
        TradList.add(ModEntities.MANA_ORB);
        TradList.add(ModEntities.BLOOD_BUBBLE);
        TradList.add(ModEntities.GRAVITY_LIFT);
        TradList.add(ModEntities.REPEATER);
        TradList.add(ModEntities.JEWELRY_BAG);
        TradList.add(ModEntities.SPIN);
        TradList.add(ModEntities.CHAIN);
        TradList.add(ModEntities.MANA_SPHERE);
        TradList.add(ModEntities.SHADOW);
        TradList.add(ModEntities.MULTI_RELEASE);
        TradList.add(ModEntities.CHARGE);
    }

    public static class EntityTypeTrade implements VillagerTrades.ITrade {
        @Override
        public MerchantOffer getOffer(Entity trader, Random rand) {
            int count = rand.nextInt(3) + 1;
            ItemStack entityType = new ItemStack(ModItems.ENTITY_TYPE.get());
            ExtraDataUtil.itemManaData(entityType, itemManaData -> {
                itemManaData.spellContext().addChild(SpawnContext.create((EntityType<?>) TradList.get(rand.nextInt(TradList.size())).get()));
            });
            ItemStack crystal = new ItemStack(ModItems.SPIRIT_CRYSTAL.get());
            crystal.setCount(count);
            return new MerchantOffer(crystal, entityType, 127, rand.nextInt(15), 0.2f);
        }
    }
}
