package com.rogoshum.magickcore.magick.lifestate;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.entity.projectile.LifeStateEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.concurrent.Callable;

public abstract class LifeState<T> {
    public static final String ELEMENT = MagickCore.Data + "_ELEMENT";
    public static final String ITEM = MagickCore.Data + "_ITEM";
    public static final String ENTITY = MagickCore.Data + "_ENTITY";
    public static final String POTION = MagickCore.Data + "_POTION";
    public static final String MANA_STATE = MagickCore.Data + "_MANA_STATE";
    public static final String ENTITY_SELECTOR = MagickCore.Data + "_ENTITY_SELECTOR";

    private static final HashMap<String, Callable<? extends LifeState<?>>> stateMap = new HashMap<>();

    protected T value;

    public T getValue() {
        return value;
    }

    public LifeState<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public abstract INBT serialize();

    public abstract void deserialize(INBT value, World world);

    public void onHitEntity(LifeStateEntity lifeState, EntityRayTraceResult result){};

    public void onHitBlock(LifeStateEntity lifeState, BlockRayTraceResult result){};

    public void tick(LifeStateEntity lifeState){};

    public static void init(){
        registerLifeState(ENTITY, EntityLifeState::new);
        registerLifeState(ELEMENT, ElementLifeState::new);
        registerLifeState(ITEM, ItemStackLifeState::new);
        registerLifeState(POTION, PotionLifeState::new);
        registerLifeState(MANA_STATE, ManaLifeState::new);
        registerLifeState(ENTITY_SELECTOR, EntitySelectorLifeState::new);
    }

    public static LifeState<?> createByName(String name){
        LifeState<?> lifeState = null;
        if(stateMap.containsKey(name)) {
            try {
                lifeState = stateMap.getOrDefault(name, null).call();
            } catch (Exception e) {
                MagickCore.LOGGER.warn("Can't get LifeState :{}.", name);
            }
        }
        return lifeState;
    }

    public static void registerLifeState(String name, Callable<? extends LifeState<?>> life){
        if(!stateMap.containsKey(name))
        {
            stateMap.put(name, life);
        }
        else try {
            throw new Exception("Containing same LifeState = [" + name +"]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
