package com.rogoshum.magickcore.common.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.item.placeable.EntityItem;
import com.rogoshum.magickcore.common.item.placeable.PlaceableEntityItem;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.recipe.SpiritCraftingRecipe;
import com.rogoshum.magickcore.common.recipe.MatrixInventory;
import com.rogoshum.magickcore.common.tileentity.MagickCraftingTileEntity;
import com.rogoshum.magickcore.common.util.EntityInteractHelper;
import com.rogoshum.magickcore.common.util.MultiBlockUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Optional;

public class PlaceableItemEntity extends Entity implements IEntityAdditionalSpawnData, IManaRefraction {
    private static final DataParameter<ItemStack> ITEM_STACK = EntityDataManager.createKey(PlaceableItemEntity.class, DataSerializers.ITEMSTACK);
    private static final DataParameter<Direction> DIRECTION = EntityDataManager.createKey(PlaceableItemEntity.class, DataSerializers.DIRECTION);
    private static final DataParameter<Float> HEIGHT = EntityDataManager.createKey(PlaceableItemEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> WIDTH = EntityDataManager.createKey(PlaceableItemEntity.class, DataSerializers.FLOAT);
    private boolean noDrops = false;

    public PlaceableItemEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.dataManager.register(ITEM_STACK, ItemStack.EMPTY);
        this.dataManager.register(DIRECTION, Direction.DOWN);
        this.dataManager.register(HEIGHT, this.getType().getHeight());
        this.dataManager.register(WIDTH, this.getType().getWidth());
    }

    public Direction getDirection() {
        return this.dataManager.get(DIRECTION);
    }

    public void setDirection(Direction direction) {
        this.dataManager.set(DIRECTION, direction);
    }

    public ItemStack getItemStack() {
        return this.dataManager.get(ITEM_STACK);
    }

    public void setItemStack(ItemStack itemStack) {
        this.dataManager.set(ITEM_STACK, itemStack.copy());
    }

    public void setHeight(float height) {
        this.getDataManager().set(HEIGHT, height);
    }
    public void setWidth(float width) {
        this.getDataManager().set(WIDTH, width);
    }

