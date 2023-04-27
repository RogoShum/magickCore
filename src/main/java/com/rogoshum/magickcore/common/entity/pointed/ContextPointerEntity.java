package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.client.entity.easyrender.ContextPointerRenderer;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.ManaItemEntity;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.item.placeable.EntityItem;
import com.rogoshum.magickcore.common.item.tool.WandItem;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.EntityInteractHelper;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.*;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.network.NetworkHooks;

public class ContextPointerEntity extends ManaPointEntity implements IManaRefraction {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/context_pointer.png");
    private final List<PosItem> stacks = Collections.synchronizedList(new ArrayList<>());
    private final SpellContext spellContext = SpellContext.create();
    private int coolDown = 40;
    private boolean dead = false;
    public ContextPointerEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.spellContext().tick(-1);
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new ContextPointerRenderer(this);
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
        if (this.tickCount == 1) {
            this.playSound(SoundEvents.STONE_PLACE, 1F, 1.0F + MagickCore.rand.nextFloat());
        }
        if(coolDown > 0) {
            coolDown-=1;
            return false;
        }
        List<Entity> items = this.findEntity((entity -> (entity instanceof ItemEntity && ((ItemEntity) entity).getItem().getItem() instanceof MagickContextItem)));

        items.forEach(entity -> {
            if(entity.isAlive() && !entity.level.isClientSide() && entity.tickCount > 25) {
                ItemEntity item = (ItemEntity) entity;
                Vec3 relativeVec = relativeVec(item);
                ItemStack stack = item.getItem().copy();
                stack.setCount(1);
                boolean isFunc = isFuncType(stack);
                PosItem posItem = new PosItem(relativeVec, stack, getLastItemSequence(isFunc), isFuncType(stack), relativeVec.normalize());
                posItem.motion = item.getDeltaMovement();
                posItem.hoverStart = item.bobOffs;
                posItem.age = item.tickCount;
                stacks.add(posItem);
                item.getItem().shrink(1);
                doNetworkUpdate();
            }
        });

