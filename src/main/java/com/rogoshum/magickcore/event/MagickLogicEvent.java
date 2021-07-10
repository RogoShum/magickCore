package com.rogoshum.magickcore.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IMagickElementObject;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.api.IManaMob;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.buff.ManaBuff;
import com.rogoshum.magickcore.capability.*;
import com.rogoshum.magickcore.entity.ManaEyeEntity;
import com.rogoshum.magickcore.entity.baseEntity.ManaEntity;
import com.rogoshum.magickcore.entity.baseEntity.ManaProjectileEntity;
import com.rogoshum.magickcore.helper.MagickReleaseHelper;
import com.rogoshum.magickcore.helper.RoguelikeHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModEffects;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.network.ElementAnimalPack;
import com.rogoshum.magickcore.network.EntityStatePack;
import com.rogoshum.magickcore.network.ManaDataPack;
import com.rogoshum.magickcore.network.Networking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

public class MagickLogicEvent {
	private static List<Entity> timeLords = new ArrayList<Entity>();

	@SubscribeEvent
	public void updateEntity(TickEvent.WorldTickEvent event)
	{
		if(event.phase == TickEvent.Phase.END && event.side.isServer())
			((ServerWorld)event.world).getEntities().forEach((e) -> MinecraftForge.EVENT_BUS.post(new EntityEvents.EntityUpdateEvent(e)));
	}

	@SubscribeEvent
	public void onStateCooldown(EntityEvents.StateCooldownEvent event)
	{
	}

	@SubscribeEvent
	public void onKnockBack(LivingKnockBackEvent event)
	{
	}

	@SubscribeEvent
	public void onMagickRelease(EntityEvents.MagickReleaseEvent event)
	{
		if(event.getEntity() instanceof ManaEyeEntity)
		{
			ManaEyeEntity eye = (ManaEyeEntity) event.getEntity();
			if(event.getMagickType() == "EYE_STAR" && eye.getOwner() instanceof LivingEntity && !((LivingEntity)eye.getOwner()).getActivePotionMap().containsKey(ModEffects.TRACE.orElse(null)))
				event.setTrace(MagickCore.emptyUUID);
		}
		else
		{
			if(event.getEntity() instanceof LivingEntity && ((LivingEntity)event.getEntity()).getActivePotionMap().containsKey(ModEffects.TRACE.orElse(null)))
				event.setTrace(MagickReleaseHelper.getTraceEntity(event.getEntity()));
		}

		if(event.getMagickType() == "SUPER_ENTITY")
			event.setTick(event.getTick() / 4);

		if(event.getMagickType() == "magickcore:mana_rift")
			event.setTick(event.getTick() * 2);

		if(event.getMagickType() == "magickcore:mana_eye")
			event.setTick(event.getTick() * 12);

		if(event.getTrace() == null)
			event.setTrace(MagickCore.emptyUUID);
	}

	@SubscribeEvent
	public void preMagickRelease(EntityEvents.MagickPreReleaseEvent event)
	{
		if(event.getEntity() instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity) event.getEntity();
			if(player.isCreative())
				return;
		}

		if(!(event.getEntity() instanceof LivingEntity))
			return;

		if(((LivingEntity)event.getEntity()).getActivePotionMap().containsKey(ModEffects.MANA_CONSUM_REDUCE.orElse(null)))
		{
			float amplifier = ((LivingEntity)event.getEntity()).getActivePotionEffect(ModEffects.MANA_CONSUM_REDUCE.orElse(null)).getAmplifier() + 2;
			event.setMana(event.getMana() / amplifier);
		}