    @Override
    public EntitySize getSize(Pose poseIn) {
        return EntitySize.flexible(this.getDataManager().get(WIDTH), this.getDataManager().get(HEIGHT));
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (HEIGHT.equals(key) || WIDTH.equals(key) || DIRECTION.equals(key)) {
            this.recalculateSize();
            double d0 = (double)getWidth() * 0.5D;
            Vector3d pos = getPositionVec();
            if(this.getDirection().getAxis().isVertical() && this.getDirection().getAxisDirection().getOffset() == -1) {
                pos = pos.subtract(0, getHeight(), 0);
            } else {
                pos = pos.add(Vector3d.copy(getDirection().getDirectionVec()).scale(d0)).subtract(0, getHeight() * 0.5, 0);
            }
            this.setBoundingBox(new AxisAlignedBB(pos.x - d0, pos.y, pos.z - d0, pos.x + d0, pos.y + (double)getHeight(), pos.z + d0));
        }
        super.notifyDataManagerChange(key);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        remove();
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        ActionResultType ret = super.processInitialInteract(player, hand);
        if (ret.isSuccessOrConsume()) return ret;
        if (!player.world.isRemote && hand == Hand.MAIN_HAND) {
            ItemStack stack = player.getHeldItemMainhand();
            if(stack.getItem() instanceof BlockItem || stack.getItem() instanceof EntityItem) {
                return EntityInteractHelper.placeBlock(player, hand, stack, this);
            }
            if(stack.getItem() instanceof PlaceableEntityItem) {
                Vector3d vector3d = player.getLookVec();
                nextNode(stack, Direction.getFacingFromVector(vector3d.x, vector3d.y, vector3d.z));
            } else if (stack.getItem() == ModItems.WAND.get()) {
                BlockPos pos = new BlockPos(this.getPositionVec());
                TileEntity tile = world.getTileEntity(pos);
                if(tile instanceof MagickCraftingTileEntity) {
                    MagickCraftingTileEntity workbench = (MagickCraftingTileEntity) tile;
                    Optional<PlaceableItemEntity>[][][] matrix = MultiBlockUtil.createBlockPosArrays(workbench.getCraftingMatrix().getMatrix(), Optional.empty());
                    if(matrix != null) {
                        MatrixInventory matrixInventory = new MatrixInventory(matrix);
                        Optional<SpiritCraftingRecipe> optional = world.getRecipeManager().getRecipe(SpiritCraftingRecipe.SPIRIT_CRAFTING, matrixInventory, world);
                        if(optional.isPresent()) {
                            workbench.getCraftingMatrix().getMatrix().values().forEach(entity -> {
                                entity.noDrops = true;
                                entity.remove();
                            });
                            Vector3d vec = Vector3d.copyCentered(pos);
                            ItemStack stack1 = optional.get().getCraftingResult(matrixInventory);
                            ItemEntity itemEntity = new ItemEntity(player.world, vec.x, vec.y, vec.z, stack1);
                            player.world.addEntity(itemEntity);
                            this.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT, 0.5f, 2.0f);
                            world.setEntityState(this, (byte) 14);
                        }
                    }
                }
            }
            return ActionResultType.CONSUME;
        }
        return ActionResultType.PASS;
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if(id == 14)
            spawnParticle();
        else
            super.handleStatusUpdate(id);
    }

    public void spawnParticle() {
        float scale = 1f;
        for (int i = 0; i < 20; ++i) {
            LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vector3d(MathHelper.sin(MagickCore.getNegativeToOne() * 0.3f) + getPositionVec().x
                    , getPositionVec().y + 0.2
                    , MathHelper.sin(MagickCore.getNegativeToOne() * 0.3f) + getPositionVec().z)
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
            this.remove();
        this.rotationPitch = -90;
        double d0 = (double)getWidth() * 0.5D;
        Vector3d pos = getPositionVec();
        if(this.getDirection().getAxis().isVertical() && this.getDirection().getAxisDirection().getOffset() == -1) {
            pos = pos.subtract(0, getHeight(), 0);
        } else {
            pos = pos.add(Vector3d.copy(getDirection().getDirectionVec()).scale(d0)).subtract(0, getHeight() * 0.5, 0);
        }
        this.setBoundingBox(new AxisAlignedBB(pos.x - d0, pos.y, pos.z - d0, pos.x + d0, pos.y + (double)getHeight(), pos.z + d0));
    }

    @Override
    public void remove() {
        super.remove();
        if(!noDrops) {
            ItemEntity entity = new ItemEntity(world, this.getPosX(), this.getPosY() + getHeight() * 0.5, this.getPosZ(), getItemStack());
            if(!this.world.isRemote)
                world.addEntity(entity);
        }
    }

    @Override
    protected void registerData() {}

    public void nextNode(ItemStack stack, Direction direction) {
        double d0 = getWidth() * 0.5;
        double d1 = getHeight() * 0.5;

        Vector3d pos = getPositionVec().add(Vector3d.copy(getDirection().getOpposite().getDirectionVec()).scale(-d1));
        if(direction.getAxis().isVertical()) {
            pos = pos.subtract(Vector3d.copy(direction.getOpposite().getDirectionVec()).scale(-d1));
        } else {
            pos = pos.subtract(Vector3d.copy(direction.getOpposite().getDirectionVec()).scale(-d0));
        }

        PlaceableItemEntity next = PlaceableEntityItem.placeEntity(world, stack, direction.getOpposite(), pos);
        if(next != null) {
            next.world.addEntity(next);
        }
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        CompoundNBT addition = new CompoundNBT();
        writeAdditional(addition);
        buffer.writeCompoundTag(addition);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        readAdditional(additionalData.readCompoundTag());
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        if(compound.contains("Direction"))
            this.setDirection(Direction.byName(compound.getString("Direction")));
        if(compound.contains("Item"))
            this.setItemStack(ItemStack.read(compound.getCompound("Item")));
        if(compound.contains("Width"))
            this.setWidth(compound.getFloat("Width"));
        if(compound.contains("Height"))
            this.setHeight(compound.getFloat("Height"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putString("Direction", getDirection().getName2());
        compound.put("Item", getItemStack().write(new CompoundNBT()));
        compound.putFloat("Height", getHeight());
        compound.putFloat("Width", getWidth());
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean refraction(SpellContext context) {
        return true;
    }
}
