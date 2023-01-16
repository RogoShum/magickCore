package com.rogoshum.magickcore.common.init;

import com.google.common.collect.ImmutableSet;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.entity.ai.sensor.VillagerHostilesSensor;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.registry.DeferredRegister;
import com.rogoshum.magickcore.common.registry.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModVillager {
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(Registry.VILLAGER_PROFESSION, MagickCore.MOD_ID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registry.POINT_OF_INTEREST_TYPE, MagickCore.MOD_ID);
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(Registry.SENSOR_TYPE, MagickCore.MOD_ID);
    public static final RegistryObject<PoiType> MAGE_POI = POI_TYPES.register("mage",
            () -> new PoiType("mage", ImmutableSet.copyOf(ModBlocks.MAGICK_CRAFTING.get().getStateDefinition().getPossibleStates()), 1, 1));
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

    public static class EntityTypeTrade implements VillagerTrades.ItemListing {
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
