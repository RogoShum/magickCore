package com.rogoshum.magickcore.helper;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.EnumManaType;
import com.rogoshum.magickcore.api.EnumTargetType;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.api.ISuperEntity;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.entity.ManaOrbEntity;
import com.rogoshum.magickcore.entity.baseEntity.ManaEntity;
import com.rogoshum.magickcore.entity.baseEntity.ManaPointEntity;
import com.rogoshum.magickcore.entity.baseEntity.ManaProjectileEntity;
import com.rogoshum.magickcore.init.ModEntites;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

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

    public static UUID getTraceEntity(Entity playerIn)
    {
        UUID uuid = MagickCore.emptyUUID;
        Entity entity = getEntityLookedAt(playerIn);
        if(entity != null)
            uuid = entity.getUniqueID();
        return uuid;
    }

    public static boolean releaseMagickTest(Entity playerIn, UUID traceEntity, float force, int tick, float range)
    {
        float trace = (traceEntity != MagickCore.emptyUUID && traceEntity != null) ? 100 : 0;
        float mana = tick + force * force * 10 + trace + range * range * 5;
        EntityEvents.MagickPreReleaseEvent event = new EntityEvents.MagickPreReleaseEvent(playerIn, mana);
        MinecraftForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

    public static void releaseProjectileEntity(EntityType type, Entity playerIn, IManaElement element, UUID traceEntity, float force, int tick
    , float range, EnumManaType manaType)
    {
        if(playerIn.world.isRemote)
            return;

        if(!releaseMagickTest(playerIn, traceEntity, force, tick, range))
            return;

        ManaProjectileEntity pro = (ManaProjectileEntity) type.create(playerIn.world);

        EntityEvents.MagickReleaseEvent event = new EntityEvents.MagickReleaseEvent(playerIn, element, force, tick, manaType, traceEntity, range, type.getRegistryName().toString());
        MinecraftForge.EVENT_BUS.post(event);
        pro.setShooter(playerIn);
        pro.setPosition(playerIn.getPosX() + playerIn.getLookVec().x * 1.5, playerIn.getPosY() + playerIn.getEyeHeight() + playerIn.getLookVec().y * 1.5, playerIn.getPosZ() + playerIn.getLookVec().z * 1.5);
        pro.shoot(playerIn.getLookVec().x, playerIn.getLookVec().y, playerIn.getLookVec().z, getVelocity(type), getINACCURACY(type));
        pro.setElement(event.getElement());
        pro.setForce(event.getForce());
        pro.setTickTime(event.getTick());
        pro.setManaType(event.getType());
        pro.setTraceTarget(event.getTrace());
        playerIn.world.addEntity(pro);
    }

    public static void releasePointEntity(EntityType type, Entity playerIn, Vector3d vec, IManaElement element, UUID traceEntity, float force, int tick
            , float range, EnumTargetType targetType, EnumManaType manaType)
    {
        if(playerIn.world.isRemote)
            return;

        if(!releaseMagickTest(playerIn, traceEntity, force, tick, range))
            return;

        ManaEntity pro = (ManaEntity) type.create(playerIn.world);
        String magickType = type.getRegistryName().toString();
        if(pro instanceof ISuperEntity)
            magickType = "SUPER_ENTITY";
        EntityEvents.MagickReleaseEvent event = new EntityEvents.MagickReleaseEvent(playerIn, element, force, tick, manaType, traceEntity, range, magickType);
        MinecraftForge.EVENT_BUS.post(event);

        pro.setPosition(vec.x, vec.y, vec.z);
        pro.setElement(event.getElement());
        pro.setForce(event.getForce());
        pro.setTickTime(event.getTick());
        pro.setManaType(event.getType());
        pro.setTraceTarget(event.getTrace());
        pro.setOwner(playerIn);
        playerIn.world.addEntity(pro);
    }

    private static float getVelocity(EntityType type)
    {
        if(type == ModEntites.mana_orb)
            return ORB_VELOCITY;

        if(type == ModEntites.mana_laser)
            return LASER_VELOCITY;

        if(type == ModEntites.mana_star)
            return STAR_VELOCITY;

        return 1.0f;
    }

    private static float getINACCURACY(EntityType type)
    {
        if(type == ModEntites.mana_orb)
            return ORB_INACCURACY;

        if(type == ModEntites.mana_laser)
            return LASER_INACCURACY;

        if(type == ModEntites.mana_star)
            return STAR_INACCURACY;

        return 0.5f;
    }

    public static Entity getEntityLookedAt(Entity e) {
        Entity foundEntity = null;

        final double finalDistance = 64;
        double distance = finalDistance;
        RayTraceResult pos = raycast(e, finalDistance);
        Vector3d positionVector = e.getPositionVec();
        if (e instanceof PlayerEntity) {
            positionVector = positionVector.add(0, e.getEyeHeight(), 0);
        }

        if (pos != null) {
            distance = pos.getHitVec().distanceTo(positionVector);
        }

        Vector3d lookVector = e.getLookVec();
        Vector3d reachVector = positionVector.add(lookVector.x * finalDistance, lookVector.y * finalDistance, lookVector.z * finalDistance);

        Entity lookedEntity = null;
        List<Entity> entitiesInBoundingBox = e.getEntityWorld().getEntitiesWithinAABBExcludingEntity(e, e.getBoundingBox().grow(lookVector.x * finalDistance, lookVector.y * finalDistance, lookVector.z * finalDistance).grow(1F, 1F, 1F));
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

    public static boolean sameLikeOwner(Entity owner, Entity other)
    {
        boolean flag = false;

        if(owner instanceof PlayerEntity && other instanceof PlayerEntity)
            flag = true;

        if(!(owner instanceof PlayerEntity) && !(other instanceof PlayerEntity))
            flag = true;

        return flag;
    }
}
