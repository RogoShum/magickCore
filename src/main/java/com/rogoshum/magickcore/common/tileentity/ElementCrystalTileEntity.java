package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.tileentity.easyrender.ElementCrystalRenderer;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ElementCrystalTileEntity extends BlockEntity implements ILightSourceEntity {
    public String eType = LibElements.ORIGIN;
    public int age;
    public ElementCrystalTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.ELEMENT_CRYSTAL_TILE_ENTITY.get(), pos ,state);
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
        age = level.getBlockState(getBlockPos()).getValue(CropBlock.AGE);
        return getDrops(age);
    }

    public List<ItemStack> getDrops(int age) {
        if(this.level.isClientSide || age != 7) return Collections.emptyList();
        ArrayList<ItemStack> stacks = new ArrayList<>();
        ItemStack stack = new ItemStack(ModItems.ELEMENT_CRYSTAL.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("ELEMENT", eType);
        stack.setTag(tag);
        stacks.add(stack);
        if(level.random.nextInt(4) > 0) {
            stack = new ItemStack(ModItems.ELEMENT_CRYSTAL_SEEDS.get());
            tag = new CompoundTag();
            tag.putString("ELEMENT", eType);
            stack.setTag(tag);
            stacks.add(stack);
        }
        return stacks;
    }

    private void dropCrystal() {
        if(this.level.isClientSide) return;
        ItemStack stack = new ItemStack(ModItems.ELEMENT_CRYSTAL.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("ELEMENT", eType);
        stack.setTag(tag);

        ItemEntity entity = new ItemEntity(level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, stack);
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

    private void updateInfo() { level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2); }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundNBT = super.getUpdateTag();
        compoundNBT.putString("TYPE", this.eType);
        return compoundNBT;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.eType = tag.getString("TYPE");
    }

    @Override
    public void load(CompoundTag compound) {
        this.eType = compound.getString("TYPE");
        super.load(compound);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.putString("TYPE", this.eType);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElementCrystalTileEntity me) {
        if(level != me.level) return;
        Optional<Integer> age = state.getOptionalValue(CropBlock.AGE);
        age.ifPresent(integer -> me.age = integer);
        if(!level.isClientSide) return;

        float scale = me.age / 100f;
        ElementRenderer renderer = MagickCore.proxy.getElementRender(me.eType);

        LitParticle par = new LitParticle(me.level, renderer.getParticleTexture()
                , new Vec3(pos.getX() + 0.5 + MagickCore.getNegativeToOne() * 0.5, pos.getY() + 0.5 + MagickCore.getNegativeToOne() * 0.5, pos.getZ() + 0.5 + MagickCore.getNegativeToOne() * 0.5)
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
    public Vec3 positionVec() {
        return Vec3.atCenterOf(this.getBlockPos());
    }

    @Override
    public AABB boundingBox() {
        return getRenderBoundingBox();
    }

    @Override
    public Level world() {
        return this.getLevel();
    }

    @Override
    public float eyeHeight() {
        return 0.5f;
    }

    @Override
    public Color getColor() {
        return MagickCore.proxy.getElementRender(this.eType).getPrimaryColor();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        MagickCore.proxy.addRenderer(() -> new ElementCrystalRenderer(this));
    }
}
