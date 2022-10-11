package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.magick.lifestate.repeater.LifeRepeater;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Callable;

public class LifeRepeaterItem extends BaseItem {
    private final Callable<? extends LifeRepeater> repeater;
    private final String sign;
    public LifeRepeaterItem(Callable<? extends LifeRepeater> repeater) {
        super(properties().maxStackSize(16));
        this.repeater = repeater;
        this.sign = "repeater";
    }

    public LifeRepeaterItem(Callable<? extends LifeRepeater> repeater, String sign) {
        super(properties().maxStackSize(16));
        this.repeater = repeater;
        this.sign = sign;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new TranslationTextComponent("item.magickcore." + this.sign);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(this.getTranslationKey()));
    }

    public LifeRepeater getRepeater(){
        LifeRepeater repeater = null;
        try{
            repeater = this.repeater.call();
        }
        catch(Exception ignored){

        }
        return repeater;
    }
}
