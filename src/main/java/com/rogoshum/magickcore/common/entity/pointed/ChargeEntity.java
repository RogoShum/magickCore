package com.rogoshum.magickcore.common.entity.pointed;

import com.google.common.collect.Lists;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.client.entity.easyrender.ChargerRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.init.ModDataSerializers;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ChargeEntity extends ManaEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/charge.png");
    private static final DataParameter<Integer> TARGET = EntityDataManager.defineId(ChargeEntity.class, DataSerializers.INT);
    private static final DataParameter<Vector3d> FORMER = EntityDataManager.defineId(ChargeEntity.class, ModDataSerializers.VECTOR3D);
    protected float charge;
    protected Entity target;
    private ManaFactor former;

    public ChargeEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        entityData.define(TARGET, -1);
        entityData.define(FORMER, new Vector3d(1, 1, 1));
    }

    public Entity getTarget() {
        return target;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new ChargerRenderer(this);
    }

    public Vector3d getFormer() {
        return entityData.get(FORMER);
    }

    public void setFormer(Vector3d vec) {
        entityData.set(FORMER, vec);
    }

    @Override
    protected void makeSound() {
        if(spellContext().element == ModElements.SOLAR) {
            if(this.tickCount == 1)
                this.level.playSound(null, this, ModSounds.explosion.get(), this.getSoundSource(), 0.2f, 1.0f);
        } else if(tickCount % 20 == 0) {
            float pitch = (float)tickCount / (float)spellContext().tick;
            if(Float.isNaN(pitch))
                pitch = 0.0f;
            pitch *=2;
            this.playSound(ModSounds.ring_pointer.get(), 0.05f, pitch);
        }
    }

    @Override
    public boolean releaseMagick() {
        if(level.isClientSide) {
            Vector3d former = getFormer();
            this.former = ManaFactor.create((float) former.x, (float) former.y, (float) former.z);
        } else if(former != null) {
            setFormer(new Vector3d(former.force, former.range, former.tick));
        }
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

        charge+=0.001f * spellContext().force;
        if(level.isClientSide) {
            target = level.getEntity(entityData.get(TARGET));
        } else {
            if(target != null && !target.isAlive())
                target = null;
            if(target != null)
                entityData.set(TARGET, target.getId());
            else
                entityData.set(TARGET, -1);
        }
        return false;
    }

    @Override
    public void remove() {
        if(!spellContext().valid()) return;
        ConditionContext condition = null;
        if(spellContext().containChild(LibContext.CONDITION))
            condition = spellContext().getChild(LibContext.CONDITION);
        List<Entity> livings = findEntity();
        for(Entity living : livings) {
            if(living != this && !suitableEntity(living)) continue;
            boolean pass = true;
            if(condition != null) {
                if(!condition.test(this, living))
                    pass = false;
            }
            if(pass) {
                MagickContext context = MagickContext.create(this.level, spellContext().postContext)
                        .<MagickContext>replenishChild(DirectionContext.create(getPostDirection(living)))
                        .caster(getOwner()).projectile(this)
                        .victim(living).noCost();
                MagickReleaseHelper.releaseMagick(beforeCast(context));
            }
        }
        super.remove();
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        if(target == null || !target.isAlive())
            return Lists.newArrayList(this);
        return Lists.newArrayList(target);
    }

    @Override
    public Vector3d getPostDirection(Entity entity) {
        if(entity == this && spellContext().containChild(LibContext.DIRECTION))
            return spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction;
        return super.getPostDirection(entity);
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public void beforeJoinWorld(MagickContext context) {
        target = context.victim;
        if(context.projectile instanceof IManaEntity) {
            former = ((IManaEntity) context.projectile).getManaFactor();
        }
    }

    @Override
    public ManaFactor getManaFactor() {
        if(former != null)
            return ManaFactor.create(1.0f + (former.force <= 1.0f ? (1.0f - former.force) : former.force) * charge + charge
                    , 1.0f + (former.range <= 1.0f ? (1.0f - former.range) : former.range) * charge + charge
                    , 1.0f + (former.tick <= 1.0f ? (1.0f - former.tick) : former.tick) * charge + charge * 20);
        return ManaFactor.create(1.0f + charge, 1.0f + charge, 1.0f + charge * 20);
    }

    @Override
    protected void applyParticle() {
        double width = (this.getBbWidth()+charge) * 0.75;
        LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * width + this.getX()
                , MagickCore.getNegativeToOne() * width + this.getY() + this.getBbHeight() * 0.5
                , MagickCore.getNegativeToOne() * width + this.getZ())
                , charge, charge, 0.5f, 15, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setGlow();
        Vector3d direction = this.position().add(0, this.getBbHeight() * 0.5, 0).subtract(par.positionVec()).scale(0.1);
        par.addMotion(direction.x, direction.y, direction.z);
        par.setParticleGravity(0f);
        par.setShakeLimit(15f);
        MagickCore.addMagickParticle(par);

        if(target == null) return;
        LitParticle litPar = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * target.getBbWidth() * 0.25 + target.getX()
                , MagickCore.getNegativeToOne() * target.getBbWidth() * 0.25 + target.getY() + target.getBbHeight() * 0.5
                , MagickCore.getNegativeToOne() * target.getBbWidth() * 0.25 + target.getZ())
                , charge * 0.5f, charge * 0.5f, 0.5f, 15, MagickCore.proxy.getElementRender(spellContext().element.type()));
        litPar.setGlow();
        litPar.setParticleGravity(0f);
        litPar.setShakeLimit(15f);
        litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1);
        MagickCore.addMagickParticle(litPar);

        if(target != null && spellContext().tick - tickCount < 6) {
            int distance = Math.max((int) (2 * target.position().distanceTo(this.position())), 1);
            Vector3d end = this.position().add(0, this.getBbHeight() * 0.5, 0);
            Vector3d start = target.position().add(0, target.getBbHeight() * 0.5, 0);
            for (int i = 0; i < distance; i++) {
                double trailFactor = i / (distance - 1.0D);
                Vector3d pos = ParticleUtil.drawLine(start, end, trailFactor);
                par = new LitParticle(this.level, spellContext().element.getRenderer().getParticleTexture()
                        , new Vector3d(pos.x, pos.y, pos.z), charge * 0.5f, charge * 0.5f, 1.0f, 1, spellContext().element.getRenderer());
                par.setParticleGravity(0);
                par.setLimitScale();
                par.setGlow();
                MagickCore.addMagickParticle(par);
            }
        }
    }
}
