package com.rogoshum.magickcore.common.entity.pointed;

import com.google.common.collect.Lists;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class ChargeEntity extends ManaEntity {
    private static final DataParameter<Integer> TARGET = EntityDataManager.createKey(ChargeEntity.class, DataSerializers.VARINT);
    protected float charge;
    protected Entity target;
    protected ManaFactor former;

    public ChargeEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        dataManager.register(TARGET, -1);
    }

    @Override
    public boolean releaseMagick() {
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
        if(target == null)
            return Lists.newArrayList(this);
        return Lists.newArrayList(target);
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return null;
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
            return ManaFactor.create(1.0f + (1.0f - former.force) * charge + charge
                    , 1.0f + (1.0f - former.range) * charge + charge
                    , 1.0f + (1.0f - former.tick) * charge + charge * 20);
        return ManaFactor.create(1.0f + charge, 1.0f + charge, 1.0f + charge * 20);
    }

    @Override
    protected void applyParticle() {
        for (int i = 0; i < 2; ++i) {
            LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() * 0.25 + this.getPosX()
                    , MagickCore.getNegativeToOne() * this.getWidth() * 0.25 + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getWidth() * 0.25 + this.getPosZ())
                    , charge, charge, 0.5f, 15, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0f);
            par.setLimitScale();
            par.setShakeLimit(15f);
            MagickCore.addMagickParticle(par);
        }

        if(target == null) return;
        LitParticle litPar = new LitParticle(this.world, this.spellContext().element.getRenderer().getMistTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + target.getPosX()
                , MagickCore.getNegativeToOne() * this.getWidth() / 2 + target.getPosY() + target.getHeight() / 2
                , MagickCore.getNegativeToOne() * this.getWidth() / 2 + target.getPosZ())
                , charge
                , charge
                , 0.5f * MagickCore.getRandFloat(), this.spellContext().element.getRenderer().getParticleRenderTick() / 2, this.spellContext().element.getRenderer());
        litPar.setGlow();
        litPar.setParticleGravity(0f);
        litPar.setShakeLimit(15.0f);
        litPar.setCanCollide(false);
        litPar.addMotion(MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.2);
        MagickCore.addMagickParticle(litPar);
    }
}
