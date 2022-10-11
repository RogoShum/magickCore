package com.rogoshum.magickcore.magick.lifestate;

import com.rogoshum.magickcore.entity.projectile.LifeStateEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

import java.util.HashMap;

public class LifeStateCarrier {
    private HashMap<String, LifeState<?>> lifeStates = new HashMap<>();
    private final static String TAG = "LIFE_STATE";

    public static LifeStateCarrier create() {
        return new LifeStateCarrier();
    }

    public void serialize(CompoundNBT nbt) {
        CompoundNBT tag = new CompoundNBT();
        lifeStates.forEach((s, life) -> {
            tag.put(s, life.serialize());
        });
        nbt.put(TAG, tag);
    }

    public void deserialize(CompoundNBT nbt, World world) {
        if (nbt.contains(TAG)) {
            CompoundNBT tag = nbt.getCompound(TAG);
            tag.keySet().forEach(key -> {
                INBT inbt = tag.get(key);
                LifeState<?> lifeState = LifeState.createByName(key);
                lifeState.deserialize(inbt, world);
                lifeStates.put(key, lifeState);
            });
        }
    }

    public void onHitEntity(LifeStateEntity lifeState, EntityRayTraceResult result){
        lifeStates.values().forEach(state -> state.onHitEntity(lifeState, result));
    }

    public void onHitBlock(LifeStateEntity lifeState, BlockRayTraceResult result){
        lifeStates.values().forEach(state -> state.onHitBlock(lifeState, result));
    }

    public void tick(LifeStateEntity lifeState){
        lifeStates.values().forEach(state -> state.tick(lifeState));
    }

    public void addState(String name, LifeState<?> state) {
        if (!lifeStates.containsKey(name)) {
            lifeStates.put(name, state);
        }
    }

    public void removeState(String name) {
        lifeStates.remove(name);
    }

    public boolean hasState(String name) {
        return lifeStates.containsKey(name);
    }

    public LifeState<?> getState(String name) {
        if (lifeStates.containsKey(name)) {
            return lifeStates.get(name);
        }

        return null;
    }

    public HashMap<String, LifeState<?>> getLifeStates() {
        return this.lifeStates;
    }

    public void copyOf(LifeStateCarrier carrier) {
        this.lifeStates = (HashMap<String, LifeState<?>>) carrier.lifeStates.clone();
    }
}
