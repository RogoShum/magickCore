package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.api.ISpellContext;
import com.rogoshum.magickcore.api.IManaMaterial;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.lib.LibItem;
import com.rogoshum.magickcore.magick.context.child.TraceContext;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ManaDragonBreathItem extends BaseItem implements IManaMaterial {
    public ManaDragonBreathItem() {
        super(BaseItem.properties());
    }

    @Override
    public boolean disappearAfterRead() {
        return false;
    }

    @Override
    public int getManaNeed(ItemStack stack) {
        return 1000;
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        if(!data.spellContext().containChild(LibContext.TRACE)) {
            data.spellContext().addChild(new TraceContext());
            return true;
        }

        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
         tooltip.add(new TranslationTextComponent(LibItem.MANA_DRAGON_BREATH));
    }
}
