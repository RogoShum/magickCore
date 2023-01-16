package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.magick.context.child.ReverseContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class ReverseItem extends BaseItem implements IManaMaterial {
    public ReverseItem() {
        super(properties());
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
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        data.spellContext().addChild(ReverseContext.create());
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
         tooltip.add(new TranslatableComponent(LibItem.CONTEXT_MATERIAL));
         tooltip.add(new TranslatableComponent(LibItem.REVERSE));
    }
}
