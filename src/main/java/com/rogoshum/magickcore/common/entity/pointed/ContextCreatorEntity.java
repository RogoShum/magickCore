package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.IMaterialLimit;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.client.entity.easyrender.ContextCreatorRenderer;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.vertex.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.init.ManaMaterials;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.item.material.ManaMaterialItem;
import com.rogoshum.magickcore.common.item.tool.WandItem;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.lib.LibMaterial;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.api.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.magick.materials.Material;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.network.EntityCompoundTagPack;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.network.NetworkHooks;

public class ContextCreatorEntity extends ManaPointEntity implements IManaRefraction {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/items/context_core.png");
    private final InnerManaData innerManaData = new InnerManaData(ManaMaterials.getMaterial(LibMaterial.ORIGIN));
    private int itemCount = 0;
    private final List<PosItem> stacks = Collections.synchronizedList(new ArrayList<>());
    private final SpellContext spellContext = SpellContext.create();
    private int coolDown = 40;
    private boolean dead = false;
    private EntityType<?> entityType = null;

    public ContextCreatorEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.spellContext().tick(-1);
    }

    public void setEntityType(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new ContextCreatorRenderer(this);
    }

    public void setMaterial(Material material) {
        innerManaData.setMaterial(material);
    }

    public Material getMaterial() {
        return innerManaData.getMaterial();
    }

    public InnerManaData getInnerManaData() {
        return innerManaData;
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        super.readSpawnData(additionalData);
        readAdditionalSaveData(additionalData.readNbt());
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        super.writeSpawnData(buffer);
        CompoundTag addition = new CompoundTag();
        addAdditionalSaveData(addition);
        buffer.writeNbt(addition);
    }

    @Override
    public boolean releaseMagick() {
        if(coolDown > 0) {
            coolDown-=1;
            return false;
        }
        List<ItemEntity> items = this.level.getEntities(EntityType.ITEM, this.getBoundingBox().inflate(1), (entity) -> entity.getItem().getItem() instanceof IManaMaterial);
        //items = new ArrayList<>();
        items.forEach(item -> {
            if(item.isAlive() && !item.level.isClientSide() && item.tickCount > 15) {
                IManaMaterial material = ((IManaMaterial)item.getItem().getItem());
                boolean creatorAndNotType = !material.typeMaterial() || getEntityType() == null;
                boolean singleItem = material.singleMaterial();
                boolean anotherType = getInnerManaData().spellContext().applyType() != ApplyType.NONE && material.typeMaterial();
                boolean anotherElement = getInnerManaData().spellContext().element() != ModElements.ORIGIN && material.elementMaterial();
                if(singleItem) {
                    singleItem = false;
                    for (PosItem posItem : stacks) {
                        if(posItem.itemStack.getItem() == material)
                            singleItem = true;
                    }
                }

                if(creatorAndNotType && !singleItem && !anotherType && !anotherElement) {
                    if(material.upgradeManaItem(item.getItem(), innerManaData)) {
                        Vec3 relativeVec = relativeVec(item);
                        ItemStack stack = item.getItem().copy();
                        stack.setCount(1);
                        PosItem posItem = new PosItem(relativeVec, stack, new Random(itemCount++));
                        posItem.motion = item.getDeltaMovement();
                        posItem.hoverStart = item.bobOffs;
                        posItem.age = item.tickCount;
                        if(!material.disappearAfterRead())
                            stacks.add(posItem);
                        Vec3 vec = new Vec3(item.getX() - this.getX(), (item.getY() + item.getBbHeight() / 2) - (this.getY() + this.getBbHeight() / 2), item.getZ() - this.getZ());
                        hitReactions.put(item.getId(), new VectorHitReaction(vec.normalize(), 0.2f, 0.02f));
                        item.getItem().shrink(1);
                        playSound(ModSounds.cruise_ship.get(), 0.5f, 0.5f*random.nextFloat());
                        doNetworkUpdate();
                    }
                }
            }
        });

        return false;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return null;
    }

    @Override
    protected void applyParticle() {

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
    protected void doClientTask() {
        //MagickCore.LOGGER.debug(suppliers);
        super.doClientTask();
        stacks.forEach(PosItem::tick);
        if(this.getEntityType() != null) {
            for (PosItem posItem : stacks) {
                LitParticle par = new LitParticle(this.level, innerManaData.spellContext().element().getRenderer().getParticleTexture()
                        , this.position().add(0, this.getBbHeight() / 2 + 0.2, 0).add(posItem.pos), 0.1f, 0.1f, 0.5f, 10, innerManaData.spellContext().element().getRenderer());
                par.setGlow();
                MagickCore.addMagickParticle(par);
            }
        }
        AtomicBoolean hasEnergy = new AtomicBoolean(false);

        if(hasEnergy.get()) {
            LitParticle par = new LitParticle(this.level, innerManaData.spellContext().element().getRenderer().getParticleTexture()
                    , this.position().add(0, this.getBbHeight() / 2, 0), 0.8f, 0.8f, 1.0f, 5, innerManaData.spellContext().element().getRenderer());
            par.setParticleGravity(0);
            par.setLimitScale();
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    protected void doServerTask() {
        super.doServerTask();
        stacks.forEach((PosItem::tick));
    }

    public void spawnSupplierParticle(Entity supplier) {
        Vec3 center = new Vec3(0, this.getBbHeight() / 2, 0);
        Vec3 end = this.position().add(center);
        Vec3 start = supplier.position().add(0, supplier.getBbHeight() / 2, 0);
        double dis = start.subtract(end).length();
        if(dis < 0.2)
            dis = 0.2;
        int distance = (int) (8 * dis);
        float directionPoint = (float) (supplier.tickCount % distance) / distance;
        int c = (int) (directionPoint * distance);

        Vec3 direction = Vec3.ZERO;
        Vec3 origin = start.subtract(end);
        double y = -origin.y;
        double x = Math.abs(origin.x);
        double z = Math.abs(origin.z);
        if(x > z)
            direction = new Vec3(x, y, 0);
        else if(z > x)
            direction = new Vec3(0, y, z);
        float scale;
        float alpha = 0.25f;

        MagickElement element = innerManaData.spellContext().element();
        if(supplier instanceof ISpellContext) {
            element = ((ISpellContext) supplier).spellContext().element();
        }
        for (int i = 0; i < distance; ++i) {
            if(i == c)
                scale = 0.3f;
            else
                scale = 0.15f;

            double trailFactor = i / (distance - 1.0D);
            Vec3 pos = ParticleUtil.drawParabola(start, end, trailFactor, dis / 3, direction);
            LitParticle par = new LitParticle(this.level, element.getRenderer().getParticleTexture()
                    , new Vec3(pos.x, pos.y, pos.z), scale, scale, alpha, 3, element.getRenderer());
            par.setParticleGravity(0);
            par.setLimitScale();
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte id) {
        //super.handleStatusUpdate(id);
        switch(id) {
            case 10:
                if(!stacks.isEmpty())
                    stacks.remove(0);
                break;
            case 11:
                stacks.clear();
                innerManaData.spellContext().clear();
                break;
            default:
                super.handleEntityEvent(id);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        InteractionResult ret = super.interact(player, hand);
        if (ret.consumesAction()) return ret;

        if (hand == InteractionHand.MAIN_HAND) {
            if(player.getMainHandItem().getItem() instanceof WandItem) {
                ItemStack stack = new ItemStack(ModItems.MAGICK_CORE.get());
                ExtraDataUtil.itemManaData(stack).spellContext().copy(innerManaData.spellContext());
                for(PosItem posItem : getStacks()) {
                    if(posItem.itemStack.getItem() instanceof ManaMaterialItem) {
                        ItemEntity entity = new ItemEntity(level, this.getX(), this.getY() + (this.getBbWidth() / 2), this.getZ(), posItem.itemStack);
                        level.addFreshEntity(entity);
                    }
                }
                getStacks().clear();
                ItemEntity entity = new ItemEntity(level, this.getX(), this.getY() + (this.getBbWidth() / 2), this.getZ(), stack);
                level.addFreshEntity(entity);
                setRemoved(RemovalReason.DISCARDED);
                playSound(SoundEvents.BEACON_DEACTIVATE, 0.5f, 2.0f);

                if(player instanceof ServerPlayer && entityType != null) {
                    AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) player, LibAdvancements.CONTEXT_POINTER);
                }

                if(level.isClientSide)
                    spawnParticle();
            } else
                dropItem();
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void defineSynchedData() {

    }

    public void spawnParticle() {
        float radius = getBbWidth() * 0.25f;
        float rho, drho, theta, dtheta;
        float x, y, z;
        int stacks = 12;
        drho = (float) (2.0f * Math.PI / stacks);
        dtheta = (float) (2.0f * Math.PI / stacks);
        for (int i = 0; i < stacks; i++) {
            rho = i * drho;
            for (int j = 0; j < stacks; j++) {
                theta = j * dtheta;
                x = (float) (-Math.sin(theta) * Math.sin(rho));
                y = (float) (Math.cos(theta) * Math.sin(rho));
                z = (float) Math.cos(rho);
                Vec3 pos = new Vec3(x * radius, y * radius, z * radius);
                LitParticle par = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element().type()).getParticleTexture()
                        , pos.add(this.position().add(0, getBbHeight() * 0.5, 0))
                        , 0.1f, 0.1f, 1.0f, 10, MagickCore.proxy.getElementRender(spellContext().element().type()));
                par.setGlow();
                par.setParticleGravity(0);
                par.setLimitScale();
                par.addMotion(x * 0.2, y * 0.2, z * 0.2);
                MagickCore.addMagickParticle(par);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.innerManaData.spellContext.clear();
        this.innerManaData.spellContext.deserialize(compound);
        if(compound.contains("MATERIAL"))
            this.innerManaData.setMaterial(ManaMaterials.getMaterial(compound.getString("MATERIAL")));
        if(compound.contains("STACKS")) {
            CompoundTag tag = compound.getCompound("STACKS");
            this.stacks.clear();
            itemCount = 0;
            tag.getAllKeys().forEach(key -> {
                CompoundTag stack = tag.getCompound(key);
                PosItem posItem = new PosItem(Vec3.ZERO, ItemStack.EMPTY, new Random(itemCount++));
                posItem.deserialize(stack);
                if(!posItem.itemStack.isEmpty())
                    this.stacks.add(posItem);
            });
        }
        Optional<EntityType<?>> entityType = EntityType.byString(compound.getString("spell_entity_type"));
        entityType.ifPresent(this::setEntityType);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        this.innerManaData.spellContext.serialize(compound);
        compound.putString("MATERIAL", this.innerManaData.material.getName());
        CompoundTag tag = new CompoundTag();
        for(int i = 0; i < stacks.size(); ++i) {
            PosItem posItem = stacks.get(i);
            CompoundTag stack = new CompoundTag();
            posItem.serialize(stack);
            tag.put(String.valueOf(i), stack);
        }
        compound.put("STACKS", tag);
        if(getEntityType() != null)
            compound.putString("spell_entity_type", EntityType.getKey(getEntityType()).toString());
    }

    public void dropItem() {
        if(!level.isClientSide) {
            getStacks().forEach((posItem) -> {
                Vec3 pos = posItem.pos.add(this.position().add(0, this.getBbHeight() / 2, 0));
                ItemEntity entity = new ItemEntity(level, pos.x, pos.y, pos.z, posItem.itemStack);
                level.addFreshEntity(entity);
            });
        }
        playSound(SoundEvents.BEACON_AMBIENT, 0.5f, 2.0f);
        getInnerManaData().spellContext().clear();
        coolDown = 40;
        getStacks().clear();
        if(getEntityType() != null) {
            getInnerManaData().spellContext().applyType(ApplyType.SPAWN_ENTITY);
            getInnerManaData().spellContext().addChild(SpawnContext.create(getEntityType()));
        }
        EntityCompoundTagPack.updateEntity(this);
    }

    @Override
    public void remove(RemovalReason reason) {
        dropItem();
        if(!dead) {
            dead = true;
            if(getEntityType() == null) {
                ItemStack stack = NBTTagHelper.createItemWithEntity(this, ModItems.CONTEXT_CORE.get(), 1);
                stack.getTag().putString("mana_material", this.innerManaData.material.getName());
                ItemEntity entity = new ItemEntity(level, this.getX(), this.getY() + 0.5f, this.getZ(), stack);
                if(!this.level.isClientSide)
                    level.addFreshEntity(entity);
            } else {
                ItemStack stack = new ItemStack(ModItems.ENTITY_TYPE.get());
                ItemManaData data = ExtraDataUtil.itemManaData(stack);
                data.spellContext().addChild(SpawnContext.create(entityType));
                data.spellContext().applyType(ApplyType.SPAWN_ENTITY);
                ItemEntity entity = new ItemEntity(level, this.getX(), this.getY() + 0.5f, this.getZ(), stack);
                if(!this.level.isClientSide)
                    level.addFreshEntity(entity);
            }
        }
        super.remove(reason);
    }

    private Vec3 relativeVec(ItemEntity entity) {
        return new Vec3(entity.getX() - this.getX(), (entity.getY()) - (this.getY() + this.getBbHeight() / 2), entity.getZ() - this.getZ());
    }

    @Override
    public boolean refraction(SpellContext context) {
        return true;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public List<PosItem> getStacks() {
        return stacks;
    }

    @Override
    public SpellContext spellContext() {
        return spellContext;
    }

    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.level.getEntities(this, this.getBoundingBox(), (entity -> this.position().distanceToSqr(entity.position()) <= 2.25 && (predicate == null || predicate.test(entity))));
    }

    public static class InnerManaData implements ISpellContext, IMaterialLimit {
        private final SpellContext spellContext = SpellContext.create();
        private Material material;

        public InnerManaData(Material material) {
            this.material = material;
        }

        @Override
        public Material getMaterial() {
            return material;
        }

        @Override
        public void setMaterial(Material material) {
            this.material = material;
        }

        @Override
        public SpellContext spellContext() {
            return spellContext;
        }
    }

    public static class PosItem {
        public Vec3 pos;
        public Vec3 prePos;
        public Vec3 motion = Vec3.ZERO;
        private ItemStack itemStack;
        private final Random rand;
        public int age;
        public float hoverStart;

        public PosItem(Vec3 pos, ItemStack stack, Random rand) {
            this.pos = pos;
            this.prePos = pos;
            this.itemStack = stack;
            this.rand = rand;
        }

        public void tick() {
            this.motion = this.motion.scale(0.995);

            if(this.motion.lengthSqr() < 0.0025) {
                double motionX = 0.05 - rand.nextDouble() * 0.1;
                double motionY = 0.05 - rand.nextDouble() * 0.1;
                double motionZ = 0.05 - rand.nextDouble() * 0.1;
                this.motion = new Vec3(motionX, motionY, motionZ);
            }
            this.prePos = pos;
            this.pos = this.pos.add(this.motion);
            if(this.pos.lengthSqr() > 1.265)
                this.pos = this.pos.normalize().scale(1.125);
            if(this.pos.lengthSqr() < 0.63)
                this.pos = this.pos.normalize().scale(0.4);
            age++;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public CompoundTag serialize(CompoundTag tag) {
            CompoundTag posItem = new CompoundTag();
            NBTTagHelper.putVectorDouble(posItem, "POS", pos);
            NBTTagHelper.putVectorDouble(posItem, "MOTION", motion);
            posItem.put("ITEM", itemStack.save(new CompoundTag()));
            posItem.putInt("AGE", age);
            posItem.putFloat("HOVER", hoverStart);
            tag.put("PosItem", posItem);
            return tag;
        }

        public void deserialize(CompoundTag tag) {
            if(!tag.contains("PosItem")) return;
            tag = tag.getCompound("PosItem");
            if(NBTTagHelper.hasVectorDouble(tag, "POS")) {
                Vec3 pos = NBTTagHelper.getVectorFromNBT(tag, "POS");
                this.pos = pos;
                this.prePos = pos;
            }
            if(tag.contains("ITEM"))
                this.itemStack = ItemStack.of(tag.getCompound("ITEM"));
            if(tag.contains("AGE"))
                this.age = tag.getInt("AGE");
            if(tag.contains("HOVER"))
                this.hoverStart = tag.getFloat("HOVER");
        }
    }
}
