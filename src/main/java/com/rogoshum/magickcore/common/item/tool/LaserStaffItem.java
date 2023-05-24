package com.rogoshum.magickcore.common.item.tool;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.init.ManaMaterials;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModGroups;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.context.child.TraceContext;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.lib.LibMaterial;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.magick.materials.Material;
import com.rogoshum.magickcore.common.util.LootUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LaserStaffItem extends ManaItem {
    public LaserStaffItem() {
        super(properties().stacksTo(1));
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack, InteractionHand handIn) {
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        MagickContext context = MagickContext.create(playerIn.level, data.spellContext()).caster(playerIn);
        if(context.containChild(LibContext.TRACE)) {
            TraceContext traceContext = context.getChild(LibContext.TRACE);
            traceContext.entity = MagickReleaseHelper.getEntityLookedAt(playerIn);
        }
        return MagickReleaseHelper.releaseMagick(context.hand(handIn));
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, Level p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayer) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) p_77663_3_, LibAdvancements.LASER_STAFF);
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(this.allowdedIn(group)) {
            int lucky = 3;
            while (MagickCore.rand.nextBoolean())
                lucky++;
            int tick = (int) (Math.pow(lucky, 3) * MagickCore.rand.nextFloat() * (MagickCore.rand.nextInt(lucky * 2) + 1));
            if(tick > 600)
                tick = 600;
            ManaItem item = this;
            EntityType<? extends IManaEntity> entityType = ModEntities.MANA_LASER.get();
            Material material = ManaMaterials.getMaterial(LibMaterial.ORIGIN);
            if(lucky > 7)
                material = ManaMaterials.getMaterialRandom();
            boolean trace = MagickCore.rand.nextInt(lucky) > 3;
            float force = (float) Math.min(MagickCore.rand.nextInt(lucky) + MagickCore.rand.nextInt(lucky) * 1.1, material.getMana());
            int mana = 0;
            for (int i = 0; i < lucky; ++i) {
                mana += MagickCore.rand.nextInt(Math.max((int) (material.getMana() * 0.25), 1));
            }

            items.add(LootUtil.createRandomManaItem(item, entityType, force, tick, Math.min(mana, 50000), trace));
        }
    }
}
