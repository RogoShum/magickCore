package com.rogoshum.magickcore.entity.projectile;

import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.api.block.ILifeStateTile;
import com.rogoshum.magickcore.api.block.IManaSupplierTile;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.magick.Color;
import com.rogoshum.magickcore.magick.ManaCapacity;
import com.rogoshum.magickcore.magick.context.SpellContext;
import com.rogoshum.magickcore.tool.EntityLightSourceHandler;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import com.rogoshum.magickcore.init.ModEntities;
import com.rogoshum.magickcore.magick.lifestate.LifeStateCarrier;
import com.rogoshum.magickcore.tool.ProjectileHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class LifeStateEntity extends ThrowableEntity implements ILightSourceEntity, ISpellContext, IManaCapacity {
    private static final DataParameter<CompoundNBT> CARRIER_NBT = EntityDataManager.createKey(LifeStateEntity.class, DataSerializers.COMPOUND_NBT);
    private final LifeStateCarrier carrier = LifeStateCarrier.create();
    private IManaSupplierTile supplier;
    private static final float REQUIRE = 0.01f;
    private ILifeStateTile repeater;
    private BlockPos supplierPos;
    private World supplierWorld;
    private World repeaterWorld;
    private BlockPos repeaterPos;
    private final ManaCapacity manaCapacity = ManaCapacity.create(100);
    private final SpellContext spellContext = SpellContext.create();

    public LifeStateEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
        this.noClip = true;
    }

    @Override
    public boolean spawnGlowBlock() {
        return true;
    }

    public LifeStateCarrier getCarrier() {
        return carrier;
    }

    public LifeStateEntity setSupplierBlock(IManaSupplierTile block) {
        this.supplier = block;
        return this;
    }

    public IManaSupplierTile getSupplierBlock() {
        return this.supplier;
    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            if (this.supplier != null) {
                if (this.supplier.supplyMana(REQUIRE) < REQUIRE) {
                    this.remove();
                }

                if (this.supplier.removed())
                    this.supplier = null;
            } else {
                TileEntity entity = getPendingTileEntityAt(supplierWorld, supplierPos);
                if (entity instanceof IManaSupplierTile)
                    this.supplier = (IManaSupplierTile) entity;

                if (this.supplier == null) {
                    this.remove();
                }
            }

            if (this.repeater != null) {
                if (this.repeater.removed())
                    this.repeater = null;
            } else {
                TileEntity entity = getPendingTileEntityAt(repeaterWorld, repeaterPos);
                if (entity instanceof ILifeStateTile)
                    this.repeater = (ILifeStateTile) entity;
            }
        }
        this.setNoGravity(true);
        super.tick();
        this.onImpact(ProjectileHelper.canTouchVisibleBlock(this, this::func_230298_a_));
        Vector3d motion = this.getMotion().normalize().scale(0.05*(this.spellContext().range + 1));
        this.setMotion(motion);
        this.getCarrier().tick(this);
        updateCarrierNbt();
    }

    public LifeStateEntity split(TileEntity tileEntity) {
        if (tileEntity instanceof ILifeStateTile)
            repeater = (ILifeStateTile) tileEntity;
        LifeStateEntity lifeState = new LifeStateEntity(ModEntities.life_state.get(), world);
        lifeState.setPosition(tileEntity.getPos().getX() + 0.5, tileEntity.getPos().getY() + 0.5, tileEntity.getPos().getZ() + 0.5);
        copyInfo(lifeState);
        if (lifeState.supplier.supplyMana(lifeState.manaCapacity().getMana()) >= lifeState.manaCapacity().getMana() && !world.isRemote)
            world.addEntity(lifeState);

        return lifeState;
    }

    public LifeStateEntity split(TileEntity tileEntity, BlockPos target) {
        LifeStateEntity lifeState = split(tileEntity);
        lifeState.setMotion(Vector3d.copy(target).add(0.5, 0.5, 0.5).subtract(lifeState.getPositionVec()).normalize().scale(0.5));
        return lifeState;
    }

    public void copyInfo(LifeStateEntity lifeState) {
        lifeState.ticksExisted = this.ticksExisted;
        lifeState.carrier.copyOf(this.carrier);
        lifeState.supplier = this.supplier;
        lifeState.repeater = this.repeater;
        lifeState.setMotion(this.getMotion());

        CompoundNBT elementData = new CompoundNBT();
        spellContext().serialize(elementData);
        lifeState.spellContext().deserialize(elementData);
    }

    @Override
    protected float getGravityVelocity() {
        return 0;
    }

    @Override
    protected void registerData() {
        this.dataManager.register(CARRIER_NBT, new CompoundNBT());
    }

    public void updateCarrierNbt() {
        if (this.world.isRemote) {
            this.getCarrier().deserialize(this.dataManager.get(CARRIER_NBT), world);
            spellContext().deserialize(this.dataManager.get(CARRIER_NBT).getCompound("elementData"));
        } else {
            CompoundNBT tag = new CompoundNBT();
            this.getCarrier().serialize(tag);
            spellContext().serialize(tag);
            this.dataManager.set(CARRIER_NBT, tag);
        }
    }

    @Override
    public void forceFireTicks(int ticks) {
    }

    @Override
    public int getFireTimer() {
        return 0;
    }

    @Override
    public boolean isImmuneToFire() {
        return true;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        this.carrier.serialize(compound);
        if (this.supplier != null) {
            NBTTagHelper.putVectorDouble(compound, "supplier", Vector3d.copy(this.supplier.pos()));
            compound.putString("supplierWorld", this.supplier.world().getDimensionKey().getLocation().toString());
        }
        if (this.repeater != null) {
            NBTTagHelper.putVectorDouble(compound, "repeater", Vector3d.copy(this.repeater.pos()));
            compound.putString("repeaterWorld", this.repeater.world().getDimensionKey().getLocation().toString());
        }

        spellContext().serialize(compound);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.carrier.deserialize(compound, world);
        spellContext().deserialize(compound.getCompound("elementData"));

        if(world.getServer() == null)
            return;
        if (NBTTagHelper.hasVectorDouble(compound, "supplier") && compound.contains("supplierWorld")) {
            Vector3d vec = NBTTagHelper.getVectorFromNBT(compound, "supplier");
            this.supplierPos = new BlockPos(vec);
            this.supplierWorld = world.getServer().getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY
                    , new ResourceLocation(compound.getString("supplierWorld"))));
        }

        if (NBTTagHelper.hasVectorDouble(compound, "repeater") && compound.contains("repeaterWorld")) {
            Vector3d vec = NBTTagHelper.getVectorFromNBT(compound, "repeater");
            this.repeaterPos = new BlockPos(vec);
            this.repeaterWorld = world.getServer().getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY
                    , new ResourceLocation(compound.getString("repeaterWorld"))));
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        EntityLightSourceHandler.addLightSource(this);
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        super.onEntityHit(p_213868_1_);
        this.getCarrier().onHitEntity(this, p_213868_1_);
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult p_230299_1_) {
        super.func_230299_a_(p_230299_1_);
        if (this.world.isRemote) return;
        this.getCarrier().onHitBlock(this, p_230299_1_);

        TileEntity tileEntity = this.world.getTileEntity(p_230299_1_.getPos());
        if (this.supplier != null && tileEntity instanceof ILifeStateTile && tileEntity != this.repeater) {
            ((ILifeStateTile) tileEntity).touch(this);
            this.remove();
        }
    }

    @Nullable
    private TileEntity getPendingTileEntityAt(World world, BlockPos pos) {
        //List<TileEntity> addedTileEntity = ObfuscationReflectionHelper.getPrivateValue(World.class, this.world, "field_147484_a");
        if(world == null) return null;

        List<TileEntity> addedTileEntity = world.loadedTileEntityList;
        for (TileEntity tileentity : addedTileEntity) {
            if (!tileentity.isRemoved() && tileentity.getPos().equals(pos)) {
                return tileentity;
            }
        }

        return null;
    }

    @Override
    public float getSourceLight() {
        return 5;
    }

    @Override
    public boolean alive() {
        return isAlive();
    }

    @Override
    public Vector3d positionVec() {
        return getPositionVec();
    }

    @Override
    public AxisAlignedBB boundingBox() {
        return getBoundingBox();
    }

    @Override
    public World world() {
        return getEntityWorld();
    }

    @Override
    public float eyeHeight() {
        return getEyeHeight();
    }

    @Override
    public Color getColor() {
        return this.spellContext().element.color();
    }

    @Override
    public ManaCapacity manaCapacity() {
        return manaCapacity;
    }

    @Override
    public SpellContext spellContext() {
        return spellContext;
    }
}
