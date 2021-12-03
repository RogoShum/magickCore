package com.rogoshum.magickcore.block.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.ManaElementOrbEntity;
import com.rogoshum.magickcore.entity.ManaPowerEntity;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.init.ModEntites;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.init.ModTileEntities;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.tool.EntityLightSourceHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class ElementCrystalTileEntity extends CanSeeTileEntity implements ITickableTileEntity, ILightSourceEntity {
    public String eType = LibElements.ORIGIN;
    public int age;
    public ElementCrystalTileEntity() {
        super(ModTileEntities.element_crystal_tileentity.get());
    }

    @Override
    public void remove() {
        if(age == 6 && world.rand.nextInt(2) == 0)
            dropCrystal();

        if(age == 7)
        {
            dropCrystal();
            if(world.rand.nextInt(2) == 0)
                dropCrystal();
        }
        super.remove();
    }

    private void dropCrystal()
    {
        if(this.world.isRemote) return;
        ItemStack stack = new ItemStack(ModItems.element_crystal.get());
        CompoundNBT tag = new CompoundNBT();
        tag.putString("ELEMENT", eType);
        stack.setTag(tag);

        ItemEntity entity = new ItemEntity(world, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);
        entity.setItem(stack);
        entity.setPickupDelay(20);
        world.addEntity(entity);

        if(world.rand.nextBoolean()) {
            if(world.rand.nextBoolean()) {
                ManaElementOrbEntity orb = new ManaElementOrbEntity(ModEntites.element_orb, world);
                orb.setPosition(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);
                orb.setElement(ModElements.getElement(eType));
                orb.setTickTime(200);
                world.addEntity(orb);
            }
            else {
                ManaPowerEntity orb2 = new ManaPowerEntity(ModEntites.mana_power, world);
                orb2.setPosition(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);
                orb2.setElement(ModElements.getElement(LibElements.ORIGIN));
                orb2.setTickTime(100);
                orb2.setMana(10);
                world.addEntity(orb2);
           }
        }
    }

    private void updateInfo() { world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE); }

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
        compoundNBT.putString("TYPE", this.eType);
        return compoundNBT;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.eType = tag.getString("TYPE");
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        this.eType = compound.getString("TYPE");
        super.read(state, compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putString("TYPE", this.eType);
        return super.write(compound);
    }

    @Override
    public void tick() {
        age = world.getBlockState(getPos()).get(CropsBlock.AGE);
        if(!world.isRemote) return;

        float scale = age / 70f;
        ElementRenderer renderer = MagickCore.proxy.getElementRender(this.eType);

        LitParticle par = new LitParticle(this.world, renderer.getParticleTexture()
                , new Vector3d(pos.getX() + 0.5 + MagickCore.getNegativeToOne() * 0.5, pos.getY() + 0.5 + MagickCore.getNegativeToOne() * 0.5, pos.getZ() + 0.5 + MagickCore.getNegativeToOne() * 0.5)
                , scale, scale, MagickCore.getNegativeToOne(), 20, renderer);
        par.setParticleGravity(0);
        par.setGlow();
        par.addMotion(MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.05);
        MagickCore.addMagickParticle(par);
    }

    @Override
    public int getSourceLight() {
        return age;
    }

    @Override
    public boolean isAlive() {
        return !this.removed;
    }

    @Override
    public Vector3d getPositionVec() {
        return Vector3d.copyCentered(this.getPos());
    }

    @Override
    public World getEntityWorld() {
        return this.getWorld();
    }

    @Override
    public float getEyeHeight() {
        return 0.5f;
    }

    @Override
    public float[] getColor() {
        return MagickCore.proxy.getElementRender(this.eType).getColor();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        EntityLightSourceHandler.addLightSource(this);
    }
}
