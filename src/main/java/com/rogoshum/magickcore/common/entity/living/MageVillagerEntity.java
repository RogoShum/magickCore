package com.rogoshum.magickcore.common.entity.living;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Queues;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.rogoshum.magickcore.api.entity.IManaTaskMob;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModVillager;
import com.rogoshum.magickcore.common.magick.condition.AlwaysCondition;
import com.rogoshum.magickcore.common.magick.condition.DistanceCondition;
import com.rogoshum.magickcore.common.magick.condition.HealthCondition;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.entity.ai.task.MagickAttackTask;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;


import java.util.HashMap;
import java.util.Queue;

public class MageVillagerEntity extends Villager implements IManaTaskMob {
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY);
    private static final ImmutableList<SensorType<? extends Sensor<? super Villager>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY, ModVillager.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_DETECTED);

    private HashMap<Activity, Queue<SpellContext>> spellMap = new HashMap<>();
    public MageVillagerEntity(EntityType<? extends MageVillagerEntity> type, Level worldIn) {
        super(type, worldIn);
        initSpellMap();
    }

    protected Brain.Provider<Villager> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    public void setVillagerData(VillagerData data) {
        data = data.setProfession(ModVillager.MAGE);
        super.setVillagerData(data);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamicIn) {
        Brain<Villager> brain = this.brainProvider().makeBrain(dynamicIn);
        this.initBrain(brain);
        initSpellMap();
        return brain;
    }

    public void initBrain(Brain<Villager> villagerBrain) {
        VillagerProfession villagerprofession = this.getVillagerData().getProfession();
        if (this.isBaby()) {
            villagerBrain.setSchedule(Schedule.VILLAGER_BABY);
            villagerBrain.addActivity(Activity.PLAY, VillagerGoalPackages.getPlayPackage(0.5F));
        } else {
            villagerBrain.setSchedule(Schedule.VILLAGER_DEFAULT);
            villagerBrain.addActivityWithConditions(Activity.WORK, VillagerGoalPackages.getWorkPackage(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
        }
        MagickAttackTask<LivingEntity> attackTask = isBaby() ? new MagickAttackTask<>(15, 8) : new MagickAttackTask<>(40, 16);
        villagerBrain.addActivity(Activity.FIGHT, 0, ImmutableList.of(new Swim(0.8F)));
        villagerBrain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 0, ImmutableList.of(attackTask), MemoryModuleType.ATTACK_TARGET);
        villagerBrain.addActivity(Activity.FIGHT, 0, ImmutableList.of(new InteractWithDoor()));
        villagerBrain.addActivity(Activity.FIGHT, 0, ImmutableList.of(new BackUpIfTooClose<>(5, 0.75F)));
        villagerBrain.addActivity(Activity.FIGHT, 0, ImmutableList.of(new LookAtTargetSink(45, 90)));
        villagerBrain.addActivity(Activity.FIGHT, 0, ImmutableList.of(new MoveToTargetSink()));
        villagerBrain.addActivity(Activity.FIGHT, 0, ImmutableList.of(new LookAndFollowTradingPlayerSink(0.5F)));

        villagerBrain.setCoreActivities(ImmutableSet.of(Activity.FIGHT));
        villagerBrain.updateActivityFromSchedule(this.level.getDayTime(), this.level.getGameTime());
    }

    @Override
    public void refreshBrain( ServerLevel serverLevelIn) {
        Brain<Villager> brain = this.getBrain();
        brain.stopAll(serverLevelIn, this);
        this.brain = brain.copyWithoutBehaviors();
        this.initBrain(this.getBrain());
    }

    @Override
    public void initSpellMap() {
        conditionSpellMap().clear();
        Queue<SpellContext> attackContext = Queues.newArrayDeque();
        SpellContext context = SpellContext.create().applyType(ApplyType.BUFF)
                .tick(40).force(5).element(MagickRegistry.getElement(LibElements.SOLAR))
                .addChild(ConditionContext.create(HealthCondition.create(0.7f)
                        .percentage(true).compare(HealthCondition.Compare.LESS_EQUAL)));
        SpellContext post = SpellContext.create().applyType(ApplyType.DIFFUSION)
                .tick(120).force(5).element(MagickRegistry.getElement(LibElements.STASIS));
        context.post(post);
        attackContext.add(context);
        conditionSpellMap().put(Activity.REST, attackContext);


        attackContext = Queues.newArrayDeque();
        context = SpellContext.create().applyType(ApplyType.SPAWN_ENTITY)
                .range(5).tick(40).element(MagickRegistry.getElement(LibElements.STASIS))
                .addChild(SpawnContext.create(ModEntities.SPHERE.get()))
                .addChild(ConditionContext.create(new DistanceCondition().distance(4.0d)))
                .post(SpellContext.create().applyType(ApplyType.ATTACK)
                        .force(20).tick(400).element(MagickRegistry.getElement(LibElements.STASIS)));

        attackContext.add(context);
        context = SpellContext.create().applyType(ApplyType.ATTACK)
                .force(3).tick(40).element(MagickRegistry.getElement(LibElements.VOID));
        SpellContext deBuffContext = SpellContext.create().applyType(ApplyType.DE_BUFF)
                .force(5).tick(20).element(MagickRegistry.getElement(LibElements.WITHER));
        context.post(deBuffContext);
        SpellContext orbContext = SpellContext.create().applyType(ApplyType.SPAWN_ENTITY)
                .addChild(SpawnContext.create(ModEntities.MANA_LASER.get()))
                .tick(200).addChild(ConditionContext.create(AlwaysCondition.ALWAYS)).addChild(new TraceContext()).element(MagickRegistry.getElement(LibElements.VOID));
        orbContext.post(context);
        attackContext.add(orbContext);
        conditionSpellMap().put(Activity.FIGHT, attackContext);
    }

    @Override
    protected void updateTrades() {
        VillagerData villagerdata = this.getVillagerData();
        Int2ObjectMap<VillagerTrades.ItemListing[]> int2objectmap = VillagerTrades.TRADES.get(villagerdata.getProfession());
        if (int2objectmap != null && !int2objectmap.isEmpty()) {
            VillagerTrades.ItemListing[] avillagertrades$itrade = int2objectmap.get(villagerdata.getLevel());
            if (avillagertrades$itrade != null) {
                MerchantOffers merchantoffers = this.getOffers();
                this.addOffersFromItemListings(merchantoffers, avillagertrades$itrade, 6);
            }
        }
    }

    @Override
    public HashMap<Activity, Queue<SpellContext>> conditionSpellMap() {
        if(spellMap == null) {
            spellMap = new HashMap<>();
        }

        return spellMap;
    }
}
