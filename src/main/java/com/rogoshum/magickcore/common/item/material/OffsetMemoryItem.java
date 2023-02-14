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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

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

    public void addPosition(ItemStack stack, Vec3 vec) {
        if(stack.hasTag()) {
            if(NBTTagHelper.hasVectorDouble(stack.getTag(), "position")) {
                Vec3 fir = NBTTagHelper.getVectorFromNBT(stack.getTag(), "position");
                NBTTagHelper.putVectorDouble(stack.getTag(), "direction", vec.subtract(fir));
                NBTTagHelper.removeVectorDouble(stack.getTag(), "position");
            } else {
                NBTTagHelper.putVectorDouble(stack.getTag(), "position", vec);
                if(NBTTagHelper.hasVectorDouble(stack.getTag(), "direction"))
                    NBTTagHelper.removeVectorDouble(stack.getTag(), "direction");
            }
        } else
            NBTTagHelper.putVectorDouble(stack.getOrCreateTag(), "position", vec);
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        if(stack.hasTag() && NBTTagHelper.hasVectorDouble(stack.getTag(), "direction")) {
            Vec3 vector3d = NBTTagHelper.getVectorFromNBT(stack.getTag(), "direction");
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
