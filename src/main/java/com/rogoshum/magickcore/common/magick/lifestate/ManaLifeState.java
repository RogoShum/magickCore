package com.rogoshum.magickcore.common.magick.lifestate;

import com.rogoshum.magickcore.common.api.event.EntityEvents;
import com.rogoshum.magickcore.common.entity.projectile.LifeStateEntity;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

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
        SpellContext data = lifeState.spellContext();
        MagickContext attribute = MagickContext.create(lifeState.world, data).caster(null).projectile(lifeState).victim(result.getEntity()).force(data.force / 5);
        MagickReleaseHelper.releaseMagick(attribute);
        lifeState.remove();
    }
}
