package com.rogoshum.magickcore.common.magick;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.api.mana.ISpellContext;
import com.rogoshum.magickcore.common.api.entity.IManaEntity;
import com.rogoshum.magickcore.common.api.entity.IManaTaskMob;
import com.rogoshum.magickcore.common.api.entity.IOwnerEntity;
import com.rogoshum.magickcore.common.api.event.EntityEvents;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.init.ModEffects;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

public class MagickReleaseHelper {
    private final static float ORB_VELOCITY = 0.5f;
    private final static float STAR_VELOCITY = 0.7f;
    private final static float LASER_VELOCITY = 1.0f;

    private final static HashMap<String, Function<DoubleEntity, Boolean>> ownerTest = new HashMap<>();

    public static void registerOwnerTest(String id, Function<DoubleEntity, Boolean> function) {
        ownerTest.put(id, function);
    }

    public static UUID getTraceEntity(Entity playerIn) {
        UUID uuid = MagickCore.emptyUUID;
        Entity entity = getEntityLookedAt(playerIn);
        if (entity != null)
            uuid = entity.getUniqueID();
        return uuid;
    }

    public static float manaNeed(MagickContext context) {
        float manaNeed = singleContextMana(context);
        SpellContext post = context.postContext;
        while (post != null) {
            manaNeed += singleContextMana(post);
            post = post.postContext;
        }
        return manaNeed;
    }

    public static float singleContextMana(SpellContext context) {
        float baseMana = context.tick * 0.25f + (float)(Math.pow(context.force, 1.2) * 10) + (float)(Math.pow(context.range, 2.5) * 3);
        if(context.containChild(LibContext.TRACE))
            baseMana *= 1.2f;
        if(context.containChild(LibContext.SPAWN)) {
            EntityType<?> type = context.<SpawnContext>getChild(LibContext.SPAWN).entityType;
            baseMana += (type.getHeight() + type.getWidth()) * 30;
        }
        if(context.containChild(LibContext.MULTI_RELEASE))
            baseMana *= 1.5f;
        return baseMana;
    }

