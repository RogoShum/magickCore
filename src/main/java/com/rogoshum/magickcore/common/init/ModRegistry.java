package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.lib.LibConditions;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.ability.*;
import com.rogoshum.magickcore.common.magick.condition.*;
import com.rogoshum.magickcore.common.magick.context.child.*;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.registry.ObjectRegistry;
import com.rogoshum.magickcore.common.registry.elementmap.ElementFunctions;

import java.util.concurrent.Callable;

public class ModRegistry {
    public static void init() {
        MagickRegistry.<MagickElement>getRegistry(LibRegistry.ELEMENT).registry().forEach((type, o) -> {
            ElementFunctions functions = MagickRegistry.<ElementFunctions>getRegistry(LibRegistry.ELEMENT_FUNCTION).registry().get(type);
            functions.add(ApplyType.SPAWN_ENTITY, MagickReleaseHelper::spawnEntity);
            switch (type) {
                case LibElements.ORIGIN:
                    functions.add(ApplyType.ATTACK, OriginAbility::damageEntity);
                    break;
                case LibElements.ARC:
                    functions.add(ApplyType.ATTACK, ArcAbility::damageEntity);
                    functions.add(ApplyType.BUFF, ArcAbility::applyBuff);
                    functions.add(ApplyType.HIT_ENTITY, ArcAbility::hitEntity);
                    functions.add(ApplyType.DE_BUFF, ArcAbility::applyDebuff);
                    functions.add(ApplyType.HIT_BLOCK, ArcAbility::hitBlock);
                    functions.add(ApplyType.SUPER, ArcAbility::superEntity);
                    functions.add(ApplyType.DIFFUSION, ArcAbility::diffusion);
                    functions.add(ApplyType.AGGLOMERATE, ArcAbility::agglomerate);
                    break;
                case LibElements.SOLAR:
                    functions.add(ApplyType.ATTACK, SolarAbility::damageEntity);
                    functions.add(ApplyType.BUFF, SolarAbility::applyBuff);
                    functions.add(ApplyType.HIT_ENTITY, SolarAbility::hitEntity);
                    functions.add(ApplyType.DE_BUFF, SolarAbility::applyDebuff);
                    functions.add(ApplyType.HIT_BLOCK, SolarAbility::hitBlock);
                    functions.add(ApplyType.SUPER, SolarAbility::superEntity);
                    functions.add(ApplyType.DIFFUSION, SolarAbility::diffusion);
                    functions.add(ApplyType.AGGLOMERATE, SolarAbility::agglomerate);
                    break;
                case LibElements.VOID:
                    functions.add(ApplyType.ATTACK, VoidAbility::damageEntity);
                    functions.add(ApplyType.BUFF, VoidAbility::applyBuff);
                    functions.add(ApplyType.HIT_ENTITY, VoidAbility::hitEntity);
                    functions.add(ApplyType.DE_BUFF, VoidAbility::applyDebuff);
                    functions.add(ApplyType.HIT_BLOCK, VoidAbility::hitBlock);
                    functions.add(ApplyType.ELEMENT_TOOL, VoidAbility::applyToolElement);
                    functions.add(ApplyType.SUPER, VoidAbility::superEntity);
                    functions.add(ApplyType.DIFFUSION, VoidAbility::diffusion);
                    functions.add(ApplyType.AGGLOMERATE, VoidAbility::agglomerate);
                    break;
                case LibElements.STASIS:
                    functions.add(ApplyType.ATTACK, StasisAbility::damageEntity);
                    functions.add(ApplyType.BUFF, StasisAbility::applyBuff);
                    functions.add(ApplyType.HIT_ENTITY, StasisAbility::hitEntity);
                    functions.add(ApplyType.DE_BUFF, StasisAbility::applyDebuff);
                    functions.add(ApplyType.HIT_BLOCK, StasisAbility::hitBlock);
                    functions.add(ApplyType.ELEMENT_TOOL, StasisAbility::applyToolElement);
                    functions.add(ApplyType.SUPER, StasisAbility::superEntity);
                    functions.add(ApplyType.DIFFUSION, StasisAbility::diffusion);
                    functions.add(ApplyType.AGGLOMERATE, StasisAbility::agglomerate);
                    break;
                case LibElements.TAKEN:
                    functions.add(ApplyType.ATTACK, TakenAbility::damageEntity);
                    functions.add(ApplyType.BUFF, TakenAbility::applyBuff);
                    functions.add(ApplyType.HIT_ENTITY, TakenAbility::hitEntity);
                    functions.add(ApplyType.DE_BUFF, TakenAbility::applyDebuff);
                    functions.add(ApplyType.HIT_BLOCK, TakenAbility::hitBlock);
                    functions.add(ApplyType.SUPER, TakenAbility::superEntity);
                    functions.add(ApplyType.AGGLOMERATE, TakenAbility::agglomerate);
                    break;
                case LibElements.WITHER:
                    functions.add(ApplyType.ATTACK, WitherAbility::damageEntity);
                    functions.add(ApplyType.BUFF, WitherAbility::applyBuff);
                    functions.add(ApplyType.HIT_ENTITY, WitherAbility::hitEntity);
                    functions.add(ApplyType.DE_BUFF, WitherAbility::applyDebuff);
                    functions.add(ApplyType.HIT_BLOCK, WitherAbility::hitBlock);
                    functions.add(ApplyType.ELEMENT_TOOL, WitherAbility::applyToolElement);
                    functions.add(ApplyType.SUPER, WitherAbility::superEntity);
                    functions.add(ApplyType.DIFFUSION, WitherAbility::diffusion);
            }
        });

        ObjectRegistry<Callable<ChildContext>> childContexts = new ObjectRegistry<>(LibRegistry.CHILD_CONTEXT);
        childContexts.register(LibContext.SPAWN, SpawnContext::new);
        childContexts.register(LibContext.TRACE, TraceContext::new);
        childContexts.register(LibContext.POSITION, PositionContext::new);
        childContexts.register(LibContext.ITEM, ItemContext::new);
        childContexts.register(LibContext.CONDITION, ConditionContext::new);
        childContexts.register(LibContext.DIRECTION, DirectionContext::new);
        childContexts.register(LibContext.MULTI_RELEASE, MultiReleaseContext::new);
        childContexts.register(LibContext.APPLY_TYPE, ExtraApplyTypeContext::new);

        ObjectRegistry<Callable<Condition>> conditions = new ObjectRegistry<>(LibRegistry.CONDITION);
        conditions.register(LibConditions.ALWAYS, AlwaysCondition::new);
        conditions.register(LibConditions.ENTITY_TYPE, EntityTypeCondition::new);
        conditions.register(LibConditions.INJURABLE, InjurableEntityCondition::new);
        conditions.register(LibConditions.LIVING_ENTITY, LivingEntityCondition::new);
        conditions.register(LibConditions.HEALTH, HealthCondition::new);
        conditions.register(LibConditions.DISTANCE, DistanceCondition::new);
    }
}
