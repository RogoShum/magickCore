package com.rogoshum.magickcore.common.event.magickevent;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.api.itemstack.IManaData;
import com.rogoshum.magickcore.common.api.mana.IManaCapacity;
import com.rogoshum.magickcore.common.api.mana.ISpellContext;
import com.rogoshum.magickcore.common.api.entity.IExistTick;
import com.rogoshum.magickcore.common.api.entity.IOwnerEntity;
import com.rogoshum.magickcore.common.api.entity.ISuperEntity;
import com.rogoshum.magickcore.common.api.event.EntityEvents;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.client.vertex.VertexShakerHelper;
import com.rogoshum.magickcore.common.entity.projectile.*;
import com.rogoshum.magickcore.common.event.RegisterEvent;
import com.rogoshum.magickcore.common.init.ModBuff;
import com.rogoshum.magickcore.common.init.ModEffects;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModRecipes;
import com.rogoshum.magickcore.common.extradata.entity.ElementToolData;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.common.entity.living.MageVillagerEntity;
import com.rogoshum.magickcore.client.event.RenderEvent;
import com.rogoshum.magickcore.common.network.*;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickPoint;
import com.rogoshum.magickcore.common.magick.ManaCapacity;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.MultiReleaseContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.ManaItemEntity;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.LootUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

public class MagickLogicEvent {
	private static final List<Entity> timeLords = new ArrayList<Entity>();
	@SubscribeEvent
	public void updateLightSource(TickEvent.ServerTickEvent event) {
		if(event.phase == TickEvent.Phase.END) {
			EntityLightSourceManager.tick(event.side);
			//MagickCore.proxy.tick(LogicalSide.SERVER);
		}
	}

	@SubscribeEvent
	public void updateLightSource(TickEvent.ClientTickEvent event) {
		if(!Minecraft.getInstance().isGamePaused() && event.phase == TickEvent.Phase.END) {
			MagickCore.proxy.addAdditionTask(() -> {
				if(RenderHelper.getPlayer() == null) {
					EntityLightSourceManager.clear();
					RenderEvent.clearParticle();
					VertexShakerHelper.clear();
				}

				VertexShakerHelper.tickGroup();
				RenderEvent.tickParticle();
				MagickPoint.points.forEach(MagickPoint::tick);
			}, () -> {
				EntityLightSourceManager.clear();
				RenderEvent.clearParticle();
				VertexShakerHelper.clear();
			});
			MagickCore.proxy.tick(LogicalSide.CLIENT);
			EntityLightSourceManager.tick(LogicalSide.CLIENT);
		}
	}

	@SubscribeEvent
	public void onExplosion(ExplosionEvent.Detonate event) {
		if(event.getWorld().isRemote()) return;
		List<Entity> list = event.getAffectedEntities();
		for (int i = 0; i < list.size(); ++i) {
			Entity entity = list.get(i);
			if(entity instanceof ItemEntity) {
				ItemStack originStack = ((ItemEntity)entity).getItem();
				ItemStack output = ModRecipes.findExplosionOutput(originStack).copy();
				if(output != ItemStack.EMPTY) {
					output.setCount(originStack.getCount());
					ManaItemEntity mana = new ManaItemEntity(event.getWorld(), entity.getPosX(), entity.getPosY(), entity.getPosZ(), output);
					if(event.getWorld().addEntity(mana))
						entity.remove();
				}
			}
		}
	}

	@SubscribeEvent
	public void onStateCooldown(EntityEvents.StateCooldownEvent event) {
	}

	@SubscribeEvent
	public void onInvisibility(LivingEvent.LivingVisibilityEvent event) {
		ExtraDataUtil.entityStateData(event.getEntityLiving(), state -> {
			if(state.getBuffList().containsKey(LibBuff.INVISIBILITY))
				event.modifyVisibility(0);
		});
	}

	@SubscribeEvent
	public void firstTimeJoinsWorld(PlayerEvent.PlayerLoggedInEvent event) {
		if(event.getEntity() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)event.getEntity();
			if(!player.getPersistentData().contains(PlayerEntity.PERSISTED_NBT_TAG))
				player.getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());

