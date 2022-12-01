package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IExistTick;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.enums.TargetType;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LampEntity extends ManaProjectileEntity implements IExistTick {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/lamp.png");
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.5f, 1.0f, 1.0f);
    public LampEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        if(isInWater())
            this.remove();

        if(spellContext().containChild(LibContext.TRACE)) {
            TraceContext traceContext = spellContext().getChild(LibContext.TRACE);
            if(traceContext.entity == null) {
                List<Entity> entityList = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(spellContext().range), null);
                for(int i = 0; i < entityList.size(); ++i) {
                    Entity entity = entityList.get(i);
                    if(!suitableEntity(entity)) continue;
                    AtomicReference<Boolean> pass = new AtomicReference<>(true);
                    if(spellContext().containChild(LibContext.CONDITION)) {
                        ConditionContext context = spellContext().getChild(LibContext.CONDITION);
                        context.conditions.forEach((condition -> {
                            if(condition.getType() == TargetType.TARGET) {
                                if(!condition.test(entity))
                                    pass.set(false);
                            } else if(!condition.test(this.getOwner()))
                                pass.set(false);
                        }));
                    }
                    if(pass.get()) {
                        traceContext.entity = entity;
                    }
                }
            }
        }
    }

    @Override
    public float getSourceLight() {
        return 10;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return MANA_FACTOR;
    }

    @Override
    protected float getGravityVelocity() {
        return 0;
    }

    @Override
    protected void applyParticle() {
        float partial = Minecraft.getInstance().getRenderPartialTicks();
        double x = MathHelper.lerp(partial, this.lastTickPosX, this.getPosX());
        double y = MathHelper.lerp(partial, this.lastTickPosY, this.getPosY());
        double z = MathHelper.lerp(partial, this.lastTickPosZ, this.getPosZ());
        float scale = Math.max(this.getWidth(), 0.5f) * 0.4f;
        for (int i = 0; i < 5; ++i) {
            LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleSprite()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + x
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + y + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + z)
                    , scale, scale * 1.2f, 0.5f, 15, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0f);
            par.addMotion(0, 0.1 * getWidth(), 0);
            par.setLimitScale();
            par.setShakeLimit(15f);
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult p_230299_1_) {
        BlockState blockstate = this.world.getBlockState(p_230299_1_.getPos());
        blockstate.onProjectileCollision(this.world, blockstate, p_230299_1_, this);
        MagickContext context = MagickContext.create(world, spellContext().postContext).<MagickContext>applyType(ApplyType.HIT_BLOCK).noCost().caster(this.func_234616_v_()).projectile(this);
        PositionContext positionContext = PositionContext.create(Vector3d.copy(p_230299_1_.getPos()));
        context.addChild(positionContext);
        MagickReleaseHelper.releaseMagick(context);

        Vector3d pos = Vector3d.copyCentered(p_230299_1_.getPos());
        Vector3d vec = this.positionVec().add(0, this.getHeight() / 2, 0);
        this.setMotion(vec.subtract(pos).normalize().scale(this.getMotion().length()));
    }

    @Override
    public int getTickThatNeedExistingBeforeRemove() {
        return spellContext().tick * 2;
    }
}
