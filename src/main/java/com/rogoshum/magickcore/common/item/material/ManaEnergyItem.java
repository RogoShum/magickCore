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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.util.List;

public class ManaEnergyItem extends ManaItem implements IManaMaterial {
    public ManaEnergyItem() {
        super(properties().setISTER(() -> ManaEnergyRenderer::new));
    }

    @Override
    public boolean disappearAfterRead() {
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
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
            Vector3d motion = entity.position().subtract(entity1.position()).normalize().scale(speed);
            entity1.setDeltaMovement(entity1.getDeltaMovement().scale(0.4).add(motion.scale(0.6)));

            if(entity.distanceToSqr(entity1) <= 0.01) {
                SpellContext other = ExtraDataUtil.itemManaData(((ItemEntity) entity1).getItem()).spellContext();
                SpellContext mine = ExtraDataUtil.itemManaData(entity.getItem()).spellContext();
                if(entity.getId() < entity1.getId() || entity.getItem().getCount() < ((ItemEntity) entity1).getItem().getCount()) {
                    for (int i = 0; i < ((ItemEntity) entity1).getItem().getCount(); ++i)
                        mine.merge(other);
                    entity1.remove();
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
        Vector3d vec = entity.position().add(0, entity.getBbHeight(), 0);
        for(int i = 0; i < 16; ++i) {
            LitParticle par = new LitParticle(entity.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * entity.getBbWidth() + vec.x
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
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
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
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LibItem.CONTEXT_MATERIAL));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }
}
