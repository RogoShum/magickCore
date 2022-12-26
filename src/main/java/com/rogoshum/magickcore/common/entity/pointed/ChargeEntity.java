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
    private static final DataParameter<Integer> TARGET = EntityDataManager.createKey(ChargeEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Vector3d> FORMER = EntityDataManager.createKey(ChargeEntity.class, ModDataSerializers.VECTOR3D);
    protected float charge;
    protected Entity target;
    private ManaFactor former;

    public ChargeEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        dataManager.register(TARGET, -1);
        dataManager.register(FORMER, new Vector3d(1, 1, 1));
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
        return dataManager.get(FORMER);
    }

    public void setFormer(Vector3d vec) {
        dataManager.set(FORMER, vec);
    }

    @Override
    protected void makeSound() {
        if(spellContext().element == ModElements.SOLAR) {
            if(this.ticksExisted == 1)
                this.world.playMovingSound(null, this, ModSounds.explosion.get(), this.getSoundCategory(), 0.05f, 1.0f);
        } else if(ticksExisted % 20 == 0) {
            float pitch = (float)ticksExisted / (float)spellContext().tick;
            if(Float.isNaN(pitch))
                pitch = 0.0f;
            pitch *=2;
            this.playSound(ModSounds.ring_pointer.get(), 0.05f, pitch);
        }
    }

    @Override
    public boolean releaseMagick() {
        if(world.isRemote) {
            Vector3d former = getFormer();
            this.former = ManaFactor.create((float) former.x, (float) former.y, (float) former.z);
        } else if(former != null) {
            setFormer(new Vector3d(former.force, former.range, former.tick));
        }
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

        charge+=0.001f * spellContext().force;
        if(world.isRemote) {
            target = world.getEntityByID(dataManager.get(TARGET));
        } else {
            if(target != null && !target.isAlive())
                target = null;
            if(target != null)
                dataManager.set(TARGET, target.getEntityId());
            else
                dataManager.set(TARGET, -1);
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
                MagickContext context = MagickContext.create(this.world, spellContext().postContext)
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
        double width = (this.getWidth()+charge) * 0.75;
        LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * width + this.getPosX()
                , MagickCore.getNegativeToOne() * width + this.getPosY() + this.getHeight() * 0.5
                , MagickCore.getNegativeToOne() * width + this.getPosZ())
                , charge, charge, 0.5f, 15, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setGlow();
        Vector3d direction = this.getPositionVec().add(0, this.getHeight() * 0.5, 0).subtract(par.positionVec()).scale(0.1);
        par.addMotion(direction.x, direction.y, direction.z);
        par.setParticleGravity(0f);
        par.setShakeLimit(15f);
        MagickCore.addMagickParticle(par);

        if(target == null) return;
        LitParticle litPar = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * target.getWidth() * 0.25 + target.getPosX()
                , MagickCore.getNegativeToOne() * target.getWidth() * 0.25 + target.getPosY() + target.getHeight() * 0.5
                , MagickCore.getNegativeToOne() * target.getWidth() * 0.25 + target.getPosZ())
                , charge * 0.5f, charge * 0.5f, 0.5f, 15, MagickCore.proxy.getElementRender(spellContext().element.type()));
        litPar.setGlow();
        litPar.setParticleGravity(0f);
        litPar.setShakeLimit(15f);
        litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1);
        MagickCore.addMagickParticle(litPar);

        if(target != null && spellContext().tick - ticksExisted < 6) {
            int distance = Math.max((int) (2 * target.getPositionVec().distanceTo(this.getPositionVec())), 1);
            Vector3d end = this.getPositionVec().add(0, this.getHeight() * 0.5, 0);
            Vector3d start = target.getPositionVec().add(0, target.getHeight() * 0.5, 0);
            for (int i = 0; i < distance; i++) {
                double trailFactor = i / (distance - 1.0D);
                Vector3d pos = ParticleUtil.drawLine(start, end, trailFactor);
                par = new LitParticle(this.world, spellContext().element.getRenderer().getParticleTexture()
                        , new Vector3d(pos.x, pos.y, pos.z), charge * 0.5f, charge * 0.5f, 1.0f, 1, spellContext().element.getRenderer());
                par.setParticleGravity(0);
                par.setLimitScale();
                par.setGlow();
                MagickCore.addMagickParticle(par);
            }
        }
    }
}
