package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IRedStoneEntity;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.RedStoneRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class RedStoneEntity extends ManaProjectileEntity implements IRedStoneEntity {
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.2f, 1.0f, 1.0f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/red_stone.png");
    public RedStoneEntity(EntityType<? extends ThrowableProjectile> type, Level worldIn) {
        super(type, worldIn);
    }
    public Vec3 clientMotion = Vec3.ZERO;
    private BlockPos blockPos;
    private boolean dead = false;

    @Override
    public void remove(RemovalReason reason) {
        dead = true;
        onRemoveRedStone(blockPos, level);
        super.remove(reason);
    }


    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new RedStoneRenderer(this);
    }

    @Override
    public void tick() {
        super.tick();
        clientMotion = getDeltaMovement().add(clientMotion);
        blockPos = onTickRedStone(position(), blockPos, level);
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
    public ManaFactor getManaFactor() {
        return MANA_FACTOR;
    }

    @Override
    public boolean hitEntityRemove(EntityHitResult entityRayTraceResult) {
        return false;
    }

    @Override
    protected float getGravity() {
        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level.getBlockState(blockpos);
        if (!blockstate.isAir()) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vector3d1 = this.position();

                for(AABB axisalignedbb : voxelshape.toAabbs()) {
                    if (axisalignedbb.move(blockpos).contains(vector3d1)) {
                        return 0;
                    }
                }
            }
        }
        return 0.03f;
    }

    @Override
    protected void onHitBlock(BlockHitResult p_230299_1_) {
        Vec3 pos = Vec3.atCenterOf(p_230299_1_.getBlockPos());
        Vec3 vec = this.positionVec().add(0, this.getBbHeight() / 2, 0);
        this.setDeltaMovement(vec.subtract(pos).normalize().scale(this.getDeltaMovement().length() * 0.4).add(getDeltaMovement().scale(0.5)));
        if(pos.y < this.positionVec().y && getDeltaMovement().length() > 0.1)
            this.setDeltaMovement(getDeltaMovement().scale(1.8 * getBbWidth()));
        if(spellContext().containChild(LibContext.CONDITION)) {
            ConditionContext condition = spellContext().getChild(LibContext.CONDITION);
            if(!condition.test(null, level.getBlockState(p_230299_1_.getBlockPos()).getBlock()))
                return;
        }
        BlockState blockstate = this.level.getBlockState(p_230299_1_.getBlockPos());
        blockstate.onProjectileHit(this.level, blockstate, p_230299_1_, this);
        MagickContext context = MagickContext.create(level, spellContext().postContext).<MagickContext>applyType(ApplyType.HIT_BLOCK).noCost().caster(this.getCaster()).projectile(this);
        PositionContext positionContext = PositionContext.create(Vec3.atLowerCornerOf(p_230299_1_.getBlockPos()));
        context.addChild(positionContext);
        MagickReleaseHelper.releaseMagick(beforeCast(context));

        context = MagickContext.create(level, spellContext().postContext).doBlock().noCost().caster(this.getCaster()).projectile(this);
        context.addChild(positionContext);
        MagickReleaseHelper.releaseMagick(beforeCast(context));
    }

    @Override
    protected void applyParticle() {
        LitParticle par = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() + this.getX()
                , MagickCore.getNegativeToOne() * this.getBbWidth() + this.getY() + this.getBbHeight() / 2
                , MagickCore.getNegativeToOne() * this.getBbWidth() + this.getZ())
                , getBbWidth() * 0.2f, getBbWidth() * 0.2f, 1.0f, 20, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setGlow();
        par.setLimitScale();
        MagickCore.addMagickParticle(par);
    }

    @Override
    public float eyeHeight() {
        return 0;
    }

    @Override
    public int getPower() {
        return dead ? 0 : 15;
    }
}