    public static EntityEvents.MagickPreReleaseEvent preReleaseMagickEvent(MagickContext context) {
        EntityEvents.MagickPreReleaseEvent event = new EntityEvents.MagickPreReleaseEvent(context, context.noCost ? 0 : manaNeed(context));
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static EntityEvents.MagickReleaseEvent releaseMagickEvent(MagickContext context) {
        EntityEvents.MagickReleaseEvent event = new EntityEvents.MagickReleaseEvent(context);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void failed(MagickContext context, Entity entity) {
        for(int i = 0; i < 40; ++i) {
            context.world.addParticle(ParticleTypes.ASH, MagickCore.getNegativeToOne() + entity.getEntity().getPosX()
                    , MagickCore.getNegativeToOne() + entity.getEntity().getPosY() + entity.getEntity().getHeight() * 0.5
                    , MagickCore.getNegativeToOne() + entity.getEntity().getPosZ(), MagickCore.getNegativeToOne() * 0.02, MagickCore.getNegativeToOne() * 0.02, MagickCore.getNegativeToOne() * 0.02);
        }
    }

    public static boolean releaseMagick(MagickContext context) {
        if(context == null)
            return false;
        EntityEvents.MagickPreReleaseEvent preEvent = preReleaseMagickEvent(context);
        if(preEvent.isCanceled()) {
            failed(context, preEvent.getEntity());
            return false;
        }
        EntityEvents.MagickReleaseEvent releaseEvent = releaseMagickEvent(preEvent.getContext());
        if(releaseEvent.isCanceled()) {
            failed(context, releaseEvent.getEntity());
            return false;
        }

        context = releaseEvent.getContext();
        MagickElement element = MagickRegistry.getElement(LibElements.ORIGIN);

        if(context.element != null && !LibElements.ORIGIN.equals(context.element.type()))
            element = context.element;
        else if(context.projectile instanceof ISpellContext)
            element = ((ISpellContext) context.projectile).spellContext().element;
        else if(context.caster instanceof ISpellContext)
            element = ((ISpellContext) context.caster).spellContext().element;
        else if(context.caster != null)
            element = ExtraDataUtil.entityStateData(context.caster).getElement();

        if(context.caster != null) {
            context.replenishChild(DirectionContext.create(context.caster.getLookVec()));
        } else if (context.projectile != null) {
            context.replenishChild(DirectionContext.create(context.projectile.getMotion()));
        }

        if(context.projectile instanceof IManaEntity && context.applyType != ApplyType.SPAWN_ENTITY) {
            ManaFactor manaFactor = ((IManaEntity) context.projectile).getManaFactor();
            context.force(manaFactor.force * context.force);
            context.range(manaFactor.range * context.range);
            context.tick((int) (manaFactor.tick * context.tick));
        }
        if(context.caster instanceof ServerPlayerEntity) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) context.caster, "element_func_" + context.element.type() + "_" + context.applyType);
        }
        return MagickRegistry.getElementFunctions(element.type()).applyElementFunction(context);
    }

    public static boolean spawnEntity(MagickContext context) {
        if (context.world == null || context.world.isRemote)
            return false;
        if(!context.containChild(LibContext.SPAWN))
            return false;

        SpawnContext spawnContext = context.getChild(LibContext.SPAWN);
        if(spawnContext.entityType == null)
            return false;
        if(context.caster instanceof ServerPlayerEntity && spawnContext.entityType.getRegistryName() != null) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) context.caster, "entity_type_" + spawnContext.entityType.getRegistryName().getPath());
        }
        Entity pro = spawnContext.entityType.create(context.world);
        if(pro == null)
            return false;

        TraceContext traceContext = null;
        if(context.containChild(LibContext.TRACE))
            traceContext = context.getChild(LibContext.TRACE);

        if(context.victim != null && context.victim != context.caster && traceContext != null) {
            if(traceContext.entity == null && traceContext.uuid == MagickCore.emptyUUID) {
                traceContext.entity = context.victim;
            }
        }

        if(pro instanceof IOwnerEntity) {
            ((IOwnerEntity) pro).setOwner(context.caster);
        }

        if(pro instanceof ISpellContext) {
            ISpellContext spellContext = (ISpellContext) pro;
            CompoundNBT tag = new CompoundNBT();
            context.serialize(tag);
            spellContext.spellContext().deserialize(tag);
            MagickElement element = MagickRegistry.getElement(LibElements.ORIGIN);
            if(context.element != null && !LibElements.ORIGIN.equals(context.element.type()))
                element = context.element;
            else if(context.caster instanceof ISpellContext)
                element = ((ISpellContext) context.caster).spellContext().element;
            else if(context.caster instanceof LivingEntity || context.caster instanceof IManaTaskMob)
                element = ExtraDataUtil.entityStateData(context.caster).getElement();
            spellContext.spellContext().element(element);
        }

        if(pro instanceof ManaProjectileEntity)
            ((ManaProjectileEntity) pro).reSize();

        if(context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);
            pro.setPosition(positionContext.pos.x, positionContext.pos.y, positionContext.pos.z);
        } else if(context.projectile != null) {
            pro.setPosition(context.projectile.getPosX(), context.projectile.getPosY() + pro.getEyeHeight(), context.projectile.getPosZ());
        } else if(context.caster != null) {
            if(pro instanceof ProjectileEntity)
                pro.setPosition(context.caster.getPosX() + context.caster.getLookVec().x * (1.25 + pro.getWidth() * 0.5),
                        context.caster.getPosY() + context.caster.getEyeHeight() + context.caster.getLookVec().y * (1.25 + pro.getHeight() * 0.5),
                        context.caster.getPosZ() + context.caster.getLookVec().z * (1.25 + pro.getWidth() * 0.5));
            else
                pro.setPosition(context.caster.getPosX(), context.caster.getPosY() + context.caster.getHeight() * 0.5, context.caster.getPosZ());
        }

        if(context.caster != null && pro instanceof ProjectileEntity) {
            ((ProjectileEntity)pro).setShooter(context.caster);

            if(context.victim != null && context.victim != context.caster) {
                Vector3d motion = context.victim.getPositionVec().add(0, (context.victim.getHeight() * 0.5) - (pro.getHeight() * 0.5), 0).subtract(pro.getPositionVec());
                ((ProjectileEntity)pro).shoot(motion.x, motion.y, motion.z, getVelocity(pro), getInaccuracy(pro));
            }
            else if(traceContext != null && traceContext.entity != null) {
                Vector3d motion = traceContext.entity.getPositionVec().add(0, (traceContext.entity.getHeight() * 0.5) - (pro.getHeight() * 0.5), 0).subtract(pro.getPositionVec());
                ((ProjectileEntity)pro).shoot(motion.x, motion.y, motion.z, getVelocity(pro), getInaccuracy(pro));
            }
            else
                ((ProjectileEntity)pro).shoot(context.caster.getLookVec().x, context.caster.getLookVec().y, context.caster.getLookVec().z, getVelocity(pro), getInaccuracy(pro));
        }
        if(pro instanceof IManaEntity)
            ((IManaEntity) pro).beforeJoinWorld(context);

        context.world.addEntity(pro);
        return true;
    }

    private static float getVelocity(Entity entity) {
        EntityType<?> type = entity.getType();
        if (type == ModEntities.MANA_ORB.get())
            return ORB_VELOCITY;

        if (type == ModEntities.MANA_LASER.get())
            return LASER_VELOCITY;

        if (type == ModEntities.MANA_STAR.get())
            return STAR_VELOCITY;

        EntityEvents.EntityVelocity event = new EntityEvents.EntityVelocity(entity);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getVelocity();
    }

    private static float getInaccuracy(Entity entity) {
        EntityEvents.EntityVelocity event = new EntityEvents.EntityVelocity(entity);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getInaccuracy();
    }

    public static Entity getEntityLookedAt(Entity e) {
        return getEntityRayTrace(e, new Vector3d(e.getPosX(), e.getPosY() + e.getEyeHeight(), e.getPosZ()), e.getLookVec(), 64);
    }

    public static boolean canEntityTraceAnother(Entity e, Entity another) {
        return another == getEntityRayTrace(e, e.getPositionVec().add(0, e.getHeight() / 2, 0), another.getPositionVec().add(0, another.getHeight() / 2, 0).subtract(e.getPositionVec().add(0, e.getHeight() / 2, 0)).normalize(), 64);
    }

    public static Entity getEntityRayTrace(Entity e, Vector3d vec, Vector3d diraction) {
        return getEntityRayTrace(e, vec, diraction, 64);
    }

    public static Entity getEntityRayTrace(Entity e, Vector3d vec, Vector3d diraction, float finalD) {
        Entity foundEntity = null;

        double distance = finalD;
        RayTraceResult pos = raycast(e, vec, diraction, finalD);
        Vector3d positionVector = vec;

        if (pos != null) {
            distance = pos.getHitVec().distanceTo(positionVector);
        }

        Vector3d lookVector = diraction;
        Vector3d reachVector = positionVector.add(lookVector.x * (double) finalD, lookVector.y * (double) finalD, lookVector.z * (double) finalD);

        Entity lookedEntity = null;
        List<Entity> entitiesInBoundingBox = e.getEntityWorld().getEntitiesWithinAABBExcludingEntity(e, e.getBoundingBox().grow(lookVector.x * (double) finalD, lookVector.y * (double) finalD, lookVector.z * (double) finalD).grow(1F, 1F, 1F));
        double minDistance = distance;

        for (Entity entity : entitiesInBoundingBox) {
            if (entity.canBeCollidedWith()) {
                float collisionBorderSize = entity.getCollisionBorderSize();
                AxisAlignedBB hitbox = entity.getBoundingBox().grow(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                Optional<Vector3d> interceptPosition = hitbox.rayTrace(positionVector, reachVector);

                if (hitbox.contains(positionVector)) {
                    if (0.0D < minDistance || minDistance == 0.0D) {
                        lookedEntity = entity;
                        minDistance = 0.0D;
                    }
                } else if (interceptPosition.isPresent()) {
                    double distanceToEntity = positionVector.distanceTo(interceptPosition.get());

                    if (distanceToEntity < minDistance || minDistance == 0.0D) {
                        lookedEntity = entity;
                        minDistance = distanceToEntity;
                    }
                }
            }

            if (lookedEntity != null && (minDistance < distance || pos == null)) {
                foundEntity = lookedEntity;
            }
        }

        return foundEntity;
    }

    public static BlockRayTraceResult raycast(Entity e, double len) {
        Vector3d vec = new Vector3d(e.getPosX(), e.getPosY() + e.getEyeHeight(), e.getPosZ());
        return raycast(e, vec, e.getLookVec(), len);
    }

    public static BlockRayTraceResult raycast(Entity entity, Vector3d origin, Vector3d ray, double len) {
        Vector3d ori = new Vector3d(origin.x, origin.y, origin.z);
        Vector3d end = origin.add(ray.normalize().scale(len));
        return entity.world.rayTraceBlocks(new RayTraceContext(ori, end, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, entity));
    }

    public static boolean sameLikeOwner(Entity owner, Entity other) {
        if(owner instanceof LivingEntity) {
            if(((LivingEntity)owner).getActivePotionMap().containsKey(ModEffects.CHAOS_THEOREM.orElse(null))) {
                return false;
            }
        }
        if (owner == null)
            return false;

        if (other == null)
            return false;

        for(Function<DoubleEntity, Boolean> test: ownerTest.values()) {
            if(test.apply(new DoubleEntity(owner, other)))
                return true;
        }

        boolean isOwnerPlayer = owner instanceof PlayerEntity;
        boolean isOtherPlayer = other instanceof PlayerEntity;

        if (other instanceof IOwnerEntity && ownerFunction(isOwnerPlayer, ((IOwnerEntity) other)::getOwner))
            return true;

        if (other instanceof ProjectileEntity && ownerFunction(isOwnerPlayer, ((ProjectileEntity) other)::func_234616_v_))
            return true;

        if (other instanceof TameableEntity && ownerFunction(isOwnerPlayer, ((TameableEntity) other)::getOwner))
            return true;

        AtomicBoolean flag = new AtomicBoolean(false);
        ExtraDataUtil.entityData(other).<TakenEntityData>execute(LibEntityData.TAKEN_ENTITY, data -> flag.set(data.getOwnerUUID().equals(owner.getUniqueID())));

        if (flag.get())
            return true;

        return isOwnerPlayer && isOtherPlayer;
    }

    public static boolean ownerFunction(boolean isOwnerPlayer, Supplier<Entity> entitySupplier) {
        Entity entity = entitySupplier.get();
        if(entity == null) return false;

        if (isOwnerPlayer && entity instanceof PlayerEntity)
            return true;

        return !isOwnerPlayer && !(entity instanceof PlayerEntity);
    }

    public static class DoubleEntity {
        Entity entityA;
        Entity entityB;

        public DoubleEntity(Entity entityA, Entity entityB) {
            this.entityA = entityA;
            this.entityB = entityB;
        }
    }
}
