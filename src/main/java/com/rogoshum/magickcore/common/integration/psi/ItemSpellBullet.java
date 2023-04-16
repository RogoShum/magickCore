package com.rogoshum.magickcore.common.integration.psi;

import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.PsiSpellContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;

public class ItemSpellBullet extends MagickContextItem{
    public ItemSpellBullet() {
        super();
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
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
    public String getDescriptionId() {
        return ModItems.MAGICK_CORE.get().getDescriptionId();
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if(entity.isInWater()) {
            ItemManaData data = ExtraDataUtil.itemManaData(stack);
            if(data.spellContext().containChild(PsiSpellContext.TYPE)) {
                PsiSpellContext spellContext = data.spellContext().getChild(PsiSpellContext.TYPE);
                entity.setItem(spellContext.itemStack);
                ParticleUtil.spawnBlastParticle(entity.level, entity.position().add(0, 0.25, 0), 3, ModElements.PSI, ParticleType.MIST);
            }
        }
        return false;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    }
}
