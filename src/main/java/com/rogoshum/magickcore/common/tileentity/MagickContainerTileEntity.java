package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.api.mana.IManaCapacity;
import com.rogoshum.magickcore.common.api.mana.IManaMaterial;
import com.rogoshum.magickcore.common.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.ManaCapacity;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class MagickContainerTileEntity extends CanSeeTileEntity implements ITickableTileEntity, IManaCapacity, ILightSourceEntity {
    private ItemStack mainItem;
    private UUID playerUniqueId = MagickCore.emptyUUID;
    private boolean transMana;
    public String eType = LibElements.ORIGIN;
    private final ManaCapacity capacity = ManaCapacity.create(10000);
    public MagickContainerTileEntity() {
        super(ModTileEntities.magick_container_tileentity.get());
    }

    @Override
    public boolean spawnGlowBlock() {
        return true;
    }

    @Override
    public void tick() {
        if(this.playerUniqueId != MagickCore.emptyUUID)
        {
            LivingEntity player = this.world.getPlayerByUuid(this.playerUniqueId);
            AtomicReference<EntityStateData> state = new AtomicReference<>();
            if(player == null)
                eType = LibElements.ORIGIN;
            else {
                ExtraDataUtil.entityData(player).<EntityStateData>execute(LibEntityData.ENTITY_STATE, (data) -> {
                    eType = data.getElement().type();
                    state.set(data);
                });
            }

            if(transMana && player != null)
            {
                double dis = Math.sqrt(player.getDistanceSq(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()));
                if(state.get() == null)
                    return;

                if(dis > 16)
                {
                    transMana = false;
                    return;
                }
                if(!this.world.isRemote)
                {
                    int manaTrans = 5;
                    if(manaCapacity().getMana() < manaCapacity().getMaxMana() && state.get().getManaValue() >= manaTrans)
                        state.get().setManaValue(state.get().getManaValue() - manaTrans + capacity.receiveMana(manaTrans));
                    else
                        transMana = false;
                    updateInfo();
                }
                else
                {
                    double offset = 0.5;
                    int distance = (int) (10 * dis);
                    float directionPoint = (float) (player.ticksExisted % distance) / distance;
                    int c = (int) (directionPoint * distance);

                    float scale;
                    for (int i = 0; i < distance; i++) {
                        if(i == c)
                            scale = 0.25f;
                        else
                            scale = 0.10f;
                        //MagickCore.LOGGER.debug(i + " " + c);
                        double trailFactor = i / (distance - 1.0D);
                        double tx = player.getPosX() + (this.pos.getX() + offset - player.getPosX()) * trailFactor + world.rand.nextGaussian() * 0.005;
                        double ty = player.getPosY() + player.getHeight() / 2 + ((this.pos.getY() + offset) - (player.getPosY() + player.getHeight() / 2)) * trailFactor + world.rand.nextGaussian() * 0.005;
                        double tz = player.getPosZ() + (this.pos.getZ() + offset - player.getPosZ()) * trailFactor + world.rand.nextGaussian() * 0.005;
                        LitParticle par = new LitParticle(this.world, state.get().getElement().getRenderer().getParticleTexture()
                                , new Vector3d(tx, ty, tz), scale, scale, 1.0f, 5, state.get().getElement().getRenderer());
                        par.setParticleGravity(0);
                        par.setLimitScale();
                        par.setGlow();
                        MagickCore.addMagickParticle(par);
                    }
                }
            }
        }
        else {
            transMana = false;
            eType = LibElements.ORIGIN;
        }
    }

    @Override
    public void remove() {
        CompoundNBT tag = new CompoundNBT();
        CompoundNBT tileTag = new CompoundNBT();
            this.write(tileTag);
        tag.put("BlockEntityTag", tileTag);

        ItemStack item = new ItemStack(ModItems.MAGICK_CONTAINER.get());
        ItemEntity newItem = new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), item);

        CompoundNBT oldTag = new CompoundNBT();
        if(newItem.writeUnlessPassenger(oldTag))
        {
            if (oldTag.contains("Item"))
                oldTag.getCompound("Item").put("tag", tag);

            newItem.read(oldTag);
            if (!world.isRemote)
                world.addEntity(newItem);

        }
        super.remove();
    }

    public void enableTrans(){if(this.transMana) this.transMana = false; else this.transMana = true; updateInfo();}
    public void setPlayerUniqueId(UUID uuid){this.playerUniqueId = uuid; updateInfo();}
    public UUID getPlayerUniqueId(){return this.playerUniqueId;}

    protected void updateInfo() { world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE); }

    public ItemStack getMaterialItem() { return mainItem; }
    public void clearMaterialItem() {
        mainItem = ItemStack.EMPTY;
        updateInfo();
    }

    public boolean putMaterialItem(ItemStack item)
    {
        if((mainItem == null || mainItem.isEmpty()) && item.getItem() instanceof IManaMaterial) {
            mainItem = item;
            updateInfo();
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(world.getBlockState(pkt.getPos()), pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT compoundNBT = super.getUpdateTag();
        if(mainItem != null && !mainItem.getItem().hasEffect(mainItem)) {
            CompoundNBT tag = new CompoundNBT();
            mainItem.write(tag);
            compoundNBT.put("MAIN_ITEM", tag);
        }
        if(!this.playerUniqueId.equals(MagickCore.emptyUUID))
            compoundNBT.putUniqueId("playerUUID", this.playerUniqueId);
        compoundNBT.putBoolean("TRANS", this.transMana);
        capacity.serialize(compoundNBT);
        compoundNBT.putString("TYPE", this.eType);
        return compoundNBT;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        if(tag.contains("MAIN_ITEM")) {
            mainItem = ItemStack.read(tag.getCompound("MAIN_ITEM"));
            if(mainItem.getItem().hasEffect(mainItem))
                mainItem = null;
        }
        this.eType = tag.getString("TYPE");
        this.transMana = tag.getBoolean("TRANS");
        capacity.deserialize(tag);
        if(tag.contains("playerUUID"))
            this.playerUniqueId = tag.getUniqueId("playerUUID");
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        if(compound.contains("MAIN_ITEM")) {
            mainItem = ItemStack.read(compound.getCompound("MAIN_ITEM"));
        }
        this.eType = compound.getString("TYPE");
        this.transMana = compound.getBoolean("TRANS");
        capacity.deserialize(compound);
        if(compound.contains("playerUUID"))
            this.playerUniqueId = compound.getUniqueId("playerUUID");
        super.read(state, compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if(mainItem != null) {
            CompoundNBT tag = new CompoundNBT();
            mainItem.write(tag);
            compound.put("MAIN_ITEM", tag);
        }
        if(!this.playerUniqueId.equals(MagickCore.emptyUUID))
            compound.putUniqueId("playerUUID", this.playerUniqueId);
        compound.putBoolean("TRANS", this.transMana);
        capacity.serialize(compound);
        compound.putString("TYPE", this.eType);
        return super.write(compound);
    }

    @Override
    public float getSourceLight() {
        return (manaCapacity().getMana() / manaCapacity().getMaxMana()) * 15;
    }

    @Override
    public boolean alive() {
        return !this.removed;
    }

    @Override
    public Vector3d positionVec() {
        return Vector3d.copyCentered(this.getPos());
    }

    @Override
    public AxisAlignedBB boundingBox() {
        return getRenderBoundingBox();
    }

    @Override
    public World world() {
        return this.getWorld();
    }

    @Override
    public float eyeHeight() {
        return 0.5f;
    }

    @Override
    public Color getColor() {
        return MagickRegistry.getElement(this.eType).color();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        EntityLightSourceManager.addLightSource(this);
    }

    @Override
    public ManaCapacity manaCapacity() {
        return capacity;
    }
}
