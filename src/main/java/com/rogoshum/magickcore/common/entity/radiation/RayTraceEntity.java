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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
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
    public RayTraceEntity(EntityType<?> entityTypeIn, Level worldIn) {
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
                entity = ((ServerLevel) this.level).getEntity(traceContext.uuid);
                traceContext.entity = entity;
            } else if(entity != null && entity.isAlive()) {
                Vec3 goal = new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ());
                Vec3 self = new Vec3(this.getX(), this.getY(), this.getZ());
                spellContext().addChild(DirectionContext.create(goal.subtract(self).normalize()));
            }
        }
        Entity target = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            Vec3 direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
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
        BlockHitResult result = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            Vec3 direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction;
            result = this.world().clip(new ClipContext(this.position(), this.position().add(direction.normalize().scale(getLength())), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));
        } else if (getOwner() != null) {
            result = this.world().clip(new ClipContext(this.position(), this.position().add(getOwner().getLookAngle().scale(getLength())), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));
        }

        if(result != null && result.getType() != HitResult.Type.MISS){
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
        Vec3 target = this.position();
        Vec3 dir = Vec3.ZERO;
        if(spellContext().containChild(LibContext.DIRECTION))
            dir = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        else if (getOwner() != null)
            dir = getOwner().getLookAngle().normalize();
        target = target.add(dir.scale(getLength()));

        Entity entity = MagickReleaseHelper.getEntityRayTrace(this, this.position(), dir, getLength());
        if(entity != null)
            target = entity.position().add(0, entity.getBbHeight() * 0.5, 0);
        BlockHitResult result = this.world().clip(new ClipContext(this.position(), this.position().add(dir.scale(getLength())), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));
        if(result.getType() != HitResult.Type.MISS)
            target = Vec3.atCenterOf(result.getBlockPos());

        float distance = (10f * spellContext().range);

        float scale = 0.1f;

        for (int i = 0; i < distance; i++) {
            double trailFactor = i / (distance - 1.0D);
            double tx = this.getX() + (target.x - this.getX()) * trailFactor + level.random.nextGaussian() * 0.005;
            double ty = this.getY() + (target.y - this.getY()) * trailFactor + level.random.nextGaussian() * 0.005;
            double tz = this.getZ() + (target.z - this.getZ()) * trailFactor + level.random.nextGaussian() * 0.005;
            LitParticle par = new LitParticle(this.level, spellContext().element.getRenderer().getParticleTexture()
                    , new Vec3(tx, ty, tz), scale, scale, 1.0f, particleAge, spellContext().element.getRenderer());
            par.setLimitScale();
            par.setGlow();
            par.addMotion(MagickCore.getNegativeToOne() * 0.2f, MagickCore.getNegativeToOne() * 0.2f, MagickCore.getNegativeToOne() * 0.2f);
            MagickCore.addMagickParticle(par);
        }
    }
}
