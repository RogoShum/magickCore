package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.enums.ManaLimit;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.api.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.api.magick.context.child.PositionContext;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.client.tileentity.easyrender.RadianceCrystalRenderer;
import com.rogoshum.magickcore.common.entity.living.LivingAgentEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.util.ItemStackUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
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

public class RadianceCrystalTileEntity extends BlockEntity implements ISpellContext {
    public static final ConcurrentHashMap<ResourceKey<Level>, List<RadianceCrystalTileEntity>> RADIANCE_CRYSTALS = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Player, HashSet<RadianceCrystalTileEntity>> RADIANCE_PLAYER = new ConcurrentHashMap<>();
    private final SpellContext context = SpellContext.create();
    private ApplyType applyType = ApplyType.RADIANCE;
    private ItemStack item = ItemStack.EMPTY;
    private MagickElement element = ModElements.ORIGIN;
    public int tickCount;
    private Entity capacity;
    private LivingAgentEntity livingAgent;
    public RadianceCrystalTileEntity(BlockPos blockPos, BlockState blockState) {
        super(ModTileEntities.RADIANCE_CRYSTAL_TILE_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundNBT = super.getUpdateTag();
        storageTag(compoundNBT);
        return compoundNBT;
    }

    public static int workRange() {
        return 10;
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
        applyType = ApplyType.getEnum(compound.getString("apply_type"));
        element = MagickRegistry.getElement(compound.getString("element"));
    }

    public void storageTag(CompoundTag compound) {
        compound.put("stack", item.save(new CompoundTag()));
        compound.putString("apply_type", applyType.toString());
        compound.putString("element", element.type());
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
        MagickCore.proxy.addRenderer(() -> new RadianceCrystalRenderer(this));
        if(!RADIANCE_CRYSTALS.containsKey(this.level.dimension()))
            RADIANCE_CRYSTALS.put(this.level.dimension(), new ArrayList<>());
        List<RadianceCrystalTileEntity> list = RADIANCE_CRYSTALS.get(this.level.dimension());
        list.add(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if(!RADIANCE_CRYSTALS.containsKey(this.level.dimension()))
            RADIANCE_CRYSTALS.put(this.level.dimension(), new ArrayList<>());
        List<RadianceCrystalTileEntity> list = RADIANCE_CRYSTALS.get(this.level.dimension());
        list.remove(this);
        removeFromPublicMap(new ArrayList<>());
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if(!RADIANCE_CRYSTALS.containsKey(this.level.dimension()))
            RADIANCE_CRYSTALS.put(this.level.dimension(), new ArrayList<>());
        List<RadianceCrystalTileEntity> list = RADIANCE_CRYSTALS.get(this.level.dimension());
        list.remove(this);
        removeFromPublicMap(new ArrayList<>());
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, RadianceCrystalTileEntity me) {
        me.tick(level, blockPos);
    }

    public void tick(Level level, BlockPos blockPos) {
        this.tickCount++;
        if(this.livingAgent == null) {
            this.livingAgent = ModEntities.LIVING_ARGENT.get().create(level);
            this.livingAgent.setPos(Vec3.atCenterOf(blockPos));
            this.livingAgent.level = level;
        }

        /*
                if(this.capacity == null) {
            List<Entity> capacities = level.getEntities((Entity) null, new AABB(blockPos).inflate(5), e -> e instanceof IManaCapacity);
            Optional<Entity> artificial = capacities.stream().min(Comparator.comparing((capacity -> capacity.distanceToSqr(Vec3.atCenterOf(blockPos)))));
            artificial.ifPresent(capacity -> this.capacity = capacity);
        } else if(!this.capacity.isAlive())
            this.capacity = null;

        if(this.capacity instanceof IManaCapacity capacity) {
            float manaNeed = 0;
            EntityStateData state = ExtraDataUtil.entityStateData(this.livingAgent);
            state.setMaxManaValue(ManaLimit.MAX_MANA.getValue());
            if(state.getManaValue() < state.getMaxManaValue()) {
                manaNeed = state.getMaxManaValue() - state.getManaValue();
            }
            if(manaNeed > 0) {
                float mana = capacity.manaCapacity().extractMana(manaNeed);
                state.setManaValue(state.getManaValue() + mana);
            }
        }
         */
        EntityStateData state = ExtraDataUtil.entityStateData(this.livingAgent);
        state.setMaxManaValue(ManaLimit.MAX_MANA.getValue());
        releaseMagick(level, blockPos, true);//applyType == ApplyType.RADIANCE
    }

    public void releaseMagick(Level level, BlockPos blockPos, boolean noCost) {
        if(this.tickCount % 10 == 0) {
            SpellContext spell = SpellContext.create().applyType(this.applyType).force(1).range(1).tick(20).element(this.element);
            List<Entity> entities = level.getEntities((Entity) null, new AABB(blockPos).inflate(workRange()), entity -> {
                if(entity instanceof IManaRefraction && ((IManaRefraction) entity).refraction(spell))
                    return false;
                return entity != this.livingAgent;
            });
            float force = 1;
            if(entities.size() > 3)//applyType == ApplyType.RADIANCE &&
                force = 3f / entities.size();
            removeFromPublicMap(entities);
            for(Entity entity : entities) {
                if(entity instanceof Player)
                    addPlayerToPublicMap((Player) entity);
                MagickContext context = MagickContext.create(level, spell).caster(this.livingAgent)
                        .replenishChild(DirectionContext.create(entity.position().add(0, entity.getBbHeight()*0.5, 0).subtract(Vec3.atCenterOf(blockPos))))
                        .<MagickContext>replenishChild(PositionContext.create(Vec3.atCenterOf(blockPos)))
                        .victim(entity).force(force);
                if(noCost)
                    context.noCost();
                MagickReleaseHelper.releaseMagick(context);
            }
        }
    }

    public void addPlayerToPublicMap(Player player) {
        if(!RADIANCE_PLAYER.containsKey(player))
            RADIANCE_PLAYER.put(player, new HashSet<>());
        if(!RADIANCE_PLAYER.get(player).contains(this))
            RADIANCE_PLAYER.get(player).add(this);
    }

    public void removeFromPublicMap(List<Entity> list) {
        for(Player player : RADIANCE_PLAYER.keySet()) {
            HashSet<RadianceCrystalTileEntity> crystals = RadianceCrystalTileEntity.RADIANCE_PLAYER.get(player);
            if(!list.contains(player) && crystals.contains(this))
                crystals.remove(this);
        }
    }

    public void setItemStack(ItemStack stack) {
        if(stack.getItem() instanceof IManaMaterial material) {
            if (material.typeMaterial()) {
                dropItem();
                material.upgradeManaItem(stack, this);
                this.setApplyType(spellContext().applyType());
                this.item = stack.copy();
                stack.shrink(1);
                this.item.setCount(1);
            }
        }
        updateInfo();
    }

    public ItemStack getItemStack() {
        return this.item;
    }

    public void dropItem() {
        if(!this.item.isEmpty()) {
            ItemStackUtil.dropItem(level, this.item, this.getBlockPos());
            this.item = ItemStack.EMPTY;
        }
        this.applyType = ApplyType.RADIANCE;
        updateInfo();
    }

    public List<ItemStack> getDrops() {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(this.item);
        ItemStack stack = new ItemStack(ModItems.RADIANCE_CRYSTAL.get());
        stack.getOrCreateTag().putString("ELEMENT", this.element.type());
        stack.getOrCreateTag().putString("APPLY_TYPE", this.applyType.getLabel());
        ItemStackUtil.storeTEInStack(stack, this);
        stacks.add(stack);
        return stacks;
    }

    public void dropThis() {
        ItemStack stack = new ItemStack(ModItems.RADIANCE_CRYSTAL.get());
        stack.getOrCreateTag().putString("ELEMENT", this.element.type());
        stack.getOrCreateTag().putString("APPLY_TYPE", this.applyType.getLabel());
        ItemStackUtil.storeTEInStack(stack, this);
        ItemStackUtil.dropItem(level, stack, this.getBlockPos());
    }

    public void setApplyType(ApplyType applyType) {
        this.applyType = applyType;
    }

    public ApplyType getApplyType() {
        return applyType;
    }

    public void setElement(MagickElement element) {
        this.element = Objects.requireNonNullElse(element, ModElements.ORIGIN);
    }

    public MagickElement getElement() {
        return this.element;
    }

    @Override
    public SpellContext spellContext() {
        return context;
    }
}
