package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IRedStoneEntity;
import com.rogoshum.magickcore.api.itemstack.IManaData;
import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.client.entity.easyrender.ManaCapacityRenderer;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.placeable.EntityItem;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.api.magick.ManaCapacity;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.api.magick.context.child.TraceContext;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.EntityInteractHelper;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;

public class ManaCapacityEntity extends ManaPointEntity implements IManaCapacity, IManaRefraction, IRedStoneEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mana_capacity.png");
    private static final EntityDataAccessor<Boolean> TRANS = SynchedEntityData.defineId(ManaCapacityEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MODE = SynchedEntityData.defineId(ManaCapacityEntity.class, EntityDataSerializers.BOOLEAN);
    private final ManaCapacity manaCapacity = ManaCapacity.create(500000);
    private boolean dead = false;
    private BlockPos blockPos;
    public ManaCapacityEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.spellContext().tick(-1);
        this.entityData.define(MODE, false);
        this.entityData.define(TRANS, false);
    }

    @Override
    public boolean isPickable() {
        return true;
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new ManaCapacityRenderer(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void setTrans(boolean trans) {
        this.entityData.set(TRANS, trans);
    }

    public void switchTrans() {
        this.entityData.set(TRANS, !this.entityData.get(TRANS));
    }

    public boolean getTrans() {
        return this.entityData.get(TRANS);
    }

    public void setMode(boolean mode) {
        this.entityData.set(MODE, mode);
    }

    public void switchMode() {
        this.entityData.set(MODE, !this.entityData.get(MODE));
    }

    public boolean getMode() {
        return this.entityData.get(MODE);
    }

    @Override
    public void remove(RemovalReason reason) {
        if(!dead) {
            dead = true;
            ItemStack stack = NBTTagHelper.createItemWithEntity(this, ModItems.MAGICK_CONTAINER.get(), 1);
            ItemEntity entity = new ItemEntity(level, this.getX(), this.getY() + 0.5f, this.getZ(), stack);
            if (!this.level.isClientSide)
                level.addFreshEntity(entity);
            onRemoveRedStone(blockPos, level);
        }
        super.remove(reason);
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(!level.isClientSide && source.getEntity() instanceof LivingEntity) {
            damageEntity();
            return true;
        }
        else
            return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount == 1) {
            this.playSound(SoundEvents.STONE_PLACE, 1F, 1.0F + MagickCore.rand.nextFloat());
        }
    }

    @Override
    public float eyeHeight() {
        return this.getBbHeight() + 0.5f;
    }

    @Override
    public float getSourceLight() {
        return 10;
    }

    @Override
    public boolean releaseMagick() {
        blockPos = onTickRedStone(position(), blockPos, level);
        List<Entity> list = this.findEntity((entity) -> entity instanceof ItemEntity);
        int size = list.size();
        list.removeIf( entity -> {
            boolean remove = !(((ItemEntity) entity).getItem().getItem() instanceof IManaData);
            if(manaCapacity().getMana() < manaCapacity().getMaxMana() && remove) {
                ItemEntity item = (ItemEntity)entity;
                if(item.getItem().isEdible()) {
                    int healing = item.getItem().getItem().getFoodProperties().getNutrition();
                    float saturation = item.getItem().getItem().getFoodProperties().getSaturationModifier();
                    boolean meat = item.getItem().getItem().getFoodProperties().isMeat();

                    float mana = meat ? (healing * 120 + saturation * 100) * 1.5f : healing * 60 + saturation * 50;
                    if(manaCapacity().getMana() + mana <= manaCapacity().getMaxMana()) {
                        manaCapacity().receiveMana(mana);
                        item.getItem().shrink(1);
                    }
                }
            }
            return remove;
        });
        if(list.size() < size)
            level.updateNeighborsAt(blockPos, level.getBlockState(blockPos).getBlock());
        else if(tickCount % 10 == 0)
                level.updateNeighborsAt(blockPos, level.getBlockState(blockPos).getBlock());
        for (Entity entity : list) {
            ItemEntity item = (ItemEntity)entity;
            float manaTrans = this.manaCapacity().extractMana(30);
            float lost = ExtraDataUtil.itemManaData(item.getItem()).manaCapacity().receiveMana(manaTrans);
            this.manaCapacity().receiveMana(lost);
        }
        if(this.getCaster() instanceof LivingEntity) {
            LivingEntity player = (LivingEntity) this.getCaster();
            AtomicReference<EntityStateData> state = new AtomicReference<>();
            ExtraDataUtil.entityData(player).<EntityStateData>execute(LibEntityData.ENTITY_STATE, (data) -> {
                this.spellContext().element(data.getElement());
                state.set(data);
            });

            if(getTrans()) {
                double dis = player.distanceToSqr(this.position().x(), this.position().y(), this.position().z());
                if(state.get() == null)
                    return false;

                if(dis > 256) {
                    setTrans(false);
                    return false;
                }

                int manaTrans = 25;
                if(!getMode()) {
                    if(manaCapacity().getMana() < manaCapacity().getMaxMana() && state.get().getManaValue() >= manaTrans)
                        state.get().setManaValue(state.get().getManaValue() - manaTrans + manaCapacity().receiveMana(manaTrans));
                    else
                        setTrans(false);
                } else if(!this.level.isClientSide && tickCount % 20 == 0){
                    float needed = state.get().getMaxManaValue() - state.get().getManaValue();
                    if(needed > manaTrans) {
                        ManaElementOrbEntity elementOrb = ModEntities.ELEMENT_ORB.get().create(level);
                        elementOrb.setPos(this.getX(), this.getY() + this.getBbHeight() * 0.5, this.getZ());
                        elementOrb.spellContext().element(spellContext().element);
                        elementOrb.spellContext().tick(200);
                        elementOrb.spellContext().addChild(TraceContext.create(getCaster()));
                        elementOrb.manaCapacity().setMana(manaCapacity().extractMana(5 * 20));
                        elementOrb.setDeltaMovement(getCaster().position().subtract(elementOrb.position()).normalize());
                        level.addFreshEntity(elementOrb);
                    }
                }
                level.updateNeighborsAt(blockPos, level.getBlockState(blockPos).getBlock());
                if(level.isClientSide && !getMode()){
                    int distance = Math.max((int) (10 * dis), 1);
                    float directionPoint = (float) (player.tickCount % distance) / distance;
                    int c = (int) (directionPoint * distance);

                    Vec3 end = this.position().add(0, this.getBbHeight() / 2, 0);
                    Vec3 start = player.position().add(0, player.getBbHeight() / 2, 0);
                    float scale;
                    for (int i = 0; i < distance; i++) {
                        if(i == c)
                            scale = 0.25f;
                        else
                            scale = 0.10f;
                        double trailFactor = i / (distance - 1.0D);
                        Vec3 pos = ParticleUtil.drawLine(start, end, trailFactor);
                        LitParticle par = new LitParticle(this.level, state.get().getElement().getRenderer().getParticleTexture()
                                , new Vec3(pos.x, pos.y, pos.z), scale, scale, 1.0f, 5, state.get().getElement().getRenderer());
                        par.setParticleGravity(0);
                        par.setLimitScale();
                        par.setGlow();
                        MagickCore.addMagickParticle(par);
                    }
                }
            }
        }
        else {
            setTrans(false);
            this.spellContext().element(MagickRegistry.getElement(LibElements.ORIGIN));
        }

        return false;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.NON_MANA;
    }

    @Override
    protected void applyParticle() {
        LitParticle litPar = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getX()
                , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getY() + this.getBbHeight() * 0.5
                , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getZ())
                , 0.1f, 0.1f, 0.8f, spellContext().element.getRenderer().getParticleRenderTick(), spellContext().element.getRenderer());
        litPar.setGlow();
        litPar.setParticleGravity(0f);
        litPar.setLimitScale();

        if(getMode()) {
            litPar.addMotion(MagickCore.getNegativeToOne() * 0.02, MagickCore.getNegativeToOne() * 0.02, MagickCore.getNegativeToOne() * 0.02);
        }
        MagickCore.addMagickParticle(litPar);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        InteractionResult ret = super.interact(player, hand);
        if (ret.consumesAction()) return ret;
        ItemStack heldItem = player.getItemInHand(hand);
        if(heldItem.getItem() instanceof BlockItem || heldItem.getItem() instanceof EntityItem) {
            return EntityInteractHelper.placeBlock(player, hand, heldItem, this);
        }
        if (!player.level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            this.setCaster(player);
            if (this.getCaster() == player) {
                if(player.getMainHandItem().getItem() == ModItems.WAND.get())
                    this.switchMode();
                else
                    this.switchTrans();
            }
            return InteractionResult.CONSUME;
        }
        playSound(SoundEvents.BEACON_ACTIVATE, 0.5f, 2.0f);
        return InteractionResult.PASS;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        manaCapacity().deserialize(compound);
        setMode(compound.getBoolean("MODE"));
        setTrans(compound.getBoolean("TRANS"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        manaCapacity().serialize(compound);
        compound.putBoolean("MODE", getMode());
        compound.putBoolean("TRANS", getTrans());
    }

    @Override
    public ManaCapacity manaCapacity() {
        return manaCapacity;
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.level.getEntities(this, this.getBoundingBox(), predicate);
    }

    @Override
    public boolean refraction(SpellContext context) {
        return true;
    }

    @Override
    public int getPower() {
        return dead ? 0 : Math.max(14 - (int) (manaCapacity().getMana() * 15 / manaCapacity().getMaxMana()), 0);
    }
}
