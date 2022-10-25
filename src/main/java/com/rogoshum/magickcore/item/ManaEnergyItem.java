package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.IMaterialLimit;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.magick.context.SpellContext;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.magick.materials.Material;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.List;

public class ManaEnergyItem extends ManaItem implements IManaMaterial {
    public ManaEnergyItem() {
        super(BaseItem.properties().setISTER(() -> ManaEnergyRenderer::new));
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
        List<Entity> entities = entity.world.getEntitiesInAABBexcluding(entity, entity.getBoundingBox().grow(1.5)
                , (entity1 -> entity1 instanceof ItemEntity &&
                        ((ItemEntity) entity1).getItem().getItem() instanceof ManaEnergyItem));

        if(entities.size() > 0) {
            ObfuscationReflectionHelper.setPrivateValue(ItemEntity.class, entity, -32768, "field_70292_b");
        }
        entities.forEach(entity1 -> {
            ObfuscationReflectionHelper.setPrivateValue(ItemEntity.class, (ItemEntity)entity1, -32768, "field_70292_b");
            double speed = entity1.getMotion().length();
            Vector3d motion = entity.getPositionVec().subtract(entity1.getPositionVec()).normalize().scale(speed);
            entity1.setMotion(entity1.getMotion().scale(0.4).add(motion.scale(0.6)));

            if(entity.getDistanceSq(entity1) <= 0.01) {
                SpellContext other = ExtraDataHelper.itemManaData(((ItemEntity) entity1).getItem()).spellContext();
                SpellContext mine = ExtraDataHelper.itemManaData(entity.getItem()).spellContext();
                if(entity.getEntityId() < entity1.getEntityId() || entity.getItem().getCount() < ((ItemEntity) entity1).getItem().getCount()) {
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
        Vector3d vec = entity.getPositionVec().add(0, entity.getHeight(), 0);
        for(int i = 0; i < 16; ++i) {
            LitParticle par = new LitParticle(entity.world, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * entity.getWidth() + vec.x
                    , MagickCore.getNegativeToOne() * entity.getWidth() + vec.y
                    , MagickCore.getNegativeToOne() * entity.getWidth() + vec.z)
                    , 0.05f, 0.05f, MagickCore.rand.nextFloat(), 20, ModElements.ORIGIN.getRenderer());
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    public int getManaNeed(ItemStack stack) {
        SpellContext item = ExtraDataHelper.itemManaData(stack).spellContext();
        return (int)((item.tick + item.range + item.force) * 1000);
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        if (data instanceof IMaterialLimit) {
            SpellContext item = ExtraDataHelper.itemManaData(stack).spellContext();
            Material limit = ((IMaterialLimit) data).getMaterial();
            if(data.spellContext().force < limit.getForce() ||
                    data.spellContext().tick < limit.getTick() ||
                    data.spellContext().range < limit.getRange()) {
                data.spellContext().merge(item);
                limit.limit(data.spellContext());
                return true;
            } else
                return false;
        } else {
            data.spellContext().merge(ExtraDataHelper.itemManaData(stack).spellContext());
            return true;
        }
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            ItemStack manaEnergy = new ItemStack(this);
            ItemStack rangeEnergy = manaEnergy.copy();
            ExtraDataHelper.itemManaData(rangeEnergy, (data) -> data.spellContext().range(1.0f));

            ItemStack forceEnergy = manaEnergy.copy();
            ExtraDataHelper.itemManaData(forceEnergy, (data) -> data.spellContext().force(1.0f));

            ItemStack tickEnergy = manaEnergy.copy();
            ExtraDataHelper.itemManaData(tickEnergy, (data) -> data.spellContext().tick(20));
            items.add(rangeEnergy);
            items.add(forceEnergy);
            items.add(tickEnergy);
        }
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }
}
