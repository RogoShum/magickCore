package com.rogoshum.magickcore.block.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.block.ILifeStateTile;
import com.rogoshum.magickcore.api.block.IManaSupplierTile;
import com.rogoshum.magickcore.capability.CapabilityEntityState;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.LifeStateEntity;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.init.ModEntites;
import com.rogoshum.magickcore.init.ModTileEntities;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MagickSupplierTileEntity extends TileEntity implements ITickableTileEntity, IManaSupplierTile {
    public int ticksExisted;
    public AxisAlignedBB box;
    public boolean powered;

    public MagickSupplierTileEntity() {
        super(ModTileEntities.magick_supplier_tileentity.get());
    }

    @Override
    public void tick() {
        ticksExisted++;
        if(this.box == null)
            box = new AxisAlignedBB(this.getPos().up());

        if(world.isRemote){
            float scale = .1f;
            ElementRenderer renderer = MagickCore.proxy.getElementRender(LibElements.ORIGIN);

            LitParticle par = new LitParticle(this.world, renderer.getParticleTexture()
                    , new Vector3d(pos.getX() + 0.5 + MagickCore.getNegativeToOne() * 0.5, pos.getY() + 0.5 + MagickCore.getNegativeToOne() * 0.5, pos.getZ() + 0.5 + MagickCore.getNegativeToOne() * 0.5)
                    , scale, scale, MagickCore.getNegativeToOne(), 20, renderer);
            par.setParticleGravity(0);
            par.setGlow();
            par.addMotion(MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.05);
            MagickCore.addMagickParticle(par);
        }
    }

    public void spawnLifeState() {
        if(this.box == null)
            box = new AxisAlignedBB(this.getPos().up());

        List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, this.box);
        if(!this.world.isRemote && !list.isEmpty()) {
            AtomicReference<TileEntity> targetTile = new AtomicReference<>(null);
            this.world.loadedTileEntityList.forEach((tile) -> {
                if (tile instanceof ILifeStateTile && (targetTile.get() == null || this.distanceTile(targetTile.get()) > this.distanceTile(tile)))
                {
                    targetTile.set(tile);
                }
            });

            if(targetTile.get() != null && this.distanceTile(targetTile.get()) <= 16)
            {
                LifeStateEntity life = new LifeStateEntity(ModEntites.life_state, world);
                life.setPosition(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);
                Vector3d tilePos = new Vector3d(targetTile.get().getPos().getX(), targetTile.get().getPos().getY(), targetTile.get().getPos().getZ());
                Vector3d selfPos = new Vector3d(this.pos.getX(), this.pos.getY(), this.pos.getZ());
                life.setMotion(tilePos.subtract(selfPos).normalize());
                life.setSupplierBlock(this);
                world.addEntity(life);
            }
        }
    }

    @Override
    public boolean shouldSpawn(boolean powered) {
        boolean flag = true;

        if(powered && this.powered)
            flag = false;
        else if(!powered)
            flag = false;

        this.powered = powered;

        return flag;
    }

    @Override
    public void setPos(BlockPos posIn) {
        super.setPos(posIn);
        box = new AxisAlignedBB(this.getPos().up());
    }

    public double distanceTile(TileEntity tileEntity) {
        float f = (float) (this.pos.getX() - tileEntity.getPos().getX());
        float f1 = (float) (this.pos.getY() - tileEntity.getPos().getY());
        float f2 = (float) (this.pos.getZ() - tileEntity.getPos().getZ());
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    @Override
    public float supplyMana(float mana) {
        if(this.box != null) {
            List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, this.box);
            for (LivingEntity entity : list) {
                if(!entity.isAlive()) continue;

                return livingBeExploited(entity, mana);
            }
        }

        return 0;
    }

    private float livingBeExploited(LivingEntity living, float mana) {
        LazyOptional<IEntityState> capa = living.getCapability(MagickCore.entityState);

        if(capa.isPresent()){
            IEntityState state = capa.orElse(new CapabilityEntityState.Implementation(ModElements.getElement(LibElements.ORIGIN)));
            float shieldMana = 0;

            if(state.getElementShieldMana() >= mana){
                state.setElementShieldMana(mana - state.getElementShieldMana());
                return mana;
            }
            else
                shieldMana = state.getElementShieldMana();

            if(state.getManaValue() >= mana){
                state.setManaValue(mana - state.getManaValue());
                return mana;
            }
            else if(state.getManaValue() + shieldMana >= mana){
                state.setManaValue(mana - (state.getManaValue() + shieldMana));
                state.setElementShieldMana(0);
                return mana;
            }
            else
                state.setManaValue(0);
        }

        if(living.getMaxHealth() >= mana) {
            living.getAttribute(Attributes.MAX_HEALTH).setBaseValue(living.getMaxHealth() - mana);
            if (living.getHealth() > living.getMaxHealth())
                living.setHealth(living.getMaxHealth());

            if (living.getMaxHealth() <= 1.0)
                living.setHealth(0);

            if (living.getHealth() <= 0)
                living.onDeath(DamageSource.OUT_OF_WORLD);

            return mana;
        }

        return living.getMaxHealth() - 1.0f;
    }
}
