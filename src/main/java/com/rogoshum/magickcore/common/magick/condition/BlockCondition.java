package com.rogoshum.magickcore.common.magick.condition;

import com.rogoshum.magickcore.api.IConditionOnlyBlock;
import net.minecraft.world.level.block.Block;

public abstract class BlockCondition extends Condition<Block> implements IConditionOnlyBlock {
    @Override
    public boolean suitable(Object object) {
        return object instanceof Block;
    }
}
