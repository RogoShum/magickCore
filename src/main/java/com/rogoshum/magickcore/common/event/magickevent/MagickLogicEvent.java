package com.rogoshum.magickcore.common.event.magickevent;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.api.itemstack.IManaData;
import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.api.entity.IExistTick;
import com.rogoshum.magickcore.api.entity.IOwnerEntity;
import com.rogoshum.magickcore.api.entity.ISuperEntity;
import com.rogoshum.magickcore.api.event.EntityEvent;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.client.vertex.VertexShakerHelper;
import com.rogoshum.magickcore.common.entity.living.ArtificialLifeEntity;
import com.rogoshum.magickcore.common.entity.pointed.ChainEntity;
import com.rogoshum.magickcore.common.entity.pointed.RepeaterEntity;
import com.rogoshum.magickcore.common.entity.projectile.*;
import com.rogoshum.magickcore.common.init.*;
import com.rogoshum.magickcore.common.extradata.entity.ElementToolData;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.common.entity.living.MageVillagerEntity;
import com.rogoshum.magickcore.client.event.RenderEvent;
import com.rogoshum.magickcore.common.item.tool.SpiritSwordItem;
import com.rogoshum.magickcore.common.lib.*;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.child.*;
import com.rogoshum.magickcore.common.network.*;
import com.rogoshum.magickcore.common.magick.MagickPoint;
import com.rogoshum.magickcore.common.magick.ManaCapacity;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.LootUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import com.rogoshum.magickcore.common.event.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

public class MagickLogicEvent {
	private static final List<Entity> timeLords = new ArrayList<Entity>();
	@SubscribeEvent
	public void updateLightSource(TickEvent.ServerTickEvent event) {
		if(event.phase == TickEvent.Phase.END) {
			//EntityLightSourceManager.tick(event.side);
			//MagickCore.proxy.tick(LogicalSide.SERVER);
		}
	}

	@SubscribeEvent
	public void entityJoinLevel(EntityJoinLevelEvent evt) {
		/*
Entity entity = evt.getEntity();

		if (entity instanceof ManaRadiateEntity) {
			evt.setCanceled(true);
		}
		 */

	}

	@SubscribeEvent
	public void updateLightSource(TickEvent.ClientTickEvent event) {
		if(!Minecraft.getInstance().isPaused() && event.phase == TickEvent.Phase.END) {
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
			//EntityLightSourceManager.tick(LogicalSide.CLIENT);
		}
	}

	/*
	@SubscribeEvent
	public void onExplosion(ExplosionEvent.Detonate event) {
		if(event.getLevel().isRemote()) return;
		List<Entity> list = event.getAffectedEntities();
		for (int i = 0; i < list.size(); ++i) {
			Entity entity = list.get(i);
			if(entity instanceof ItemEntity) {
				ItemStack originStack = ((ItemEntity)entity).getItem();
				ItemStack output = ModRecipes.findExplosionOutput(originStack).copy();
				if(output != ItemStack.EMPTY) {
					output.setCount(originStack.getCount());
					ManaItemEntity mana = new ManaItemEntity(event.getLevel(), entity.getPosX(), entity.getPosY(), entity.getPosZ(), output);
					if(event.getLevel().addEntity(mana))
						entity.remove();
				}
			}
		}
	}

	 */

	@SubscribeEvent
	public void onStateCooldown(EntityEvent.StateCooldownEvent event) {
		if(event.getEntityLiving().getActiveEffectsMap().containsKey(ModEffects.SHIELD_REGEN.orElse(null)))
			event.setCooldown(event.getCooldown() - (event.getEntityLiving().getEffect(ModEffects.SHIELD_REGEN.orElse(null)).getAmplifier() + 1));
	}

	@SubscribeEvent
	public void onInvisibility(LivingEvent.LivingVisibilityEvent event) {
		ExtraDataUtil.entityStateData(event.getEntityLiving(), state -> {
			if(state.getBuffList().containsKey(LibBuff.INVISIBILITY))
				event.modifyVisibility(0);
		});
	}

	@SubscribeEvent
	public void firstTimeJoinsLevel(PlayerEvent.PlayerLoggedInEvent event) {
		if(event.getEntity() instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer)event.getEntity();
			if(!player.getPersistentData().contains(Player.PERSISTED_NBT_TAG))
				player.getPersistentData().put(Player.PERSISTED_NBT_TAG, new CompoundTag());

			CompoundTag PERSISTED_NBT_TAG = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
			if(!PERSISTED_NBT_TAG.contains("MAGICKCORE_FIRST")) {
				Item book = ForgeRegistries.ITEMS.getValue(new ResourceLocation("patchouli:guide_book"));
				if(book != null) {
					ItemStack stack = new ItemStack(book);
					NBTTagHelper.getStackTag(stack).putString("patchouli:book", "magickcore:magickcore");
					player.inventory.add(stack);
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
				player.inventory.add(staff);
			}
		}
	}

