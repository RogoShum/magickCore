package com.rogoshum.magickcore.block.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.api.IManaMaterial;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.init.ModTileEntities;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

public class MagickContainerTileEntity extends CanSeeTileEntity implements ITickableTileEntity {
    private ItemStack mainItem;
    private int manaCapacity;
    public final int maxManaCapacity = 10000;
    private UUID playerUniqueId = MagickCore.emptyUUID;
    private boolean transMana;
    public String eType = LibElements.ORIGIN;
    public MagickContainerTileEntity() {
        super(ModTileEntities.magick_container_tileentity.get());
    }

    @Override
    public void tick() {
        if(this.playerUniqueId != MagickCore.emptyUUID)
        {
            PlayerEntity player = this.world.getPlayerByUuid(this.playerUniqueId);

            if(player == null)
                eType = LibElements.ORIGIN;
            else {
                IEntityState state = player.getCapability(MagickCore.entityState).orElse(null);
                eType = state.getElement().getType();
            }

            if(transMana && player != null)
            {
                double dis = Math.sqrt(player.getDistanceSq(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()));
                IEntityState state = player.getCapability(MagickCore.entityState).orElse(null);
                if(dis > 16)
                {
                    transMana = false;
                    return;
                }
                if(!this.world.isRemote)
                {
                    int manaTrans = 5;
                    if(manaCapacity < maxManaCapacity && state.getManaValue() >= manaTrans)
                        state.setManaValue(state.getManaValue() - manaTrans + this.receiveManaCapacity(manaTrans));
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
                        LitParticle par = new LitParticle(this.world, state.getElement().getRenderer().getParticleTexture()
                                , new Vector3d(tx, ty, tz), scale, scale, 1.0f, 5, state.getElement().getRenderer());
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

        ItemStack item = new ItemStack(ModItems.magick_container.get());
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

    public int getManaCapacity(){
        return manaCapacity;
    }

    public int outputManaCapacity(int mana){
        int out = Math.min(manaCapacity, mana);
        manaCapacity = Math.max(0, manaCapacity - mana);
        updateInfo();
        return out;
    }

    public int receiveManaCapacity(int mana)
    {
        int remaining = 0;
        int addUp = mana + manaCapacity;
        if(addUp > maxManaCapacity) {
            remaining = addUp - maxManaCapacity;
            manaCapacity = maxManaCapacity;
        }
        else
            manaCapacity = addUp;
        updateInfo();
        return remaining;
    }

    protected void updateInfo() { world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE); }

    public ItemStack getMaterialItem() { return mainItem; }
    public void clearMaterialItem() {
        mainItem = ItemStack.EMPTY;
        updateInfo();
    }

    public boolean putMaterialItem(ItemStack item)
    {
        if((mainItem == null || mainItem.isEmpty()) && item.getItem() instanceof IManaMaterial)
        {
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
        if(mainItem != null) {
            CompoundNBT tag = new CompoundNBT();
            mainItem.write(tag);
            compoundNBT.put("MAIN_ITEM", tag);
        }
        if(!this.playerUniqueId.equals(MagickCore.emptyUUID))
            compoundNBT.putUniqueId("playerUUID", this.playerUniqueId);
        compoundNBT.putBoolean("TRANS", this.transMana);
        compoundNBT.putInt("MANA", this.manaCapacity);
        compoundNBT.putString("TYPE", this.eType);
        return compoundNBT;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        if(tag.contains("MAIN_ITEM")) {
            mainItem = ItemStack.read(tag.getCompound("MAIN_ITEM"));
        }
        this.eType = tag.getString("TYPE");
        this.transMana = tag.getBoolean("TRANS");
        this.manaCapacity = tag.getInt("MANA");
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
        this.manaCapacity = compound.getInt("MANA");
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
        compound.putInt("MANA", this.manaCapacity);
        compound.putString("TYPE", this.eType);
        return super.write(compound);
    }
}
