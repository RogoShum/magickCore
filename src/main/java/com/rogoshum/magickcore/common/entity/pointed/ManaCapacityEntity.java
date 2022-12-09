package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.itemstack.IManaData;
import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.client.entity.easyrender.GravityLiftRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.ManaCapacityRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.magick.ManaCapacity;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ManaCapacityEntity extends ManaPointEntity implements IManaCapacity, IManaRefraction {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mana_capacity.png");
    private static final DataParameter<Boolean> TRANS = EntityDataManager.createKey(ManaCapacityEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> MODE = EntityDataManager.createKey(ManaCapacityEntity.class, DataSerializers.BOOLEAN);
    private final ManaCapacity manaCapacity = ManaCapacity.create(20000);
    private boolean dead = false;
    public ManaCapacityEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.spellContext().tick(-1);
        this.dataManager.register(MODE, false);
        this.dataManager.register(TRANS, false);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new ManaCapacityRenderer(this);
    }

    @Override
    protected void registerData() {
        super.registerData();
    }

    public void setTrans(boolean trans) {
        this.dataManager.set(TRANS, trans);
    }

    public void switchTrans() {
        this.dataManager.set(TRANS, !this.dataManager.get(TRANS));
    }

    public boolean getTrans() {
        return this.dataManager.get(TRANS);
    }

    public void setMode(boolean mode) {
        this.dataManager.set(MODE, mode);
    }

    public void switchMode() {
        this.dataManager.set(MODE, !this.dataManager.get(MODE));
    }

    public boolean getMode() {
        return this.dataManager.get(MODE);
    }

    @Override
    public void remove() {
        if(!dead) {
            dead = true;
            ItemStack stack = NBTTagHelper.createItemWithEntity(this, ModItems.MAGICK_CONTAINER.get(), 1);
            ItemEntity entity = new ItemEntity(world, this.getPosX(), this.getPosY() + 0.5f, this.getPosZ(), stack);
            if (!this.world.isRemote)
                world.addEntity(entity);
        }
        super.remove();
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if(!world.isRemote && source.getTrueSource() instanceof LivingEntity) {
            damageEntity();
            return true;
        }
        else
            return false;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public float eyeHeight() {
        return this.getHeight() + 0.5f;
    }

    @Override
    public float getSourceLight() {
        return 10;
    }

    @Override
    public void releaseMagick() {
        //this.manaCapacity().receiveMana(50f);
        List<Entity> list = this.findEntity((entity) -> entity instanceof ItemEntity);
        list.removeIf( entity -> {
            boolean remove = !(((ItemEntity) entity).getItem().getItem() instanceof IManaData);
            if(manaCapacity().getMana() < manaCapacity().getMaxMana() && remove) {
                ItemEntity item = (ItemEntity)entity;
                if(item.getItem().isFood()) {
                    int healing = item.getItem().getItem().getFood().getHealing();
                    float saturation = item.getItem().getItem().getFood().getSaturation();
                    boolean meat = item.getItem().getItem().getFood().isMeat();

                    float mana = meat ? (healing * 20 + saturation * 10) * 1.5f : healing * 10 + saturation * 5;
                    if(manaCapacity().getMana() + mana <= manaCapacity().getMaxMana()) {
                        manaCapacity().receiveMana(mana);
                        item.getItem().shrink(1);
                    }
                }
            }
            return remove;
        });
        for (Entity entity : list) {
            ItemEntity item = (ItemEntity)entity;
            float manaTrans = this.manaCapacity().extractMana(5);
            float lost = ExtraDataUtil.itemManaData(item.getItem()).manaCapacity().receiveMana(manaTrans);
            this.manaCapacity().receiveMana(lost);
        }
        if(this.getOwner() instanceof LivingEntity) {
            LivingEntity player = (LivingEntity) this.getOwner();
            AtomicReference<EntityStateData> state = new AtomicReference<>();
            ExtraDataUtil.entityData(player).<EntityStateData>execute(LibEntityData.ENTITY_STATE, (data) -> {
                this.spellContext().element(data.getElement());
                state.set(data);
            });

            if(getTrans()) {
                double dis = player.getDistanceSq(this.getPositionVec().getX(), this.getPositionVec().getY(), this.getPositionVec().getZ());
                if(state.get() == null)
                    return;

                if(dis > 256) {
                    setTrans(false);
                    return;
                }

                int manaTrans = 5;
                if(!getMode()) {
                    if(manaCapacity().getMana() < manaCapacity().getMaxMana() && state.get().getManaValue() >= manaTrans)
                        state.get().setManaValue(state.get().getManaValue() - manaTrans + manaCapacity().receiveMana(manaTrans));
                    else
                        setTrans(false);
                } else if(!this.world.isRemote && ticksExisted % 20 == 0){
                    float needed = state.get().getMaxManaValue() - state.get().getManaValue();
                    if(needed > manaTrans) {
                        ManaElementOrbEntity elementOrb = ModEntities.ELEMENT_ORB.get().create(world);
                        elementOrb.setPosition(this.getPosX(), this.getPosY() + this.getHeight() * 0.5, this.getPosZ());
                        elementOrb.spellContext().element(spellContext().element);
                        elementOrb.spellContext().tick(200);
                        elementOrb.spellContext().addChild(TraceContext.create(getOwner()));
                        elementOrb.manaCapacity().setMana(manaCapacity().extractMana(5 * 20));
                        elementOrb.setMotion(getOwner().getPositionVec().subtract(elementOrb.getPositionVec()).normalize());
                        world.addEntity(elementOrb);
                    }
                }

                if(world.isRemote && !getMode()){
                    int distance = Math.max((int) (10 * dis), 1);
                    float directionPoint = (float) (player.ticksExisted % distance) / distance;
                    int c = (int) (directionPoint * distance);

                    Vector3d end = this.getPositionVec().add(0, this.getHeight() / 2, 0);
                    Vector3d start = player.getPositionVec().add(0, player.getHeight() / 2, 0);
                    float scale;
                    for (int i = 0; i < distance; i++) {
                        if(i == c)
                            scale = 0.25f;
                        else
                            scale = 0.10f;
                        double trailFactor = i / (distance - 1.0D);
                        Vector3d pos = ParticleUtil.drawLine(start, end, trailFactor);
                        LitParticle par = new LitParticle(this.world, state.get().getElement().getRenderer().getParticleTexture()
                                , new Vector3d(pos.x, pos.y, pos.z), scale, scale, 1.0f, 5, state.get().getElement().getRenderer());
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
        LitParticle litPar = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + this.getPosX()
                , MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + this.getPosY() + this.getHeight() * 0.5
                , MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + this.getPosZ())
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
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        ActionResultType ret = super.processInitialInteract(player, hand);
        if (ret.isSuccessOrConsume()) return ret;
        if (!player.world.isRemote && hand == Hand.MAIN_HAND) {
            this.setOwner(player);
            if (this.getOwner() == player) {
                if(player.getHeldItemMainhand().getItem() == ModItems.WAND.get())
                    this.switchMode();
                else
                    this.switchTrans();
            }
            return ActionResultType.CONSUME;
        }
        playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, 0.5f, 2.0f);
        return ActionResultType.PASS;
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        manaCapacity().deserialize(compound);
        setMode(compound.getBoolean("MODE"));
        setTrans(compound.getBoolean("TRANS"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
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
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), predicate);
    }

    @Override
    public boolean refraction(SpellContext context) {
        return true;
    }
}
