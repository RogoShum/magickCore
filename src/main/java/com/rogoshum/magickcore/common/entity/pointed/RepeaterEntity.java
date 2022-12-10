package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class RepeaterEntity extends ManaPointEntity {
    private static final ManaFactor FACTOR = ManaFactor.create(0.2f, 1.0f, 0.5f);
    private static final List<Entity> EMPTY = Collections.emptyList();
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/repeater.png");
    public byte cool_down = 0;
    private Entity spawnEntity;
    public RepeaterEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public void setSpawnEntity(Entity entity) {
        if(entity instanceof IManaEntity)
            spawnEntity = entity;
    }



    @Override
    public void releaseMagick() {
        if(!spellContext().valid()) return;
        if(world.isRemote) return;
        if(cool_down >= 0)
            cool_down -= this.spellContext().force * 3;

        if(spawnEntity != null && spawnEntity.isAlive()) return;
        if(cool_down < 0) {
            MagickContext context = MagickContext.create(this.world, spellContext().postContext)
                    .<MagickContext>replenishChild(PositionContext.create(this.getPositionVec()))
                    .caster(getOwner()).projectile(this).noCost();

            if(spellContext().containChild(LibContext.TRACE)) {
                TraceContext traceContext = spellContext().getChild(LibContext.TRACE);
                context.addChild(traceContext);
                Entity entity = traceContext.entity;
                if(entity == null && traceContext.uuid != MagickCore.emptyUUID && !this.world.isRemote) {
                    entity = ((ServerWorld) this.world).getEntityByUuid(traceContext.uuid);
                    traceContext.entity = entity;
                    if(entity == null)
                        traceContext.uuid = MagickCore.emptyUUID;
                }
                if(entity != null && entity.isAlive()) {
                    Vector3d goal = new Vector3d(entity.getPosX(), entity.getPosY() + entity.getHeight() * 0.5, entity.getPosZ());
                    Vector3d self = new Vector3d(this.getPosX(), this.getPosY(), this.getPosZ());
                    context.replenishChild(DirectionContext.create(goal.subtract(self).normalize()));
                } else if(spellContext().containChild(LibContext.DIRECTION)) {
                    context.replenishChild(spellContext().getChild(LibContext.DIRECTION));
                }
            } else if(spellContext().containChild(LibContext.DIRECTION)) {
                context.replenishChild(spellContext().getChild(LibContext.DIRECTION));
            }
            MagickReleaseHelper.releaseMagick(beforeCast(context));
            cool_down = 20;
        }
    }

    @Override
    protected boolean fixedPosition() {
        return false;
    }

    @Override
    public void beforeJoinWorld(MagickContext context) {
        //if(context.containChild(LibContext.DIRECTION))
            //spellContext().replenishChild(context.getChild(LibContext.DIRECTION));
    }

    @Override
    public void reSize() {
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return EMPTY;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return FACTOR;
    }

    @Override
    protected void applyParticle() {
        LitParticle litPar = new LitParticle(this.world, this.spellContext().element.getRenderer().getMistTexture()
                , new Vector3d(this.getPosX()
                , this.getPosY() + this.getHeight() * 0.5
                , this.getPosZ())
                , MagickCore.getRandFloat() * 0.25f, MagickCore.getRandFloat() * 0.25f
                , MagickCore.getRandFloat()
                , this.spellContext().element.getRenderer().getParticleRenderTick(), this.spellContext().element.getRenderer());
        litPar.setGlow();
        litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1);
        MagickCore.addMagickParticle(litPar);

        float height = Float.parseFloat(String.format("%.1f", this.rand.nextFloat())) * 0.6f;
        float width = Float.parseFloat(String.format("%.1f", this.rand.nextFloat())) * 0.6f;
        LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleSprite()
                , new Vector3d(this.getPosX()
                , this.getPosY() + this.getHeight() * 0.5
                , this.getPosZ())
                , width, height, 0.5f, 20, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setGlow();
        par.setParticleGravity(0f);
        par.setLimitScale();
        par.setShakeLimit(15f);
        MagickCore.addMagickParticle(par);
    }
}
