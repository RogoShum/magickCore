package com.rogoshum.magickcore.common.item.tool;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.ExtraManaFactorContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class SpiritWoodStaffItem extends ManaItem implements IManaContextItem {
    public SpiritWoodStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 114514;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        super.onUsingTick(stack, player, count);
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        MagickContext magickContext = MagickContext.create(player.world, data.spellContext());
        MagickElement element = data.spellContext().element;
        MagickContext context = magickContext.caster(player).victim(player).element(element);
        if(context.containChild(LibContext.TRACE)) {
            TraceContext traceContext = context.getChild(LibContext.TRACE);
            traceContext.entity = MagickReleaseHelper.getEntityLookedAt(player);
        }
        EntityStateData state = ExtraDataUtil.entityStateData(player);
        float reduce = 0;
        int spellCount = 0;
        SpellContext context1 = context;
        while (context1 != null) {
            if(context1.applyType.isForm()) {
                reduce += MagickReleaseHelper.singleContextMana(context1);
                spellCount++;
            }
            context1 = context1.postContext;
        }
        if(spellCount > 0) {
            reduce = reduce/spellCount;
        }
        ManaFactor factor = ManaFactor.DEFAULT;
        if(reduce > 0) {
            context.addReduceCost(reduce);
            float scale = 1f/spellCount;
            factor = ManaFactor.create(scale, scale, scale);
        }
        context1 = context;
        while (context1 != null) {
            if(context1.applyType.isForm()) {
                context1.addChild(ExtraManaFactorContext.create(factor));
            }
            context1 = context1.postContext;
        }
        if(MagickReleaseHelper.releaseMagick(context, factor)) {
            ParticleUtil.spawnBlastParticle(player.world, player.getPositionVec().add(0, player.getHeight() * 0.5, 0), 2, state.getElement(), ParticleType.PARTICLE);
        }
    }


    @Override
    public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayerEntity) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) p_77663_3_, LibAdvancements.WAND);
            if(MagickCore.isModLoaded("curios"))
                AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) p_77663_3_, LibAdvancements.RING);
        }
    }
}
