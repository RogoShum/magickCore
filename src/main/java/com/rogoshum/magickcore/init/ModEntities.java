package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.entity.PlaceableItemEntity;
import com.rogoshum.magickcore.entity.pointed.*;
import com.rogoshum.magickcore.entity.projectile.*;
import com.rogoshum.magickcore.entity.radiated.*;
import com.rogoshum.magickcore.entity.superentity.*;

import com.rogoshum.magickcore.lib.LibEntities;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {
	public static final DeferredRegister<EntityType<?>> Entities = DeferredRegister.create(ForgeRegistries.ENTITIES, MagickCore.MOD_ID);
	//public static final EntityType<TimeManagerEntity> time_manager = (EntityType<TimeManagerEntity>) EntityType.Builder.create(TimeManagerEntity::new, EntityClassification.MISC).size(1f, 1f).build("time_manager").setRegistryName("time_manager");
	public static final RegistryObject<EntityType<ManaOrbEntity>> mana_orb = Entities.register(LibEntities.MANA_ORB, () -> EntityType.Builder.create(ManaOrbEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.MANA_ORB));
	public static final RegistryObject<EntityType<ManaStarEntity>> mana_star = Entities.register(LibEntities.MANA_STAR, () -> EntityType.Builder.create(ManaStarEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.MANA_STAR));
	public static final RegistryObject<EntityType<ManaLaserEntity>> mana_laser = Entities.register(LibEntities.MANA_LASER, () -> EntityType.Builder.create(ManaLaserEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.MANA_LASER));
	public static final RegistryObject<EntityType<DawnWardEntity>> mana_shield = Entities.register(LibEntities.MANA_SHIELD, () -> EntityType.Builder.<DawnWardEntity>create(DawnWardEntity::new, EntityClassification.MISC).size(8f, 8f).build(LibEntities.MANA_SHIELD));
	public static final RegistryObject<EntityType<ManaSphereEntity>> mana_sphere = Entities.register(LibEntities.MANA_SPHERE, () -> EntityType.Builder.create(ManaSphereEntity::new, EntityClassification.MISC).size(2.2f, 2.2f).build(LibEntities.MANA_SPHERE));
	public static final RegistryObject<EntityType<RadianceWellEntity>> radiance_wall = Entities.register(LibEntities.RADIANCE_WALL, () -> EntityType.Builder.create(RadianceWellEntity::new, EntityClassification.MISC).size(9.0f, 2.0f).build(LibEntities.RADIANCE_WALL));
	public static final RegistryObject<EntityType<ChaoReachEntity>> chaos_reach = Entities.register(LibEntities.CHAOS_REACH, () -> EntityType.Builder.create(ChaoReachEntity::new, EntityClassification.MISC).size(2.0f, 2.0f).build(LibEntities.CHAOS_REACH));
	public static final RegistryObject<EntityType<ThornsCaressEntity>> thorns_caress = Entities.register(LibEntities.THORNS_CARESS, () -> EntityType.Builder.create(ThornsCaressEntity::new, EntityClassification.MISC).size(2.0f, 2.0f).build(LibEntities.THORNS_CARESS));
	public static final RegistryObject<EntityType<SilenceSquallEntity>> silence_squall = Entities.register(LibEntities.SILENCE_SQUALL, () -> EntityType.Builder.create(SilenceSquallEntity::new, EntityClassification.MISC).size(3.0f, 3.0f).build(LibEntities.SILENCE_SQUALL));
	public static final RegistryObject<EntityType<AscendantRealmEntity>> ascendant_realm = Entities.register(LibEntities.ASCENDANT_REALM, () -> EntityType.Builder.create(AscendantRealmEntity::new, EntityClassification.MISC).size(10.0f, 2.0f).build(LibEntities.ASCENDANT_REALM));
	public static final RegistryObject<EntityType<ManaElementOrbEntity>> element_orb = Entities.register(LibEntities.ELEMENT_ORB, () -> EntityType.Builder.create(ManaElementOrbEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.ELEMENT_ORB));
	public static final RegistryObject<EntityType<ManaPowerEntity>> mana_power = Entities.register(LibEntities.MANA_POWER, () -> EntityType.Builder.create(ManaPowerEntity::new, EntityClassification.MISC).size(0.0f, 0.0f).build(LibEntities.MANA_POWER));
	public static final RegistryObject<EntityType<LifeStateEntity>> life_state = Entities.register(LibEntities.LIFE_STATE, () -> EntityType.Builder.create(LifeStateEntity::new, EntityClassification.MISC).size(0.2f, 0.2f).build(LibEntities.LIFE_STATE));
	public static final RegistryObject<EntityType<ContextCreatorEntity>> context_creator = Entities.register(LibEntities.CONTEXT_CREATOR, () -> EntityType.Builder.create(ContextCreatorEntity::new, EntityClassification.MISC).size(6.0f, 6.0f).build(LibEntities.CONTEXT_CREATOR));
	public static final RegistryObject<EntityType<ManaCapacityEntity>> mana_capacity = Entities.register(LibEntities.MANA_CAPACITY, () -> EntityType.Builder.create(ManaCapacityEntity::new, EntityClassification.MISC).size(1.0f, 1.0f).build(LibEntities.MANA_CAPACITY));
	public static final RegistryObject<EntityType<ContextPointerEntity>> context_pointer = Entities.register(LibEntities.CONTEXT_POINTER, () -> EntityType.Builder.create(ContextPointerEntity::new, EntityClassification.MISC).size(1.0f, 1.0f).build(LibEntities.CONTEXT_POINTER));
	public static final RegistryObject<EntityType<RayTraceEntity>> ray_trace = Entities.register(LibEntities.RAY_TRACE, () -> EntityType.Builder.create(RayTraceEntity::new, EntityClassification.MISC).size(0.0f, 0.0f).build(LibEntities.RAY_TRACE));
	public static final RegistryObject<EntityType<EntityHunterEntity>> entity_capture = Entities.register(LibEntities.ENTITY_HUNTER, () -> EntityType.Builder.create(EntityHunterEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(LibEntities.ENTITY_HUNTER));
	public static final RegistryObject<EntityType<ConeEntity>> cone = Entities.register(LibEntities.CONE, () -> EntityType.Builder.create(ConeEntity::new, EntityClassification.MISC).size(0.0f, 0.0f).build(LibEntities.CONE));
	public static final RegistryObject<EntityType<SectorEntity>> sector = Entities.register(LibEntities.SECTOR, () -> EntityType.Builder.create(SectorEntity::new, EntityClassification.MISC).size(0.0f, 0.0f).build(LibEntities.SECTOR));
	public static final RegistryObject<EntityType<SphereEntity>> sphere = Entities.register(LibEntities.SPHERE, () -> EntityType.Builder.create(SphereEntity::new, EntityClassification.MISC).size(0.0f, 0.0f).build(LibEntities.SPHERE));
	public static final RegistryObject<EntityType<SquareEntity>> square = Entities.register(LibEntities.SQUARE, () -> EntityType.Builder.create(SquareEntity::new, EntityClassification.MISC).size(0.0f, 0.0f).build(LibEntities.SQUARE));
	public static final RegistryObject<EntityType<RayEntity>> ray = Entities.register(LibEntities.RAY, () -> EntityType.Builder.create(RayEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.RAY));
	public static final RegistryObject<EntityType<BloodBubbleEntity>> blood_bubble = Entities.register(LibEntities.BLOOD_BUBBLE, () -> EntityType.Builder.create(BloodBubbleEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(LibEntities.BLOOD_BUBBLE));
	public static final RegistryObject<EntityType<LampEntity>> lamp = Entities.register(LibEntities.LAMP, () -> EntityType.Builder.create(LampEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(LibEntities.LAMP));
	public static final RegistryObject<EntityType<ManaArrowEntity>> arrow = Entities.register(LibEntities.ARROW, () -> EntityType.Builder.create(ManaArrowEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.ARROW));
	public static final RegistryObject<EntityType<BubbleEntity>> bubble = Entities.register(LibEntities.BUBBLE, () -> EntityType.Builder.create(BubbleEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(LibEntities.BUBBLE));
	public static final RegistryObject<EntityType<LeafEntity>> leaf = Entities.register(LibEntities.LEAF, () -> EntityType.Builder.create(LeafEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.LEAF));
	public static final RegistryObject<EntityType<RedStoneEntity>> red_stone = Entities.register(LibEntities.RED_STONE, () -> EntityType.Builder.create(RedStoneEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build(LibEntities.RED_STONE));
	public static final RegistryObject<EntityType<ShadowEntity>> shadow = Entities.register(LibEntities.SHADOW, () -> EntityType.Builder.create(ShadowEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(LibEntities.SHADOW));
	public static final RegistryObject<EntityType<WindEntity>> wind = Entities.register(LibEntities.WIND, () -> EntityType.Builder.create(WindEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(LibEntities.WIND));
	public static final RegistryObject<EntityType<GravityLiftEntity>> gravity_lift = Entities.register(LibEntities.GRAVITY_LIFT, () -> EntityType.Builder.create(GravityLiftEntity::new, EntityClassification.MISC).size(1.0f, 1.0f).build(LibEntities.GRAVITY_LIFT));
	public static final RegistryObject<EntityType<PlaceableItemEntity>> placeable_entity = Entities.register(LibEntities.PLACEABLE_ENTITY, () -> EntityType.Builder.create(PlaceableItemEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(LibEntities.PLACEABLE_ENTITY));

	@SubscribeEvent
    public void setupAttributes(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            //GlobalEntityTypeAttributes.put(time_manager, MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 10.0D).create());
        });
    }
	
	@SubscribeEvent
	public void registerEntitySpawnEggs(final RegistryEvent.Register<Item> event)
	{
		MagickCore.LOGGER.info("Loading SpawnEggs...");
		event.getRegistry().registerAll
		(
				//registerEntitySpawnEgg(time_manager, 0x32a852, 0x30407a, "time_manager_entity_egg")
		);
		MagickCore.LOGGER.info("SpawnEggs Loaded.");
	}
	
	public static Item registerEntitySpawnEgg(EntityType<?> type, int color1, int color2, String name)
	{
		SpawnEggItem item = new SpawnEggItem(type, color1, color2, new Item.Properties().group(ModGroup.itemGroup));
		
		item.setRegistryName(MagickCore.MOD_ID + "_" + name);
		
		return item;
	}
}
