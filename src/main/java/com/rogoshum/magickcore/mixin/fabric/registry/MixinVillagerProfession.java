package com.rogoshum.magickcore.mixin.fabric.registry;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(VillagerProfession.class)
public abstract class MixinVillagerProfession {
    @Shadow
    static VillagerProfession register(String string, PoiType poiType, @Nullable SoundEvent soundEvent) {
        return null;
    }

    protected static VillagerProfession create(String string, PoiType poiType, @Nullable SoundEvent soundEvent) {
        return register(string, poiType, soundEvent);
    }
}
