package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.entity.PlaceableItemEntity;
import com.rogoshum.magickcore.common.entity.living.ArtificialLifeEntity;
import com.rogoshum.magickcore.common.entity.pointed.*;
import com.rogoshum.magickcore.common.entity.projectile.*;
import com.rogoshum.magickcore.common.entity.radiated.*;
import com.rogoshum.magickcore.common.entity.superentity.*;
import com.rogoshum.magickcore.common.entity.living.MageVillagerEntity;

import com.rogoshum.magickcore.common.lib.LibEntities;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = MagickCore.MOD_ID)
public class ModEntities {
	public static final DeferredRegister<EntityType<?>> Entities = DeferredRegister.create(ForgeRegistries.ENTITIES, MagickCore.MOD_ID);
	//public static final EntityType<TimeManagerEntity> time_manager = (EntityType<TimeManagerEntity>) EntityType.Builder.create(TimeManagerEntity::new, EntityClassification.MISC).size(1f, 1f).build("time_manager").setRegistryName("time_manager");
	public static final RegistryObject<EntityType<ManaOrbEntity>> MANA_ORB = Entities.register(LibEntities.MANA_ORB, () -> EntityType.Builder.create(ManaOrbEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.MANA_ORB));
	public static final RegistryObject<EntityType<ManaStarEntity>> MANA_STAR = Entities.register(LibEntities.MANA_STAR, () -> EntityType.Builder.create(ManaStarEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.MANA_STAR));
	public static final RegistryObject<EntityType<ManaLaserEntity>> MANA_LASER = Entities.register(LibEntities.MANA_LASER, () -> EntityType.Builder.create(ManaLaserEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.MANA_LASER));
	public static final RegistryObject<EntityType<DawnWardEntity>> MANA_SHIELD = Entities.register(LibEntities.MANA_SHIELD, () -> EntityType.Builder.<DawnWardEntity>create(DawnWardEntity::new, EntityClassification.MISC).size(8f, 8f).build(LibEntities.MANA_SHIELD));
	public static final RegistryObject<EntityType<ManaSphereEntity>> MANA_SPHERE = Entities.register(LibEntities.MANA_SPHERE, () -> EntityType.Builder.create(ManaSphereEntity::new, EntityClassification.MISC).size(2.2f, 2.2f).build(LibEntities.MANA_SPHERE));
	public static final RegistryObject<EntityType<RadianceWellEntity>> RADIANCE_WALL = Entities.register(LibEntities.RADIANCE_WALL, () -> EntityType.Builder.create(RadianceWellEntity::new, EntityClassification.MISC).size(9.0f, 2.0f).build(LibEntities.RADIANCE_WALL));
	public static final RegistryObject<EntityType<ChaoReachEntity>> CHAOS_REACH = Entities.register(LibEntities.CHAOS_REACH, () -> EntityType.Builder.create(ChaoReachEntity::new, EntityClassification.MISC).size(2.0f, 2.0f).build(LibEntities.CHAOS_REACH));
	public static final RegistryObject<EntityType<ThornsCaressEntity>> THORNS_CARESS = Entities.register(LibEntities.THORNS_CARESS, () -> EntityType.Builder.create(ThornsCaressEntity::new, EntityClassification.MISC).size(2.0f, 2.0f).build(LibEntities.THORNS_CARESS));
	public static final RegistryObject<EntityType<SilenceSquallEntity>> SILENCE_SQUALL = Entities.register(LibEntities.SILENCE_SQUALL, () -> EntityType.Builder.create(SilenceSquallEntity::new, EntityClassification.MISC).size(3.0f, 3.0f).build(LibEntities.SILENCE_SQUALL));
	public static final RegistryObject<EntityType<AscendantRealmEntity>> ASCENDANT_REALM = Entities.register(LibEntities.ASCENDANT_REALM, () -> EntityType.Builder.create(AscendantRealmEntity::new, EntityClassification.MISC).size(10.0f, 2.0f).build(LibEntities.ASCENDANT_REALM));
	public static final RegistryObject<EntityType<ManaElementOrbEntity>> ELEMENT_ORB = Entities.register(LibEntities.ELEMENT_ORB, () -> EntityType.Builder.create(ManaElementOrbEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.ELEMENT_ORB));
	public static final RegistryObject<EntityType<ContextCreatorEntity>> CONTEXT_CREATOR = Entities.register(LibEntities.CONTEXT_CREATOR, () -> EntityType.Builder.create(ContextCreatorEntity::new, EntityClassification.MISC).size(3.0f, 3.0f).build(LibEntities.CONTEXT_CREATOR));
	public static final RegistryObject<EntityType<ManaCapacityEntity>> MANA_CAPACITY = Entities.register(LibEntities.MANA_CAPACITY, () -> EntityType.Builder.create(ManaCapacityEntity::new, EntityClassification.MISC).size(1.0f, 1.0f).build(LibEntities.MANA_CAPACITY));
	public static final RegistryObject<EntityType<ContextPointerEntity>> CONTEXT_POINTER = Entities.register(LibEntities.CONTEXT_POINTER, () -> EntityType.Builder.create(ContextPointerEntity::new, EntityClassification.MISC).size(1.0f, 1.0f).build(LibEntities.CONTEXT_POINTER));
	public static final RegistryObject<EntityType<RayTraceEntity>> RAY_TRACE = Entities.register(LibEntities.RAY_TRACE, () -> EntityType.Builder.create(RayTraceEntity::new, EntityClassification.MISC).size(0.0f, 0.0f).build(LibEntities.RAY_TRACE));
	public static final RegistryObject<EntityType<EntityHunterEntity>> ENTITY_CAPTURE = Entities.register(LibEntities.ENTITY_HUNTER, () -> EntityType.Builder.create(EntityHunterEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(LibEntities.ENTITY_HUNTER));
	public static final RegistryObject<EntityType<ConeEntity>> CONE = Entities.register(LibEntities.CONE, () -> EntityType.Builder.create(ConeEntity::new, EntityClassification.MISC).size(0.0f, 0.0f).build(LibEntities.CONE));
	public static final RegistryObject<EntityType<SectorEntity>> SECTOR = Entities.register(LibEntities.SECTOR, () -> EntityType.Builder.create(SectorEntity::new, EntityClassification.MISC).size(0.0f, 0.0f).build(LibEntities.SECTOR));
	public static final RegistryObject<EntityType<SphereEntity>> SPHERE = Entities.register(LibEntities.SPHERE, () -> EntityType.Builder.create(SphereEntity::new, EntityClassification.MISC).size(0.0f, 0.0f).build(LibEntities.SPHERE));
	public static final RegistryObject<EntityType<SquareEntity>> SQUARE = Entities.register(LibEntities.SQUARE, () -> EntityType.Builder.create(SquareEntity::new, EntityClassification.MISC).size(0.0f, 0.0f).build(LibEntities.SQUARE));
	public static final RegistryObject<EntityType<RayEntity>> RAY = Entities.register(LibEntities.RAY, () -> EntityType.Builder.create(RayEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.RAY));
	public static final RegistryObject<EntityType<BloodBubbleEntity>> BLOOD_BUBBLE = Entities.register(LibEntities.BLOOD_BUBBLE, () -> EntityType.Builder.create(BloodBubbleEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(LibEntities.BLOOD_BUBBLE));
	public static final RegistryObject<EntityType<LampEntity>> LAMP = Entities.register(LibEntities.LAMP, () -> EntityType.Builder.create(LampEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(LibEntities.LAMP));
	public static final RegistryObject<EntityType<ManaArrowEntity>> ARROW = Entities.register(LibEntities.ARROW, () -> EntityType.Builder.create(ManaArrowEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.ARROW));
	public static final RegistryObject<EntityType<BubbleEntity>> BUBBLE = Entities.register(LibEntities.BUBBLE, () -> EntityType.Builder.create(BubbleEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(LibEntities.BUBBLE));
	public static final RegistryObject<EntityType<LeafEntity>> LEAF = Entities.register(LibEntities.LEAF, () -> EntityType.Builder.create(LeafEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.LEAF));
	public static final RegistryObject<EntityType<RedStoneEntity>> RED_STONE = Entities.register(LibEntities.RED_STONE, () -> EntityType.Builder.create(RedStoneEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.RED_STONE));
	public static final RegistryObject<EntityType<ShadowEntity>> SHADOW = Entities.register(LibEntities.SHADOW, () -> EntityType.Builder.create(ShadowEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(LibEntities.SHADOW));
	public static final RegistryObject<EntityType<WindEntity>> WIND = Entities.register(LibEntities.WIND, () -> EntityType.Builder.create(WindEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(LibEntities.WIND));
	public static final RegistryObject<EntityType<GravityLiftEntity>> GRAVITY_LIFT = Entities.register(LibEntities.GRAVITY_LIFT, () -> EntityType.Builder.create(GravityLiftEntity::new, EntityClassification.MISC).size(1.0f, 1.0f).build(LibEntities.GRAVITY_LIFT));
	public static final RegistryObject<EntityType<PlaceableItemEntity>> PLACEABLE_ENTITY = Entities.register(LibEntities.PLACEABLE_ENTITY, () -> EntityType.Builder.create(PlaceableItemEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(LibEntities.PLACEABLE_ENTITY));
	public static final RegistryObject<EntityType<MageVillagerEntity>> MAGE = Entities.register(LibEntities.MAGE, () -> EntityType.Builder.create(MageVillagerEntity::new, EntityClassification.CREATURE).size(0.6F, 1.95F).trackingRange(10).build(LibEntities.MAGE));
	public static final RegistryObject<EntityType<PhantomEntity>> PHANTOM = Entities.register(LibEntities.PHANTOM, () -> EntityType.Builder.create(PhantomEntity::new, EntityClassification.MISC).size(0.0F, 0.0F).build(LibEntities.PHANTOM));
	public static final RegistryObject<EntityType<RepeaterEntity>> REPEATER = Entities.register(LibEntities.REPEATER, () -> EntityType.Builder.create(RepeaterEntity::new, EntityClassification.MISC).size(0.0F, 0.0F).build(LibEntities.REPEATER));
	public static final RegistryObject<EntityType<JewelryBagEntity>> JEWELRY_BAG = Entities.register(LibEntities.JEWELRY_BAG, () -> EntityType.Builder.create(JewelryBagEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).build(LibEntities.JEWELRY_BAG));
	public static final RegistryObject<EntityType<ArtificialLifeEntity>> ARTIFICIAL_LIFE = Entities.register(LibEntities.ARTIFICIAL_LIFE, () -> EntityType.Builder.create(ArtificialLifeEntity::new, EntityClassification.CREATURE).size(1.0F, 1.0F).trackingRange(10).build(LibEntities.ARTIFICIAL_LIFE));
	public static final RegistryObject<EntityType<SpinEntity>> SPIN = Entities.register(LibEntities.SPIN, () -> EntityType.Builder.create(SpinEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).build(LibEntities.SPIN));
	public static final RegistryObject<EntityType<ChainEntity>> CHAIN = Entities.register(LibEntities.CHAIN, () -> EntityType.Builder.create(ChainEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).build(LibEntities.CHAIN));

	public static Item registerEntitySpawnEgg(EntityType<?> type, int color1, int color2, String name) {
		SpawnEggItem item = new SpawnEggItem(type, color1, color2, new Item.Properties().group(ModGroups.ITEM_GROUP));
		
		item.setRegistryName(MagickCore.MOD_ID + "_" + name);
		
		return item;
	}

	@SubscribeEvent
	public void registerEntitySpawnEggs(final RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				registerEntitySpawnEgg(MAGE.get(), 0x32a852, 0x30407a, "mage_egg")
		);
	}
}
