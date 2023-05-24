package com.rogoshum.magickcore.common.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.item.placeable.EntityItem;
import com.rogoshum.magickcore.common.item.placeable.PlaceableEntityItem;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.common.recipe.SpiritCraftingRecipe;
import com.rogoshum.magickcore.common.recipe.MatrixInventory;
import com.rogoshum.magickcore.common.tileentity.MagickCraftingTileEntity;
import com.rogoshum.magickcore.common.util.EntityInteractHelper;
import com.rogoshum.magickcore.common.util.MultiBlockUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.Optional;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public class PlaceableItemEntity extends Entity implements IEntityAdditionalSpawnData, IManaRefraction {
    private static final EntityDataAccessor<ItemStack> ITEM_STACK = SynchedEntityData.defineId(PlaceableItemEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Direction> DIRECTION = SynchedEntityData.defineId(PlaceableItemEntity.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(PlaceableItemEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(PlaceableItemEntity.class, EntityDataSerializers.FLOAT);
    private boolean noDrops = false;

    public PlaceableItemEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.entityData.define(ITEM_STACK, ItemStack.EMPTY);
        this.entityData.define(DIRECTION, Direction.DOWN);
        this.entityData.define(HEIGHT, this.getType().getHeight());
        this.entityData.define(WIDTH, this.getType().getWidth());
    }

    @Override
    public Component getDisplayName() {
        return getItemStack().getDisplayName();
    }

    @Override
    protected Component getTypeName() {
        return getItemStack().getDisplayName();
    }

    public Direction getDirection() {
        return this.entityData.get(DIRECTION);
    }

    public void setDirection(Direction direction) {
        this.entityData.set(DIRECTION, direction);
    }

    public ItemStack getItemStack() {
        return this.entityData.get(ITEM_STACK);
    }

    public void setItemStack(ItemStack itemStack) {
        this.entityData.set(ITEM_STACK, itemStack.copy());
    }

    public void setHeight(float height) {
        this.getEntityData().set(HEIGHT, height);
    }
    public void setWidth(float width) {
        this.getEntityData().set(WIDTH, width);
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn) {
        return EntityDimensions.scalable(this.getEntityData().get(WIDTH), this.getEntityData().get(HEIGHT));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (HEIGHT.equals(key) || WIDTH.equals(key) || DIRECTION.equals(key)) {
            this.refreshDimensions();
            double d0 = (double)getBbWidth() * 0.5D;
            Vec3 pos = position();
            if(this.getDirection().getAxis().isVertical() && this.getDirection().getAxisDirection().getStep() == -1) {
                pos = pos.subtract(0, getBbHeight(), 0);
            } else {
                pos = pos.add(Vec3.atLowerCornerOf(getDirection().getNormal()).scale(d0)).subtract(0, getBbHeight() * 0.5, 0);
            }
            this.setBoundingBox(new AABB(pos.x - d0, pos.y, pos.z - d0, pos.x + d0, pos.y + (double)getBbHeight(), pos.z + d0));
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        remove(RemovalReason.DISCARDED);
        return super.hurt(source, amount);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        InteractionResult ret = super.interact(player, hand);
        if (ret.consumesAction()) return ret;
        if (!player.level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            ItemStack stack = player.getMainHandItem();
            if(stack.getItem() instanceof PlaceableEntityItem) {
                Vec3 vector3d = player.getLookAngle();
                nextNode(stack, Direction.getNearest(vector3d.x, vector3d.y, vector3d.z));
            } else if(stack.getItem() instanceof BlockItem || stack.getItem() instanceof EntityItem) {
                return EntityInteractHelper.placeBlock(player, hand, stack, this);
            } else if (stack.getItem() == ModItems.WAND.get()) {
                BlockPos pos = new BlockPos(this.position());
                BlockEntity tile = level.getBlockEntity(pos);
                if(tile instanceof MagickCraftingTileEntity) {
                    MagickCraftingTileEntity workbench = (MagickCraftingTileEntity) tile;
                    Optional<PlaceableItemEntity>[][][] matrix = MultiBlockUtil.createBlockPosArrays(workbench.getCraftingMatrix().getMatrix(), Optional.empty());
                    if(matrix != null) {
                        MatrixInventory matrixInventory = new MatrixInventory(matrix);
                        Optional<SpiritCraftingRecipe> optional = level.getRecipeManager().getRecipeFor(SpiritCraftingRecipe.SPIRIT_CRAFTING, matrixInventory, level);
                        if(optional.isPresent()) {
                            level.broadcastEntityEvent(this, (byte) 14);
                            workbench.getCraftingMatrix().getMatrix().values().forEach(entity -> {
                                entity.noDrops = true;
                                entity.remove(RemovalReason.DISCARDED);
                            });
                            Vec3 vec = Vec3.atCenterOf(pos);
                            ItemStack stack1 = optional.get().assemble(matrixInventory);
                            ItemEntity itemEntity = new ItemEntity(player.level, vec.x, vec.y, vec.z, stack1);
                            player.level.addFreshEntity(itemEntity);
                            this.playSound(SoundEvents.BEACON_POWER_SELECT, 0.5f, 2.0f);
                        }
                    }
                }
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if(id == 14)
            spawnParticle();
        else
            super.handleEntityEvent(id);
    }

    public void spawnParticle() {
        float scale = 1f;
        for (int i = 0; i < 20; ++i) {
            LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vec3(Mth.sin(MagickCore.getNegativeToOne() * 0.3f) + position().x
                    , position().y + 0.2
                    , Mth.sin(MagickCore.getNegativeToOne() * 0.3f) + position().z)
                    , scale * 0.2f, scale * 2f, 0.5f, Math.max((int) (40 * MagickCore.rand.nextFloat()), 20), ModElements.ORIGIN.getRenderer());
            par.setGlow();
            par.setParticleGravity(-0.1f);
            par.setColor(Color.BLUE_COLOR);
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(getItemStack().isEmpty())
            this.remove(RemovalReason.DISCARDED);
        this.setXRot(-90);
        double d0 = (double)getBbWidth() * 0.5D;
        Vec3 pos = position();
        if(this.getDirection().getAxis().isVertical() && this.getDirection().getAxisDirection().getStep() == -1) {
            pos = pos.subtract(0, getBbHeight(), 0);
        } else {
            pos = pos.add(Vec3.atLowerCornerOf(getDirection().getNormal()).scale(d0)).subtract(0, getBbHeight() * 0.5, 0);
        }
        this.setBoundingBox(new AABB(pos.x - d0, pos.y, pos.z - d0, pos.x + d0, pos.y + (double)getBbHeight(), pos.z + d0));
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if(!noDrops) {
            ItemEntity entity = new ItemEntity(level, this.getX(), this.getY() + getBbHeight() * 0.5, this.getZ(), getItemStack());
            if(!this.level.isClientSide)
                level.addFreshEntity(entity);
        }
    }

    @Override
    protected void defineSynchedData() {}

    public void nextNode(ItemStack stack, Direction direction) {
        double d0 = getBbWidth() * 0.5;
        double d1 = getBbHeight() * 0.5;

        Vec3 pos = position().add(Vec3.atLowerCornerOf(getDirection().getOpposite().getNormal()).scale(-d1));
        if(direction.getAxis().isVertical()) {
            pos = pos.subtract(Vec3.atLowerCornerOf(direction.getOpposite().getNormal()).scale(-d1));
        } else {
            pos = pos.subtract(Vec3.atLowerCornerOf(direction.getOpposite().getNormal()).scale(-d0));
        }

        PlaceableItemEntity next = PlaceableEntityItem.placeEntity(level, stack, direction.getOpposite(), pos);
        if(next != null) {
            next.level.addFreshEntity(next);
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        CompoundTag addition = new CompoundTag();
        addAdditionalSaveData(addition);
        buffer.writeNbt(addition);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        readAdditionalSaveData(additionalData.readNbt());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if(compound.contains("Direction"))
            this.setDirection(Direction.byName(compound.getString("Direction")));
        if(compound.contains("Item"))
            this.setItemStack(ItemStack.of(compound.getCompound("Item")));
        if(compound.contains("Width"))
            this.setWidth(compound.getFloat("Width"));
        if(compound.contains("Height"))
            this.setHeight(compound.getFloat("Height"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putString("Direction", getDirection().getName());
        compound.put("Item", getItemStack().save(new CompoundTag()));
        compound.putFloat("Height", getBbHeight());
        compound.putFloat("Width", getBbWidth());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean refraction(SpellContext context) {
        return true;
    }
}
