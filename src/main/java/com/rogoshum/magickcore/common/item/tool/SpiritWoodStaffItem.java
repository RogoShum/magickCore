package com.rogoshum.magickcore.common.item.tool;

import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.api.itemstack.ISpiritDimension;
import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.client.item.StaffRenderer;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.api.magick.context.child.ExtraManaFactorContext;
import com.rogoshum.magickcore.api.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;

public class SpiritWoodStaffItem extends ManaItem implements IManaContextItem, ISpiritDimension {
    public SpiritWoodStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return new StaffRenderer();
            }
        });
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack, InteractionHand handIn) {
        return false;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 114514;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        super.onUsingTick(stack, player, count);
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        MagickContext magickContext = MagickContext.create(player.level, data.spellContext());
        MagickElement element = data.spellContext().element();
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
            if(context1.applyType().isForm()) {
                reduce += MagickReleaseHelper.singleContextMana(context1);
                spellCount++;
            }
            context1 = context1.postContext();
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
            if(context1.applyType().isForm()) {
                context1.addChild(ExtraManaFactorContext.create(factor));
            }
            context1 = context1.postContext();
        }
        if(MagickReleaseHelper.releaseMagick(context.hand(player.getUsedItemHand()), factor)) {
            ParticleUtil.spawnBlastParticle(player.level, player.position().add(0, player.getBbHeight() * 0.5, 0), 2, state.getElement(), ParticleType.PARTICLE);
        }
    }
}
