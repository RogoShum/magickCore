package com.rogoshum.magickcore.api.entity;

import com.rogoshum.magickcore.enums.ApplyType;
import com.rogoshum.magickcore.magick.context.SpellContext;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.merchant.villager.VillagerEntity;

import java.util.HashMap;
import java.util.Queue;

public interface IManaTaskMob {
    void initSpellMap();

    HashMap<Activity, Queue<SpellContext>> conditionSpellMap();
}
