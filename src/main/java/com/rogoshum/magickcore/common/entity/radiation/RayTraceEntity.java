package com.rogoshum.magickcore.common.entity.radiation;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.radiation.RayRadiateRenderer;
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
            if(entity == null && traceContext.uuid != MagickCore.emptyUUID && !this.level.isClientSide) {
                entity = ((ServerWorld) this.level).getEntity(traceContext.uuid);
                traceContext.entity = entity;
            } else if(entity != null && entity.isAlive()) {
                Vector3d goal = new Vector3d(entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ());
                Vector3d self = new Vector3d(this.getX(), this.getY(), this.getZ());
                spellContext().addChild(DirectionContext.create(goal.subtract(self).normalize()));
            }
        }
        Entity target = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            Vector3d direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
            target = MagickReleaseHelper.getEntityRayTrace(this, this.position().add(direction.scale(0.5)), direction, getLength(), false);
        } else if (getOwner() != null) {
            target = MagickReleaseHelper.getEntityRayTrace(this, this.position().add(getOwner().getLookAngle().scale(0.5)), getOwner().getLookAngle(), getLength(), false);
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
            result = this.world().clip(new RayTraceContext(this.position(), this.position().add(direction.normalize().scale(getLength())), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, null));
        } else if (getOwner() != null) {
            result = this.world().clip(new RayTraceContext(this.position(), this.position().add(getOwner().getLookAngle().scale(getLength())), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, null));
        }

        if(result != null && result.getType() != RayTraceResult.Type.MISS){
            List<BlockPos> list = new ArrayList<>();
            list.add(result.getBlockPos());
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
        Vector3d target = this.position();
        Vector3d dir = Vector3d.ZERO;
        if(spellContext().containChild(LibContext.DIRECTION))
            dir = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        else if (getOwner() != null)
            dir = getOwner().getLookAngle().normalize();
        target = target.add(dir.scale(getLength()));

        Entity entity = MagickReleaseHelper.getEntityRayTrace(this, this.position(), dir, getLength());
        if(entity != null)
            target = entity.position().add(0, entity.getBbHeight() * 0.5, 0);
        BlockRayTraceResult result = this.world().clip(new RayTraceContext(this.position(), this.position().add(dir.scale(getLength())), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, null));
        if(result.getType() != RayTraceResult.Type.MISS)
            target = Vector3d.atCenterOf(result.getBlockPos());

        float distance = (10f * spellContext().range);

        float scale = 0.1f;

        for (int i = 0; i < distance; i++) {
            double trailFactor = i / (distance - 1.0D);
            double tx = this.getX() + (target.x - this.getX()) * trailFactor + level.random.nextGaussian() * 0.005;
            double ty = this.getY() + (target.y - this.getY()) * trailFactor + level.random.nextGaussian() * 0.005;
            double tz = this.getZ() + (target.z - this.getZ()) * trailFactor + level.random.nextGaussian() * 0.005;
            LitParticle par = new LitParticle(this.level, spellContext().element.getRenderer().getParticleTexture()
                    , new Vector3d(tx, ty, tz), scale, scale, 1.0f, particleAge, spellContext().element.getRenderer());
            par.setLimitScale();
            par.setGlow();
            par.addMotion(MagickCore.getNegativeToOne() * 0.2f, MagickCore.getNegativeToOne() * 0.2f, MagickCore.getNegativeToOne() * 0.2f);
            MagickCore.addMagickParticle(par);
        }
    }
}
