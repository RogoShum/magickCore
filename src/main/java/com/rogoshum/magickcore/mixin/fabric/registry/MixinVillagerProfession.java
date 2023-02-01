package com.rogoshum.magickcore.mixin.fabric.registry;

import com.google.common.collect.ImmutableSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(VillagerProfession.class)
public interface MixinVillagerProfession {
    @Invoker("register")
    static VillagerProfession create(String string, PoiType poiType, @Nullable SoundEvent soundEvent) {
        throw new AssertionError("Untransformed Accessor!");
    }
}
