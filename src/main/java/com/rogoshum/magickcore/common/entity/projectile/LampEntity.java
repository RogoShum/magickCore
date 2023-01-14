package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IExistTick;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.enums.TargetType;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModSounds;
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
import net.minecraft.util.SoundEvents;
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
    protected void makeSound() {
        if (this.tickCount == 1) {
            this.playSound(ModSounds.flame.get(), 0.25F, 1.0F + this.random.nextFloat());
        }
        if(this.tickCount % 20 == 0)
            this.playSound(SoundEvents.FIRE_AMBIENT, 0.5F, 1.0F + this.random.nextFloat());
    }

    @Override
    public void tick() {
        super.tick();
        if(isInWater())
            this.remove();

        if(spellContext().containChild(LibContext.TRACE)) {
            TraceContext traceContext = spellContext().getChild(LibContext.TRACE);
            if(traceContext.entity == null) {
                List<Entity> entityList = this.level.getEntities(this, this.getBoundingBox().inflate(spellContext().range), null);
                for(int i = 0; i < entityList.size(); ++i) {
                    Entity entity = entityList.get(i);
                    if(!suitableEntity(entity)) continue;
                    AtomicReference<Boolean> pass = new AtomicReference<>(true);
                    if(spellContext().containChild(LibContext.CONDITION)) {
                        ConditionContext context = spellContext().getChild(LibContext.CONDITION);
                        if(!context.test(this.getOwner(), entity))
                            pass.set(false);
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
    protected float getGravity() {
        return 0;
    }

    @Override
    protected void applyParticle() {
        float partial = Minecraft.getInstance().getFrameTime();
        double x = MathHelper.lerp(partial, this.xOld, this.getX());
        double y = MathHelper.lerp(partial, this.yOld, this.getY());
        double z = MathHelper.lerp(partial, this.zOld, this.getZ());
        float scale = Math.max(this.getBbWidth(), 0.5f) * 0.5f;
        for (int i = 0; i < 2; ++i) {
            LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleSprite()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.3 + x
                    , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.3 + y
                    , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.3 + z)
                    , scale, scale * 1.2f, 0.5f, 15, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0f);
            par.addMotion(0, 0.1 * getBbWidth(), 0);
            par.setLimitScale();
            par.setShakeLimit(15f);
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult p_230299_1_) {
        Vector3d pos = Vector3d.atCenterOf(p_230299_1_.getBlockPos());
        Vector3d vec = this.positionVec().add(0, this.getBbHeight() / 2, 0);
        this.setDeltaMovement(vec.subtract(pos).normalize().scale(this.getDeltaMovement().length()));
        if(spellContext().containChild(LibContext.CONDITION)) {
            ConditionContext condition = spellContext().getChild(LibContext.CONDITION);
            if(!condition.test(null, level.getBlockState(p_230299_1_.getBlockPos()).getBlock()))
                return;
        }
        BlockState blockstate = this.level.getBlockState(p_230299_1_.getBlockPos());
        blockstate.onProjectileHit(this.level, blockstate, p_230299_1_, this);
        MagickContext context = MagickContext.create(level, spellContext().postContext).<MagickContext>applyType(ApplyType.HIT_BLOCK).noCost().caster(this.getOwner()).projectile(this);
        PositionContext positionContext = PositionContext.create(Vector3d.atLowerCornerOf(p_230299_1_.getBlockPos()));
        context.addChild(positionContext);
        MagickReleaseHelper.releaseMagick(beforeCast(context));

        context = MagickContext.create(level, spellContext().postContext).doBlock().noCost().caster(this.getOwner()).projectile(this);
        context.addChild(positionContext);
        MagickReleaseHelper.releaseMagick(beforeCast(context));
    }

    @Override
    public int getTickThatNeedExistingBeforeRemove() {
        return spellContext().tick * 2;
    }
}
