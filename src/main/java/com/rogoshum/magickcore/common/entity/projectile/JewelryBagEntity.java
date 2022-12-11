package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IConditionOnlyEntity;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.BubbleRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.JewelryBagRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibConditions;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.magick.context.child.ItemContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.network.EntityCompoundTagPack;
import com.rogoshum.magickcore.common.util.ItemStackUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class JewelryBagEntity extends ManaProjectileEntity implements IManaRefraction {
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.7f, 1.0f, 1.0f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/bag.png");
    public JewelryBagEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        if(!spellContext().containChild(LibContext.ITEM) && !world.isRemote) {
            Entity entity = null;
            UUID uuid = MagickCore.emptyUUID;
            if(spellContext().containChild(LibContext.TRACE)) {
                TraceContext traceContext = spellContext().getChild(LibContext.TRACE);
                uuid = traceContext.uuid;
                if(traceContext.entity != null)
                    entity = traceContext.entity;
                else if(traceContext.uuid != MagickCore.emptyUUID) {
                    entity = ((ServerWorld) this.world).getEntityByUuid(traceContext.uuid);
                    traceContext.entity = entity;
                }
            }

            Entity finalEntity = entity;
            UUID finalUuid = uuid;
            List<ItemEntity> items = this.world.getEntitiesWithinAABB(EntityType.ITEM, this.getBoundingBox().grow(1.0), Objects::nonNull);
            items.removeIf((entity1 -> {
                if(finalEntity != null && entity1 != finalEntity)
                    return true;
                return finalUuid != MagickCore.emptyUUID && !entity1.getUniqueID().equals(finalUuid);
            }));
            if(items.size() > 0) {
                ItemEntity itemEntity = items.get(0);
                if(itemEntity.isAlive()) {
                    EntityEvents.HitEntityEvent event = new EntityEvents.HitEntityEvent(this, itemEntity);
                    MinecraftForge.EVENT_BUS.post(event);
                    ItemContext context = ItemContext.create(itemEntity.getItem());
                    itemEntity.remove();
                    spellContext().addChild(context);
                    doNetworkUpdate();
                }
            }
        }

        super.tick();
    }

    @Override
    protected void traceTarget() {
        if (!this.spellContext().containChild(LibContext.TRACE) || this.world.isRemote) return;
        Entity entity = null;
        if (!this.spellContext().containChild(LibContext.ITEM)) {
            TraceContext traceContext = spellContext().getChild(LibContext.TRACE);

            if(traceContext.entity != null)
                entity = traceContext.entity;
            else if(traceContext.uuid != MagickCore.emptyUUID) {
                entity = ((ServerWorld) this.world).getEntityByUuid(traceContext.uuid);
                traceContext.entity = entity;
            }
        } else
            entity = getOwner();

        if(entity != null && entity.isAlive()) {
            Vector3d goal = new Vector3d(entity.getPosX(), entity.getPosY() + entity.getHeight() / 1.5f, entity.getPosZ());
            Vector3d self = new Vector3d(this.getPosX(), this.getPosY(), this.getPosZ());

            double length = maxMotion * 0.3;
            Vector3d motion = goal.subtract(self).normalize().scale(Math.max(length * 0.2, 0.02));
            this.setMotion(motion.add(this.getMotion().scale(0.8)));
        }
    }

    @Override
    public void remove() {
        if(!world.isRemote && spellContext().containChild(LibContext.ITEM)) {
            ItemContext context = this.spellContext().getChild(LibContext.ITEM);
            if(context.valid()) {
                ItemEntity entity = new ItemEntity(world, getPosX(), getPosY() + getHeight() * 0.5, getPosZ(), context.itemStack);
                world.addEntity(entity);
            }
        }
        super.remove();
    }

    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new JewelryBagRenderer(this);
    }

    @Override
    public boolean hitBlockRemove(BlockRayTraceResult blockRayTraceResult) {
        if(world.isRemote) return false;
        BlockPos pos = blockRayTraceResult.getPos();
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof IInventory) {
            if(spellContext().containChild(LibContext.ITEM)) {
                ItemContext context = this.spellContext().getChild(LibContext.ITEM);
                ItemStack stack = context.itemStack;
                IInventory inventory = (IInventory) tile;
                for (int i = 0; i < inventory.getSizeInventory(); i++) {
                    ItemStack slot = inventory.getStackInSlot(i);
                    if(slot.isEmpty()) {
                        inventory.setInventorySlotContents(i, stack);
                        spellContext().removeChild(LibContext.ITEM);
                        inventory.markDirty();
                        break;
                    } else if(ItemStackUtil.canMergeStacks(slot, stack)) {
                        ItemStack newSlot = ItemStackUtil.mergeInventoryStacks(slot, stack, Math.min(inventory.getInventoryStackLimit(), slot.getMaxStackSize()));
                        inventory.setInventorySlotContents(i, newSlot);
                        inventory.markDirty();
                        if(stack.isEmpty()) {
                            spellContext().removeChild(LibContext.ITEM);
                            break;
                        }
                    }
                }
            } else
                return super.hitBlockRemove(blockRayTraceResult);
        } else if(spellContext().containChild(LibContext.ITEM) && spellContext().containChild(LibContext.TRACE))
            return false;
        else if (spellContext().containChild(LibContext.TRACE)) {
            TraceContext traceContext = spellContext().getChild(LibContext.TRACE);
            if(traceContext.entity == null && traceContext.uuid != MagickCore.emptyUUID) {
                traceContext.entity = ((ServerWorld) this.world).getEntityByUuid(traceContext.uuid);
            }
            if(traceContext.entity instanceof ItemEntity)
                return false;
        }
        else if(spellContext().containChild(LibContext.CONDITION)) {
            AtomicBoolean entityOnly = new AtomicBoolean(false);
            ConditionContext condition = spellContext().getChild(LibContext.CONDITION);
            condition.conditions.forEach(condition1 -> {
                if(condition1 instanceof IConditionOnlyEntity)
                    entityOnly.set(true);
            });
            if(entityOnly.get())
                return false;
        }
        return super.hitBlockRemove(blockRayTraceResult);
    }

    @Override
    public boolean hitEntityRemove(EntityRayTraceResult entityRayTraceResult) {
        if(spellContext().containChild(LibContext.ITEM) && entityRayTraceResult.getEntity() == getOwner())
            return true;
        if(entityRayTraceResult.getEntity() instanceof ItemEntity) {
            if(spellContext().containChild(LibContext.ITEM)) {
                ItemEntity entity = (ItemEntity) entityRayTraceResult.getEntity();
                ItemContext context = spellContext().getChild(LibContext.ITEM);
                if(ItemStackUtil.canMergeStacks(context.itemStack, entity.getItem())) {
                    context.itemStack = ItemStackUtil.mergeStacks(context.itemStack, entity.getItem(), 64);
                }
                //spellContext().addChild(context);
                doNetworkUpdate();
            }
            return false;
        } else if(entityRayTraceResult.getEntity() instanceof JewelryBagEntity) {
            return false;
        } else if (suitableEntity(entityRayTraceResult.getEntity())) {
            return false;
        }
        return super.hitEntityRemove(entityRayTraceResult);
    }

    @Override
    public MagickContext beforeCast(MagickContext context) {
        if(spellContext().containChild(LibContext.ITEM) && context.applyType == ApplyType.HIT_BLOCK)
            return context.applyType(ApplyType.NONE);
        return super.beforeCast(context);
    }

    @Override
    public float getSourceLight() {
        return 3;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    protected void applyParticle() {
        LitParticle litPar = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleSprite()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() * 0.25 + this.getPosX()
                , MagickCore.getNegativeToOne() * this.getWidth() * 0.25 + this.getPosY() + this.getHeight() * 0.5
                , MagickCore.getNegativeToOne() * this.getWidth() * 0.25 + this.getPosZ())
                , (MagickCore.getRandFloat() * this.getWidth()) * 0.3f, (MagickCore.getRandFloat() * this.getWidth()) * 0.3f, 1.0f
                , spellContext().element.getRenderer().getParticleRenderTick(), spellContext().element.getRenderer());
        litPar.setGlow();
        litPar.setShakeLimit(15.0f);
        litPar.setLimitScale();
        MagickCore.addMagickParticle(litPar);
    }

    @Override
    protected float getGravityVelocity() {
        return 0;
    }

    @Override
    public void renderFrame(float partialTicks) {
        LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleSprite()
                , new Vector3d(this.lastTickPosX + (this.getPosX() - this.lastTickPosX) * partialTicks
                , this.lastTickPosY + (this.getPosY() - this.lastTickPosY) * partialTicks + this.getHeight() * 0.5
                , this.lastTickPosZ + (this.getPosZ() - this.lastTickPosZ) * partialTicks)
                , 0.2f * this.getWidth(), 0.2f * this.getWidth(), 1.0f, 5, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setGlow();
        par.setParticleGravity(0);
        par.setLimitScale();
        MagickCore.addMagickParticle(par);
    }

    @Override
    public ManaFactor getManaFactor() {
        return MANA_FACTOR;
    }

    @Override
    public boolean refraction(SpellContext context) {
        return true;
    }
}
