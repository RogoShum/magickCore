package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.child.OffsetContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class OffsetMemoryItem extends DirectionMemoryItem implements IManaMaterial {
    public OffsetMemoryItem() {
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

    public void addPosition(ItemStack stack, Vector3d vec) {
        if(stack.hasTag()) {
            if(NBTTagHelper.hasVectorDouble(stack.getTag(), "position")) {
                Vector3d fir = NBTTagHelper.getVectorFromNBT(stack.getTag(), "position");
                NBTTagHelper.putVectorDouble(stack.getTag(), "direction", vec.subtract(fir));
                NBTTagHelper.removeVectorDouble(stack.getTag(), "position");
            } else
                NBTTagHelper.putVectorDouble(stack.getTag(), "position", vec);
            if(NBTTagHelper.hasVectorDouble(stack.getTag(), "direction"))
                NBTTagHelper.removeVectorDouble(stack.getTag(), "direction");
        } else
            NBTTagHelper.putVectorDouble(stack.getOrCreateTag(), "position", vec);
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        if(stack.hasTag() && NBTTagHelper.hasVectorDouble(stack.getTag(), "direction")) {
            Vector3d vector3d = NBTTagHelper.getVectorFromNBT(stack.getTag(), "direction");
            if(data.spellContext().containChild(LibContext.OFFSET)) {
                OffsetContext offsetContext = data.spellContext().getChild(LibContext.OFFSET);
                data.spellContext().addChild(offsetContext.add(vector3d));
            } else {
                data.spellContext().addChild(OffsetContext.create(vector3d));
            }
            return true;
        }
        return false;
    }
}