	@SubscribeEvent
	public void voidElement(ItemAttributeModifierEvent event) {
		if(event.getSlotType() == EquipmentSlotType.MAINHAND) {
			if(NBTTagHelper.hasElementOnTool(event.getItemStack(), LibElements.VOID)) {
				CompoundTag tag = NBTTagHelper.getStackTag(event.getItemStack());
				event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785CCCC"), () -> "Weapon modifier", Math.pow(1.1, tag.getInt("VOID_LEVEL") * 6), AttributeModifier.Operation.ADDITION));
			}
		}
	}

	@SubscribeEvent
	public void onProjectileCreate(EntityEvent.EntityVelocity event) {
		if(event.getEntity() instanceof LampEntity) {
			event.setVelocity(0.2f);
			event.setInaccuracy(((LampEntity) event.getEntity()).spellContext().range);
		}

		if(event.getEntity() instanceof ManaArrowEntity) {
			event.setVelocity(0.5f);
			event.setInaccuracy(0);
		}

		if(event.getEntity() instanceof JewelryBagEntity) {
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
	public void onMagickRelease(EntityEvent.MagickReleaseEvent event) {
		if(event.getContext().containChild(LibContext.SPAWN)) {
			SpawnContext spawnContext = event.getContext().getChild(LibContext.SPAWN);
			Entity spawnEntity = spawnContext.entityType.create(event.getContext().world);
			if(spawnEntity instanceof ISuperEntity) {
				event.getContext().tick((int) (event.getContext().tick * 0.25));
			}
		}

		if(event.getContext().projectile instanceof IManaEntity) {
			SpellContext spellContext = ((IManaEntity) event.getContext().projectile).spellContext();
			if(spellContext.containChild(LibContext.REMOVE_HURT_TIME) && event.getContext().victim != null) {
				event.getContext().victim.invulnerableTime = 0;
			}
			if(spellContext.containChild(LibContext.MANA_FACTOR)) {
				ManaFactor manaFactor = spellContext.<ExtraManaFactorContext>getChild(LibContext.MANA_FACTOR).manaFactor;
				event.getContext().force(manaFactor.force * event.getContext().force);
				event.getContext().range(manaFactor.range * event.getContext().range);
				event.getContext().tick((int) (manaFactor.tick * event.getContext().tick));
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void preMagickRelease(EntityEvent.MagickPreReleaseEvent event) {
		if(!(event.getEntity() instanceof LivingEntity))
			return;
		if(event.getContext().containChild(LibContext.SPAWN)) {
			EntityType<?> type = event.getContext().<SpawnContext>getChild(LibContext.SPAWN).entityType;
			if(ModEntities.REPEATER.get().equals(type))
				event.setMana(event.getMana() + 50);
		}
		if(event.getContext().containChild(LibContext.POSITION)) {
			PositionContext positionContext = event.getContext().getChild(LibContext.POSITION);
			event.setMana((float) (event.getMana() + positionContext.pos.distanceTo(event.getEntity().position())));
		}
		/*
		if(!event.getContext().containChild(LibContext.MULTI_RELEASE))
			if(((LivingEntity)event.getEntity()).getActivePotionMap().containsKey(ModEffects.MULTI_RELEASE.orElse(null))) {
				int amplifier = ((LivingEntity)event.getEntity()).getActivePotionEffect(ModEffects.MULTI_RELEASE.orElse(null)).getAmplifier() + 1;
				amplifier = Math.min(2, amplifier);
				event.getContext().addChild(MultiReleaseContext.create());
				for (int i = 0; i < amplifier; ++i) {
					MagickContext copy = MagickContext.create(event.getContext().world, event.getContext());
					copy.caster(event.getContext().caster).victim(event.getContext().victim).projectile(event.getContext().projectile);
					if(event.getContext().noCost)
						copy.noCost();

					MagickReleaseHelper.releaseMagick(copy);
				}
			}


		 */
		float level = 0;
		for (ItemStack stack : event.getEntity().getAllSlots()) {
			if(NBTTagHelper.hasElementOnTool(stack, LibElements.ORIGIN)) {
				level += 1;
			}
		}
		event.setMana((float) (event.getMana() * Math.pow(0.8f, level)));
		if(level > 0) {
			event.getContext().force((float) (event.getContext().force * Math.pow(1.05f, level)));
			event.getContext().range((float) (event.getContext().range * Math.pow(1.07f, level)));
			event.getContext().tick((int) (event.getContext().tick * Math.pow(1.1f, level)));
		}

		if(((LivingEntity)event.getEntity()).getActiveEffectsMap().containsKey(ModEffects.MANA_FORCE.orElse(null))) {
			int amplifier = ((LivingEntity)event.getEntity()).getEffect(ModEffects.MANA_FORCE.orElse(null)).getAmplifier() + 1;
			event.getContext().force((float) (event.getContext().force * Math.pow(1.3f, amplifier)));
		}

		if(((LivingEntity)event.getEntity()).getActiveEffectsMap().containsKey(ModEffects.MANA_RANGE.orElse(null))) {
			int amplifier = ((LivingEntity)event.getEntity()).getEffect(ModEffects.MANA_RANGE.orElse(null)).getAmplifier() + 1;
			event.getContext().range((float) (event.getContext().range * Math.pow(1.4f, amplifier)));
		}

		if(((LivingEntity)event.getEntity()).getActiveEffectsMap().containsKey(ModEffects.MANA_TICK.orElse(null))) {
			int amplifier = ((LivingEntity)event.getEntity()).getEffect(ModEffects.MANA_TICK.orElse(null)).getAmplifier() + 1;
			event.getContext().tick((int) (event.getContext().tick * Math.pow(1.5f, amplifier)));
		}

		if(((LivingEntity)event.getEntity()).getActiveEffectsMap().containsKey(ModEffects.TRACE.orElse(null))
			&& !event.getContext().containChild(LibContext.TRACE)) {
			Entity entity = MagickReleaseHelper.getEntityLookedAt(event.getEntity());
			if(entity != null)
				event.getContext().addChild(TraceContext.create(entity));
			else
				event.getContext().addChild(new TraceContext());
		}
		
		if(((LivingEntity)event.getEntity()).getActiveEffectsMap().containsKey(ModEffects.MANA_CONSUME_REDUCE.orElse(null))) {
			float amplifier = ((LivingEntity)event.getEntity()).getEffect(ModEffects.MANA_CONSUME_REDUCE.orElse(null)).getAmplifier() + 1;
			event.setMana((float) (event.getMana() * Math.pow(0.5f, amplifier)));
		}

		EntityStateData state = ExtraDataUtil.entityStateData(event.getEntity());
		if(event.getMana() <= 0 || event.getContext().noCost)
			state = null;
		if(event.getEntity() instanceof Player && ((Player) event.getEntity()).isCreative())
			state = null;
		if(state != null) {
			if(state.getBuffList().containsKey(LibBuff.FREEZE)) {
				event.setCanceled(true);
				return;
			}

			boolean flag = false;

			ItemStack stack = ((LivingEntity) event.getEntity()).getUseItem();

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
					}
				}
				else
					event.setCanceled(true);
			}
		}
	}


	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onShieldCapacity(EntityEvent.ShieldCapacityEvent event) {
		int mana = 0;
		if(event.getEntityLiving().getActiveEffectsMap().containsKey(ModEffects.SHIELD_VALUE.orElse(null)))
			mana += 25 * (event.getEntityLiving().getEffect(ModEffects.SHIELD_VALUE.orElse(null)).getAmplifier() + 1);
		if(event.getEntityLiving().getMainHandItem().getItem() instanceof SpiritSwordItem || event.getEntityLiving().getOffhandItem().getItem() instanceof SpiritSwordItem)
			mana += event.getEntityLiving().getHealth();
		event.setCapacity(event.getCapacity() + mana);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onShieldRegen(EntityEvent.ShieldRegenerationEvent event) {
		int mana = 0;
		if(event.getEntityLiving().getActiveEffectsMap().containsKey(ModEffects.SHIELD_REGEN.orElse(null)))
			mana = (event.getEntityLiving().getEffect(ModEffects.SHIELD_REGEN.orElse(null)).getAmplifier() + 1);

		event.setAmount(event.getAmount() + mana);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onManaRegen(EntityEvent.ManaRegenerationEvent event) {
		float mana = 0;
		if(event.getEntityLiving().getActiveEffectsMap().containsKey(ModEffects.MANA_REGEN.orElse(null)))
			mana = (event.getEntityLiving().getEffect(ModEffects.MANA_REGEN.orElse(null)).getAmplifier() + 1) * 2;

		boolean maxHealth = event.getEntityLiving().getHealth() == event.getEntityLiving().getMaxHealth();
		for (ItemStack stack : event.getEntity().getAllSlots()) {
			if(NBTTagHelper.hasElementOnTool(stack, LibElements.ORIGIN)) {
				if(event.getEntityLiving().tickCount % 20 == 0)
					NBTTagHelper.consumeElementOnTool(stack, LibElements.ORIGIN);
				mana += 0.3;
			}
			if(maxHealth && NBTTagHelper.hasElementOnTool(stack, LibElements.ARC)) {
				if(event.getEntityLiving().tickCount % 20 == 0)
					NBTTagHelper.consumeElementOnTool(stack, LibElements.ARC);
				mana += 0.7;
			}
		}

		if(event.getEntityLiving().getActiveEffectsMap().containsKey(ModEffects.MANA_STASIS.orElse(null)))
			mana -= (event.getEntityLiving().getEffect(ModEffects.MANA_STASIS.orElse(null)).getAmplifier() + 1);

		event.setMana(event.getMana() + mana);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onArtificialLifeManaRegen(EntityEvent.ManaRegenerationEvent event) {
		if(event.getEntityLiving() instanceof ArtificialLifeEntity)
			event.setMana(0);
	}

	@SubscribeEvent
	public void HitEntity(EntityEvent.HitEntityEvent event) {
		if(event.getEntity() instanceof ISpellContext) {
			float force = 0;
			float range = 0;
			int tick = 0;
			SpellContext mana = ((ISpellContext) event.getEntity()).spellContext();
			force = mana.force;
			range = mana.range;
			tick = mana.tick;
			if(mana.postContext != null)
				mana = mana.postContext;
			force = Math.max(force, mana.force);
			range = Math.max(range, mana.range);
			tick = Math.max(tick, mana.tick);

			MagickContext attribute = new MagickContext(event.getEntity().level).noCost()
					.caster(event.getEntity()).projectile(event.getEntity()).victim(event.getVictim())
					.tick(tick).range(range).force(force).applyType(ApplyType.HIT_ENTITY);
			if(event.getEntity() instanceof IOwnerEntity)
				attribute = new MagickContext(event.getEntity().level).noCost()
						.caster(((IOwnerEntity) event.getEntity()).getOwner()).projectile(event.getEntity()).victim(event.getVictim())
						.tick(tick).range(range).force(force).applyType(ApplyType.HIT_ENTITY);
			MagickReleaseHelper.releaseMagick(attribute);
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityStateData state = ExtraDataUtil.entityStateData(event.getEntityLiving());
		if(event.getEntityLiving() instanceof Mob && state != null && state.allowElement())
			RegisterEvent.testIfElement(event.getEntityLiving());

		if(!event.getEntity().level.isClientSide() && event.getEntity() instanceof MageVillagerEntity) {
			if(!((MageVillagerEntity) event.getEntity()).getBrain().isActive(Activity.FIGHT)) {
				((MageVillagerEntity) event.getEntity()).refreshBrain((ServerLevel) event.getEntity().level);
			}
		}

		if(Float.isNaN(event.getEntityLiving().getHealth()))
			(event.getEntityLiving()).setHealth(0.0f);

		ElementToolData tool = ExtraDataUtil.elementToolData(event.getEntity());
		if (tool != null) {
			tool.tick((LivingEntity) event.getEntity());
		}

		TakenEntityData takenState = ExtraDataUtil.takenEntityData(event.getEntity());
		if(takenState != null) {
			takenState.tick((Mob) event.getEntity());
			if(!event.getEntity().level.isClientSide && !event.getEntity().removed) {
				if(event.getEntity().tickCount % 40 == 0)
					Networking.INSTANCE.send(
							PacketDistributor.TRACKING_ENTITY_AND_SELF.with(event::getEntity),
							new TakenStatePack(event.getEntity().getId(), takenState.getTime(), takenState.getOwnerUUID()));
			}
		}

		if(state == null) return;

		if(state.getElementShieldMana() < 0.0f)
			state.setElementShieldMana(0.0f);

		if(state.getMaxManaValue() < 50.0f)
			state.setMaxManaValue(50f);

		if(event.getEntity() instanceof LivingEntity && Float.isNaN(((LivingEntity)event.getEntity()).getHealth()))
			((LivingEntity)event.getEntity()).setHealth(0.0f);

		CompoundTag effect_tick = new CompoundTag();
		for (String s : state.getBuffList().keySet()) {
			ManaBuff buff = state.getBuffList().get(s);
			effect_tick.putInt(buff.getType(), buff.getTick());
		}

		CompoundTag effect_force = new CompoundTag();
		for (String s : state.getBuffList().keySet()) {
			ManaBuff buff = state.getBuffList().get(s);
			effect_force.putFloat(buff.getType(), buff.getForce());
		}

		state.tick(event.getEntity());

		if(!event.getEntity().level.isClientSide && !event.getEntity().removed && !state.getState().equals(state.getPreState())) {
			CompoundTag tag = new CompoundTag();
			state.write(tag);
			Networking.INSTANCE.send(
					PacketDistributor.TRACKING_ENTITY_AND_SELF.with(event::getEntity),
					new EntityStatePack(event.getEntity().getId(), tag));
		}

		if(state.getBuffList().isEmpty()) return;

		if(state.getBuffList().containsKey(LibBuff.HYPERMUTEKI))
			return;

		if(state.getBuffList().containsKey(LibBuff.FREEZE)) {
			if(event.getEntityLiving().invulnerableTime > 0)
				event.getEntityLiving().invulnerableTime--;
			if(event.getEntityLiving().hurtTime > 0)
				event.getEntityLiving().hurtTime--;
			event.getEntityLiving().attackAnim = event.getEntityLiving().oAttackAnim;
			event.getEntityLiving().animationSpeed = event.getEntityLiving().animationSpeedOld;
			event.getEntityLiving().yBodyRot = event.getEntityLiving().yBodyRotO;
			event.getEntityLiving().yHeadRot = event.getEntityLiving().yHeadRotO;
			event.getEntityLiving().xRot = event.getEntityLiving().xRotO;
			event.getEntityLiving().yRot = event.getEntityLiving().yRotO;
			event.getEntityLiving().tickCount -= 1;
			//event.getEntityLiving().setSilent(true);
			if(event.getEntityLiving().getHealth() > 0)
				event.setCanceled(true);
		}

		if(state.getBuffList().containsKey(LibBuff.CRIPPLE)) {
			float force = state.getBuffList().get(LibBuff.CRIPPLE).getForce();
			float maxHealth = event.getEntityLiving().getMaxHealth() - (event.getEntityLiving().getMaxHealth() * force * 0.05f);
			if(event.getEntityLiving().getHealth() > maxHealth)
				event.getEntityLiving().setHealth(maxHealth);
		}
	}

	@SubscribeEvent
	public void onRemoveBuff(LivingDeathEvent event) {
		if(event.getSource().getEntity() instanceof LivingEntity) {
			ElementToolData tool = ExtraDataUtil.elementToolData(event.getSource().getEntity());
			if (tool != null) {
				tool.setAdditionDamage(200);
				tool.consumeElementOnTool((LivingEntity) event.getSource().getEntity(), LibElements.SOLAR);
			}
		}
	}

	@SubscribeEvent
	public void onHealEvent(LivingHealEvent event) {
		EntityStateData state = ExtraDataUtil.entityStateData(event.getEntityLiving());
		if(state != null && state.getBuffList().containsKey(LibBuff.WITHER))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onWeakenBuff(LivingHurtEvent event) {
		EntityStateData state = ExtraDataUtil.entityStateData(event.getEntityLiving());
		Entity entity = event.getSource().getEntity();
		if(entity instanceof LivingEntity) {
			EntityStateData attacker = ExtraDataUtil.entityStateData(entity);
			if(attacker != null) {
				if(attacker.getBuffList().containsKey(LibBuff.LIGHT)) {
					float force = attacker.getBuffList().get(LibBuff.LIGHT).getForce();
					event.setAmount((float) (event.getAmount() * Math.pow(1.1, force)));
				} else if(attacker.getBuffList().containsKey(LibBuff.RADIANCE_WELL)){
					event.setAmount(event.getAmount() * 1.3f);
				}
			}
		}

		if(state != null) {
			state.hitElementShield();
			if(((LivingEntity)event.getEntity()).getActiveEffectsMap().containsKey(ModEffects.MANA_CONVERT.orElse(null))) {
				int amplifier = ((LivingEntity)event.getEntity()).getEffect(ModEffects.MANA_CONVERT.orElse(null)).getAmplifier() + 1;
				state.setManaValue(state.getManaValue() + event.getAmount() * amplifier * 100);
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
		if(state != null && !(event.getEntity() instanceof Player)) {
			if(state.getElement().type().equals(LibElements.ORIGIN)) {
				state.setElement(MagickRegistry.getElement(LibElements.ARC));
				state.setElementShieldMana(50);
				state.setFinalMaxElementShield(50);
				state.setMaxManaValue(50f);
			}
		}
	}

	public void reduceDamage(LivingEvent event, float reduceAmount) {
		if(event instanceof LivingDamageEvent) {
			LivingDamageEvent damageEvent = (LivingDamageEvent) event;
			damageEvent.setAmount(damageEvent.getAmount() - reduceAmount);
			if(damageEvent.getAmount() <= 0)
				event.setCanceled(true);
			else
				damageEvent.setAmount(damageEvent.getAmount() * (10 - Math.min(2.5f, reduceAmount)) * 0.1f);
		} else if (event instanceof LivingAttackEvent) {
			LivingAttackEvent damageEvent = (LivingAttackEvent) event;
			float amount = damageEvent.getAmount() - reduceAmount;
			if(amount <= 0)
				event.setCanceled(true);
		}
	}

	public void onDamage(DamageSource damageSource, LivingEvent event, EntityStateData state, Entity trueSource) {
		float reduceAmount = state.getMaxManaValue() * 0.0005f;

		if(state.getElement() == ModElements.SOLAR) {
			if(damageSource.isFire() || damageSource.isExplosion()) {
				reduceDamage(event, reduceAmount);
				if(event instanceof LivingDamageEvent) {
					LivingDamageEvent damageEvent = (LivingDamageEvent) event;
					if(reduceAmount >= damageEvent.getAmount()) {
						if(event.getEntityLiving().getRemainingFireTicks() > 0)
							event.getEntityLiving().setSecondsOnFire(0);
					}
				}
			}
		} else if(state.getElement() == ModElements.VOID) {
			if(damageSource.isBypassArmor()) {
				reduceDamage(event, reduceAmount);
			}
		} else if(state.getElement() == ModElements.ARC) {
			if(damageSource.isFire()
					|| damageSource.isMagic()
					|| damageSource.msgId.contains("lightning")
					|| damageSource.msgId.contains("elc")
					|| damageSource.msgId.contains("arc")) {
				reduceDamage(event, reduceAmount);
			}
		} else if(state.getElement() == ModElements.WITHER) {
			if(damageSource.msgId.contains("wither")
					|| damageSource.msgId.contains("starve")
					|| damageSource.msgId.contains("inWall")
					|| damageSource.msgId.contains("drown")
					|| damageSource.msgId.contains("fallingBlock")) {
				reduceDamage(event, reduceAmount);
			}
		} else if(state.getElement() == ModElements.TAKEN) {
			if(trueSource instanceof LivingEntity) {
				reduceDamage(event, reduceAmount);
			}
		} else if(state.getElement() == ModElements.STASIS) {
			if(trueSource != null) {
				if(!(trueSource instanceof LivingEntity)) {
					reduceDamage(event, reduceAmount);
				} else {
					EntityStateData attackerState = ExtraDataUtil.entityStateData(trueSource);
					if(attackerState.getBuffList().containsKey(LibBuff.FREEZE) || attackerState.getBuffList().containsKey(LibBuff.SLOW))
						reduceDamage(event, reduceAmount);
				}
			}
		}
	}

	@SubscribeEvent
	public void onDamage(LivingDamageEvent event) {
		EntityStateData state = ExtraDataUtil.entityStateData(event.getEntityLiving());
		if(state != null && state.getBuffList().containsKey(LibBuff.HYPERMUTEKI))
			event.setCanceled(true);

		if(state != null && state.getBuffList().containsKey(LibBuff.WEAKEN)) {
			float force = state.getBuffList().get(LibBuff.WEAKEN).getForce();
			event.setAmount((float) (event.getAmount() * Math.pow(1.1, force)));
		}

		if(state != null && state.getBuffList().containsKey(LibBuff.RADIANCE_WELL) && (event.getSource().isFire() || event.getSource().isExplosion()))
			event.setCanceled(true);

		Entity entity = event.getSource().getEntity();

		if(state != null) {
			onDamage(event.getSource(), event, state, entity);
		}

		if(entity instanceof LivingEntity || entity instanceof IMob) {
			ElementToolData tool = ExtraDataUtil.elementToolData(entity);

			if (tool != null) {
				event.setAmount(tool.applyAdditionDamage(event.getAmount()));
			}

			if(!(entity instanceof Player)) {
				EntityStateData attacker = ExtraDataUtil.entityStateData(entity);
				float manaNeed = event.getAmount();

				if (attacker != null && !attacker.getElement().type().equals(LibElements.ORIGIN)) {
					if(event.getEntityLiving() instanceof ServerPlayer)
						AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) event.getEntityLiving(), LibAdvancements.ELEMENT_CREATURE);
					if (attacker.getElementShieldMana() > 0) {
						MagickContext attribute = new MagickContext(entity.level).noCost().caster(entity).projectile(event.getSource().getDirectEntity()).victim(event.getEntityLiving()).applyType(ApplyType.DE_BUFF).tick((int) manaNeed * 40).force(manaNeed);
						MagickReleaseHelper.releaseMagick(attribute);
					}
				}
			}

			TakenEntityData taken = ExtraDataUtil.takenEntityData(event.getSource().getEntity());
			if(event.getSource().getEntity() instanceof Mob && taken != null && !taken.getOwnerUUID().equals(MagickCore.emptyUUID) && taken.getTime() > 0) {
				if(entity.level instanceof ServerLevel) {
					Entity entity1 = ((ServerLevel) entity.level).getEntity(taken.getOwnerUUID());
					if(entity1 instanceof LivingEntity) {
						EntityStateData ownerState = ExtraDataUtil.entityStateData(entity1);
						if(ownerState != null && ownerState.getBuffList().containsKey(LibBuff.TAKEN_KING)) {
							event.setAmount(event.getAmount() * 1.1f * ownerState.getBuffList().get(LibBuff.TAKEN_KING).getForce());
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onApplyManaBuff(EntityEvent.ApplyManaBuffEvent event) {
		EntityStateData state = ExtraDataUtil.entityStateData(event.getEntityLiving());
		if(state != null && !event.getBeneficial() && (state.getBuffList().containsKey(LibBuff.HYPERMUTEKI) || (state.getElementShieldMana() > 0 && !event.getType().getElement().equals(state.getElement().type()))))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onManaEntitySpawn(EntityEvent.MagickSpawnEntityEvent event) {
		if(event.getMagickContext().projectile instanceof RepeaterEntity)
			((RepeaterEntity) event.getMagickContext().projectile).setSpawnEntity(event.getEntity());
		if(event.getMagickContext().projectile instanceof ChainEntity)
			((ChainEntity) event.getMagickContext().projectile).setPostEntity(event.getEntity());
	}

	@SubscribeEvent
	public void onElementShield(LivingAttackEvent event) {
		EntityStateData state = ExtraDataUtil.entityStateData(event.getEntityLiving());
		if(state != null && state.getBuffList().containsKey(LibBuff.HYPERMUTEKI))
			event.setCanceled(true);

		if(event.getSource().getEntity() instanceof LivingEntity)
			event.getSource().getEntity().getAllSlots().forEach((s) -> NBTTagHelper.consumeElementOnTool(s, LibElements.VOID));

		if(state != null) {
			onDamage(event.getSource(), event, state, event.getSource().getEntity());
		}

		if(state != null) {
			if(event.getEntityLiving().invulnerableTime > 0.0f && state.getElementShieldMana() > 0.0f)
				event.setCanceled(true);

			String shieldElement = state.getElement().damageType().getMsgId();
			String damageType = event.getSource().getMsgId();
			boolean matchMeleeType = false;
			if(event.getSource().getEntity() instanceof LivingEntity)
				matchMeleeType = NBTTagHelper.hasElementOnTool(((LivingEntity) event.getSource().getEntity()).getMainHandItem(), state.getElement().type());

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

		if(event.getSource().getEntity() instanceof LivingEntity) {
			float chance = 0;
			for (ItemStack stack : event.getSource().getEntity().getAllSlots()) {
				if (stack != null && NBTTagHelper.hasElementOnTool(stack, LibElements.TAKEN)) {
					chance+=0.02;
					chance*=1.15;
					NBTTagHelper.consumeElementOnTool(stack, LibElements.TAKEN);
				}
			}
			if(MagickCore.rand.nextFloat() < chance) {
				ModBuffs.applyBuff(event.getEntityLiving(), LibBuff.TAKEN, 100, 1, true);
				MagickContext context = MagickContext.create(event.getSource().getEntity().level)
						.caster(event.getSource().getEntity())
						.victim(event.getEntity()).tick(100).force(1)
						.applyType(ApplyType.DE_BUFF).element(MagickRegistry.getElement(LibElements.TAKEN));
				MagickReleaseHelper.releaseMagickEvent(context);
				event.getEntityLiving().setLastHurtMob(null);
				event.getEntityLiving().setLastHurtByMob(null);
				if(event.getEntityLiving() instanceof Mob)
					((Mob) event.getEntityLiving()).setTarget(null);
			}
		}
	}

	public static void HandleElementShield(EntityStateData state, float damage, LivingAttackEvent event) {
		if(state.getElementShieldMana() >= damage) {
			state.hitElementShield();
			state.setElementShieldMana(state.getElementShieldMana() - damage);
			if(damage > 0.0f && event.getEntityLiving().invulnerableTime <= 10)
				spawnParticle(state.getElement().type(), event.getEntity());
			event.getEntityLiving().playSound(SoundEvents.SNOW_BREAK, 1.0f, 0.0f);
			event.getEntityLiving().invulnerableTime = 20;
			event.setCanceled(true);
		} else if(state.getElementShieldMana() > 0.0f) {
			float amount = damage - state.getElementShieldMana();
			state.hitElementShield();
			state.setElementShieldMana(0.0f);
			event.getEntityLiving().playSound(SoundEvents.GLASS_BREAK, 1.0f, 0.0f);
			event.getEntityLiving().hurt(event.getSource(), amount);
			event.getEntityLiving().invulnerableTime = 20;
			event.setCanceled(true);
			if(damage > 0.0f)
				spawnParticle(state.getElement().type(), event.getEntity());
		}
	}

	public static void spawnParticle(String element, Entity entity) {
		Level world = entity.level;
		if(!world.isClientSide) return;
		ElementRenderer render = MagickCore.proxy.getElementRender(element);

		for(int i = 0; i < 20; ++i) {
			LitParticle litPar = new LitParticle(world, render.getParticleTexture()
					, new Vec3(MagickCore.getNegativeToOne() * entity.getBbWidth() / 2f + entity.getX()
					, MagickCore.getNegativeToOne() / 2f + entity.getY() + entity.getBbHeight() / 2
					, MagickCore.getNegativeToOne() * entity.getBbWidth() / 2f + entity.getZ())
					, entity.getBbWidth() / 5f, entity.getBbWidth() / 5f, 0.8f * MagickCore.rand.nextFloat(), 20, render);
			litPar.setGlow();
			litPar.addMotion(MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10);
			MagickCore.addMagickParticle(litPar);
		}
	}

	@SubscribeEvent
	public void manaEntityUpdate(EntityEvent.EntityUpdateEvent event) {
		if(event.getEntity() instanceof IOwnerEntity && ((IOwnerEntity) event.getEntity()).getOwner() != null && event.getEntity().tickCount % 40 == 0) {
			if(!event.getEntity().level.isClientSide && !event.getEntity().removed)
				Networking.INSTANCE.send(
						PacketDistributor.TRACKING_ENTITY_AND_SELF.with(event::getEntity),
						new OwnerStatePack(event.getEntity().getId(), ((IOwnerEntity) event.getEntity()).getOwner().getUUID()));
		}

		if(event.getEntity() instanceof IManaCapacity) {
			ManaCapacity data = ((IManaCapacity) event.getEntity()).manaCapacity();
			if(!event.getEntity().level.isClientSide && !event.getEntity().removed && event.getEntity().tickCount % 10 == 0) {
				Networking.INSTANCE.send(
						PacketDistributor.TRACKING_ENTITY_AND_SELF.with(event::getEntity),
						new ManaCapacityPack(event.getEntity().getId(), data));
			}
		}

		if(event.getEntity() instanceof ISpellContext) {
			SpellContext state = ((ISpellContext) event.getEntity()).spellContext();
			if(state != null) {
				if(!event.getEntity().level.isClientSide && event.getEntity().tickCount == state.tick - 5) {
					event.getEntity().playSound(ModSounds.ring_pointer.get(), 0.5F, (1.0F + MagickCore.rand.nextFloat()));
				}

				int ticksExisted = event.getEntity().tickCount;
				if(event.getEntity() instanceof IExistTick) {
					IExistTick existTick = (IExistTick) event.getEntity();
					ticksExisted -= existTick.getTickThatNeedExistingBeforeRemove();
				}
				if(ticksExisted > state.tick && state.tick >= 0)
					event.getEntity().remove();
			}
		}

		if(event.getEntity() instanceof ItemEntity && event.getEntity().isInWater()) {
			ItemEntity item = (ItemEntity) event.getEntity();
			ItemStack stack = item.getItem();
			if(!stack.isEmpty() && stack.hasTag() && stack.getTag().contains(LibElementTool.TOOL_ELEMENT)) {
				CompoundTag tag = NBTTagHelper.getToolElementTable(stack);
				for(String element : tag.getAllKeys()) {
					int count = tag.getInt(element);
					if(count > 1)
						tag.putInt(element, count - 1);
					else
						tag.remove(element);
				}
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
				ModBuffs.applyBuff(event.getPlayer(), buff.getType(), buff.getTick(), buff.getForce(), true);
			}
		}
		else {
			state.setElement(old.getElement());
			state.setMaxManaValue(old.getMaxManaValue() * 0.95f);
			if(state.getMaxManaValue() <= 2500 && event.getPlayer() instanceof ServerPlayer)
				AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) event.getPlayer(), "below2500");
		}
	}
}
