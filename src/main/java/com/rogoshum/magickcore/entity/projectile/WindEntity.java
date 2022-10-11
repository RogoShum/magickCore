package com.rogoshum.magickcore.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.enums.EnumTargetType;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.context.SpellContext;
import com.rogoshum.magickcore.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.magick.context.child.PositionContext;
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

public class WindEntity extends ManaProjectileEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mana_orb.png");
    public WindEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        //this.setNoGravity(true);
        List<Entity> entityList = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(spellContext().range), null);
        for(int i = 0; i < entityList.size(); ++i) {
            Entity entity = entityList.get(i);
            if(!suitableEntity(entity)) continue;
            AtomicReference<Boolean> pass = new AtomicReference<>(true);
            if(spellContext().containChild(LibContext.CONDITION)) {
                ConditionContext context = spellContext().getChild(LibContext.CONDITION);
                context.conditions.forEach((condition -> {
                    if(condition.getType() == EnumTargetType.TARGET) {
                        if(!condition.test(entity))
                            pass.set(false);
                    } else if(!condition.test(this.getOwner()))
                        pass.set(false);
                }));
            }
            if(pass.get()) {
                Vector3d motion = this.getPositionVec().add(0, getHeight() / 2, 0).subtract(entity.getPositionVec().add(0, entity.getHeight() / 2, 0)).normalize();
                motion = motion.scale(0.04 * getWidth());
                entity.addVelocity(motion.x, motion.y, motion.z);
            }
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.001f;
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult p_230299_1_) {
        BlockState blockstate = this.world.getBlockState(p_230299_1_.getPos());
        blockstate.onProjectileCollision(this.world, blockstate, p_230299_1_, this);
        MagickContext context = MagickContext.create(world, spellContext().postContext).<MagickContext>applyType(EnumApplyType.HIT_BLOCK).saveMana().caster(this.func_234616_v_()).projectile(this);
        PositionContext positionContext = PositionContext.create(Vector3d.copy(p_230299_1_.getPos()));
        context.addChild(positionContext);
        MagickReleaseHelper.releaseMagick(context);
        blockstate.onProjectileCollision(this.world, blockstate, p_230299_1_, this);
    }

    @Override
    public float getSourceLight() {
        return 3;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    protected void applyParticle() {
        float partial = Minecraft.getInstance().getRenderPartialTicks();
        double x = MathHelper.lerp(partial, this.lastTickPosX, this.getPosX());
        double y = MathHelper.lerp(partial, this.lastTickPosY, this.getPosY());
        double z = MathHelper.lerp(partial, this.lastTickPosZ, this.getPosZ());
        float times = Math.max(getWidth(), 2) * 5;
        int age = Math.max((int) (4 * spellContext().range), 10);
        for (int i = 0; i < times; ++i) {
            LitParticle par = new LitParticle(this.world, spellContext().element.getRenderer().getParticleTexture()
                    , new Vector3d(x , y + getHeight() / 2 , z)
                    , 0.1f, 0.1f, 1.0f, age, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0f);
            par.setLimitScale();
            par.setTraceTarget(this);
            MagickCore.addMagickParticle(par);
        }
    }
}
