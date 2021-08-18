package com.rogoshum.magickcore.tool;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.*;
import com.rogoshum.magickcore.api.entity.IOwnerEntity;
import com.rogoshum.magickcore.api.entity.ISuperEntity;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.capability.ITakenState;
import com.rogoshum.magickcore.entity.baseEntity.ManaEntity;
import com.rogoshum.magickcore.entity.baseEntity.ManaProjectileEntity;
import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.enums.EnumTargetType;
import com.rogoshum.magickcore.init.ModEntites;
import com.rogoshum.magickcore.magick.ElementFunction;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MagickReleaseHelper {
    private final static float ORB_VELOCITY = 0.9f;
    private final static float ORB_INACCURACY = 1.0f;

    private final static float STAR_VELOCITY = 1.5f;
    private final static float STAR_INACCURACY = 0.5f;

    private final static float LASER_VELOCITY = 3.5f;
    private final static float LASER_INACCURACY = 1.0f;

    private static final HashMap<String, ElementFunction> elementFunctionMap = new HashMap<>();

    public static void registryElementFunction(IManaElement element, ElementFunction functions) {
        if (!elementFunctionMap.containsKey(element.getType()))
            elementFunctionMap.put(element.getType(), functions);
        else try {
            throw new Exception("Containing same element on the map = [" + element.getType() + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean applyElementFunction(IManaElement element, EnumManaType type, ReleaseAttribute attribute) {
        if(element != null && elementFunctionMap.containsKey(element.getType()))
        {
            ElementFunction functions = elementFunctionMap.get(element.getType());
            return functions.applyElementFunction(type, attribute);
        }

        return false;
    }

    public static UUID getTraceEntity(Entity playerIn) {
        UUID uuid = MagickCore.emptyUUID;
        Entity entity = getEntityLookedAt(playerIn);
        if (entity != null)
            uuid = entity.getUniqueID();
        return uuid;
    }

    public static boolean releaseMagickTest(Entity playerIn, UUID traceEntity, float force, int tick, float range) {
        float trace = (traceEntity != MagickCore.emptyUUID && traceEntity != null) ? 100 : 0;
        float mana = tick + force * force * 10 + trace + range * range * 5;
        EntityEvents.MagickPreReleaseEvent event = new EntityEvents.MagickPreReleaseEvent(playerIn, mana);
        MinecraftForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

    public static EntityEvents.MagickReleaseEvent postReleaseMagick(Entity entity, IManaElement element, float force, int tick, EnumManaType type, UUID trace, float range, String magickType) {
        EntityEvents.MagickReleaseEvent event = new EntityEvents.MagickReleaseEvent(entity, element, force, tick, type, trace, range, magickType);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void releaseProjectileEntity(EntityType<? extends ManaProjectileEntity> type, Entity playerIn, IManaElement element, UUID traceEntity, float force, int tick
            , float range, EnumManaType manaType) {
        if (playerIn.world.isRemote)
            return;

        if (!releaseMagickTest(playerIn, traceEntity, force, tick, range))
            return;

        ManaProjectileEntity pro = type.create(playerIn.world);

        EntityEvents.MagickReleaseEvent event = postReleaseMagick(playerIn, element, force, tick, manaType, traceEntity, range, type.getRegistryName().toString());
        pro.setShooter(playerIn);
        pro.setPosition(playerIn.getPosX() + playerIn.getLookVec().x * 1.5, playerIn.getPosY() + playerIn.getEyeHeight() + playerIn.getLookVec().y * 1.5, playerIn.getPosZ() + playerIn.getLookVec().z * 1.5);
        pro.shoot(playerIn.getLookVec().x, playerIn.getLookVec().y, playerIn.getLookVec().z, getVelocity(type), getINACCURACY(type));
        pro.setElement(event.getElement());
        pro.setForce(event.getForce());
        pro.setTickTime(event.getTick());
        pro.setManaType(event.getType());
        pro.setTraceTarget(event.getTrace());
        pro.setRange(event.getRange());

        playerIn.world.addEntity(pro);
    }

    public static void releasePointEntity(EntityType<? extends ManaEntity> type, Entity playerIn, Vector3d vec, IManaElement element, UUID traceEntity, float force, int tick
            , float range, EnumTargetType targetType, EnumManaType manaType) {
        if (playerIn.world.isRemote)
            return;

        if (!releaseMagickTest(playerIn, traceEntity, force, tick, range))
            return;

        ManaEntity pro = type.create(playerIn.world);
        String magickType = type.getRegistryName().toString();
        if (pro instanceof ISuperEntity)
            magickType = "SUPER_ENTITY";
        EntityEvents.MagickReleaseEvent event = postReleaseMagick(playerIn, element, force, tick, manaType, traceEntity, range, magickType);

        pro.setPosition(vec.x, vec.y, vec.z);
        pro.setElement(event.getElement());
        pro.setForce(event.getForce());
        pro.setTickTime(event.getTick());
        pro.setManaType(event.getType());
        pro.setTraceTarget(event.getTrace());
        pro.setOwner(playerIn);
        pro.setRange(event.getRange());
        playerIn.world.addEntity(pro);
    }

    private static float getVelocity(EntityType type) {
        if (type == ModEntites.mana_orb)
            return ORB_VELOCITY;

        if (type == ModEntites.mana_laser)
            return LASER_VELOCITY;

        if (type == ModEntites.mana_star)
            return STAR_VELOCITY;

        return 1.0f;
    }

    private static float getINACCURACY(EntityType type) {
        if (type == ModEntites.mana_orb)
            return ORB_INACCURACY;

        if (type == ModEntites.mana_laser)
            return LASER_INACCURACY;

        if (type == ModEntites.mana_star)
            return STAR_INACCURACY;

        return 0.5f;
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
        if (owner == null)
            return false;

        if (other == null)
            return false;

        boolean isOwnerPlayer = owner instanceof PlayerEntity;
        boolean isOtherPlayer = other instanceof PlayerEntity;

        if (other instanceof IOwnerEntity && ((IOwnerEntity) other).getOwner() != null) {
            if (isOwnerPlayer && ((IOwnerEntity) other).getOwner() instanceof PlayerEntity)
                return true;

            if (!isOwnerPlayer && !(((IOwnerEntity) other).getOwner() instanceof PlayerEntity))
                return true;
        }

        if (other instanceof ProjectileEntity && ((ProjectileEntity) other).func_234616_v_() != null) {
            if (!isOwnerPlayer && !(((ProjectileEntity) other).func_234616_v_() instanceof PlayerEntity))
                return true;

            if (isOwnerPlayer && ((ProjectileEntity) other).func_234616_v_() instanceof PlayerEntity)
                return true;
        }

        if (other instanceof TameableEntity && ((TameableEntity) other).getOwner() != null) {
            if (isOwnerPlayer && ((TameableEntity) other).getOwner() instanceof PlayerEntity)
                return true;

            if (!isOwnerPlayer && !(((TameableEntity) other).getOwner() instanceof PlayerEntity))
                return true;
        }

        ITakenState state = other.getCapability(MagickCore.takenState).orElse(null);
        if (state != null && state.getOwnerUUID().equals(owner.getUniqueID()))
            return true;

        if (isOwnerPlayer && isOtherPlayer)
            return true;

        return !isOwnerPlayer && !isOtherPlayer;
    }
}