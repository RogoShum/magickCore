package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.ISpellContext;
import com.rogoshum.magickcore.api.IManaMaterial;
import com.rogoshum.magickcore.api.IMaterialLimit;
import com.rogoshum.magickcore.init.ManaMaterials;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ManaMaterialsItem extends BaseItem implements IManaMaterial {
    private final String material;
    public ManaMaterialsItem(String material) {
        super(BaseItem.properties());
        this.material = material;
    }

    @Override
    public boolean disappearAfterRead() {
        return false;
    }

    @Override
    public int getManaNeed(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        if(data instanceof IMaterialLimit) {
            ((IMaterialLimit) data).setMaterial(ManaMaterials.getMaterial(material));
            return true;
        }
        return false;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new TranslationTextComponent("item.magickcore.magick_core");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(MagickCore.MOD_ID + ".material." + this.material));
    }
}
