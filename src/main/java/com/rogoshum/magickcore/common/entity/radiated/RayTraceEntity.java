package com.rogoshum.magickcore.common.entity.radiated;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaRadiateEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class RayTraceEntity extends ManaRadiateEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/ray_trace.png");
    public RayTraceEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void applyParticle() {
        applyParticle(2);
    }

    @Override
    public void successFX() {
        applyParticle(20);
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        Entity target = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            Vector3d direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction;
            target = MagickReleaseHelper.getEntityRayTrace(this, this.getPositionVec(), direction, spellContext().range * 5);
        } else if (getOwner() != null) {
            target = MagickReleaseHelper.getEntityRayTrace(this, this.getPositionVec(), getOwner().getLookVec(), spellContext().range * 5);
        }

        List<Entity> list = new ArrayList<>();
        if(target != null)
            list.add(target);
        return list;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.RADIATE_DEFAULT;
    }

    protected void applyParticle(int particleAge) {
        Vector3d target = this.getPositionVec();
        if(spellContext().containChild(LibContext.DIRECTION)) {
            Vector3d direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction;
            target = target.add(direction.normalize().scale(spellContext().range * 5));

            Entity entity = MagickReleaseHelper.getEntityRayTrace(this, this.getPositionVec(), direction, spellContext().range * 5);
            if(entity != null)
                target = entity.getPositionVec().add(0, entity.getHeight() / 2, 0);
        } else if (getOwner() != null){
            target = target.add(getOwner().getLookVec().normalize().scale(spellContext().range * 5));

            Entity entity = MagickReleaseHelper.getEntityRayTrace(this, this.getPositionVec(), getOwner().getLookVec(), spellContext().range * 5);
            if(entity != null)
                target = entity.getPositionVec().add(0, entity.getHeight() / 2, 0);
        }

        int distance = (int) (50 * spellContext().range);

        float scale = 0.1f;

        for (int i = 0; i < distance; i++) {
            double trailFactor = i / (distance - 1.0D);
            double tx = this.getPosX() + (target.x - this.getPosX()) * trailFactor + world.rand.nextGaussian() * 0.005;
            double ty = this.getPosY() + (target.y - this.getPosY()) * trailFactor + world.rand.nextGaussian() * 0.005;
            double tz = this.getPosZ() + (target.z - this.getPosZ()) * trailFactor + world.rand.nextGaussian() * 0.005;
            LitParticle par = new LitParticle(this.world, spellContext().element.getRenderer().getParticleTexture()
                    , new Vector3d(tx, ty, tz), scale, scale, 1.0f, particleAge, spellContext().element.getRenderer());
            par.setLimitScale();
            par.setGlow();
            par.addMotion(MagickCore.getNegativeToOne() * 0.2f, MagickCore.getNegativeToOne() * 0.2f, MagickCore.getNegativeToOne() * 0.2f);
            MagickCore.addMagickParticle(par);
        }
    }
}
