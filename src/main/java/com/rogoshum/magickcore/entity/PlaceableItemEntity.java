package com.rogoshum.magickcore.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.init.ModBlocks;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.item.placeable.PlaceableEntityItem;
import com.rogoshum.magickcore.magick.Color;
import com.rogoshum.magickcore.magick.context.SpellContext;
import com.rogoshum.magickcore.recipes.MagickCraftingRecipe;
import com.rogoshum.magickcore.recipes.SpawnContext;
import com.rogoshum.magickcore.registry.MagickRegistry;
import com.rogoshum.magickcore.tool.MultiBlockHelper;
import com.rogoshum.magickcore.tool.ParticleHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.HashMap;
import java.util.Optional;

public class PlaceableItemEntity extends Entity implements IEntityAdditionalSpawnData, IManaRefraction {
    private PlaceableItemEntity origin = this;
    private BlockPos pos = BlockPos.ZERO;
    private final HashMap<BlockPos, PlaceableItemEntity> entityMap = new HashMap<>();
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
        this.entityMap.put(BlockPos.ZERO, this);
    }

    public PlaceableItemEntity getOrigin() {
        return origin;
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
            if(stack.getItem() instanceof PlaceableEntityItem) {
                Vector3d vector3d = player.getLookVec();
                nextNode(stack, Direction.getFacingFromVector(vector3d.x, vector3d.y, vector3d.z));
            } else if (stack.getItem() == ModItems.wand.get()) {
                BlockPos pos = new BlockPos(this.getPositionVec());
                if(world.getBlockState(pos).getBlock() == ModBlocks.magick_crafting.get()) {
                    Optional<PlaceableItemEntity>[][][] matrix = MultiBlockHelper.createBlockPosArrays(this.origin.entityMap, Optional.empty());
                    if(matrix != null) {
                        Optional<MagickCraftingRecipe> optional = MagickRegistry.matchMagickCraftingRecipe(matrix);
                        if(optional.isPresent()) {
                            optional.get().craft(SpawnContext.create(player, Vector3d.copyCentered(pos)));
                            this.origin.entityMap.entrySet().removeIf((pr) -> {
                                pr.getValue().noDrops = true;
                                pr.getValue().remove();
                                return true;
                            });
                        }
                    }
                }
            }
            return ActionResultType.CONSUME;
        }
        return ActionResultType.PASS;
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
            this.origin.entityMap.remove(this.pos);
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
            BlockPos nextPos = this.pos.subtract(direction.getDirectionVec());
            next.origin = this.origin;
            next.pos = nextPos;
            if(this.origin.entityMap.containsKey(nextPos)) {
                this.origin.entityMap.get(nextPos).remove();
            }
            this.origin.entityMap.put(nextPos, next);
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
