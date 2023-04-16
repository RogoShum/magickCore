package com.rogoshum.magickcore.common.init;

import com.google.common.collect.ImmutableSet;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.entity.ai.sensor.VillagerHostilesSensor;
import com.rogoshum.magickcore.common.extradata.entity.PlayerTradeUnlock;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.Util;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class ModVillager {
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, MagickCore.MOD_ID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, MagickCore.MOD_ID);
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, MagickCore.MOD_ID);
    public static final RegistryObject<PoiType> MAGE_POI = POI_TYPES.register("mage",
            () -> new PoiType("mage", ImmutableSet.copyOf(ModBlocks.MAGICK_CRAFTING.get().getStateDefinition().getPossibleStates()), 1, 1));
    public static final RegistryObject<VillagerProfession> MAGE = VILLAGER_PROFESSIONS.register("mage",
            () -> new VillagerProfession("mage", MAGE_POI.get(), ImmutableSet.of(), ImmutableSet.of(), (SoundEvent)null));

    public static final RegistryObject<SensorType<Sensor<? super LivingEntity>>> VILLAGER_HOSTILES = SENSOR_TYPES.register("villager_hostiles", () -> new SensorType<>(VillagerHostilesSensor::new));
    private static final List<EntityType<?>> TradList = new ArrayList<>();

    public static void init() {
        TradList.clear();
        TradList.add(ModEntities.BUBBLE.get());
        TradList.add(ModEntities.RAY_TRACE.get());
        TradList.add(ModEntities.CONE.get());
        TradList.add(ModEntities.SPHERE.get());
        TradList.add(ModEntities.SQUARE.get());
        TradList.add(ModEntities.SECTOR.get());
        TradList.add(ModEntities.ENTITY_CAPTURE.get());
        TradList.add(ModEntities.LEAF.get());
        TradList.add(ModEntities.WIND.get());
        TradList.add(ModEntities.RAY.get());
        TradList.add(ModEntities.RED_STONE.get());
        TradList.add(ModEntities.MANA_STAR.get());
        TradList.add(ModEntities.MANA_LASER.get());
        TradList.add(ModEntities.MANA_ORB.get());
        TradList.add(ModEntities.BLOOD_BUBBLE.get());
        TradList.add(ModEntities.GRAVITY_LIFT.get());
        TradList.add(ModEntities.REPEATER.get());
        TradList.add(ModEntities.JEWELRY_BAG.get());
        TradList.add(ModEntities.SPIN.get());
        TradList.add(ModEntities.CHAIN.get());
        TradList.add(ModEntities.MANA_SPHERE.get());
        TradList.add(ModEntities.SHADOW.get());
        TradList.add(ModEntities.MULTI_RELEASE.get());
        TradList.add(ModEntities.CHARGE.get());
        TradList.add(ModEntities.LAMP.get());
        TradList.add(ModEntities.ARROW.get());
    }

    private static ItemStack CRYSTAL;

    public static List<MerchantOffer> getPlayerTrades(Player player) {
        List<MerchantOffer> offers = new ArrayList<>();
        PlayerTradeUnlock lock = ExtraDataUtil.playerTradeData(player);
        for(EntityType<?> type : lock.getUnLock()) {
            if(TradList.contains(type))
                offers.add(new MerchantOffer(getCrystal()
                        , getEntityTypeItem(type)
                        , 127, 5, 0.0f));
        }
        return offers;
    }

    public static ItemStack getCrystal() {
        if(CRYSTAL == null)
            CRYSTAL = Util.make(new ItemStack(ModItems.SPIRIT_CRYSTAL.get()), stack -> stack.setCount(5));
        return CRYSTAL;
    }

    public static ItemStack getEntityTypeItem(EntityType<?> type) {
        ItemStack entityType = new ItemStack(ModItems.ENTITY_TYPE.get());
        ExtraDataUtil.itemManaData(entityType, itemManaData -> {
            itemManaData.spellContext().applyType(ApplyType.SPAWN_ENTITY);
            itemManaData.spellContext().addChild(SpawnContext.create(type));
        });
        return entityType;
    }

    public static EntityType<?> getRandomType() {
        return TradList.get(MagickCore.rand.nextInt(TradList.size()));
    }

    public static class EntityTypeTrade implements VillagerTrades.ItemListing {
        @Override
        public MerchantOffer getOffer(Entity trader, Random rand) {
            return new MerchantOffer(getCrystal(), getEntityTypeItem(getRandomType()), 127, rand.nextInt(15), 0.0f);
        }
    }
}
