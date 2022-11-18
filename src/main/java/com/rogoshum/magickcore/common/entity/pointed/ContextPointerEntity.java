package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.api.entity.IManaRefraction;
import com.rogoshum.magickcore.common.api.mana.IManaCapacity;
import com.rogoshum.magickcore.common.api.mana.ISpellContext;
import com.rogoshum.magickcore.client.entity.easyrender.ContextPointerRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.item.WandItem;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.ManaCapacity;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class ContextPointerEntity extends ManaPointEntity implements IManaRefraction {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/context_pointer.png");
    private final List<PosItem> stacks = Collections.synchronizedList(new ArrayList<>());
    private final SpellContext spellContext = SpellContext.create();
    private int coolDown = 40;
    private boolean dead = false;
    public ContextPointerEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.spellContext().tick(-1);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(() -> new ContextPointerRenderer(this));
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        super.readSpawnData(additionalData);
        readAdditional(additionalData.readCompoundTag());
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        super.writeSpawnData(buffer);
        CompoundNBT addition = new CompoundNBT();
        writeAdditional(addition);
        buffer.writeCompoundTag(addition);
    }

    @Override
    public void releaseMagick() {
        if(coolDown > 0) {
            coolDown-=1;
            return;
        }
        List<Entity> items = this.findEntity((entity -> entity instanceof ItemEntity && ((ItemEntity) entity).getItem().getItem() instanceof MagickContextItem));

        items.forEach(entity -> {
            if(entity.isAlive() && entity.ticksExisted > 10) {
                ItemEntity item = (ItemEntity) entity;
                Vector3d relativeVec = relativeVec(item);
                ItemStack stack = item.getItem().copy();
                stack.setCount(1);
                PosItem posItem = new PosItem(relativeVec, stack, getStacks().size());
                posItem.motion = item.getMotion();
                posItem.hoverStart = item.hoverStart;
                posItem.age = item.ticksExisted;
                stacks.add(posItem);
                item.getItem().shrink(1);
            }
        });
    }

    @Override
    public void reSize() {
        float height = stacks.size() + this.getType().getHeight();
        if(this.getHeight() > height)
            this.setHeight(getHeight() - 0.1f);
        if(this.getHeight() < height)
            this.setHeight(getHeight() + 0.1f);
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
            LitParticle par = new LitParticle(this.world, spellContext().element.getRenderer().getParticleTexture()
                    , new Vector3d(this.getPosX() + (0.5 - rand.nextFloat()), this.getPosY(), this.getPosZ() + (0.5 - rand.nextFloat())), 0.03f, 0.03f, rand.nextFloat(), (int)(20f * getHeight()), spellContext().element.getRenderer());
            par.setParticleGravity(0);
            par.setLimitScale();
            par.setGlow();
            par.addMotion(0, 0.02 * getHeight(), 0);
            MagickCore.addMagickParticle(par);
        }
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
    protected void doClientTask() {
        //MagickCore.LOGGER.debug(suppliers);
        super.doClientTask();
        stacks.forEach(PosItem::tick);
    }

    @Override
    protected void doServerTask() {
        super.doServerTask();
        stacks.forEach((PosItem::tick));
    }

    public void spawnSupplierParticle(Entity supplier) {
        Vector3d center = new Vector3d(0, this.getHeight() / 2, 0);
        Vector3d end = this.getPositionVec().add(center);
        Vector3d start = supplier.getPositionVec().add(0, supplier.getHeight() / 2, 0);
        double dis = start.subtract(end).length();
        if(dis < 0.2)
            dis = 0.2;
        int distance = (int) (8 * dis);
        float directionPoint = (float) (supplier.ticksExisted % distance) / distance;
        int c = (int) (directionPoint * distance);

        Vector3d direction = Vector3d.ZERO;
        Vector3d origin = start.subtract(end);
        double y = -origin.y;
        double x = Math.abs(origin.x);
        double z = Math.abs(origin.z);
        if(x > z)
            direction = new Vector3d(x, y, 0);
        else if(z > x)
            direction = new Vector3d(0, y, z);
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
            Vector3d pos = ParticleUtil.drawParabola(start, end, trailFactor, dis / 3, direction);
            LitParticle par = new LitParticle(this.world, element.getRenderer().getParticleTexture()
                    , new Vector3d(pos.x, pos.y, pos.z), scale, scale, alpha, 3, element.getRenderer());
            par.setParticleGravity(0);
            par.setLimitScale();
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleStatusUpdate(byte id) {
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
                super.handleStatusUpdate(id);
        }
    }

    @Override
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        ActionResultType ret = super.processInitialInteract(player, hand);
        if (ret.isSuccessOrConsume()) return ret;
        if (hand == Hand.MAIN_HAND) {
            if(player.getHeldItemMainhand().getItem() instanceof WandItem && getStacks().size() > 1) {
                if(!this.world.isRemote) {
                    ItemStack stack = new ItemStack(ModItems.MAGICK_CORE.get());
                    SpellContext context = ExtraDataUtil.itemManaData(stack).spellContext();
                    context.copy(ExtraDataUtil.itemManaData(getStacks().get(getStacks().size() - 1).getItemStack()).spellContext());
                    for(int i = getStacks().size() - 2; i > -1; --i) {
                        context = ExtraDataUtil.itemManaData(getStacks().get(i).getItemStack()).spellContext().copy().post(context);
                    }
                    ExtraDataUtil.itemManaData(stack).spellContext().copy(context);
                    ItemEntity entity = new ItemEntity(world, this.getPosX(), this.getPosY() + (this.getWidth() / 2), this.getPosZ(), stack);
                    world.addEntity(entity);
                    playSound(SoundEvents.BLOCK_PORTAL_AMBIENT, 0.5f, 2.0f);
                } else
                    spawnParticle();
                getStacks().clear();
            } else
                dropItem();
            return ActionResultType.CONSUME;
        }
        return ActionResultType.PASS;
    }

    public void spawnParticle() {
        float scale = 1f;
        for (int i = 0; i < 20; ++i) {
            LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vector3d(MathHelper.sin(MagickCore.getNegativeToOne() * 0.3f) + getPositionVec().x
                    , getPositionVec().y + 0.2
                    , MathHelper.sin(MagickCore.getNegativeToOne() * 0.3f) + getPositionVec().z)
                    , scale * 0.2f, scale * 2f, 0.5f, Math.max((int) (80 * MagickCore.rand.nextFloat()), 20), ModElements.ORIGIN.getRenderer());
            par.setGlow();
            par.setParticleGravity(-0.1f);
            par.setColor(Color.BLUE_COLOR);
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    protected void registerData() {

    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        if(compound.contains("STACKS")) {
            CompoundNBT tag = compound.getCompound("STACKS");
            tag.keySet().forEach(key -> {
                CompoundNBT stack = tag.getCompound(key);
                PosItem posItem = new PosItem(Vector3d.ZERO, ItemStack.EMPTY, getStacks().size());
                posItem.deserialize(stack);
                if(!posItem.itemStack.isEmpty())
                    this.stacks.add(posItem);
            });
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        CompoundNBT tag = new CompoundNBT();
        for(int i = 0; i < stacks.size(); ++i) {
            PosItem posItem = stacks.get(i);
            CompoundNBT stack = new CompoundNBT();
            posItem.serialize(stack);
            tag.put(String.valueOf(i), stack);
        }
        compound.put("STACKS", tag);
    }

    public void dropItem() {
        if(!world.isRemote) {
            getStacks().forEach((posItem) -> {
                Vector3d pos = posItem.pos.add(this.getPositionVec());
                ItemEntity entity = new ItemEntity(world, pos.x, pos.y, pos.z, posItem.itemStack);
                world.addEntity(entity);
            });
        }
        coolDown = 40;
        playSound(SoundEvents.BLOCK_BEACON_AMBIENT, 0.5f, 2.0f);
        getStacks().clear();
    }

    @Override
    public void remove() {
        dropItem();
        if(!dead) {
            dead = true;
            ItemStack stack = NBTTagHelper.createItemWithEntity(this, ModItems.CONTEXT_POINTER.get(), 1);
            ItemEntity entity = new ItemEntity(world, this.getPosX(), this.getPosY() + 0.5f, this.getPosZ(), stack);
            if (!this.world.isRemote)
                world.addEntity(entity);
        }
        super.remove();
    }

    private Vector3d relativeVec(ItemEntity entity) {
        return new Vector3d(entity.getPosX() - this.getPosX(), entity.getPosY() - this.getPosY(), entity.getPosZ() - this.getPosZ());
    }

    @Override
    public IPacket<?> createSpawnPacket() {
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
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(0.5, getStacks().size() + 0.5, 0.5), predicate);
    }

    @Override
    public boolean refraction(SpellContext context) {
        return true;
    }

    public static class PosItem {
        public Vector3d pos;
        public Vector3d prePos;
        public Vector3d motion = Vector3d.ZERO;
        private ItemStack itemStack;
        private final int sequence;
        public int age;
        public float hoverStart;

        public PosItem(Vector3d pos, ItemStack stack, int sequence) {
            this.pos = pos;
            this.prePos = pos;
            this.itemStack = stack;
            this.sequence = sequence;
        }

        public void tick() {
            this.motion = pos;
            this.motion = this.motion.subtract(0, 0.5 + sequence, 0);

            this.motion = this.motion.scale(0.1);
            this.prePos = pos;
            this.pos = this.pos.subtract(this.motion);
            age++;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public CompoundNBT serialize(CompoundNBT tag) {
            CompoundNBT posItem = new CompoundNBT();
            NBTTagHelper.putVectorDouble(posItem, "POS", pos);
            NBTTagHelper.putVectorDouble(posItem, "MOTION", motion);
            posItem.put("ITEM", itemStack.write(new CompoundNBT()));
            posItem.putInt("AGE", age);
            posItem.putFloat("HOVER", hoverStart);
            tag.put("PosItem", posItem);
            return tag;
        }

        public void deserialize(CompoundNBT tag) {
            if(!tag.contains("PosItem")) return;
            tag = tag.getCompound("PosItem");
            if(NBTTagHelper.hasVectorDouble(tag, "POS")) {
                Vector3d pos = NBTTagHelper.getVectorFromNBT(tag, "POS");
                this.pos = pos;
                this.prePos = pos;
            }
            if(tag.contains("ITEM"))
                this.itemStack = ItemStack.read(tag.getCompound("ITEM"));
            if(tag.contains("AGE"))
                this.age = tag.getInt("AGE");
            if(tag.contains("HOVER"))
                this.hoverStart = tag.getFloat("HOVER");
        }
    }
}