			CompoundNBT PERSISTED_NBT_TAG = player.getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
			if(!PERSISTED_NBT_TAG.contains("MAGICKCORE_FIRST")) {
				Item book = ForgeRegistries.ITEMS.getValue(new ResourceLocation("patchouli:guide_book"));
				if(book != null) {
					ItemStack stack = new ItemStack(book);
					NBTTagHelper.getStackTag(stack).putString("patchouli:book", "magickcore:magickcore");
					player.inventory.addItemStackToInventory(stack);
					PERSISTED_NBT_TAG.putBoolean("MAGICKCORE_FIRST", true);
				}
				int lucky = 1;
				while (MagickCore.rand.nextBoolean())
					lucky++;

				ItemStack staff = LootUtil.createRandomItemByLucky(lucky);
				while (!(staff.getItem() instanceof IManaData)) {
					staff = LootUtil.createRandomItemByLucky(lucky);
				}
				ItemManaData data = ExtraDataUtil.itemManaData(staff);
				data.spellContext().applyType = ApplyType.DE_BUFF;
				while (data.spellContext().element == ModElements.ORIGIN) {
					data.spellContext().element = MagickRegistry.getRandomElement();
				}
				data.manaCapacity().setMana(data.manaCapacity().getMaxMana());
				player.inventory.addItemStackToInventory(staff);
			}
		}
	}

	@SubscribeEvent
	public void onLiftClick(LivingAttackEvent event) {
		if(event.getSource().getTrueSource() instanceof LivingEntity)
			event.getSource().getTrueSource().getEquipmentAndArmor().forEach((s) -> NBTTagHelper.consumeElementOnTool(s, LibElements.VOID));
	}

	@SubscribeEvent
	public void voidElement(ItemAttributeModifierEvent event) {
		if(event.getSlotType() == EquipmentSlotType.MAINHAND) {
			if(NBTTagHelper.hasElementOnTool(event.getItemStack(), LibElements.VOID)) {
				CompoundNBT tag = NBTTagHelper.getStackTag(event.getItemStack());
				event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785CCCC"), () -> "Weapon modifier", Math.pow(1.1, tag.getInt("VOID_LEVEL") * 6), AttributeModifier.Operation.ADDITION));
			}
		}
	}

	@SubscribeEvent
	public void onProjectileCreate(EntityEvents.EntityVelocity event) {
		if(event.getEntity() instanceof LampEntity) {
			event.setVelocity(0.2f);
			event.setInaccuracy(((LampEntity) event.getEntity()).spellContext().range);
		}

		if(event.getEntity() instanceof ManaArrowEntity) {
			event.setVelocity(0.5f);
			event.setInaccuracy(0);
		}

		if(event.getEntity() instanceof RayEntity) {
			RayEntity ray = (RayEntity) event.getEntity();
			float velocity = ray.spellContext().force * 0.5f - ray.spellContext().range * 0.5f;
			event.setVelocity(0.3f + Math.max(velocity, 0));
			event.setInaccuracy(velocity);
		}

		if(event.getEntity() instanceof ShadowEntity) {
			event.setVelocity(0.3f);
		}

		if(event.getEntity() instanceof WindEntity) {
			event.setVelocity(0.65f);
		}
	}

	@SubscribeEvent
	public void onMagickRelease(EntityEvents.MagickReleaseEvent event) {
		if(event.getContext().containChild(LibContext.SPAWN)) {
			SpawnContext spawnContext = event.getContext().getChild(LibContext.SPAWN);
			Entity spawnEntity = spawnContext.entityType.create(event.getContext().world);
			if(spawnEntity instanceof ISuperEntity) {
				event.getContext().tick((int) (event.getContext().tick * 0.25));
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void preMagickRelease(EntityEvents.MagickPreReleaseEvent event) {
		if(!(event.getEntity() instanceof LivingEntity))
			return;
		if(event.getContext().containChild(LibContext.POSITION)) {
			PositionContext positionContext = event.getContext().getChild(LibContext.POSITION);
			event.setMana((float) (event.getMana() + positionContext.pos.distanceTo(event.getEntity().getPositionVec())));
		}
		if(!event.getContext().containChild(LibContext.MULTI_RELEASE))
			if(((LivingEntity)event.getEntity()).getActivePotionMap().containsKey(ModEffects.MULTI_RELEASE.orElse(null))) {
				int amplifier = ((LivingEntity)event.getEntity()).getActivePotionEffect(ModEffects.MULTI_RELEASE.orElse(null)).getAmplifier() + 1;
				event.getContext().addChild(MultiReleaseContext.create());
				for (int i = 0; i < amplifier; ++i) {
					MagickContext copy = MagickContext.create(event.getContext().world, event.getContext());
					copy.caster(event.getContext().caster).victim(event.getContext().victim).projectile(event.getContext().projectile);
					if(event.getContext().noCost)
						copy.noCost();

					MagickReleaseHelper.releaseMagick(copy);
				}
			}

		if(((LivingEntity)event.getEntity()).getActivePotionMap().containsKey(ModEffects.MANA_FORCE.orElse(null))) {
			int amplifier = ((LivingEntity)event.getEntity()).getActivePotionEffect(ModEffects.MANA_FORCE.orElse(null)).getAmplifier() + 1;
			event.getContext().force((float) (event.getContext().force * Math.pow(1.3f, amplifier)));
		}

		if(((LivingEntity)event.getEntity()).getActivePotionMap().containsKey(ModEffects.MANA_RANGE.orElse(null))) {
			int amplifier = ((LivingEntity)event.getEntity()).getActivePotionEffect(ModEffects.MANA_RANGE.orElse(null)).getAmplifier() + 1;
			event.getContext().force((float) (event.getContext().range * Math.pow(1.4f, amplifier)));
		}

		if(((LivingEntity)event.getEntity()).getActivePotionMap().containsKey(ModEffects.MANA_TICK.orElse(null))) {
			int amplifier = ((LivingEntity)event.getEntity()).getActivePotionEffect(ModEffects.MANA_TICK.orElse(null)).getAmplifier() + 1;
			event.getContext().force((float) (event.getContext().tick * Math.pow(1.5f, amplifier)));
		}

		if(((LivingEntity)event.getEntity()).getActivePotionMap().containsKey(ModEffects.TRACE.orElse(null))
			&& !event.getContext().containChild(LibContext.TRACE)) {
			Entity entity = MagickReleaseHelper.getEntityLookedAt(event.getEntity());
			if(entity != null)
				event.getContext().addChild(TraceContext.create(entity));
			else
				event.getContext().addChild(new TraceContext());
		}
		
		if(((LivingEntity)event.getEntity()).getActivePotionMap().containsKey(ModEffects.MANA_CONSUME_REDUCE.orElse(null))) {
			float amplifier = ((LivingEntity)event.getEntity()).getActivePotionEffect(ModEffects.MANA_CONSUME_REDUCE.orElse(null)).getAmplifier() + 1;
			event.setMana((float) (event.getMana() * Math.pow(0.8f, amplifier)));
		}

		float level = 0;
		for (ItemStack stack : event.getEntity().getEquipmentAndArmor()) {
			if(NBTTagHelper.hasElementOnTool(stack, LibElements.ORIGIN)) {
				level += 1;
			}
		}
		event.setMana((float) (event.getMana() * Math.pow(0.9f, level)));

		EntityStateData state = ExtraDataUtil.entityStateData(event.getEntity());
		if(event.getMana() <= 0 || event.getContext().noCost)
			state = null;
		if(event.getEntity() instanceof PlayerEntity && ((PlayerEntity) event.getEntity()).isCreative())
			state = null;
		if(state != null) {
			if(state.getBuffList().containsKey(LibBuff.FREEZE)) {
				event.setCanceled(true);
				return;
			}

			boolean flag = false;

			ItemStack stack = ((LivingEntity) event.getEntity()).getActiveItemStack();
			ItemManaData item = ExtraDataUtil.itemManaData(stack);
			if(item.manaCapacity().getMana() >= event.getMana()) {
				item.manaCapacity().extractMana(event.getMana());
				flag = true;
			}
			float extraMana = item.manaCapacity().getMana();

			if(!flag) {
				if (state.getManaValue() + extraMana >= event.getMana()) {
					state.setManaValue(state.getManaValue() + extraMana - event.getMana());
					if(extraMana > 0) {
						item.manaCapacity().setMana(0);
						item.spellContext().element(MagickRegistry.getElement(LibElements.ORIGIN));
					}
				}
				else
					event.setCanceled(true);
			}
		}
	}


	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onShieldCapacity(EntityEvents.ShieldCapacityEvent event) {
		int mana = 0;
		if(event.getEntityLiving().getActivePotionMap().containsKey(ModEffects.SHIELD_VALUE.orElse(null)))
			mana += 25 * (event.getEntityLiving().getActivePotionEffect(ModEffects.SHIELD_VALUE.orElse(null)).getAmplifier() + 1);

		event.setCapacity(event.getCapacity() + mana);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onShieldRegen(EntityEvents.ShieldRegenerationEvent event) {
		int mana = 0;
		if(event.getEntityLiving().getActivePotionMap().containsKey(ModEffects.SHIELD_REGEN.orElse(null)))
			mana = (event.getEntityLiving().getActivePotionEffect(ModEffects.SHIELD_REGEN.orElse(null)).getAmplifier() + 1);

		event.setAmount(event.getAmount() + mana);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onManaRegen(EntityEvents.ManaRegenerationEvent event) {
		float mana = 0;
		if(event.getEntityLiving().getActivePotionMap().containsKey(ModEffects.MANA_REGEN.orElse(null)))
			mana = (event.getEntityLiving().getActivePotionEffect(ModEffects.MANA_REGEN.orElse(null)).getAmplifier() + 1);

		for (ItemStack stack : event.getEntity().getEquipmentAndArmor()) {
			if(NBTTagHelper.hasElementOnTool(stack, LibElements.ORIGIN)) {
				if(event.getEntityLiving().ticksExisted % 20 == 0)
					NBTTagHelper.consumeElementOnTool(stack, LibElements.ORIGIN);
				mana += 0.25;
			}
		}

		if(event.getEntityLiving().getActivePotionMap().containsKey(ModEffects.MANA_STASIS.orElse(null)))
			mana -= (event.getEntityLiving().getActivePotionEffect(ModEffects.MANA_STASIS.orElse(null)).getAmplifier() + 1);

		event.setMana(event.getMana() + mana);
	}

	@SubscribeEvent
	public void ManaObjectUpdateEvent(EntityEvents.EntityUpdateEvent event) {
		if(event.getEntity() instanceof ISpellContext) {
			SpellContext state = ((ISpellContext) event.getEntity()).spellContext();
			if(state != null) {
				if(!event.getEntity().world.isRemote && event.getEntity().ticksExisted == state.tick - 5) {
					event.getEntity().playSound(SoundEvents.UI_TOAST_OUT, 3.0F, (1.0F + MagickCore.rand.nextFloat()));
				}

				int ticksExisted = event.getEntity().ticksExisted;
				if(event.getEntity() instanceof IExistTick) {
					IExistTick existTick = (IExistTick) event.getEntity();
					ticksExisted -= existTick.getTickThatNeedExistingBeforeRemove();
				}
				if(ticksExisted > state.tick && state.tick >= 0)
					event.getEntity().remove();
			}
		}
		if(!event.getEntity().world.isRemote() && event.getEntity() instanceof MageVillagerEntity) {
			if(!((MageVillagerEntity) event.getEntity()).getBrain().hasActivity(Activity.FIGHT)) {
				((MageVillagerEntity) event.getEntity()).resetBrain((ServerWorld) event.getEntity().world);
			}
		}
	}

	@SubscribeEvent
	public void HitEntity(EntityEvents.HitEntityEvent event) {
		if(event.getEntity() instanceof ISpellContext) {
			SpellContext mana = ((ISpellContext) event.getEntity()).spellContext();
			MagickContext attribute = new MagickContext(event.getEntity().world).noCost().caster(event.getEntity()).projectile(event.getEntity()).victim(event.getVictim()).tick(mana.tick).force(mana.force).applyType(ApplyType.HIT_ENTITY);
			if(event.getEntity() instanceof IOwnerEntity)
				attribute = new MagickContext(event.getEntity().world).noCost().caster(((IOwnerEntity) event.getEntity()).getOwner()).projectile(event.getEntity()).victim(event.getVictim()).tick(mana.tick).force(mana.force).applyType(ApplyType.HIT_ENTITY);
			MagickReleaseHelper.releaseMagick(attribute);
		}
	}

	@SubscribeEvent
	public void onDeBuff(LivingEvent.LivingUpdateEvent event) {
		EntityStateData state = ExtraDataUtil.entityStateData(event.getEntityLiving());
		if(event.getEntityLiving() instanceof MobEntity && state != null && state.allowElement())
			RegisterEvent.testIfElement(event.getEntityLiving());

		if(state != null && state.getBuffList().containsKey(LibBuff.PURE)) {
			float force = state.getBuffList().get(LibBuff.PURE).getForce() * 0.5f;
			List<Entity> entityList = event.getEntityLiving().world.getEntitiesWithinAABBExcludingEntity(event.getEntityLiving(), event.getEntityLiving().getBoundingBox().grow(force, force, force));

			for(int i = 0; i< entityList.size(); ++i) {
				Entity entity = entityList.get(i);
				if(entity instanceof ProjectileEntity) {
					double factor = entity.getMotion().normalize().dotProduct(event.getEntity().getPositionVec().add(0, event.getEntity().getHeight() * 0.5, 0).subtract(entity.getPositionVec().add(0, entity.getHeight() * 0.5, 0)).normalize());
					if(factor > 0.8) {
						Vector3d motion = entity.getMotion();
						entity.addVelocity(-motion.x, -motion.y, -motion.z);
					}
				}
			}
		}

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
			//event.getEntityLiving().setSilent(true);
			if(event.getEntityLiving().getHealth() > 0)
				event.setCanceled(true);
		}

		if(state != null && state.getBuffList().containsKey(LibBuff.CRIPPLE)) {
			float force = state.getBuffList().get(LibBuff.CRIPPLE).getForce();
			float maxHealth = event.getEntityLiving().getMaxHealth() - (event.getEntityLiving().getMaxHealth() * force * 0.05f);
			if(event.getEntityLiving().getHealth() > maxHealth)
				event.getEntityLiving().setHealth(maxHealth);
		}

		if(state != null && state.getBuffList().containsKey(LibBuff.STASIS)) {
			float force = state.getBuffList().get(LibBuff.STASIS).getForce();
			List<Entity> entityList = event.getEntityLiving().world.getEntitiesWithinAABBExcludingEntity(event.getEntityLiving(), event.getEntityLiving().getBoundingBox().grow(force, force, force));

			for(int i = 0; i< entityList.size(); ++i) {
				ModBuff.applyBuff(entityList.get(i), LibBuff.SLOW, 20, force, true);
			}
		}
	}

	@SubscribeEvent
	public void onRemoveBuff(LivingDeathEvent event) {
		if(event.getSource().getTrueSource() instanceof LivingEntity) {
			ElementToolData tool = ExtraDataUtil.elementToolData(event.getSource().getTrueSource());
			if (tool != null) {
				tool.setAdditionDamage(200);
				tool.consumeElementOnTool((LivingEntity) event.getSource().getTrueSource(), LibElements.SOLAR);
			}
		}
	}

	@SubscribeEvent
	public void onWitherBuff(LivingHealEvent event) {
		AtomicReference<EntityStateData> ref = new AtomicReference<>();
		ExtraDataUtil.entityStateData(event.getEntityLiving(), ref::set);
		EntityStateData state = ref.get();
		if(state != null && state.getBuffList().containsKey(LibBuff.WITHER))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onWeakenBuff(LivingHurtEvent event) {
		AtomicReference<EntityStateData> ref = new AtomicReference<>();
		ExtraDataUtil.entityStateData(event.getEntityLiving(), ref::set);
		EntityStateData state = ref.get();
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof LivingEntity) {
			ExtraDataUtil.entityStateData(entity, ref::set);
			EntityStateData attacker = ref.get();
			if(attacker != null && attacker.getBuffList().containsKey(LibBuff.LIGHT))
				event.setAmount(event.getAmount() * 1.5f);
		}

		if(state != null) {
			state.hitElementShield();
			if(((LivingEntity)event.getEntity()).getActivePotionMap().containsKey(ModEffects.MANA_CONVERT.orElse(null))) {
				int amplifier = ((LivingEntity)event.getEntity()).getActivePotionEffect(ModEffects.MANA_CONVERT.orElse(null)).getAmplifier() + 1;
				state.setManaValue(state.getManaValue() + event.getAmount() * amplifier * 40);
			}
		}

		if(state != null && state.getBuffList().containsKey(LibBuff.HYPERMUTEKI))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onLightning(EntityStruckByLightningEvent event) {
		AtomicReference<EntityStateData> ref = new AtomicReference<>();
		ExtraDataUtil.entityStateData(event.getEntity(), ref::set);
		EntityStateData state = ref.get();
		if(state != null && !(event.getEntity() instanceof PlayerEntity)) {
			if(state.getElement().type().equals(LibElements.ORIGIN)) {
				state.setElement(MagickRegistry.getElement(LibElements.ARC));
				state.setElementShieldMana(50);
				state.setFinalMaxElementShield(50);
				state.setMaxManaValue(50f);
			}
		}
	}

	@SubscribeEvent
	public void onDamage(LivingDamageEvent event) {
		EntityStateData state = ExtraDataUtil.entityStateData(event.getEntityLiving());
		if(state != null && state.getBuffList().containsKey(LibBuff.HYPERMUTEKI))
			event.setCanceled(true);

		if(state != null && state.getBuffList().containsKey(LibBuff.WEAKEN))
			event.setAmount(event.getAmount() * 1.3f);

		Entity entity = event.getSource().getTrueSource();

		if(entity instanceof LivingEntity || entity instanceof IMob) {
			ElementToolData tool = ExtraDataUtil.elementToolData(entity);

			if (tool != null) {
				event.setAmount(tool.applyAdditionDamage(event.getAmount()));
			}

			if(!(entity instanceof PlayerEntity)) {
				EntityStateData attacker = ExtraDataUtil.entityStateData(entity);
				float manaNeed = event.getAmount();

				if (attacker != null && !attacker.getElement().type().equals(LibElements.ORIGIN)) {
					if(event.getEntityLiving() instanceof ServerPlayerEntity)
						AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) event.getEntityLiving(), LibAdvancements.ELEMENT_CREATURE);
					if (attacker.getElementShieldMana() > 0) {
						MagickContext attribute = new MagickContext(entity.world).noCost().caster(entity).projectile(event.getSource().getImmediateSource()).victim(event.getEntityLiving()).applyType(ApplyType.DE_BUFF).tick((int) manaNeed * 40).force(manaNeed);
						MagickReleaseHelper.releaseMagick(attribute);
					}
				}
			}

			TakenEntityData taken = ExtraDataUtil.takenEntityData(event.getSource().getTrueSource());
			if(taken != null && !taken.getOwnerUUID().equals(MagickCore.emptyUUID) && taken.getTime() > 0 && ModBuff.hasBuff(event.getSource().getTrueSource(), LibBuff.TAKEN_KING)) {
				event.setAmount(event.getAmount() * 1.5f);
			}
		}
	}

	@SubscribeEvent
	public void onApplyManaBuff(EntityEvents.ApplyManaBuffEvent event) {
		AtomicReference<EntityStateData> ref = new AtomicReference<>();
		ExtraDataUtil.entityStateData(event.getEntityLiving(), ref::set);
		EntityStateData state = ref.get();
		if(state != null && state.getBuffList().containsKey(LibBuff.HYPERMUTEKI) && !event.getBeneficial())
			event.setCanceled(true);

		if(state != null && state.getElementShieldMana() > 0) {
			if(!event.getType().getElement().equals(state.getElement().type()))
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onElementShield(LivingAttackEvent event) {
		AtomicReference<EntityStateData> ref = new AtomicReference<>();
		ExtraDataUtil.entityStateData(event.getEntityLiving(), ref::set);
		EntityStateData state = ref.get();
		if(state != null && state.getBuffList().containsKey(LibBuff.HYPERMUTEKI))
			event.setCanceled(true);

		if(state != null) {
			if(event.getEntityLiving().hurtResistantTime > 0.0f && state.getElementShieldMana() > 0.0f)
				event.setCanceled(true);

			String shieldElement = state.getElement().damageType().getDamageType();
			String damageType = event.getSource().getDamageType();
			boolean matchMeleeType = false;
			if(event.getSource().getTrueSource() instanceof LivingEntity)
				matchMeleeType = NBTTagHelper.hasElementOnTool(((LivingEntity) event.getSource().getTrueSource()).getHeldItemMainhand(), state.getElement().type());

			float damage;

			if(shieldElement.equals(damageType)) {
				damage = event.getAmount() * 2f;
			}
			else if(matchMeleeType) {
				damage = event.getAmount() * 2.25f;
			}
			else if(state.getElement().type().equals(LibElements.ORIGIN))
				damage = event.getAmount();
			else {
				damage = Math.min(event.getAmount(), 5.0f);
			}
			HandleElementShield(state, damage, event);
		}

		if(event.getSource().getTrueSource() instanceof LivingEntity) {
			for (ItemStack stack : event.getSource().getTrueSource().getEquipmentAndArmor()) {
				float chance = 0;
				if (stack != null && NBTTagHelper.hasElementOnTool(stack, LibElements.TAKEN)) {
					chance+=0.05;
					chance*=1.25;
				}
				if(MagickCore.rand.nextFloat() < chance)
					ModBuff.applyBuff(event.getEntityLiving(), LibBuff.TAKEN, 100, 1, true);
			}
		}
	}

	public static void HandleElementShield(EntityStateData state, float damage, LivingAttackEvent event) {
		if(state.getElementShieldMana() >= damage) {
			state.hitElementShield();
			state.setElementShieldMana(state.getElementShieldMana() - damage);
			if(damage > 0.0f && event.getEntityLiving().hurtResistantTime <= 10)
				spawnParticle(state.getElement().type(), event.getEntity());
			event.getEntityLiving().playSound(SoundEvents.BLOCK_SNOW_BREAK, 1.0f, 0.0f);
			event.getEntityLiving().hurtResistantTime = 20;
			event.setCanceled(true);
		} else if(state.getElementShieldMana() > 0.0f) {
			float amount = damage - state.getElementShieldMana();
			state.hitElementShield();
			state.setElementShieldMana(0.0f);
			event.getEntityLiving().playSound(SoundEvents.BLOCK_GLASS_BREAK, 0.7f, 0.0f);
			event.getEntityLiving().attackEntityFrom(event.getSource(), amount);
			event.getEntityLiving().hurtResistantTime = 20;
			event.setCanceled(true);
			if(damage > 0.0f)
				spawnParticle(state.getElement().type(), event.getEntity());
		}
	}

	public static void spawnParticle(String element, Entity entity) {
		World world = entity.world;
		if(!world.isRemote) return;
		ElementRenderer render = MagickCore.proxy.getElementRender(element);

		for(int i = 0; i < 20; ++i) {
			LitParticle litPar = new LitParticle(world, render.getParticleTexture()
					, new Vector3d(MagickCore.getNegativeToOne() * entity.getWidth() / 2f + entity.getPosX()
					, MagickCore.getNegativeToOne() / 2f + entity.getPosY() + entity.getHeight() / 2
					, MagickCore.getNegativeToOne() * entity.getWidth() / 2f + entity.getPosZ())
					, entity.getWidth() / 5f, entity.getWidth() / 5f, 0.8f * MagickCore.rand.nextFloat(), 20, render);
			litPar.setGlow();
			litPar.addMotion(MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10);
			MagickCore.addMagickParticle(litPar);
		}
	}

	@SubscribeEvent
	public void EntityStateUpdate(EntityEvents.EntityUpdateEvent event) {
		if(event.getEntity() instanceof LivingEntity && Float.isNaN(((LivingEntity)event.getEntity()).getHealth()))
			((LivingEntity)event.getEntity()).setHealth(0.0f);

		if(event.getEntity() instanceof LivingEntity) {
			AtomicReference<ElementToolData> ref = new AtomicReference<>();
			ExtraDataUtil.elementToolData(event.getEntity(), ref::set);
			ElementToolData tool = ref.get();
			if (tool != null) {
				tool.tick((LivingEntity) event.getEntity());
			}
		}

		AtomicReference<TakenEntityData> ref = new AtomicReference<>();
		ExtraDataUtil.takenEntityData(event.getEntity(), ref::set);
		TakenEntityData takenState = ref.get();
		if(takenState != null && event.getEntity() instanceof MobEntity) {
			takenState.tick((MobEntity) event.getEntity());
			if(!event.getEntity().world.isRemote && !event.getEntity().removed) {
				if(event.getEntity().ticksExisted % 40 == 0 || takenState.getOwnerUUID() != MagickCore.emptyUUID)
					Networking.INSTANCE.send(
							PacketDistributor.TRACKING_ENTITY_AND_SELF.with(event::getEntity),
							new TakenStatePack(event.getEntity().getEntityId(), takenState.getTime(), takenState.getOwnerUUID()));
			}
		}

		if(event.getEntity() instanceof IOwnerEntity && ((IOwnerEntity) event.getEntity()).getOwner() != null && event.getEntity().ticksExisted % 40 == 0) {
			if(!event.getEntity().world.isRemote && !event.getEntity().removed)
				Networking.INSTANCE.send(
						PacketDistributor.TRACKING_ENTITY_AND_SELF.with(event::getEntity),
						new OwnerStatePack(event.getEntity().getEntityId(), ((IOwnerEntity) event.getEntity()).getOwner().getUniqueID()));
		}

		EntityStateData state = ExtraDataUtil.entityStateData(event.getEntity());
		if(state != null) {
			if(state.getElementShieldMana() < 0.0f)
				state.setElementShieldMana(0.0f);

			if(state.getMaxManaValue() < 50.0f)
				state.setMaxManaValue(50f);

			if(event.getEntity() instanceof LivingEntity && Float.isNaN(((LivingEntity)event.getEntity()).getHealth()))
				((LivingEntity)event.getEntity()).setHealth(0.0f);

			CompoundNBT effect_tick = new CompoundNBT();
			for (String s : state.getBuffList().keySet()) {
				ManaBuff buff = state.getBuffList().get(s);
				effect_tick.putInt(buff.getType(), buff.getTick());
			}

			CompoundNBT effect_force = new CompoundNBT();
			for (String s : state.getBuffList().keySet()) {
				ManaBuff buff = state.getBuffList().get(s);
				effect_force.putFloat(buff.getType(), buff.getForce());
			}

			state.tick(event.getEntity());

			for (ItemStack stack : event.getEntity().getEquipmentAndArmor()) {
				if(stack != null && NBTTagHelper.hasElementOnTool(stack, LibElements.ARC))
					state.tick(event.getEntity());
			}

			if(!event.getEntity().world.isRemote && !event.getEntity().removed && event.getEntity().ticksExisted % 40 == 0)
				Networking.INSTANCE.send(
						PacketDistributor.TRACKING_ENTITY_AND_SELF.with(event::getEntity),
						new EntityStatePack(event.getEntity().getEntityId(), state.getElement().type(), state.getElementShieldMana(), state.getManaValue()
								, state.getMaxElementShieldMana(), state.getMaxManaValue(), effect_tick, effect_force));
		}

		if(event.getEntity() instanceof IManaCapacity) {
			ManaCapacity data = ((IManaCapacity) event.getEntity()).manaCapacity();
			if(!event.getEntity().world.isRemote && !event.getEntity().removed && event.getEntity().ticksExisted % 10 == 0) {
				Networking.INSTANCE.send(
						PacketDistributor.TRACKING_ENTITY_AND_SELF.with(event::getEntity),
						new ManaCapacityPack(event.getEntity().getEntityId(), data));
			}
		}
	}

	@SubscribeEvent
	public void playerClone(PlayerEvent.Clone event) {
		AtomicReference<EntityStateData> ref = new AtomicReference<>();
		ExtraDataUtil.entityStateData(event.getOriginal(), ref::set);
		EntityStateData old = ref.get();
		ExtraDataUtil.entityStateData(event.getPlayer(), ref::set);
		EntityStateData state = ref.get();

		if(!event.isWasDeath()) {
			state.setMaxManaValue(old.getMaxManaValue() );
			state.setElement(old.getElement());
			state.setMaxElementShieldMana(old.getMaxElementShieldMana());
			state.setManaValue(old.getManaValue());
			state.setElementShieldMana(old.getElementShieldMana());
			for (String s : old.getBuffList().keySet()) {
				ManaBuff buff = old.getBuffList().get(s);
				ModBuff.applyBuff(event.getPlayer(), buff.getType(), buff.getTick(), buff.getForce(), true);
			}
		}
		else {
			state.setElement(old.getElement());
			state.setMaxManaValue(old.getMaxManaValue() * 0.95f);
			if(state.getMaxManaValue() <= 2500 && event.getPlayer() instanceof ServerPlayerEntity)
				AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) event.getPlayer(), "below2500");
		}
	}
}