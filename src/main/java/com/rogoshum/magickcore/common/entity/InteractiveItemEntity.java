package com.rogoshum.magickcore.common.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.item.ItemDimensionData;
import com.rogoshum.magickcore.api.item.IDimensionItem;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.client.entity.easyrender.LightSourceRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.item.AssemblyEssenceItem;
import com.rogoshum.magickcore.common.item.ElementStringItem;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.recipe.ElementToolRecipe;
import com.rogoshum.magickcore.common.tileentity.DimensionInflateTileEntity;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import com.rogoshum.magickcore.common.util.ItemStackUtil;
import com.rogoshum.magickcore.common.util.ParticleBuilder;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class InteractiveItemEntity extends Entity implements IEntityAdditionalSpawnData, IManaRefraction, ILightSourceEntity {
    private static final EntityDataAccessor<ItemStack> ITEM_STACK = SynchedEntityData.defineId(InteractiveItemEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<BlockPos> BLOCK_POS = SynchedEntityData.defineId(InteractiveItemEntity.class, EntityDataSerializers.BLOCK_POS);
    private DimensionInflateTileEntity dimensionBlock;
    private int index = 0;

    public InteractiveItemEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.entityData.define(ITEM_STACK, ItemStack.EMPTY);
        this.entityData.define(BLOCK_POS, BlockPos.ZERO);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        EntityLightSourceManager.addLightSource(this);
        MagickCore.proxy.addRenderer(() -> new LightSourceRenderer(this));
    }

    @Override
    public Component getDisplayName() {
        return getItemStack().getDisplayName();
    }

    @Override
    protected Component getTypeName() {
        return getItemStack().getDisplayName();
    }

    public void setDimensionBlock(DimensionInflateTileEntity block, int index) {
        this.dimensionBlock = block;
        this.index = index;
    }

    public ItemStack getItemStack() {
        return this.entityData.get(ITEM_STACK);
    }

    public void setItemStack(ItemStack itemStack) {
        this.entityData.set(ITEM_STACK, itemStack.copy());
    }

    public BlockPos getBlockPos() {
        return this.entityData.get(BLOCK_POS);
    }

    public void setBlockPos(BlockPos pos) {
        this.entityData.set(BLOCK_POS, pos);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        InteractionResult ret = super.interact(player, hand);
        if (ret.consumesAction()) return ret;
        if (!player.level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            ItemStack stack = player.getMainHandItem();
            if(this.dimensionBlock != null) {
                ItemDimensionData data = ExtraDataUtil.itemDimensionData(this.dimensionBlock.getItemStack());
                if(this.dimensionBlock.getItemStack().getItem() instanceof IDimensionItem
                        && (stack.isEmpty() || ((IDimensionItem) this.dimensionBlock.getItemStack().getItem()).shouldAddToSlots(player, stack, data.getSlots()))) {
                    ItemStack copy = stack.copy();
                    ((IDimensionItem) this.dimensionBlock.getItemStack().getItem()).onSetToSlot(player, stack, copy);
                    data.setSlot(this.index, copy);
                    ItemStackUtil.dropItem(level, this.getItemStack(), this.position().add(0, 0.25, 0));
                    this.setItemStack(copy);
                    this.level.playSound(null, this.getBlockPos(), SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 0.25f, 2.0f);
                } else if(ElementToolRecipe.isTool(this.dimensionBlock.getItemStack()) && (stack.isEmpty() || stack.getItem() instanceof ElementStringItem || stack.getItem() instanceof AssemblyEssenceItem)) {
                    List<ItemStack> stacks = data.getSlots();
                    int count = 0;
                    for(ItemStack slot : stacks) {
                        if(slot.getItem() instanceof AssemblyEssenceItem && stack.getItem() instanceof AssemblyEssenceItem)
                            return InteractionResult.FAIL;
                        if(slot.getItem() instanceof ElementStringItem)
                            count++;
                    }
                    if(stack.getItem() instanceof ElementStringItem && count >= 2)
                        return InteractionResult.FAIL;
                    ItemStack copy = stack.copy();
                    stack.setCount(0);
                    data.setSlot(this.index, copy);
                    ItemStackUtil.dropItem(level, this.getItemStack(), this.position().add(0, 0.25, 0));
                    this.setItemStack(copy);
                    this.level.playSound(null, this.getBlockPos(), SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 0.25f, 2.0f);
                } else {
                    this.level.playSound(null, this.getBlockPos(), ModSounds.horror_effect.get(), SoundSource.BLOCKS, 0.25f, 1.5f);
                    ParticleBuilder builder = ParticleBuilder.create(level, ParticleType.PARTICLE, new Vec3(MagickCore.getNegativeToOne() * 0.25 + this.position().x
                                    , MagickCore.getNegativeToOne() * 0.25 + this.position().y+0.1
                                    , MagickCore.getNegativeToOne() * 0.25 + this.position().z)
                            , 0.3f, 0.3f, 0.5f, 15, "origin");
                    builder.color(Color.RED_COLOR);
                    builder.glow();
                    builder.grav(0);
                    builder.shake(15f);
                    builder.send();
                }
            }

            return InteractionResult.PASS;
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
        if(tickCount < 20) {
            LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vec3(Mth.sin(MagickCore.getNegativeToOne() * 0.3f) + position().x
                    , position().y + 0.2 + Mth.sin(MagickCore.getNegativeToOne() * 0.3f)
                    , Mth.sin(MagickCore.getNegativeToOne() * 0.3f) + position().z)
                    , scale * 0.4f, scale * 0.4f, 0.5f, 10, ModElements.ORIGIN.getRenderer());
            par.setGlow();
            par.setParticleGravity(0);
            par.setColor(Color.BLUE_COLOR);
            MagickCore.addMagickParticle(par);
            return;
        }
        if(tickCount % 3 != 0) return;

        LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                , new Vec3(Mth.sin(MagickCore.getNegativeToOne() * 0.3f) + position().x
                , position().y + 0.2
                , Mth.sin(MagickCore.getNegativeToOne() * 0.3f) + position().z)
                , scale * 0.2f, scale * 0.2f, 0.5f, 40, ModElements.ORIGIN.getRenderer());
        par.setGlow();
        par.setParticleGravity(0);
        par.setColor(Color.BLUE_COLOR);
        Vec3 motion = Vec3.atCenterOf(getBlockPos()).add(0, 0.3, 0).subtract(par.positionVec()).normalize().scale(0.05);
        par.addMotion(motion.x, motion.y, motion.z);
        MagickCore.addMagickParticle(par);
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.level.isClientSide()) {
            if(dimensionBlock == null)
                discard();
            else
                setBlockPos(dimensionBlock.getBlockPos());
        } else if(!getBlockPos().equals(BlockPos.ZERO)){
            spawnParticle();
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
    }

    @Override
    protected void defineSynchedData() {}

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
        if(compound.contains("Item"))
            this.setItemStack(ItemStack.of(compound.getCompound("Item")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.put("Item", getItemStack().save(new CompoundTag()));
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean refraction(SpellContext context) {
        return true;
    }

    @Override
    public float getSourceLight() {
        return 5;
    }

    @Override
    public boolean alive() {
        return this.isAlive();
    }

    @Override
    public Level world() {
        return this.level;
    }

    @Override
    public float eyeHeight() {
        return 0.25f;
    }

    @Override
    public Color getColor() {
        return Color.BLUE_COLOR;
    }

    @Override
    public boolean spawnGlowBlock() {
        return true;
    }

    @Override
    public Vec3 positionVec() {
        return this.position();
    }

    @Override
    public AABB boundingBox() {
        return this.getBoundingBox();
    }
}
