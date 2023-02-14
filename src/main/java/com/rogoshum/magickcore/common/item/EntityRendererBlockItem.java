package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.item.ContextPointerRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.fml.DistExecutor;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntityRendererBlockItem extends BlockItem {
    private BlockEntityWithoutLevelRenderer renderProperties;
    public EntityRendererBlockItem(Block p_40565_, Properties p_40566_, Supplier<BlockEntityWithoutLevelRenderer> renderProperties) {
        super(p_40565_, p_40566_);
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> this.renderProperties = renderProperties.get());
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderProperties;
            }
        });
    }
}
