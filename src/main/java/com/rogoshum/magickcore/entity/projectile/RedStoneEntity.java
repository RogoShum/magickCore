package com.rogoshum.magickcore.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IRedStoneEntity;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.client.entity.easyrender.RedStoneRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.enums.ApplyType;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.context.child.PositionContext;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class RedStoneEntity extends ManaProjectileEntity implements IRedStoneEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/red_stone.png");
    public RedStoneEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }
    public Vector3d clientMotion = Vector3d.ZERO;

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(new RedStoneRenderer(this));
    }

    @Override
    public void tick() {
        super.tick();
        clientMotion = getMotion().add(clientMotion);
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
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        EntityEvents.HitEntityEvent event = new EntityEvents.HitEntityEvent(this, p_213868_1_.getEntity());
        MinecraftForge.EVENT_BUS.post(event);
        if(!suitableEntity(p_213868_1_.getEntity())) return;
        MagickContext context = MagickContext.create(world, spellContext().postContext).noCost().caster(this.getOwner()).projectile(this).victim(p_213868_1_.getEntity()).force(this.spellContext().force);
        MagickReleaseHelper.releaseMagick(context);
    }

    @Override
    protected float getGravityVelocity() {
        BlockPos blockpos = this.getPosition();
        BlockState blockstate = this.world.getBlockState(blockpos);
        if (!blockstate.isAir(this.world, blockpos)) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.world, blockpos);
            if (!voxelshape.isEmpty()) {
                Vector3d vector3d1 = this.getPositionVec();

                for(AxisAlignedBB axisalignedbb : voxelshape.toBoundingBoxList()) {
                    if (axisalignedbb.offset(blockpos).contains(vector3d1)) {
                        return 0;
                    }
                }
            }
        }
        return 0.03f;
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
        this.setMotion(vec.subtract(pos).normalize().scale(this.getMotion().length() * 0.4).add(getMotion().scale(0.5)));
        if(pos.y < this.positionVec().y && getMotion().length() > 0.1)
            this.setMotion(getMotion().scale(1.8 * getWidth()));
    }

    @Override
    protected void applyParticle() {
        LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() + this.getPosX()
                , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY() + this.getHeight() / 2
                , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                , getWidth() * 0.2f, getWidth() * 0.2f, 1.0f, 20, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setGlow();
        par.setLimitScale();
        MagickCore.addMagickParticle(par);
    }



    @Override
    public int getPower() {
        return 15;
    }
}
