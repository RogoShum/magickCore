package com.rogoshum.magickcore.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.itemstack.IManaData;
import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.client.entity.easyrender.ManaCapacityRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.ManaSphereRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.entity.projectile.ManaElementOrbEntity;
import com.rogoshum.magickcore.init.ModEntities;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.item.ManaItem;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibEntityData;
import com.rogoshum.magickcore.magick.ManaCapacity;
import com.rogoshum.magickcore.magick.context.SpellContext;
import com.rogoshum.magickcore.magick.context.child.TraceContext;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.registry.MagickRegistry;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import com.rogoshum.magickcore.tool.ParticleHelper;
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
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class ManaCapacityEntity extends ManaPointEntity implements IManaCapacity, IManaRefraction {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mana_capacity.png");
    private static final DataParameter<Boolean> TRANS = EntityDataManager.createKey(ManaCapacityEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> MODE = EntityDataManager.createKey(ManaCapacityEntity.class, DataSerializers.BOOLEAN);
    private final ManaCapacity manaCapacity = ManaCapacity.create(5000);

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
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(new ManaCapacityRenderer(this));
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
        ItemStack stack = NBTTagHelper.createItemWithEntity(this, ModItems.magick_container.get(), 1);
        ItemEntity entity = new ItemEntity(world, this.getPosX(), this.getPosY() + 0.5f, this.getPosZ(), stack);
        if(!this.world.isRemote)
            world.addEntity(entity);
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
        this.manaCapacity().receiveMana(50f);
        List<Entity> list = this.findEntity((entity) -> entity instanceof ItemEntity && ((ItemEntity) entity).getItem().getItem() instanceof IManaData);
        for (Entity entity : list) {
            ItemEntity item = (ItemEntity)entity;
            float manaTrans = this.manaCapacity().extractMana(5);
            float lost = ExtraDataHelper.itemManaData(item.getItem()).manaCapacity().receiveMana(manaTrans);
            this.manaCapacity().receiveMana(lost);
        }
        if(this.getOwner() instanceof LivingEntity) {
            LivingEntity player = (LivingEntity) this.getOwner();
            AtomicReference<EntityStateData> state = new AtomicReference<>();
            ExtraDataHelper.entityData(player).<EntityStateData>execute(LibEntityData.ENTITY_STATE, (data) -> {
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
                if(!this.world.isRemote) {
                    int manaTrans = 5;
                    if(!getMode()) {
                        if(manaCapacity().getMana() < manaCapacity().getMaxMana() && state.get().getManaValue() >= manaTrans)
                            state.get().setManaValue(state.get().getManaValue() - manaTrans + manaCapacity().receiveMana(manaTrans));
                        else
                            setTrans(false);
                    } else if(ticksExisted % 20 == 0){
                        float needed = state.get().getMaxManaValue() - state.get().getManaValue();
                        if(needed > manaTrans) {
                            ManaElementOrbEntity elementOrb = ModEntities.element_orb.get().create(world);
                            elementOrb.setPosition(this.getPosX(), this.getPosY() + this.getHeight() * 0.5, this.getPosZ());
                            elementOrb.spellContext().element(spellContext().element);
                            elementOrb.spellContext().tick(200);
                            elementOrb.spellContext().addChild(TraceContext.create(getOwner()));
                            elementOrb.manaCapacity().setMana(manaCapacity().extractMana(5 * 20));
                            elementOrb.setMotion(getOwner().getPositionVec().subtract(elementOrb.getPositionVec()).normalize().scale(0.1));
                            world.addEntity(elementOrb);
                        }
                    }
                } else if(!getMode()){
                    int distance = (int) (10 * dis);
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
                        Vector3d pos = ParticleHelper.drawLine(start, end, trailFactor);
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
    protected void applyParticle() {

    }

    @Override
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        ActionResultType ret = super.processInitialInteract(player, hand);
        if (ret.isSuccessOrConsume()) return ret;
        if (!player.world.isRemote && hand == Hand.MAIN_HAND) {
            this.setOwner(player);
            if (this.getOwner() == player) {
                if(player.getHeldItemMainhand().getItem() == ModItems.wand.get())
                    this.switchMode();
                else
                    this.switchTrans();
            }
            return ActionResultType.CONSUME;
        }
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
