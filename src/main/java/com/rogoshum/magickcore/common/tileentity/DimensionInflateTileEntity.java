package com.rogoshum.magickcore.common.tileentity;

import com.google.common.collect.ImmutableList;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.enums.ManaLimit;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.api.extradata.item.ItemDimensionData;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.api.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.api.magick.context.child.PositionContext;
import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.InteractiveItemEntity;
import com.rogoshum.magickcore.common.entity.living.LivingAgentEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.util.ItemStackUtil;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DimensionInflateTileEntity extends BlockEntity {
    private final List<InteractiveItemEntity> interactiveItems = new ArrayList<>();
    private ItemStack item = ItemStack.EMPTY;
    public DimensionInflateTileEntity(BlockPos blockPos, BlockState blockState) {
        super(ModTileEntities.DIMENSION_INFLATE_TILE_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundNBT = super.getUpdateTag();
        storageTag(compoundNBT);
        return compoundNBT;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        extractTag(tag);
    }

    @Override
    public void load(CompoundTag compound) {
        extractTag(compound);
        super.load(compound);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        storageTag(compound);
    }

    public void extractTag(CompoundTag compound) {
        item = ItemStack.of(compound.getCompound("stack"));
    }

    public void storageTag(CompoundTag compound) {
        compound.put("stack", item.save(new CompoundTag()));
    }

    protected void updateInfo() {
        if (!level.isClientSide)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
    }

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
    public void onLoad() {
        super.onLoad();
        //MagickCore.proxy.addRenderer(() -> new RadianceCrystalRenderer(this));
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, DimensionInflateTileEntity me) {
        me.tick(level, blockPos);
    }

    public void tick(Level level, BlockPos blockPos) {
    }

    public void setItemStack(ItemStack stack) {
        if(!this.level.isClientSide) {
            dropItem();
            this.item = stack;
            ItemDimensionData dimensionData = ExtraDataUtil.itemDimensionData(item);
            ImmutableList<ItemStack> slots = dimensionData.getSlots();
            if(slots.size() > 0) {
                float part = 360f / slots.size();
                for(int i = 0; i < slots.size(); ++i) {
                    InteractiveItemEntity interactiveItem = ModEntities.INTERACTIVE_ITEM.get().create(this.level);
                    interactiveItem.setDimensionBlock(this, i);
                    Vec3 rotate = ParticleUtil.getVectorForRotation(0, i * part).scale(1.5);
                    interactiveItem.setPos(Vec3.atCenterOf(this.getBlockPos()).add(rotate));
                    interactiveItem.setItemStack(slots.get(i));
                    if(this.level.addFreshEntity(interactiveItem))
                        interactiveItems.add(interactiveItem);
                }
                this.level.playSound(null, this.getBlockPos(), SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 0.25f, 2.0f);
            }
            updateInfo();
        }
    }

    public ItemStack getItemStack() {
        return this.item;
    }

    public void dropItem() {
        if(!this.item.isEmpty()) {
            ItemStackUtil.dropItem(level, this.item, this.getBlockPos());
            this.item = ItemStack.EMPTY;
        }
        interactiveItems.forEach(Entity::discard);
        interactiveItems.clear();
        updateInfo();
    }

    public List<ItemStack> getDrops() {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(this.item);
        ItemStack stack = new ItemStack(ModItems.DIMENSION_INFLATE.get());
        ItemStackUtil.storeTEInStack(stack, this);
        stacks.add(stack);
        return stacks;
    }

    public void dropThis() {
        ItemStack stack = new ItemStack(ModItems.DIMENSION_INFLATE.get());
        ItemStackUtil.storeTEInStack(stack, this);
        ItemStackUtil.dropItem(level, stack, this.getBlockPos());
    }
}
