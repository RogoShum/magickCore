package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.IMaterialLimit;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.magick.materials.Material;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.util.ItemStackUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ManaEnergyItem extends ManaItem implements IManaMaterial {
    public ManaEnergyItem() {
        super(properties());
    }

    @Override
    public boolean disappearAfterRead() {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return new ManaEnergyRenderer();
            }
        });
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if(!entity.isAlive()) return false;
        if(entity.getItem().getCount() > 1) return false;
        List<Entity> entities = entity.level.getEntities(entity, entity.getBoundingBox().inflate(1.5)
                , (entity1 -> entity1 instanceof ItemEntity &&
                        ((ItemEntity) entity1).getItem().getItem() instanceof ManaEnergyItem));

        if(entities.size() > 0) {
            ItemStackUtil.setItemEntityAge(entity, -32768);
        }
        entities.forEach(entity1 -> {
            ItemStackUtil.setItemEntityAge((ItemEntity)entity1, -32768);
            double speed = entity1.getDeltaMovement().length();
            Vec3 motion = entity.position().subtract(entity1.position()).normalize().scale(speed);
            entity1.setDeltaMovement(entity1.getDeltaMovement().scale(0.4).add(motion.scale(0.6)));

            if(entity.distanceToSqr(entity1) <= 0.01) {
                SpellContext other = ExtraDataUtil.itemManaData(((ItemEntity) entity1).getItem()).spellContext();
                SpellContext mine = ExtraDataUtil.itemManaData(entity.getItem()).spellContext();
                if(entity.getId() < entity1.getId() || entity.getItem().getCount() < ((ItemEntity) entity1).getItem().getCount()) {
                    for (int i = 0; i < ((ItemEntity) entity1).getItem().getCount(); ++i)
                        mine.merge(other);
                    entity1.remove(Entity.RemovalReason.DISCARDED);
                    spawnParticle(entity);
                    entity.getItem().hasTag();
                    entity.setItem(entity.getItem());
                }
            }
        });
        return false;
    }

    public void spawnParticle(ItemEntity entity) {
        if(!entity.level.isClientSide) return;
        Vec3 vec = entity.position().add(0, entity.getBbHeight(), 0);
        for(int i = 0; i < 16; ++i) {
            LitParticle par = new LitParticle(entity.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * entity.getBbWidth() + vec.x
                    , MagickCore.getNegativeToOne() * entity.getBbWidth() + vec.y
                    , MagickCore.getNegativeToOne() * entity.getBbWidth() + vec.z)
                    , 0.05f, 0.05f, MagickCore.rand.nextFloat(), 20, ModElements.ORIGIN.getRenderer());
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    public int getManaNeed(ItemStack stack) {
        SpellContext item = ExtraDataUtil.itemManaData(stack).spellContext();
        return (int)((item.tick + item.range + item.force) * 1000);
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        if (data instanceof IMaterialLimit) {
            SpellContext item = ExtraDataUtil.itemManaData(stack).spellContext();
            Material limit = ((IMaterialLimit) data).getMaterial();
            if((data.spellContext().force < limit.getForce() && item.force > 0) ||
                    (data.spellContext().tick < limit.getTick() && item.tick > 0) ||
                    (data.spellContext().range < limit.getRange() && item.range > 0)) {
                data.spellContext().merge(item);
                limit.limit(data.spellContext());
                return true;
            }
            return false;
        } else {
            data.spellContext().merge(ExtraDataUtil.itemManaData(stack).spellContext());
            return true;
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            ItemStack manaEnergy = new ItemStack(this);
            ItemStack rangeEnergy = manaEnergy.copy();
            ExtraDataUtil.itemManaData(rangeEnergy, (data) -> data.spellContext().range(1.0f));

            ItemStack forceEnergy = manaEnergy.copy();
            ExtraDataUtil.itemManaData(forceEnergy, (data) -> data.spellContext().force(1.0f));

            ItemStack tickEnergy = manaEnergy.copy();
            ExtraDataUtil.itemManaData(tickEnergy, (data) -> data.spellContext().tick(20));
            items.add(rangeEnergy);
            items.add(forceEnergy);
            items.add(tickEnergy);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent(LibItem.CONTEXT_MATERIAL));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }
}
