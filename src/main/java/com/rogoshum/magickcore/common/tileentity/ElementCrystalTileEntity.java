package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElementCrystalTileEntity extends TileEntity implements ITickableTileEntity, ILightSourceEntity {
    public String eType = LibElements.ORIGIN;
    public int age;
    public ElementCrystalTileEntity() {
        super(ModTileEntities.ELEMENT_CRYSTAL_TILE_ENTITY.get());
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public boolean spawnGlowBlock() {
        return true;
    }

    public void dropItem() {
        if(age == 6 && world.rand.nextInt(2) == 0)
            dropCrystal();

        if(age == 7) {
            dropCrystal();
            if(world.rand.nextInt(2) == 0)
                dropCrystal();
        }
    }

    public List<ItemStack> getDrops() {
        age = world.getBlockState(getPos()).get(CropsBlock.AGE);
        if(this.world.isRemote || age != 7) return Collections.emptyList();
        ArrayList<ItemStack> stacks = new ArrayList<>();
        ItemStack stack = new ItemStack(ModItems.ELEMENT_CRYSTAL.get());
        CompoundNBT tag = new CompoundNBT();
        tag.putString("ELEMENT", eType);
        stack.setTag(tag);
        stacks.add(stack);
        if(world.rand.nextInt(4) > 0) {
            stack = new ItemStack(ModItems.ELEMENT_CRYSTAL_SEEDS.get());
            tag = new CompoundNBT();
            tag.putString("ELEMENT", eType);
            stack.setTag(tag);
            stacks.add(stack);
        }
        return stacks;
    }

    private void dropCrystal() {
        if(this.world.isRemote) return;
        ItemStack stack = new ItemStack(ModItems.ELEMENT_CRYSTAL.get());
        CompoundNBT tag = new CompoundNBT();
        tag.putString("ELEMENT", eType);
        stack.setTag(tag);

        ItemEntity entity = new ItemEntity(world, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);
        entity.setItem(stack);
        entity.setPickupDelay(20);
        world.addEntity(entity);

        if(world.rand.nextInt(4) > 0) {
            spawnElementOrb();
            if(world.rand.nextBoolean())
                spawnElementOrb();
        }
    }

    private void spawnElementOrb() {
        ManaElementOrbEntity orb = new ManaElementOrbEntity(ModEntities.ELEMENT_ORB.get(), world);
        orb.setPosition(this.pos.getX() + 0.5 * world.rand.nextFloat(), this.pos.getY() + 0.5, this.pos.getZ() + 0.5 * world.rand.nextFloat());
        orb.spellContext().element(MagickRegistry.getElement(eType));
        orb.spellContext().tick(200);
        orb.manaCapacity().setMana(5);
        orb.setOrbType(true);
        world.addEntity(orb);
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

        float scale = age / 100f;
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
    public float getSourceLight() {
        return age;
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
        return MagickCore.proxy.getElementRender(this.eType).getColor();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        //MagickCore.proxy.addRenderer(new ElementCrystalRenderer(this));
    }
}
