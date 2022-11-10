package com.rogoshum.magickcore.common.entity.living;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Queues;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.rogoshum.magickcore.common.api.entity.IManaTaskMob;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModVillager;
import com.rogoshum.magickcore.common.magick.condition.AlwaysCondition;
import com.rogoshum.magickcore.common.magick.condition.DistanceCondition;
import com.rogoshum.magickcore.common.magick.condition.HealthCondition;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.entity.ai.task.MagickAttackTask;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Queue;

public class MageVillagerEntity extends VillagerEntity implements IManaTaskMob {
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.OPENED_DOORS, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY);
    private static final ImmutableList<SensorType<? extends Sensor<? super VillagerEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY, ModVillager.VILLAGER_HOSTILES.get(), SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_DETECTED);

    private HashMap<Activity, Queue<SpellContext>> spellMap = new HashMap<>();
    public MageVillagerEntity(EntityType<? extends MageVillagerEntity> type, World worldIn) {
        super(type, worldIn);
        initSpellMap();
    }

    protected Brain.BrainCodec<VillagerEntity> getBrainCodec() {
        return Brain.createCodec(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    public void setVillagerData(VillagerData data) {
        data = data.withProfession(ModVillager.MAGE.get());
        super.setVillagerData(data);
    }

    @Nonnull
    @Override
    protected Brain<?> createBrain(@Nonnull Dynamic<?> dynamicIn) {
        Brain<VillagerEntity> brain = this.getBrainCodec().deserialize(dynamicIn);
        this.initBrain(brain);
        initSpellMap();
        return brain;
    }

    public void initBrain(Brain<VillagerEntity> villagerBrain) {
        VillagerProfession villagerprofession = this.getVillagerData().getProfession();
        if (this.isChild()) {
            villagerBrain.setSchedule(Schedule.VILLAGER_BABY);
            villagerBrain.registerActivity(Activity.PLAY, VillagerTasks.play(0.5F));
        } else {
            villagerBrain.setSchedule(Schedule.VILLAGER_DEFAULT);
            villagerBrain.registerActivity(Activity.WORK, VillagerTasks.work(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_PRESENT)));
        }
        MagickAttackTask<LivingEntity> attackTask = isChild() ? new MagickAttackTask<>(15, 8) : new MagickAttackTask<>(40, 16);

        villagerBrain.registerActivity(Activity.FIGHT, 0, ImmutableList.of(new SwimTask(0.8F)));
        villagerBrain.registerActivity(Activity.FIGHT, 0, ImmutableList.of(attackTask), MemoryModuleType.ATTACK_TARGET);
        villagerBrain.registerActivity(Activity.FIGHT, 0, ImmutableList.of(new InteractWithDoorTask()));
        villagerBrain.registerActivity(Activity.FIGHT, 0, ImmutableList.of(new AttackStrafingTask<>(5, 0.75F)));
        villagerBrain.registerActivity(Activity.FIGHT, 0, ImmutableList.of(new LookTask(45, 90)));
        villagerBrain.registerActivity(Activity.FIGHT, 0, ImmutableList.of(new WalkToTargetTask()));
        villagerBrain.registerActivity(Activity.FIGHT, 0, ImmutableList.of(new TradeTask(0.5F)));

        villagerBrain.setDefaultActivities(ImmutableSet.of(Activity.FIGHT));
        villagerBrain.updateActivity(this.world.getDayTime(), this.world.getGameTime());
    }

    @Override
    public void resetBrain(@Nonnull ServerWorld serverWorldIn) {
        Brain<VillagerEntity> brain = this.getBrain();
        brain.stopAllTasks(serverWorldIn, this);
        this.brain = brain.copy();
        this.initBrain(this.getBrain());
    }

    @Override
    public void initSpellMap() {
        conditionSpellMap().clear();
        Queue<SpellContext> attackContext = Queues.newArrayDeque();
        SpellContext context = SpellContext.create().applyType(ApplyType.BUFF)
                .tick(2).element(MagickRegistry.getElement(LibElements.SOLAR))
                .addChild(ConditionContext.create(HealthCondition.create(0.7f)
                        .percentage(true).compare(HealthCondition.Compare.LESS_EQUAL)));
        attackContext.add(context);
        conditionSpellMap().put(Activity.REST, attackContext);
        attackContext = Queues.newArrayDeque();
        context = SpellContext.create().applyType(ApplyType.ATTACK)
                .force(4).tick(200).element(MagickRegistry.getElement(LibElements.STASIS))
                .addChild(ConditionContext.create(new DistanceCondition().distance(4.0d)));
        attackContext.add(context);
        context = SpellContext.create().applyType(ApplyType.ATTACK)
                .force(3).tick(40).element(MagickRegistry.getElement(LibElements.VOID));
        SpellContext orbContext = SpellContext.create().applyType(ApplyType.SPAWN_ENTITY)
                .addChild(SpawnContext.create(ModEntities.MANA_LASER.get()))
                .tick(200).addChild(ConditionContext.create(AlwaysCondition.ALWAYS)).addChild(new TraceContext()).element(MagickRegistry.getElement(LibElements.VOID));
        orbContext.post(context);
        attackContext.add(orbContext);
        conditionSpellMap().put(Activity.FIGHT, attackContext);
    }

    @Override
    public HashMap<Activity, Queue<SpellContext>> conditionSpellMap() {
        if(spellMap == null) {
            spellMap = new HashMap<>();
        }

        return spellMap;
    }
}
