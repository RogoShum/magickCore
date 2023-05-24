package com.rogoshum.magickcore.common.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.item.ItemDimensionData;
import com.rogoshum.magickcore.api.item.IDimensionItem;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.client.entity.easyrender.LightSourceRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.init.ModDataSerializers;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.item.AssemblyEssenceItem;
import com.rogoshum.magickcore.common.item.ElementStringItem;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.recipe.ElementToolRecipe;
import com.rogoshum.magickcore.common.tileentity.DimensionInflateTileEntity;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import com.rogoshum.magickcore.common.util.ItemStackUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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

public class BlockLinkLightEntity extends Entity implements IEntityAdditionalSpawnData, IManaRefraction, ILightSourceEntity {
    private static final EntityDataAccessor<Integer> INTENSITY = SynchedEntityData.defineId(BlockLinkLightEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> ELEMENT = SynchedEntityData.defineId(BlockLinkLightEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Vec3> COLOR = SynchedEntityData.defineId(BlockLinkLightEntity.class, ModDataSerializers.VECTOR3D);
    private Vec3 position;

    public BlockLinkLightEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.entityData.define(INTENSITY, 5);
        this.entityData.define(ELEMENT, "origin");
        this.entityData.define(COLOR, Vec3.ZERO);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        EntityLightSourceManager.addLightSource(this);
        MagickCore.proxy.addRenderer(() -> new LightSourceRenderer(this));
    }

    public int getIntensity() {
        return this.entityData.get(INTENSITY);
    }

    public void setIntensity(int i) {
        this.entityData.set(INTENSITY, i);
    }

    public String getElement() {
        return this.entityData.get(ELEMENT);
    }

    public void setElement(String e) {
        this.entityData.set(ELEMENT, e);
    }

    public void setColor(Vec3 e) {
        this.entityData.set(COLOR, e);
    }

    public void setColor(Color e) {
        setColor(new Vec3(e.r(), e.g(), e.b()));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.position == null)
            this.position = this.position();
        else if(!position.equals(this.position()))
            this.setPos(position);

        List<BlockLinkLightEntity> entities = this.world().getEntitiesOfClass(BlockLinkLightEntity.class, this.getBoundingBox().inflate(0.5), BlockLinkLightEntity::alive);
        for(BlockLinkLightEntity entity : entities) {
            if(entity.tickCount > this.tickCount)
                entity.discard();
        }
        if(this.level.getBlockState(this.blockPosition()).isAir())
            this.discard();
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
        if(compound.contains("Intensity"))
            this.setIntensity(compound.getInt("Intensity"));
        if(compound.contains("Element"))
            this.setElement(compound.getString("Element"));
        if(compound.contains("Color"))
            this.setColor(Color.create(compound.getInt("Color")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Intensity", getIntensity());
        compound.putString("Element", getElement());
        compound.putInt("Color", getColor().decimalColor());
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
        return getIntensity();
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
        return 0f;
    }

    @Override
    public Color getColor() {
        Vec3 vec3 = this.entityData.get(COLOR);
        if(!vec3.equals(Vec3.ZERO))
            return Color.create((float) vec3.x, (float) vec3.y, (float) vec3.z);
        return MagickRegistry.getElement(getElement()).primaryColor();
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