		IEntityState state = event.getEntity().getCapability(MagickCore.entityState).orElse(null);
		if(state != null)
		{
			if(state.getBuffList().containsKey(LibBuff.FREEZE))
			{
				event.setCanceled(true);
				return;
			}

			boolean flag = false;
			float extraMana = 0;
			ItemStack stack = ((LivingEntity) event.getEntity()).getActiveItemStack();
			if(stack.getItem() instanceof IManaItem)
			{
				IManaItem item = (IManaItem) stack.getItem();
				if(item.getMana(stack) >= event.getMana()) {
					item.receiveMana(stack, -event.getMana());
					flag = true;
				}
				extraMana = item.getMana(stack);
			}

			if(!flag) {
				if (state.getManaValue() + extraMana >= event.getMana()) {
					state.setManaValue(state.getManaValue() + extraMana - event.getMana());
					if(extraMana > 0)
					{
						IManaItem item = (IManaItem) stack.getItem();
						item.setMana(stack, 0);
						item.setElement(stack, ModElements.getElement(LibElements.ORIGIN));
					}
				}
				else
					event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onShieldCapacity(EntityEvents.ShieldCapacityEvent event)
	{
		int mana = 0;
		if(event.getEntityLiving().getActivePotionMap().containsKey(ModEffects.SHIELD_VALUE.orElse(null)))
			mana += 25 * (event.getEntityLiving().getActivePotionEffect(ModEffects.SHIELD_VALUE.orElse(null)).getAmplifier() + 1);

		event.setCapacity(event.getCapacity() + mana);
	}

	@SubscribeEvent
	public void onShieldRegen(EntityEvents.ShieldRegenerationEvent event)
	{
		int mana = 0;
		if(event.getEntityLiving().getActivePotionMap().containsKey(ModEffects.SHIELD_REGEN.orElse(null)))
			mana = (event.getEntityLiving().getActivePotionEffect(ModEffects.SHIELD_REGEN.orElse(null)).getAmplifier() + 1);

		event.setAmount(event.getAmount() + mana);
	}

	@SubscribeEvent
	public void onManaRegen(EntityEvents.ManaRegenerationEvent event)
	{
		int mana = 0;
		if(event.getEntityLiving().getActivePotionMap().containsKey(ModEffects.MANA_REGEN.orElse(null)))
			mana = (event.getEntityLiving().getActivePotionEffect(ModEffects.MANA_REGEN.orElse(null)).getAmplifier() + 1);

		event.setMana(event.getMana() + mana);
	}

	@SubscribeEvent
	public void ManaObjectUpdateEvent(EntityEvents.EntityUpdateEvent event)
	{
		IManaData state = event.getEntity().getCapability(MagickCore.manaData).orElse(null);
		if(state != null)
		{
			if(event.getEntity().ticksExisted > state.getTickTime() && state.getTickTime() >= 0)
				event.getEntity().remove();
		}

		if(event.getEntity() instanceof ItemEntity)
		{
			ItemStack stack = ((ItemEntity) event.getEntity()).getItem();
			RoguelikeHelper.HandleTickItem(stack);
		}
	}

	@SubscribeEvent
	public void HitEntity(EntityEvents.HitEntityEvent event)
	{
		if(event.getEntity() instanceof IMagickElementObject)
		{
			IMagickElementObject mana = (IMagickElementObject) event.getEntity();
			IManaData data = event.getEntity().getCapability(MagickCore.manaData).orElse(null);

			if(data != null)
				mana.getElement().getAbility().hitEntity(event.getEntity(), event.getVictim(), data.getTickTime(), data.getForce());

			if(event.getVictim() instanceof ManaProjectileEntity)
			{
				mana.hitMixing((IMagickElementObject) event.getVictim());
			}
		}
	}

	@SubscribeEvent
	public void onRogueItemUpdate(LivingEvent.LivingUpdateEvent event)
	{
		if(event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) event.getEntityLiving();
			player.inventory.mainInventory.forEach(RoguelikeHelper::HandleTickItem);
		}
	}

	@SubscribeEvent
	public void onDeBuff(LivingEvent.LivingUpdateEvent event)
	{
		IEntityState state = event.getEntityLiving().getCapability(MagickCore.entityState).orElse(null);
		if(state != null && state.getBuffList().containsKey(LibBuff.HYPERMUTEKI))
			return;

		if(state != null && state.getBuffList().containsKey(LibBuff.FREEZE)) {
			if(event.getEntityLiving().hurtResistantTime > 0)
				event.getEntityLiving().hurtResistantTime--;
			if(event.getEntityLiving().hurtTime > 0)
				event.getEntityLiving().hurtTime--;
			event.getEntityLiving().swingProgress = event.getEntityLiving().prevSwingProgress;
			event.getEntityLiving().limbSwingAmount = event.getEntityLiving().prevLimbSwingAmount;
			event.getEntityLiving().renderYawOffset = event.getEntityLiving().prevRenderYawOffset;
			event.getEntityLiving().rotationYawHead = event.getEntityLiving().prevRotationYawHead;
			event.getEntityLiving().rotationPitch = event.getEntityLiving().prevRotationPitch;
			event.getEntityLiving().rotationYaw = event.getEntityLiving().prevRotationYaw;
			event.getEntityLiving().ticksExisted -= 1;
			if(event.getEntityLiving().getHealth() > 0)
				event.setCanceled(true);
		}

		if(state != null && state.getBuffList().containsKey(LibBuff.CRIPPLE)) {
			float force = state.getBuffList().get(LibBuff.CRIPPLE).getForce();
			float maxHealth = event.getEntityLiving().getMaxHealth() * 1/(force + 1)*1.9f;
			if(event.getEntityLiving().getHealth() > maxHealth)
				event.getEntityLiving().setHealth(maxHealth);
		}

		if(state != null && state.getBuffList().containsKey(LibBuff.STASIS)) {
			float force = state.getBuffList().get(LibBuff.STASIS).getForce();
			List<Entity> entityList = event.getEntityLiving().world.getEntitiesWithinAABBExcludingEntity(event.getEntityLiving(), event.getEntityLiving().getBoundingBox().expand(force, force, force));

			for(int i = 0; i< entityList.size(); ++i)
			{
				ModBuff.applyBuff(entityList.get(i), LibBuff.FREEZE, 20, 1, true);
			}
		}
	}

	@SubscribeEvent
	public void onRemoveBuff(LivingDeathEvent event)
	{
		IEntityState state = event.getEntityLiving().getCapability(MagickCore.entityState).orElse(null);
		if(event.getEntityLiving() instanceof PlayerEntity && state != null) {
			Iterator<String> it = state.getBuffList().keySet().iterator();
			while(it.hasNext())
				it.remove();
		}
	}

	@SubscribeEvent
	public void onWitherBuff(LivingHealEvent event)
	{
		IEntityState state = event.getEntityLiving().getCapability(MagickCore.entityState).orElse(null);
		if(state != null && state.getBuffList().containsKey(LibBuff.WITHER))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onWeakenBuff(LivingHurtEvent event)
	{
		IEntityState state = event.getEntityLiving().getCapability(MagickCore.entityState).orElse(null);

		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof LivingEntity)
		{
			IEntityState attacker = entity.getCapability(MagickCore.entityState).orElse(null);
			if(attacker.getBuffList().containsKey(LibBuff.LIGHT))
				event.setAmount(event.getAmount() * 1.5f);

			if(((LivingEntity)entity).getActivePotionMap().containsKey(ModEffects.MANA_FORCE.orElse(null)))
			{
				float amplifier = ((LivingEntity)entity).getActivePotionEffect(ModEffects.MANA_FORCE.orElse(null)).getAmplifier() + 1;
				if(event.getSource().isMagicDamage())
					event.setAmount(event.getAmount() + amplifier * 1.333f);
			}
		}

		if(!(event.getEntityLiving() instanceof PlayerEntity) && event.getSource().getDamageType().equals(DamageSource.LIGHTNING_BOLT.getDamageType()) && event.getAmount() >= 5.0f)
		{
			if(state.getElement().getType().equals(LibElements.ORIGIN)) {
				state.setElement(ModElements.getElement(LibElements.ARC));
				state.setElementShieldMana(50);
				state.setFinalMaxElementShield(50);
			}
		}

		if(state != null && state.getBuffList().containsKey(LibBuff.HYPERMUTEKI))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onDamage(LivingDamageEvent event)
	{
		IEntityState state = event.getEntityLiving().getCapability(MagickCore.entityState).orElse(null);
		if(state != null && state.getBuffList().containsKey(LibBuff.HYPERMUTEKI))
			event.setCanceled(true);

		if(state != null && state.getBuffList().containsKey(LibBuff.WEAKEN))
			event.setAmount(event.getAmount() * 1.3f);
	}

	@SubscribeEvent
	public void onApplyManaBuff(EntityEvents.ApplyManaBuffEvent event)
	{
		IEntityState state = event.getEntityLiving().getCapability(MagickCore.entityState).orElse(null);
		if(state != null && state.getBuffList().containsKey(LibBuff.HYPERMUTEKI) && !event.getBeneficial())
			event.setCanceled(true);

		if(state != null && state.getElementShieldMana() > 0)
		{
			if(event.getType().equals(LibBuff.PARALYSIS) && !state.getElement().getType().equals(LibElements.ARC))
				event.setCanceled(true);

			if((event.getType().equals(LibBuff.CRIPPLE) || event.getType().equals(LibBuff.WITHER)) && !state.getElement().getType().equals(LibElements.WITHER))
				event.setCanceled(true);

			if((event.getType().equals(LibBuff.FRAGILE) || event.getType().equals(LibBuff.WEAKEN)) && !state.getElement().getType().equals(LibElements.VOID))
				event.setCanceled(true);

			if((event.getType().equals(LibBuff.SLOW) || event.getType().equals(LibBuff.FREEZE)) && !state.getElement().getType().equals(LibElements.STASIS))
				event.setCanceled(true);

			if(event.getType().equals(LibBuff.TAKEN) && !state.getElement().getType().equals(LibElements.TAKEN))
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onElementShield(LivingAttackEvent event)
	{
		IEntityState state = event.getEntityLiving().getCapability(MagickCore.entityState).orElse(null);
		if(state != null && state.getBuffList().containsKey(LibBuff.HYPERMUTEKI))
			event.setCanceled(true);

		if(state != null)
		{
			if(event.getEntityLiving().hurtResistantTime > 0.0f && state.getElementShieldMana() > 0.0f)
				event.setCanceled(true);

			if(event.getSource().getDamageType().equals(state.getElement().getAbility().getDamageSource().getDamageType())) {
				float damage = event.getAmount() * 2;
				HandleElementShield(state, damage, event);
			}
			else
			{
				float damage = Math.min(event.getAmount(), 4.0f);
				if(state.getElement().getType().equals(LibElements.ORIGIN))
					damage = event.getAmount();
				HandleElementShield(state, damage, event);
			}
		}
	}

	public static void HandleElementShield(IEntityState state, float damage, LivingAttackEvent event)
	{
		if(state.getElementShieldMana() >= damage)
		{
			state.hitElementShield();
			state.setElementShieldMana(state.getElementShieldMana() - damage);
			event.getEntityLiving().hurtResistantTime = 20;
			event.setCanceled(true);
		}
		else if(state.getElementShieldMana() > 0.0f)
		{
			float amount = damage - state.getElementShieldMana();
			state.hitElementShield();
			state.setElementShieldMana(0.0f);
			event.getEntityLiving().attackEntityFrom(event.getSource(), amount);
			event.getEntityLiving().hurtResistantTime = 20;
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void EntityStateUpdate(EntityEvents.EntityUpdateEvent event)
	{
		if(event.getEntity() instanceof LivingEntity && Float.isNaN(((LivingEntity)event.getEntity()).getHealth()))
			((LivingEntity)event.getEntity()).setHealth(0.0f);

		IEntityState state = event.getEntity().getCapability(MagickCore.entityState).orElse(null);
		if(state != null)
		{
			if(state.getElementShieldMana() < 0.0f)
				state.setElementShieldMana(0.0f);

			if(state.getMaxManaValue() < 5000.0f)
				state.setMaxManaValue(5000.0f);

			if(event.getEntity() instanceof LivingEntity && Float.isNaN(((LivingEntity)event.getEntity()).getHealth()))
				((LivingEntity)event.getEntity()).setHealth(0.0f);

			CompoundNBT effect_tick = new CompoundNBT();
			Iterator<String> i = state.getBuffList().keySet().iterator();
			while(i.hasNext()) {
				ManaBuff buff = state.getBuffList().get(i.next());
				effect_tick.putInt(buff.getType(), buff.getTick());
			}

			CompoundNBT effect_force = new CompoundNBT();
			Iterator<String> a = state.getBuffList().keySet().iterator();
			while(a.hasNext()) {
				ManaBuff buff = state.getBuffList().get(a.next());
				effect_force.putFloat(buff.getType(), buff.getForce());
			}

			state.tick(event.getEntity());
			if(!event.getEntity().world.isRemote && !event.getEntity().removed)
			Networking.INSTANCE.send(
					PacketDistributor.TRACKING_ENTITY_AND_SELF.with(event::getEntity),
					new EntityStatePack(event.getEntity().getEntityId(), state.getElement().getType(), state.getElementShieldMana(), state.getManaValue()
							, state.getMaxElementShieldMana(), state.getMaxManaValue(), effect_tick, effect_force));
		}

		IManaData data = event.getEntity().getCapability(MagickCore.manaData).orElse(null);
		if(event.getEntity().getCapability(MagickCore.manaData).orElse(null) != null)
		{
			if(!event.getEntity().world.isRemote && !event.getEntity().removed)
			Networking.INSTANCE.send(
					PacketDistributor.TRACKING_ENTITY_AND_SELF.with(event::getEntity),
					new ManaDataPack(event.getEntity().getEntityId(), data.getElement().getType(), data.getTargetType().getLabel()
							, data.getManaType().getLabel(), data.getRange(), data.getForce()
							, data.getTraceTarget(), data.getTickTime()));
		}

		IElementAnimalState animalState = event.getEntity().getCapability(MagickCore.elementAnimal).orElse(null);
		if(animalState != null)
		{
			if(!event.getEntity().world.isRemote && !event.getEntity().removed)
				Networking.INSTANCE.send(
						PacketDistributor.TRACKING_ENTITY_AND_SELF.with(event::getEntity),
						new ElementAnimalPack(event.getEntity().getEntityId(), animalState.getElement().getType()));
		}
	}

	@SubscribeEvent
	public void playerClone(PlayerEvent.Clone event) {
		IEntityState old = event.getOriginal().getCapability(MagickCore.entityState).orElse(null);
		IEntityState state = event.getPlayer().getCapability(MagickCore.entityState).orElse(null);

		state.setElement(old.getElement());
		state.setMaxManaValue(old.getMaxManaValue());
		state.setMaxElementShieldMana(old.getMaxElementShieldMana());

		if(!event.isWasDeath())
		{
			state.setManaValue(old.getManaValue());
			state.setElementShieldMana(old.getElementShieldMana());
			Iterator it = old.getBuffList().keySet().iterator();
			while (it.hasNext())
			{
				ManaBuff buff = old.getBuffList().get(it.next());
				ModBuff.applyBuff(event.getPlayer(), buff.getType(), buff.getTick(), buff.getForce(), true);
			}
		}
	}

	@SubscribeEvent
	public void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();

		if(entity instanceof LivingEntity || entity instanceof IManaMob)
		{
			if(!event.getCapabilities().containsKey(new ResourceLocation(MagickCore.MOD_ID, "capability_entity_state")));
				event.addCapability(new ResourceLocation(MagickCore.MOD_ID, "capability_entity_state"), new CapabilityEntityState.EntityStateProvider());
		}

		if(entity instanceof IMagickElementObject && !event.getCapabilities().containsKey(new ResourceLocation(MagickCore.MOD_ID, "capability_mana_data")))
			event.addCapability(new ResourceLocation(MagickCore.MOD_ID, "capability_mana_data"), new CapabilityManaData.ManaDataProvider());

		if(ElementOrbEvent.containAnimalType(entity.getType()))
		{
			if(!event.getCapabilities().containsKey(new ResourceLocation(MagickCore.MOD_ID, "capability_element_animal")));
			event.addCapability(new ResourceLocation(MagickCore.MOD_ID, "capability_element_animal"), new CapabilityElementAnimalState.ElementAnimalStateProvider());
		}
	}

	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent<ItemStack> event) {
		ItemStack item = event.getObject();

		if(item.getItem() instanceof IManaItem && !event.getCapabilities().containsKey(new ResourceLocation(MagickCore.MOD_ID, "capability_mana_item_data")))
			event.addCapability(new ResourceLocation(MagickCore.MOD_ID, "capability_mana_item_data"), new CapabilityManaItemData.ManaItemDataProvider());
	}
}
