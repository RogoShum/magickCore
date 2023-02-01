package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.block.ILoadBlockEntity;
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
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElementCrystalTileEntity extends BlockEntity implements TickableBlockEntity, ILightSourceEntity, BlockEntityClientSerializable, ILoadBlockEntity {
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
        age = level.getBlockState(getBlockPos()).getValue(CropBlock.AGE);
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

    private void updateInfo() { level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2); }

    @Override
    public void load(BlockState state, CompoundTag compound) {
        this.eType = compound.getString("TYPE");
        super.load(state, compound);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putString("TYPE", this.eType);
        return super.save(compound);
    }

    @Override
    public void tick() {
        age = level.getBlockState(getBlockPos()).getValue(CropBlock.AGE);
        if(!level.isClientSide) return;

        float scale = age / 100f;
        ElementRenderer renderer = MagickCore.proxy.getElementRender(this.eType);

        LitParticle par = new LitParticle(this.level, renderer.getParticleTexture()
                , new Vec3(worldPosition.getX() + 0.5 + MagickCore.getNegativeToOne() * 0.5, worldPosition.getY() + 0.5 + MagickCore.getNegativeToOne() * 0.5, worldPosition.getZ() + 0.5 + MagickCore.getNegativeToOne() * 0.5)
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
        return new AABB(this.getBlockPos());
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
        return MagickCore.proxy.getElementRender(this.eType).getColor();
    }

    @Override
    public void onLoad() {
        //MagickCore.proxy.addRenderer(new ElementCrystalRenderer(this));
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.eType = tag.getString("TYPE");
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag.putString("TYPE", this.eType);
        return tag;
    }
}
