package com.rogoshum.magickcore.common.api.entity;

import com.rogoshum.magickcore.common.magick.context.SpellContext;
import net.minecraft.entity.ai.brain.schedule.Activity;

import java.util.HashMap;
import java.util.Queue;

public interface IManaTaskMob {
    void initSpellMap();

    HashMap<Activity, Queue<SpellContext>> conditionSpellMap();
}
