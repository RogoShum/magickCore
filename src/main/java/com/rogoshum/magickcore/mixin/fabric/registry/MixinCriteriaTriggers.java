package com.rogoshum.magickcore.mixin.fabric.registry;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CriteriaTriggers.class)
public abstract class MixinCriteriaTriggers {
    @Shadow
    private static <T extends CriterionTrigger<?>> T register(T criterionTrigger) {
        return null;
    }

    protected static <T extends CriterionTrigger<?>> T create(T criterionTrigger) {
        return register(criterionTrigger);
    }
}
