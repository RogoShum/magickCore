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
import com.rogoshum.magickcore.common.init.ModBlocks;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModSounds;
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
import com.rogoshum.magickcore.common.util.ProjectileUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class JewelryBagEntity extends ManaProjectileEntity implements IManaRefraction {
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.7f, 1.0f, 1.0f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/bag.png");
    public JewelryBagEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void makeSound() {
        if (this.tickCount == 1) {
            this.playSound(ModSounds.glitter.get(), 2F, 1.0F + this.random.nextFloat());
        }
    }
    @Override
    public void tick() {
        if(!spellContext().containChild(LibContext.ITEM) && !level.isClientSide) {
            Entity entity = null;
            UUID uuid = MagickCore.emptyUUID;
            if(spellContext().containChild(LibContext.TRACE)) {
                TraceContext traceContext = spellContext().getChild(LibContext.TRACE);
                uuid = traceContext.uuid;
                if(traceContext.entity != null)
                    entity = traceContext.entity;
                else if(traceContext.uuid != MagickCore.emptyUUID) {
                    entity = ((ServerWorld) this.level).getEntity(traceContext.uuid);
                    traceContext.entity = entity;
                }
            }

            Entity finalEntity = entity;
            UUID finalUuid = uuid;
            List<ItemEntity> items = this.level.getEntities(EntityType.ITEM, this.getBoundingBox().inflate(1.0), Objects::nonNull);
            items.removeIf((entity1 -> {
                if(finalEntity != null && entity1 != finalEntity)
                    return true;
                return finalUuid != MagickCore.emptyUUID && !entity1.getUUID().equals(finalUuid);
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

        if(spellContext().containChild(LibContext.TRACE) && !level.isClientSide()) {
            RayTraceResult raytraceresult = ProjectileUtil.canTouchVisibleBlock(this, Objects::nonNull);
            if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
                BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) raytraceresult;
                Block block = level.getBlockState(blockRayTraceResult.getBlockPos()).getBlock();
                if(ModBlocks.ITEM_EXTRACTOR.isPresent() && block == ModBlocks.ITEM_EXTRACTOR.get()) {
                    this.setPos(blockRayTraceResult.getBlockPos().getX() + 0.5, blockRayTraceResult.getBlockPos().getY(), blockRayTraceResult.getBlockPos().getZ() + 0.5);
                    this.remove();
                }
            } else {
                BlockPos pos = new BlockPos(position());
                Block block = level.getBlockState(pos).getBlock();
                if(ModBlocks.ITEM_EXTRACTOR.isPresent() && block == ModBlocks.ITEM_EXTRACTOR.get()) {
                    this.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    this.remove();
                }
            }
        }
    }

    @Override
    protected void traceTarget() {
        if (!this.spellContext().containChild(LibContext.TRACE) || this.level.isClientSide) return;
        Entity entity = null;
        if (!this.spellContext().containChild(LibContext.ITEM)) {
            TraceContext traceContext = spellContext().getChild(LibContext.TRACE);

            if(traceContext.entity != null)
                entity = traceContext.entity;
            else if(traceContext.uuid != MagickCore.emptyUUID) {
                entity = ((ServerWorld) this.level).getEntity(traceContext.uuid);
                traceContext.entity = entity;
            }
        } else
            entity = getOwner();

        if(entity != null && entity.isAlive()) {
            Vector3d goal = new Vector3d(entity.getX(), entity.getY() + entity.getBbHeight() / 1.5f, entity.getZ());
            Vector3d self = new Vector3d(this.getX(), this.getY(), this.getZ());

            double length = maxMotion * 0.3;
            Vector3d motion = goal.subtract(self).normalize().scale(Math.max(length * 0.2, 0.02));
            this.setDeltaMovement(motion.add(this.getDeltaMovement().scale(0.8)));
        }
    }

    @Override
    public void remove() {
        if(!level.isClientSide && spellContext().containChild(LibContext.ITEM)) {
            ItemContext context = this.spellContext().getChild(LibContext.ITEM);
            if(context.valid()) {
                ItemEntity entity = new ItemEntity(level, getX(), getY() + getBbHeight() * 0.5, getZ(), context.itemStack);
                level.addFreshEntity(entity);
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
        if(level.isClientSide) return false;
        BlockPos pos = blockRayTraceResult.getBlockPos();
        TileEntity tile = level.getBlockEntity(pos);
        if(tile instanceof IInventory) {
            if(spellContext().containChild(LibContext.ITEM)) {
                ItemContext context = this.spellContext().getChild(LibContext.ITEM);
                ItemStack stack = context.itemStack;
                IInventory inventory = (IInventory) tile;
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack slot = inventory.getItem(i);
                    if(slot.isEmpty()) {
                        inventory.setItem(i, stack);
                        spellContext().removeChild(LibContext.ITEM);
                        inventory.setChanged();
                        break;
                    } else if(ItemStackUtil.canMergeStacks(slot, stack)) {
                        ItemStack newSlot = ItemStackUtil.mergeInventoryStacks(slot, stack, Math.min(inventory.getMaxStackSize(), slot.getMaxStackSize()));
                        inventory.setItem(i, newSlot);
                        inventory.setChanged();
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
                traceContext.entity = ((ServerWorld) this.level).getEntity(traceContext.uuid);
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
    protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
        if(suitableEntity(p_213868_1_.getEntity())) {
            ConditionContext condition = null;
            if(spellContext().containChild(LibContext.CONDITION))
                condition = spellContext().getChild(LibContext.CONDITION);
            AtomicReference<Boolean> pass = new AtomicReference<>(true);
            if(condition != null) {
                if(!condition.test(this.getOwner(), p_213868_1_.getEntity()))
                    pass.set(false);
            }
            if(pass.get()) {
                EntityEvents.HitEntityEvent event = new EntityEvents.HitEntityEvent(this, p_213868_1_.getEntity());
                MinecraftForge.EVENT_BUS.post(event);
            }
        }

        if (victim != null && !this.level.isClientSide) {
            releaseMagick();
            if(hitEntityRemove(p_213868_1_))
                this.remove();
        }
        super.onHitEntity(p_213868_1_);
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
        } else if (suitableEntity(entityRayTraceResult.getEntity()) && (getOwner() == null || (getOwner() != null && getOwner() != entityRayTraceResult.getEntity()))) {
            return false;
        }
        return super.hitEntityRemove(entityRayTraceResult);
    }

    @Override
    public MagickContext beforeCast(MagickContext context) {
        if(spellContext().containChild(LibContext.ITEM) && (context.doBlock || context.applyType == ApplyType.HIT_BLOCK))
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
        LitParticle litPar = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleSprite()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.25 + this.getX()
                , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.25 + this.getY() + this.getBbHeight() * 0.5
                , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.25 + this.getZ())
                , (MagickCore.getRandFloat() * this.getBbWidth()) * 0.3f, (MagickCore.getRandFloat() * this.getBbWidth()) * 0.3f, 1.0f
                , 20, spellContext().element.getRenderer());
        litPar.setGlow();
        litPar.setShakeLimit(15.0f);
        litPar.setLimitScale();
        MagickCore.addMagickParticle(litPar);
    }

    @Override
    protected float getGravity() {
        return 0;
    }

    @Override
    public void renderFrame(float partialTicks) {
        LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleSprite()
                , new Vector3d(this.xOld + (this.getX() - this.xOld) * partialTicks
                , this.yOld + (this.getY() - this.yOld) * partialTicks + this.getBbHeight() * 0.5
                , this.zOld + (this.getZ() - this.zOld) * partialTicks)
                , 0.2f * this.getBbWidth(), 0.2f * this.getBbWidth(), 1.0f, 5, MagickCore.proxy.getElementRender(spellContext().element.type()));
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
