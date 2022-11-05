package com.rogoshum.magickcore.init;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.entity.ai.sensor.VillagerHostilesSensor;
import com.rogoshum.magickcore.entity.radiated.RayTraceEntity;
import com.rogoshum.magickcore.lib.LibRegistry;
import com.rogoshum.magickcore.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModVillager {
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, MagickCore.MOD_ID);
    public static final DeferredRegister<PointOfInterestType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, MagickCore.MOD_ID);
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, MagickCore.MOD_ID);
    public static final RegistryObject<PointOfInterestType> MAGE_POI = POI_TYPES.register("mage",
            () -> new PointOfInterestType("mage", ImmutableSet.copyOf(ModBlocks.magick_crafting.get().getStateContainer().getValidStates()), 1, 1));
    public static final RegistryObject<VillagerProfession> MAGE = VILLAGER_PROFESSIONS.register("mage",
            () -> new VillagerProfession("mage", MAGE_POI.get(), ImmutableSet.of(), ImmutableSet.of(), (SoundEvent)null));

    public static final RegistryObject<SensorType<Sensor<? super LivingEntity>>> VILLAGER_HOSTILES = SENSOR_TYPES.register("villager_hostiles", () -> new SensorType<>(VillagerHostilesSensor::new));
    private static final List<RegistryObject<?>> TradList = new ArrayList<>();

    public static void init() {
        TradList.clear();
        TradList.add(ModEntities.bubble);
        TradList.add(ModEntities.ray_trace);
        TradList.add(ModEntities.cone);
        TradList.add(ModEntities.sphere);
        TradList.add(ModEntities.square);
        TradList.add(ModEntities.sector);
        TradList.add(ModEntities.entity_capture);
        TradList.add(ModEntities.leaf);
        TradList.add(ModEntities.wind);
        TradList.add(ModEntities.ray);
        TradList.add(ModEntities.red_stone);
        TradList.add(ModEntities.mana_star);
        TradList.add(ModEntities.mana_laser);
        TradList.add(ModEntities.mana_orb);
        TradList.add(ModEntities.blood_bubble);
        TradList.add(ModEntities.gravity_lift);
    }

    public static class EntityTypeTrade implements VillagerTrades.ITrade {
        @Override
        public MerchantOffer getOffer(Entity trader, Random rand) {
            int count = rand.nextInt(15) + 15;
            ItemStack entityType = new ItemStack(ModItems.ENTITY_TYPE.get());
            ExtraDataHelper.itemManaData(entityType, itemManaData -> {
                itemManaData.spellContext().addChild(SpawnContext.create((EntityType<?>) TradList.get(rand.nextInt(TradList.size())).get()));
            });
            ItemStack crystal = new ItemStack(ModItems.spirit_crystal.get());
            crystal.setCount(count);
            return new MerchantOffer(crystal, entityType, 3, rand.nextInt(15) + 15, 1);
        }
    }
}
