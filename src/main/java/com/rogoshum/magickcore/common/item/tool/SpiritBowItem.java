package com.rogoshum.magickcore.common.item.tool;

import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.client.item.SpiritBowRenderer;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;

public class SpiritBowItem extends ManaItem implements IManaContextItem {
    public SpiritBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return new SpiritBowRenderer();
            }
        });
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        EntityStateData state = ExtraDataUtil.entityStateData(entityLiving);
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        MagickContext magickContext = MagickContext.create(entityLiving.level);
        int tick = Math.min(40, (getUseDuration(stack) - timeLeft));
        magickContext.range(tick * 0.25f);
        magickContext.tick(tick * 4);
        MagickElement element = data.spellContext().element;
        MagickContext context = magickContext.caster(entityLiving).element(element);
        SpawnContext spawnContext = SpawnContext.create(ModEntities.ARROW.get());
        context.addChild(spawnContext);
        context.addReduceCost(MagickReleaseHelper.singleContextMana(context));
        context.post(data.spellContext().copy().element(element));
        if(context.postContext.containChild(LibContext.TRACE)) {
            TraceContext traceContext = context.postContext.getChild(LibContext.TRACE);
            traceContext.entity = MagickReleaseHelper.getEntityLookedAt(entityLiving);
            context.addChild(traceContext);
        }
        context.applyType(ApplyType.SPAWN_ENTITY);
        boolean success = MagickReleaseHelper.releaseMagick(context);
        if(success)
            spawnParticle(entityLiving, state);
        super.releaseUsing(stack, worldIn, entityLiving, timeLeft);
    }

    public int getUseDuration(ItemStack stack) {
        return 114514;
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }
}
