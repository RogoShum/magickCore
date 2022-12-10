package com.rogoshum.magickcore.common.entity.radiated;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.radiate.RayRadiateRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.superrender.RadianceWellRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaRadiateEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RayTraceEntity extends ManaRadiateEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/ray_trace.png");
    public RayTraceEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void applyParticle() {
    }

    @Override
    public void successFX() {
        applyParticle(10);
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new RayRadiateRenderer(this);
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        if(spellContext().containChild(LibContext.TRACE)) {
            TraceContext traceContext = spellContext().getChild(LibContext.TRACE);
            Entity entity = traceContext.entity;
            if(entity == null && traceContext.uuid != MagickCore.emptyUUID && !this.world.isRemote) {
                entity = ((ServerWorld) this.world).getEntityByUuid(traceContext.uuid);
                traceContext.entity = entity;
            } else if(entity != null && entity.isAlive()) {
                Vector3d goal = new Vector3d(entity.getPosX(), entity.getPosY() + entity.getHeight() * 0.5, entity.getPosZ());
                Vector3d self = new Vector3d(this.getPosX(), this.getPosY(), this.getPosZ());
                spellContext().addChild(DirectionContext.create(goal.subtract(self).normalize()));
            }
        }
        Entity target = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            Vector3d direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
            target = MagickReleaseHelper.getEntityRayTrace(this, this.getPositionVec().add(direction.scale(0.5)), direction, getLength(), false);
        } else if (getOwner() != null) {
            target = MagickReleaseHelper.getEntityRayTrace(this, this.getPositionVec().add(getOwner().getLookVec().scale(0.5)), getOwner().getLookVec(), getLength(), false);
        }

        List<Entity> list = new ArrayList<>();
        if(target != null)
            list.add(target);
        return list;
    }

    public float getLength() {
        return spellContext().range * 5;
    }

    @Override
    public Iterable<BlockPos> findBlocks() {
        BlockRayTraceResult result = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            Vector3d direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction;
            result = this.world().rayTraceBlocks(new RayTraceContext(this.getPositionVec(), this.getPositionVec().add(direction.normalize().scale(getLength())), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, null));
        } else if (getOwner() != null) {
            result = this.world().rayTraceBlocks(new RayTraceContext(this.getPositionVec(), this.getPositionVec().add(getOwner().getLookVec().scale(getLength())), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, null));
        }

        if(result != null && result.getType() != RayTraceResult.Type.MISS){
            List<BlockPos> list = new ArrayList<>();
            list.add(result.getPos());
            return list;
        }
        return super.findBlocks();
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
        Vector3d dir = Vector3d.ZERO;
        if(spellContext().containChild(LibContext.DIRECTION))
            dir = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        else if (getOwner() != null)
            dir = getOwner().getLookVec().normalize();
        target = target.add(dir.scale(getLength()));

        Entity entity = MagickReleaseHelper.getEntityRayTrace(this, this.getPositionVec(), dir, getLength());
        if(entity != null)
            target = entity.getPositionVec().add(0, entity.getHeight() * 0.5, 0);
        BlockRayTraceResult result = this.world().rayTraceBlocks(new RayTraceContext(this.getPositionVec(), this.getPositionVec().add(dir.scale(getLength())), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, null));
        if(result.getType() != RayTraceResult.Type.MISS)
            target = Vector3d.copyCentered(result.getPos());

        float distance = (10f * spellContext().range);

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
