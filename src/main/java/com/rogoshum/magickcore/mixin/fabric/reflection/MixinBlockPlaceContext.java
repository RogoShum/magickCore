package com.rogoshum.magickcore.mixin.fabric.reflection;

import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockPlaceContext.class)
public class MixinBlockPlaceContext implements IReplaceClicked{
    @Shadow
    protected boolean replaceClicked;

    public void setReplaceClicked(boolean replaceClicked) {
        this.replaceClicked = replaceClicked;
    }
}
