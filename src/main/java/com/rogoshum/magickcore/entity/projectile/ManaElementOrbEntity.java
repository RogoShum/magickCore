package com.rogoshum.magickcore.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.entity.pointed.ManaCapacityEntity;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.ManaCapacity;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class ManaElementOrbEntity extends ManaProjectileEntity implements IManaCapacity {
    private final ManaCapacity manaCapacity = ManaCapacity.create(Float.MAX_VALUE);
    private static final DataParameter<Boolean> TYPE = EntityDataManager.createKey(ManaElementOrbEntity.class, DataSerializers.BOOLEAN);
    public ManaElementOrbEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
        this.dataManager.register(TYPE, false);
    }

    @Override
    protected void makeSound() {
        if(!this.world.isRemote && this.ticksExisted == 1) {
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F - this.rand.nextFloat());
        }
    }

    public boolean getOrbType() {
        return this.dataManager.get(TYPE);
    }

    public void setOrbType(boolean type) {
        this.dataManager.set(TYPE, type);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        manaCapacity().deserialize(compound);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        manaCapacity().serialize(compound);
    }

    @Override
    public void releaseMagick() {
        if(world.isRemote) return;
        List<Entity> list = findEntity(entity -> entity instanceof LivingEntity && entity.isAlive());
        if(!list.isEmpty()) {
            Entity entity = list.get(0);
            EntityStateData state = ExtraDataHelper.entityStateData(entity);
            if(getOrbType())
                state.setMaxManaValue(state.getMaxManaValue() + manaCapacity.getMana());
            else
                state.setManaValue(state.getManaValue() + manaCapacity.getMana());
            this.remove();
        }
    }

    @Override
    public boolean hitEntityRemove(EntityRayTraceResult entityRayTraceResult) {
        return false;
    }

    @Override
    public ManaCapacity manaCapacity() {
        return manaCapacity;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ResourceLocation getEntityIcon() {
        return spellContext().element.getRenderer().getParticleTexture();
    }

    @Override
    protected void applyParticle() {
        if(this.world.isRemote() && this.spellContext().element != null) {
            int count = (int) (20 * getWidth());
            double scaleX = (this.getPosX() - this.lastTickPosX)/count;
            double scaleY = (this.getPosY() - this.lastTickPosY)/count;
            double scaleZ = (this.getPosZ() - this.lastTickPosZ)/count;
            for (int i = 0; i < count; ++i) {
                LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getMistTexture()
                        , new Vector3d(this.lastTickPosX + scaleX * i
                        , this.lastTickPosY + scaleY * i + this.getHeight() / 2
                        , this.lastTickPosZ + scaleZ * i)
                        , 0.3f * getWidth(), 0.3f * getWidth(), 1.0f, 2, MagickCore.proxy.getElementRender(spellContext().element.type()));
                par.setGlow();
                par.setParticleGravity(0);
                par.setNoScale();
                MagickCore.addMagickParticle(par);
            }
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
}
