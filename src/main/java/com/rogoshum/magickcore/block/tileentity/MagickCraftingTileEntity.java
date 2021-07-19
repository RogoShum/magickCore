package com.rogoshum.magickcore.block.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.api.IManaMaterial;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.init.ModTileEntities;
import com.rogoshum.magickcore.item.ManaItem;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MagickCraftingTileEntity extends CanSeeTileEntity implements ITickableTileEntity {
    private ItemStack mainItem;
    private int manaCapacity;
    private UUID playerUniqueId = MagickCore.emptyUUID;
    public String eType = LibElements.ORIGIN;
    private boolean crafting;
    private int ticksExisted;
    private ErrorRenderer error = new ErrorRenderer();

    public MagickCraftingTileEntity() {
        super(ModTileEntities.magick_crafting_tileentity.get());
    }

    @Override
    public void tick() {
        ticksExisted++;
        if(!crafting || this.getMainItem() == null || this.getMainItem().isEmpty()) {
            if(crafting)
                crafting = false;
            return;
        }

        ItemStack material = null;
        MagickContainerTileEntity tileEntity = null;
        List<MagickContainerTileEntity> container = new ArrayList<>();
        world.tickableTileEntities.forEach((tile) -> { if(tile instanceof MagickContainerTileEntity && ((MagickContainerTileEntity) tile).getPlayerUniqueId().equals(this.getPlayerUniqueId())) { container.add((MagickContainerTileEntity) tile); }});
        container.sort((MagickContainerTileEntity o1, MagickContainerTileEntity o2)->o2.getManaCapacity() - o1.getManaCapacity());

        for(MagickContainerTileEntity tile : container)
        {
            if(material == null && tile.getMaterialItem() != null && !tile.getMaterialItem().isEmpty() && tile.getMaterialItem().getItem() instanceof IManaMaterial) {
                material = tile.getMaterialItem();
                tileEntity = tile;
            }
        }

        for(MagickContainerTileEntity tile : container)
        {
            if(tile.getManaCapacity() <= 0)
                continue;

            //upgradeAction
            if(material != null && tile == tileEntity) {
                if(manaCapacity < ((IManaMaterial)material.getItem()).getManaNeed())
                {
                    if(!this.world.isRemote) {
                        manaCapacity += tile.outputManaCapacity(1);
                        updateInfo();
                        if(this.ticksExisted % 20 == 0)
                        {
                            this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.BLOCKS, 0.1F, 1.0F + MagickCore.rand.nextFloat());
                        }
                    }
                    else
                        makeParticle(tile.getPos(), tile.eType, 1f);
                }
                else
                {
                    boolean flag = ((IManaMaterial)material.getItem()).upgradeManaItem(((ManaItem)getMainItem().getItem()).getItemData(getMainItem()));
                    if(flag && !this.world.isRemote)
                    {
                        material.setCount(material.getCount() - 1);
                        if(material.getCount() <= 0) {
                            tileEntity.updateInfo();
                            material = null;
                        }
                        manaCapacity = 0;
                        updateInfo();
                        this.world.playSound(null, this.getPos(), SoundEvents.ENTITY_ENDER_EYE_DEATH, SoundCategory.BLOCKS, 1.5F, 1.0F + MagickCore.rand.nextFloat());
                    }
                    else if(!flag && this.world.isRemote)
                    {
                        makeErrorParticle();
                    }
                }
            }
            else if(material == null && ((ManaItem) getMainItem().getItem()).getMana(getMainItem()) < ((ManaItem) getMainItem().getItem()).getMaxMana(getMainItem()))    //transManaAction
            {
                if(!this.world.isRemote) {
                    int manaGet = tile.outputManaCapacity(5);
                    float back = ((ManaItem) getMainItem().getItem()).receiveMana(getMainItem(), manaGet);
                    tile.receiveManaCapacity((int) back);
                    ((ManaItem) getMainItem().getItem()).setElement(getMainItem(), ModElements.getElement(tile.eType));
                    updateInfo();
                    if(this.ticksExisted % 20 == 0)
                    {
                        this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.BLOCKS, 0.1F, 1.0F + MagickCore.rand.nextFloat());
                    }
                }
                else
                    makeParticle(tile.getPos(), tile.eType, 0.5f);
            }
        }
    }

    public void makeErrorParticle() {
        LitParticle par = new LitParticle(this.world, error.getParticleTexture()
                , new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), 0.15f, 0.15f, 1.0f, 40, error);
        par.setParticleGravity(0);
        par.setLimitScale();
        par.setGlow();
        par.addMotion(MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.05);
        MagickCore.addMagickParticle(par);
    }

    public void makeParticle(BlockPos pos, String type, float scaleP)
    {
        ElementRenderer renderer = MagickCore.proxy.getElementRender(type);
        double dis = Math.sqrt(pos.distanceSq(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), true));
        if(dis > 16)
        {
            LitParticle par = new LitParticle(this.world, renderer.getParticleTexture()
                    , new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), 0.15f * scaleP, 0.15f * scaleP, 1.0f, 40, renderer);
            par.setParticleGravity(-0.1f);
            par.setLimitScale();
            par.setGlow();
            MagickCore.addMagickParticle(par);

            LitParticle par_ = new LitParticle(this.world, renderer.getParticleTexture()
                    , new Vector3d(this.pos.getX() + 0.5, this.pos.getY() + 0.5 + 3, this.pos.getZ() + 0.5), 0.15f * scaleP, 0.15f * scaleP, 1.0f, 40, renderer);
            par_.setParticleGravity(0.1f);
            //par_.setLimitScale();
            par_.setGlow();
            MagickCore.addMagickParticle(par_);

            return;
        }


        double offset = 0.5;
        int distance = (int) (10 * dis);
        float directionPoint = (float) (this.ticksExisted % distance) / distance;
        int c = (int) (directionPoint * distance);

        float scale;
        for (int i = 0; i < distance; i++) {
            if(i == c)
                scale = 0.25f;
            else
                scale = 0.10f;
            //MagickCore.LOGGER.debug(i + " " + c);
            double trailFactor = i / (distance - 1.0D);
            double tx = pos.getX() + offset + (this.pos.getX() - pos.getX()) * trailFactor + world.rand.nextGaussian() * 0.005;
            double ty = pos.getY() + offset + (this.pos.getY() - pos.getY()) * trailFactor + world.rand.nextGaussian() * 0.005;
            double tz = pos.getZ() + offset + (this.pos.getZ() - pos.getZ()) * trailFactor + world.rand.nextGaussian() * 0.005;
            LitParticle par = new LitParticle(this.world, renderer.getParticleTexture()
                    , new Vector3d(tx, ty, tz), scale * scaleP, scale * scaleP, 1.0f, 5, renderer);
            par.setParticleGravity(0);
            par.setLimitScale();
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }
    }

    public void enableTrans(){if(this.crafting) this.crafting = false; else this.crafting = true; updateInfo();}
    public void setPlayerUniqueId(UUID uuid){this.playerUniqueId = uuid; updateInfo();}
    public UUID getPlayerUniqueId(){return this.playerUniqueId;}

    private void updateInfo() {
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE); }

    public ItemStack getMainItem() {
        return mainItem;
    }

    public void clearMainItem() {
        mainItem = ItemStack.EMPTY;
        updateInfo();
    }

    public boolean putManaItem(ItemStack item)
    {
        if((mainItem == null || mainItem.isEmpty()) && item.getItem() instanceof IManaItem)
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
    public void remove() {
        CompoundNBT tag = new CompoundNBT();
        CompoundNBT tileTag = new CompoundNBT();
        this.write(tileTag);
        tag.put("BlockEntityTag", tileTag);

        ItemStack item = new ItemStack(ModItems.magick_crafting.get());
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
        compoundNBT.putBoolean("TRANS", this.crafting);
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
        this.crafting = tag.getBoolean("TRANS");
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
        this.crafting = compound.getBoolean("TRANS");
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
        compound.putBoolean("TRANS", this.crafting);
        compound.putInt("MANA", this.manaCapacity);
        compound.putString("TYPE", this.eType);
        return super.write(compound);
    }

    public class ErrorRenderer extends ElementRenderer
    {
        public ErrorRenderer() {
            super(new float[]{1f, 0, 0});
        }
    }
}
