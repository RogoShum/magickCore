package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.entity.*;
import com.rogoshum.magickcore.entity.superentity.*;
import com.rogoshum.magickcore.entity.living.TimeManagerEntity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = MagickCore.MOD_ID)
public class ModEntites {
	//public static final EntityType<TimeManagerEntity> time_manager = (EntityType<TimeManagerEntity>) EntityType.Builder.create(TimeManagerEntity::new, EntityClassification.MISC).size(1f, 1f).build("time_manager").setRegistryName("time_manager");
	public static final EntityType<ManaOrbEntity> mana_orb = (EntityType<ManaOrbEntity>) EntityType.Builder.create(ManaOrbEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build("mana_orb").setRegistryName("mana_orb");
	public static final EntityType<ManaStarEntity> mana_star = (EntityType<ManaStarEntity>) EntityType.Builder.create(ManaStarEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build("mana_star").setRegistryName("mana_star");
	public static final EntityType<ManaLaserEntity> mana_laser = (EntityType<ManaLaserEntity>) EntityType.Builder.create(ManaLaserEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build("mana_laser").setRegistryName("mana_laser");
	public static final EntityType<DawnWardEntity> mana_shield = (EntityType<DawnWardEntity>) EntityType.Builder.create(DawnWardEntity::new, EntityClassification.MISC).size(8f, 8f).build("mana_shield").setRegistryName("mana_shield");
	public static final EntityType<ManaEyeEntity> mana_eye = (EntityType<ManaEyeEntity>) EntityType.Builder.create(ManaEyeEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build("mana_eye").setRegistryName("mana_eye");
	public static final EntityType<ManaRiftEntity> mana_rift = (EntityType<ManaRiftEntity>) EntityType.Builder.create(ManaRiftEntity::new, EntityClassification.MISC).size(5.0f, 2.0f).build("mana_rift").setRegistryName("mana_rift");
	public static final EntityType<ManaRuneEntity> mana_rune = (EntityType<ManaRuneEntity>) EntityType.Builder.create(ManaRuneEntity::new, EntityClassification.MISC).size(1.0f, 0.1f).build("mana_rune").setRegistryName("mana_rune");
	public static final EntityType<ManaSphereEntity> mana_sphere = (EntityType<ManaSphereEntity>) EntityType.Builder.create(ManaSphereEntity::new, EntityClassification.MISC).size(2.2f, 2.2f).build("mana_sphere").setRegistryName("mana_sphere");
	public static final EntityType<RadianceWellEntity> radiance_wall = (EntityType<RadianceWellEntity>) EntityType.Builder.create(RadianceWellEntity::new, EntityClassification.MISC).size(9.0f, 2.0f).build("radiance_wall").setRegistryName("radiance_wall");
	public static final EntityType<ChaoReachEntity> chaos_reach = (EntityType<ChaoReachEntity>) EntityType.Builder.create(ChaoReachEntity::new, EntityClassification.MISC).size(2.0f, 2.0f).build("chaos_reach").setRegistryName("chaos_reach");
	public static final EntityType<ThornsCaressEntity> thorns_caress = (EntityType<ThornsCaressEntity>) EntityType.Builder.create(ThornsCaressEntity::new, EntityClassification.MISC).size(2.0f, 2.0f).build("thorns_caress").setRegistryName("thorns_caress");
	public static final EntityType<SilenceSquallEntity> silence_squall = (EntityType<SilenceSquallEntity>) EntityType.Builder.create(SilenceSquallEntity::new, EntityClassification.MISC).size(3.0f, 3.0f).build("silence_squall").setRegistryName("silence_squall");
	public static final EntityType<AscendantRealmEntity> ascendant_realm = (EntityType<AscendantRealmEntity>) EntityType.Builder.create(AscendantRealmEntity::new, EntityClassification.MISC).size(10.0f, 2.0f).build("ascendant_realm").setRegistryName("ascendant_realm");
	public static final EntityType<ManaElementOrbEntity> element_orb = (EntityType<ManaElementOrbEntity>) EntityType.Builder.create(ManaElementOrbEntity::new, EntityClassification.MISC).size(0.3f, 0.3f).build("element_orb").setRegistryName("element_orb");
	public static final EntityType<ManaPowerEntity> mana_power = (EntityType<ManaPowerEntity>) EntityType.Builder.create(ManaPowerEntity::new, EntityClassification.MISC).size(0.0f, 0.0f).build("mana_power").setRegistryName("mana_power");
	public static final EntityType<LifeStateEntity> life_state = (EntityType<LifeStateEntity>) EntityType.Builder.create(LifeStateEntity::new, EntityClassification.MISC).size(0.2f, 0.2f).build("life_state").setRegistryName("life_state");

	@SubscribeEvent
    public static void setupAttributes(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            //GlobalEntityTypeAttributes.put(time_manager, MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 10.0D).create());
        });
    }
	
	@SubscribeEvent
	public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event)
	{
		event.getRegistry().registerAll
		(
				//time_manager,
				mana_orb,
				mana_shield,
				mana_star,
				mana_laser,
				mana_eye,
				mana_rift,
				mana_rune,
				mana_sphere,
				radiance_wall,
				chaos_reach,
				thorns_caress,
				silence_squall,
				ascendant_realm,
				element_orb,
				mana_power,
				life_state
		);
	}
	
	@SubscribeEvent
	public static void registerEntitySpawnEggs(final RegistryEvent.Register<Item> event)
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