        return false;
    }

    public int getLastItemSequence(boolean isFuncType) {
        if(getStacks().size() < 1) return 0;
        PosItem posItem = getStacks().get(getStacks().size() - 1);
        int sequence = posItem.sequence;
        if(!isFuncType)
            sequence++;
        return sequence;
    }

    public boolean isFuncType(ItemStack stack) {
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        return !data.spellContext().applyType.isForm();
    }

    @Override
    public void reSize() {
        float height = getLastItemSequence(false)*0.5f + this.getType().getHeight();
        if(this.getBbHeight() > height)
            this.setHeight(getBbHeight() - 0.1f);
        if(this.getBbHeight() < height)
            this.setHeight(getBbHeight() + 0.1f);
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
        for (int i = 0; i < 1; ++i) {
            LitParticle par = new LitParticle(this.level, spellContext().element.getRenderer().getParticleTexture()
                    , new Vec3(this.getX() + (0.5 - random.nextFloat()), this.getY(), this.getZ() + (0.5 - random.nextFloat())), 0.03f, 0.03f, random.nextFloat(), (int)(20f * getBbHeight()), spellContext().element.getRenderer());
            par.setParticleGravity(0);
            par.setLimitScale();
            par.setGlow();
            par.addMotion(0, 0.02 * getBbHeight(), 0);
            MagickCore.addMagickParticle(par);
        }
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
        for(PosItem posItem : stacks) {
            posItem.tick(this.tickCount);
            if(tickCount % 5 == 0 && posItem.function)
                posItem.spawnParticle(level, spellContext().element, this.position());
        }
    }

    @Override
    protected void doServerTask() {
        super.doServerTask();
        for(PosItem posItem : stacks) {
            posItem.tick(this.tickCount);
        }
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
        float alpha = 0.5f;


        MagickElement element = spellContext().element;
        if(supplier instanceof ISpellContext) {
            element = ((ISpellContext) supplier).spellContext().element;
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
            ItemStack heldItem = player.getItemInHand(hand);
            if(heldItem.getItem() instanceof BlockItem || heldItem.getItem() instanceof EntityItem) {
                return EntityInteractHelper.placeBlock(player, hand, heldItem, this);
            }
            if(player.getMainHandItem().getItem() instanceof WandItem) {
                boolean notClear = false;
                if(!this.level.isClientSide) {
                    if(getStacks().size() > 1) {
                        ItemStack stack = new ItemStack(ModItems.MAGICK_CORE.get());
                        SpellContext context = ExtraDataUtil.itemManaData(stack).spellContext();
                        context.copy(ExtraDataUtil.itemManaData(getStacks().get(getStacks().size() - 1).getItemStack()).spellContext());
                        for(int i = getStacks().size() - 2; i > -1; --i) {
                            SpellContext origin = ExtraDataUtil.itemManaData(getStacks().get(i).getItemStack()).spellContext().copy();
                            SpellContext post = origin;
                            while (post.postContext != null)
                                post = post.postContext;
                            post.post(context);
                            context = origin;
                        }
                        ExtraDataUtil.itemManaData(stack).spellContext().copy(context);
                        ItemEntity entity = new ManaItemEntity(level, this.getX(), this.getY() + (this.getBbWidth() / 2), this.getZ(), stack);
                        level.addFreshEntity(entity);
                        playSound(SoundEvents.PORTAL_AMBIENT, 0.5f, 2.0f);
                    } else if(getStacks().size() == 1) {
                        PosItem item = getStacks().get(0);
                        ItemManaData data = ExtraDataUtil.itemManaData(item.itemStack);
                        if(data.spellContext().postContext != null) {
                            List<SpellContext> spellContexts = new ArrayList<>();
                            SpellContext context = data.spellContext();
                            while (context.postContext != null) {
                                SpellContext spellContext1 = context.postContext;
                                context.postContext = null;
                                spellContexts.add(spellContext1);
                                context = spellContext1;
                            }

                            for (SpellContext post : spellContexts) {
                                ItemStack newCore = new ItemStack(ModItems.MAGICK_CORE.get());
                                ItemManaData coreData = ExtraDataUtil.itemManaData(newCore);
                                post.postContext = null;
                                coreData.spellContext().copy(post);
                                CompoundTag tag = item.serialize(new CompoundTag());
                                PosItem posItem = new PosItem(Vec3.ZERO, ItemStack.EMPTY, getStacks().size(), true, Vec3.ZERO);
                                CompoundTag posTag = tag.getCompound("PosItem");
                                posTag.put("ITEM", newCore.save(new CompoundTag()));
                                posItem.deserialize(tag);
                                boolean isFunc = isFuncType(newCore);
                                int s = getLastItemSequence(isFunc);
                                Vec3 randomVec = new Vec3(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne());
                                posItem.function = isFunc;
                                posItem.sequence = s;
                                posItem.offset = randomVec;
                                stacks.add(posItem);
                            }

                            data.spellContext().postContext = null;
                            data.spellContext().copy(data.spellContext());
                            playSound(SoundEvents.PORTAL_AMBIENT, 0.5f, 2.0f);
                            notClear = true;
                        }
                    }
                } else
                    spawnParticle();
                if(!notClear)
                    getStacks().clear();
                else
                    doNetworkUpdate();
            } else
                dropItem();
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    public void spawnParticle() {
        float scale = 1f;
        for (int i = 0; i < 20; ++i) {
            LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vec3(Mth.sin(MagickCore.getNegativeToOne() * 0.3f) + position().x
                    , position().y + 0.2
                    , Mth.sin(MagickCore.getNegativeToOne() * 0.3f) + position().z)
                    , scale * 0.2f, scale * 2f, 0.5f, Math.max((int) (80 * MagickCore.rand.nextFloat()), 20), ModElements.ORIGIN.getRenderer());
            par.setGlow();
            par.setParticleGravity(-0.1f);
            par.setColor(Color.BLUE_COLOR);
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if(compound.contains("STACKS")) {
            CompoundTag tag = compound.getCompound("STACKS");
            this.stacks.clear();
            tag.getAllKeys().forEach(key -> {
                CompoundTag stack = tag.getCompound(key);
                PosItem posItem = new PosItem(Vec3.ZERO, ItemStack.EMPTY, getStacks().size(), false, Vec3.ZERO);
                posItem.deserialize(stack);
                if(!posItem.itemStack.isEmpty())
                    this.stacks.add(posItem);
            });
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        CompoundTag tag = new CompoundTag();
        for(int i = 0; i < stacks.size(); ++i) {
            PosItem posItem = stacks.get(i);
            CompoundTag stack = new CompoundTag();
            posItem.serialize(stack);
            tag.put(String.valueOf(i), stack);
        }
        compound.put("STACKS", tag);
    }

    public void dropItem() {
        if(!level.isClientSide) {
            getStacks().forEach((posItem) -> {
                Vec3 pos = posItem.pos.add(this.position());
                ItemEntity entity = new ItemEntity(level, pos.x, pos.y, pos.z, posItem.itemStack);
                level.addFreshEntity(entity);
            });
        }
        coolDown = 40;
        playSound(SoundEvents.BEACON_AMBIENT, 0.5f, 2.0f);
        getStacks().clear();
    }

    @Override
    public void remove(RemovalReason reason) {
        dropItem();
        if(!dead) {
            dead = true;
            ItemStack stack = NBTTagHelper.createItemWithEntity(this, ModItems.CONTEXT_POINTER.get(), 1);
            ItemEntity entity = new ItemEntity(level, this.getX(), this.getY() + 0.5f, this.getZ(), stack);
            if (!this.level.isClientSide)
                level.addFreshEntity(entity);
        }
        super.remove(reason);
    }

    private Vec3 relativeVec(ItemEntity entity) {
        return new Vec3(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ());
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
        return this.level.getEntities(this, this.getBoundingBox().inflate(0.5, getStacks().size() + 0.5, 0.5), predicate);
    }

    @Override
    public boolean refraction(SpellContext context) {
        return true;
    }

    public static class PosItem {
        public Vec3 pos;
        public Vec3 prePos;
        public Vec3 motion = Vec3.ZERO;
        private ItemStack itemStack;
        private int sequence;
        public int age;
        public float hoverStart;
        private Vec3 offset = Vec3.ZERO;
        private Vec2 rotation = null;
        public boolean function;

        public PosItem(Vec3 pos, ItemStack stack, int sequence, boolean function, Vec3 offset) {
            this.pos = pos;
            this.prePos = pos;
            this.itemStack = stack;
            this.sequence = sequence;
            this.function = function;
            this.offset = offset == Vec3.ZERO ? new Vec3(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne()) : offset;
        }

        public void tick(int tick) {
            this.motion = pos;
            if(function) {
                if(rotation == null) {
                    rotation = EasyRenderer.getRotationFromVector(offset);
                }
                Vec3 rota = Vec3.directionFromRotation(rotation.x + tick, rotation.y);
                this.motion = this.motion.subtract(rota.x * 0.5, 0, rota.y * 0.5);
            }

            this.motion = this.motion.subtract(0, 0.5 + sequence*0.5, 0);
            this.motion = this.motion.scale(0.1);

            this.prePos = pos;
            this.pos = this.pos.subtract(this.motion);
            age++;
        }

        public void spawnParticle(Level level, MagickElement element, Vec3 origin) {
            Vec3 pos = origin.add(this.pos);
            LitParticle par = new LitParticle(level, element.getRenderer().getParticleTexture()
                    , new Vec3(pos.x, pos.y+0.2, pos.z), 0.05f, 0.05f, 0.5f, 30, element.getRenderer());
            par.setParticleGravity(0);
            par.setLimitScale();
            par.setGlow();
            MagickCore.addMagickParticle(par);
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
            posItem.putBoolean("FUNCTION", function);
            posItem.putInt("SEQUENCE", sequence);
            NBTTagHelper.putVectorDouble(posItem, "OFFSET", offset);
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
            if(tag.contains("FUNCTION"))
                this.function = tag.getBoolean("FUNCTION");
            if(tag.contains("OFFSET"))
                this.offset = NBTTagHelper.getVectorFromNBT(tag, "OFFSET");
            if(tag.contains("SEQUENCE"))
                this.sequence = tag.getInt("SEQUENCE");
        }
    }
}
