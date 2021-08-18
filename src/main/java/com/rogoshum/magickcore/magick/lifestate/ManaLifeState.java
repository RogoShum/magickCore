package com.rogoshum.magickcore.magick.lifestate;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.capability.IManaItemData;
import com.rogoshum.magickcore.entity.LifeStateEntity;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;

public class ManaLifeState extends LifeState<Object>{
    @Override
    public INBT serialize() {
        return new CompoundNBT();
    }

    @Override
    public void deserialize(INBT value, World world) {
    }

    @Override
    public void onHitEntity(LifeStateEntity lifeState, EntityRayTraceResult result) {
        EntityEvents.HitEntityEvent event = new EntityEvents.HitEntityEvent(lifeState, result.getEntity());
        MinecraftForge.EVENT_BUS.post(event);
        IManaItemData data = lifeState.getElementData();
        ReleaseAttribute attribute = new ReleaseAttribute(null, lifeState, result.getEntity(), data.getTickTime(), data.getForce() / 5);
        MagickReleaseHelper.applyElementFunction(data.getElement(), data.getManaType(), attribute);
        lifeState.remove();
    }
}
