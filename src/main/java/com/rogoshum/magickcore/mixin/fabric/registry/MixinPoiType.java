package com.rogoshum.magickcore.mixin.fabric.registry;

import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(PoiType.class)
public abstract class MixinPoiType {
    @Shadow
    private static PoiType register(String string, Set<BlockState> set, int i, int j) {
        return null;
    }

    protected static PoiType create(String string, Set<BlockState> set, int i, int j) {
        return register(string, set, i, j);
    }
}
