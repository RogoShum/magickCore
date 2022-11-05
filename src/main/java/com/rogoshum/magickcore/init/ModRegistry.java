package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.enums.ApplyType;
import com.rogoshum.magickcore.lib.LibConditions;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibRegistry;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.ability.*;
import com.rogoshum.magickcore.magick.condition.*;
import com.rogoshum.magickcore.magick.context.child.*;
import com.rogoshum.magickcore.registry.MagickRegistry;
import com.rogoshum.magickcore.registry.ObjectRegistry;
import com.rogoshum.magickcore.registry.elementmap.ElementFunctions;

import java.util.concurrent.Callable;

public class ModRegistry {
    public static void init() {
        MagickRegistry.<MagickElement>getRegistry(LibRegistry.ELEMENT).registry().forEach((type, o) -> {
            ElementFunctions functions = MagickRegistry.<ElementFunctions>getRegistry(LibRegistry.ELEMENT_FUNCTION).registry().get(type);
            functions.add(ApplyType.SPAWN_ENTITY, MagickReleaseHelper::spawnEntity);
            switch (type) {
                case LibElements.ORIGIN:
                    functions.add(ApplyType.ATTACK, OriginElement::damageEntity);
                    break;
                case LibElements.ARC:
                    functions.add(ApplyType.ATTACK, ArcAbility::damageEntity);
                    functions.add(ApplyType.BUFF, ArcAbility::applyBuff);
                    functions.add(ApplyType.HIT_ENTITY, ArcAbility::hitEntity);
                    functions.add(ApplyType.DE_BUFF, ArcAbility::applyDebuff);
                    functions.add(ApplyType.HIT_BLOCK, ArcAbility::hitBlock);
                    functions.add(ApplyType.ELEMENT_TOOL, ArcAbility::applyToolElement);
                    break;
                case LibElements.SOLAR:
                    functions.add(ApplyType.ATTACK, SolarAbility::damageEntity);
                    functions.add(ApplyType.BUFF, SolarAbility::applyBuff);
                    functions.add(ApplyType.HIT_ENTITY, SolarAbility::hitEntity);
                    functions.add(ApplyType.DE_BUFF, SolarAbility::applyDebuff);
                    functions.add(ApplyType.HIT_BLOCK, SolarAbility::hitBlock);
                    break;
                case LibElements.VOID:
                    functions.add(ApplyType.ATTACK, VoidAbility::damageEntity);
                    functions.add(ApplyType.BUFF, VoidAbility::applyBuff);
                    functions.add(ApplyType.HIT_ENTITY, VoidAbility::hitEntity);
                    functions.add(ApplyType.DE_BUFF, VoidAbility::applyDebuff);
                    functions.add(ApplyType.HIT_BLOCK, VoidAbility::hitBlock);
                    functions.add(ApplyType.ELEMENT_TOOL, VoidAbility::applyToolElement);
                    break;
                case LibElements.STASIS:
                    functions.add(ApplyType.ATTACK, StasisAbility::damageEntity);
                    functions.add(ApplyType.BUFF, StasisAbility::applyBuff);
                    functions.add(ApplyType.HIT_ENTITY, StasisAbility::hitEntity);
                    functions.add(ApplyType.DE_BUFF, StasisAbility::applyDebuff);
                    functions.add(ApplyType.HIT_BLOCK, StasisAbility::hitBlock);
                    functions.add(ApplyType.ELEMENT_TOOL, StasisAbility::applyToolElement);
                    break;
                case LibElements.TAKEN:
                    functions.add(ApplyType.ATTACK, TakenAbility::damageEntity);
                    functions.add(ApplyType.BUFF, TakenAbility::applyBuff);
                    functions.add(ApplyType.HIT_ENTITY, TakenAbility::hitEntity);
                    functions.add(ApplyType.DE_BUFF, TakenAbility::applyDebuff);
                    functions.add(ApplyType.HIT_BLOCK, TakenAbility::hitBlock);
                    break;
                case LibElements.WITHER:
                    functions.add(ApplyType.ATTACK, WitherAbility::damageEntity);
                    functions.add(ApplyType.BUFF, WitherAbility::applyBuff);
                    functions.add(ApplyType.HIT_ENTITY, WitherAbility::hitEntity);
                    functions.add(ApplyType.DE_BUFF, WitherAbility::applyDebuff);
                    functions.add(ApplyType.HIT_BLOCK, WitherAbility::hitBlock);
                    functions.add(ApplyType.ELEMENT_TOOL, WitherAbility::applyToolElement);
                case LibElements.AIR:
                    functions.add(ApplyType.ATTACK, AirAbility::damageEntity);
                    functions.add(ApplyType.BUFF, AirAbility::applyBuff);
                    functions.add(ApplyType.HIT_ENTITY, AirAbility::hitEntity);
                    functions.add(ApplyType.DE_BUFF, AirAbility::applyDebuff);
                    functions.add(ApplyType.HIT_BLOCK, AirAbility::hitBlock);
                    functions.add(ApplyType.ELEMENT_TOOL, AirAbility::applyToolElement);
                    break;
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

        ObjectRegistry<Callable<Condition>> conditions = new ObjectRegistry<>(LibRegistry.CONDITION);
        conditions.register(LibConditions.ALWAYS, AlwaysCondition::new);
        conditions.register(LibConditions.ENTITY_TYPE, EntityTypeCondition::new);
        conditions.register(LibConditions.INJURABLE, InjurableEntityCondition::new);
        conditions.register(LibConditions.LIVING_ENTITY, LivingEntityCondition::new);
        conditions.register(LibConditions.HEALTH, HealthCondition::new);
        conditions.register(LibConditions.DISTANCE, DistanceCondition::new);
    }
}
