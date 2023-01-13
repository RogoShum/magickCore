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
    public void setRemoved() {
        super.setRemoved();
    }

    @Override
    public boolean spawnGlowBlock() {
        return true;
    }

    public void dropItem() {
        if(age == 6 && level.random.nextInt(2) == 0)
            dropCrystal();

        if(age == 7) {
            dropCrystal();
            if(level.random.nextInt(2) == 0)
                dropCrystal();
        }
    }

    public List<ItemStack> getDrops() {
        age = level.getBlockState(getBlockPos()).getValue(CropsBlock.AGE);
        if(this.level.isClientSide || age != 7) return Collections.emptyList();
        ArrayList<ItemStack> stacks = new ArrayList<>();
        ItemStack stack = new ItemStack(ModItems.ELEMENT_CRYSTAL.get());
        CompoundNBT tag = new CompoundNBT();
        tag.putString("ELEMENT", eType);
        stack.setTag(tag);
        stacks.add(stack);
        if(level.random.nextInt(4) > 0) {
            stack = new ItemStack(ModItems.ELEMENT_CRYSTAL_SEEDS.get());
            tag = new CompoundNBT();
            tag.putString("ELEMENT", eType);
            stack.setTag(tag);
            stacks.add(stack);
        }
        return stacks;
    }

    private void dropCrystal() {
        if(this.level.isClientSide) return;
        ItemStack stack = new ItemStack(ModItems.ELEMENT_CRYSTAL.get());
        CompoundNBT tag = new CompoundNBT();
        tag.putString("ELEMENT", eType);
        stack.setTag(tag);

        ItemEntity entity = new ItemEntity(level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5);
        entity.setItem(stack);
        entity.setPickUpDelay(20);
        level.addFreshEntity(entity);

        if(level.random.nextInt(4) > 0) {
            spawnElementOrb();
            if(level.random.nextBoolean())
                spawnElementOrb();
        }
    }

    private void spawnElementOrb() {
        ManaElementOrbEntity orb = new ManaElementOrbEntity(ModEntities.ELEMENT_ORB.get(), level);
        orb.setPos(this.worldPosition.getX() + 0.5 * level.random.nextFloat(), this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5 * level.random.nextFloat());
        orb.spellContext().element(MagickRegistry.getElement(eType));
        orb.spellContext().tick(200);
        orb.manaCapacity().setMana(5);
        orb.setOrbType(true);
        level.addFreshEntity(orb);
    }

    private void updateInfo() { level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE); }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(level.getBlockState(pkt.getPos()), pkt.getTag());
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
    public void load(BlockState state, CompoundNBT compound) {
        this.eType = compound.getString("TYPE");
        super.load(state, compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putString("TYPE", this.eType);
        return super.save(compound);
    }

    @Override
    public void tick() {
        age = level.getBlockState(getBlockPos()).getValue(CropsBlock.AGE);
        if(!level.isClientSide) return;

        float scale = age / 100f;
        ElementRenderer renderer = MagickCore.proxy.getElementRender(this.eType);

        LitParticle par = new LitParticle(this.level, renderer.getParticleTexture()
                , new Vector3d(worldPosition.getX() + 0.5 + MagickCore.getNegativeToOne() * 0.5, worldPosition.getY() + 0.5 + MagickCore.getNegativeToOne() * 0.5, worldPosition.getZ() + 0.5 + MagickCore.getNegativeToOne() * 0.5)
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
        return !this.remove;
    }

    @Override
    public Vector3d positionVec() {
        return Vector3d.atCenterOf(this.getBlockPos());
    }

    @Override
    public AxisAlignedBB boundingBox() {
        return getRenderBoundingBox();
    }

    @Override
    public World world() {
        return this.getLevel();
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
