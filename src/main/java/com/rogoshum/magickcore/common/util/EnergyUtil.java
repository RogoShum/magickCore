package com.rogoshum.magickcore.common.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyUtil {
    public static Capability<IEnergyStorage> FORGE_ENERGY;

    public static int receiveEnergy(TileEntity tileEntity, int mana) {
        int left = receiveEnergy(tileEntity, mana, null);
        if(left <= 0)
            return 0;
        for (Direction direction : Direction.values()) {
            left = receiveEnergy(tileEntity, left, direction);
            if(left <= 0)
                return 0;
        }
        return left;
    }

    public static int receiveEnergy(TileEntity tileEntity, int mana, Direction side) {
        LazyOptional<IEnergyStorage> optional = tileEntity.getCapability(FORGE_ENERGY, side);
        if(optional.isPresent()) {
            return mana - optional.orElse(null).receiveEnergy(mana, false);
        }
        return mana;
    }

    public static int extractEnergy(TileEntity tileEntity, int mana) {
        int get = extractEnergy(tileEntity, mana, null);
        if(get >= mana)
            return get;
        for (Direction direction : Direction.values()) {
            get += extractEnergy(tileEntity, mana - get, direction);
            if(get >= mana)
                return get;
        }
        return get;
    }

    public static int extractEnergy(TileEntity tileEntity, int mana, Direction side) {
        LazyOptional<IEnergyStorage> optional = tileEntity.getCapability(FORGE_ENERGY, side);
        if(optional.isPresent()) {
            int energy = optional.orElse(null).extractEnergy(mana, false);
            if(energy > 0)
                return energy;
        }
        return 0;
    }

    @CapabilityInject(IEnergyStorage.class)
    private static void capIEnergyStorageRegistered(Capability<IEnergyStorage> cap) {
        FORGE_ENERGY = cap;
    }
}
