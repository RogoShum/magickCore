package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.api.magick.context.child.PositionContext;
import com.rogoshum.magickcore.api.magick.context.child.TraceContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

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
    public RepeaterEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public void setSpawnEntity(Entity entity) {
        if(entity instanceof IManaEntity)
            spawnEntity = entity;
    }

    @Override
    protected void makeSound() {
        if (this.tickCount == 1) {
            this.playSound(ModSounds.soft_buildup_high.get(), 0.5F, 1.0F + this.random.nextFloat());
        }
    }


    @Override
    public boolean releaseMagick() {
        if(!spellContext().valid()) return false;
        if(level.isClientSide) return false;

        if(cool_down >= 0)
            cool_down -= Math.max(this.spellContext().force() * 3, 1);

        if(spawnEntity != null && spawnEntity.isAlive()) return false;
        if(cool_down < 0) {
            this.playSound(ModSounds.thunder.get(), 0.15F, 1.0F - this.random.nextFloat());
            MagickContext context = MagickContext.create(this.level, spellContext().postContext())
                    .<MagickContext>replenishChild(PositionContext.create(this.position()))
                    .caster(getCaster()).projectile(this).noCost();

            if(spellContext().containChild(LibContext.TRACE)) {
                TraceContext traceContext = spellContext().getChild(LibContext.TRACE);
                context.addChild(traceContext);
                Entity entity = traceContext.entity;
                if(entity == null && traceContext.uuid != MagickCore.emptyUUID && !this.level.isClientSide) {
                    entity = ((ServerLevel) this.level).getEntity(traceContext.uuid);
                    traceContext.entity = entity;
                    if(entity == null)
                        traceContext.uuid = MagickCore.emptyUUID;
                }
                if(entity != null && entity.isAlive()) {
                    Vec3 goal = new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ());
                    Vec3 self = new Vec3(this.getX(), this.getY(), this.getZ());
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
        return true;
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
        LitParticle litPar = new LitParticle(this.level, this.spellContext().element().getRenderer().getMistTexture()
                , new Vec3(this.getX()
                , this.getY() + this.getBbHeight() * 0.5
                , this.getZ())
                , MagickCore.getRandFloat() * 0.25f, MagickCore.getRandFloat() * 0.25f
                , MagickCore.getRandFloat()
                , this.spellContext().element().getRenderer().getParticleRenderTick(), this.spellContext().element().getRenderer());
        litPar.setGlow();
        litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1);
        MagickCore.addMagickParticle(litPar);

        float height = Float.parseFloat(String.format("%.1f", this.random.nextFloat())) * 0.6f;
        float width = Float.parseFloat(String.format("%.1f", this.random.nextFloat())) * 0.6f;
        LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleSprite()
                , new Vec3(this.getX()
                , this.getY() + this.getBbHeight() * 0.5
                , this.getZ())
                , width, height, 0.5f, 20, MagickCore.proxy.getElementRender(spellContext().element().type()));
        par.setGlow();
        par.setParticleGravity(0f);
        par.setLimitScale();
        par.setShakeLimit(5f);
        MagickCore.addMagickParticle(par);
    }
}
