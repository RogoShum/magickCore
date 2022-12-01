package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.IMaterialLimit;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.client.entity.easyrender.ContextCreatorRenderer;
import com.rogoshum.magickcore.client.vertex.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.init.ManaMaterials;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.WandItem;
import com.rogoshum.magickcore.common.lib.LibMaterial;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.materials.Material;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class ContextCreatorEntity extends ManaPointEntity implements IManaRefraction {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/items/context_core.png");
    private final InnerManaData innerManaData = new InnerManaData(ManaMaterials.getMaterial(LibMaterial.ORIGIN));
    private int itemCount = 0;
    private final List<PosItem> stacks = Collections.synchronizedList(new ArrayList<>());
    private final SpellContext spellContext = SpellContext.create();
    private int coolDown = 40;
    private boolean dead = false;

    public ContextCreatorEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.spellContext().tick(-1);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(() -> new ContextCreatorRenderer(this));
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
        List<ItemEntity> items = this.world.getEntitiesWithinAABB(EntityType.ITEM, this.getBoundingBox(), (entity) -> entity.getItem().getItem() instanceof IManaMaterial);
        //items = new ArrayList<>();
        items.forEach(item -> {
            if(item.isAlive() && item.ticksExisted > 10 && item.getPositionVec().squareDistanceTo(this.getPositionVec()) <= 8) {
                IManaMaterial material = ((IManaMaterial)item.getItem().getItem());
                if(material.upgradeManaItem(item.getItem(), innerManaData)) {
                    Vector3d relativeVec = relativeVec(item);
                    ItemStack stack = item.getItem().copy();
                    stack.setCount(1);
                    PosItem posItem = new PosItem(relativeVec, stack, new Random(itemCount++));
                    posItem.motion = item.getMotion();
                    posItem.hoverStart = item.hoverStart;
                    posItem.age = item.ticksExisted;
                    if(!material.disappearAfterRead())
                        stacks.add(posItem);
                    Vector3d vec = new Vector3d(item.getPosX() - this.getPosX(), (item.getPosY() + item.getHeight() / 2) - (this.getPosY() + this.getHeight() / 2), item.getPosZ() - this.getPosZ());
                    hitReactions.put(item.getEntityId(), new VectorHitReaction(vec.normalize(), 0.2f, 0.02f));
                    item.getItem().shrink(1);
                }
            }
        });
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
        AtomicBoolean hasEnergy = new AtomicBoolean(false);

        if(hasEnergy.get()) {
            LitParticle par = new LitParticle(this.world, innerManaData.spellContext().element.getRenderer().getParticleTexture()
                    , this.getPositionVec().add(0, this.getHeight() / 2, 0), 0.8f, 0.8f, 1.0f, 5, innerManaData.spellContext().element.getRenderer());
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
        float alpha = 0.25f;

        MagickElement element = innerManaData.spellContext().element;
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
                innerManaData.spellContext().clear();
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
            if(player.getHeldItemMainhand().getItem() instanceof WandItem) {
                ItemStack stack = new ItemStack(ModItems.MAGICK_CORE.get());
                ExtraDataUtil.itemManaData(stack).spellContext().copy(innerManaData.spellContext());
                getStacks().clear();
                ItemEntity entity = new ItemEntity(world, this.getPosX(), this.getPosY() + (this.getWidth() / 2), this.getPosZ(), stack);
                world.addEntity(entity);
                remove(false);
                playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 0.5f, 2.0f);
                if(world.isRemote)
                    spawnParticle();
            } else
                dropItem();
            return ActionResultType.CONSUME;
        }
        return ActionResultType.PASS;
    }

    @Override
    protected void registerData() {

    }

    public void spawnParticle() {
        float radius = getWidth() * 0.25f;
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
                Vector3d pos = new Vector3d(x * radius, y * radius, z * radius);
                LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                        , pos.add(this.getPositionVec().add(0, getHeight() * 0.5, 0))
                        , 0.1f, 0.1f, 1.0f, 10, MagickCore.proxy.getElementRender(spellContext().element.type()));
                par.setGlow();
                par.setParticleGravity(0);
                par.setLimitScale();
                par.addMotion(x * 0.2, y * 0.2, z * 0.2);
                MagickCore.addMagickParticle(par);
            }
        }
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        this.innerManaData.spellContext.deserialize(compound);
        if(compound.contains("MATERIAL"))
            this.innerManaData.setMaterial(ManaMaterials.getMaterial(compound.getString("MATERIAL")));
        if(compound.contains("STACKS")) {
            CompoundNBT tag = compound.getCompound("STACKS");
            tag.keySet().forEach(key -> {
                CompoundNBT stack = tag.getCompound(key);
                PosItem posItem = new PosItem(Vector3d.ZERO, ItemStack.EMPTY, new Random(itemCount++));
                posItem.deserialize(stack);
                if(!posItem.itemStack.isEmpty())
                    this.stacks.add(posItem);
            });
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        this.innerManaData.spellContext.serialize(compound);
        compound.putString("MATERIAL", this.innerManaData.material.getName());
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
                Vector3d pos = posItem.pos.add(this.getPositionVec().add(0, this.getHeight() / 2, 0));
                ItemEntity entity = new ItemEntity(world, pos.x, pos.y, pos.z, posItem.itemStack);
                world.addEntity(entity);
            });
        }
        playSound(SoundEvents.BLOCK_BEACON_AMBIENT, 0.5f, 2.0f);
        getInnerManaData().spellContext().clear();
        coolDown = 40;
        getStacks().clear();
    }

    @Override
    public void remove() {
        dropItem();
        if(!dead) {
            dead = true;
            ItemStack stack = NBTTagHelper.createItemWithEntity(this, ModItems.CONTEXT_CORE.get(), 1);
            ItemEntity entity = new ItemEntity(world, this.getPosX(), this.getPosY() + 0.5f, this.getPosZ(), stack);
            if(!this.world.isRemote)
                world.addEntity(entity);
        }
        super.remove();
    }

    private Vector3d relativeVec(ItemEntity entity) {
        return new Vector3d(entity.getPosX() - this.getPosX(), (entity.getPosY()) - (this.getPosY() + this.getHeight() / 2), entity.getPosZ() - this.getPosZ());
    }

    @Override
    public boolean refraction(SpellContext context) {
        return true;
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
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), (entity -> this.getPositionVec().squareDistanceTo(entity.getPositionVec()) <= 9 && (predicate == null || predicate.test(entity))));
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
        public Vector3d pos;
        public Vector3d prePos;
        public Vector3d motion = Vector3d.ZERO;
        private ItemStack itemStack;
        private final Random rand;
        public int age;
        public float hoverStart;

        public PosItem(Vector3d pos, ItemStack stack, Random rand) {
            this.pos = pos;
            this.prePos = pos;
            this.itemStack = stack;
            this.rand = rand;
        }

        public void tick() {
            this.motion = this.motion.scale(0.995);

            if(this.motion.lengthSquared() < 0.01) {
                double motionX = 0.1 - rand.nextDouble() * 0.2;
                double motionY = 0.1 - rand.nextDouble() * 0.2;
                double motionZ = 0.1 - rand.nextDouble() * 0.2;
                this.motion = new Vector3d(motionX, motionY, motionZ);
            }
            this.prePos = pos;
            this.pos = this.pos.add(this.motion);
            if(this.pos.lengthSquared() > 9)
                this.pos = this.pos.normalize().scale(3);
            if(this.pos.lengthSquared() < 1)
                this.pos = this.pos.normalize().scale(1);
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
