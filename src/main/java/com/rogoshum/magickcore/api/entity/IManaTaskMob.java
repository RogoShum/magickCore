package com.rogoshum.magickcore.api.entity;

import com.rogoshum.magickcore.api.magick.context.SpellContext;
import net.minecraft.world.entity.schedule.Activity;

import java.util.HashMap;
import java.util.Queue;

public interface IManaTaskMob {
    void initSpellMap();

    HashMap<Activity, Queue<SpellContext>> conditionSpellMap();
}
