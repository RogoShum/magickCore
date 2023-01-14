package com.rogoshum.magickcore.common.util;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.impl.transfer.transaction.TransactionManagerImpl;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;

public class EnergyUtil {

    public static int receiveEnergy(BlockEntity tileEntity, int mana) {
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

    public static int receiveEnergy(BlockEntity tileEntity, int mana, Direction side) {
        EnergyStorage maybeStorage = EnergyStorage.SIDED.find(tileEntity.getLevel(), tileEntity.getBlockPos(), side);
        TransactionManagerImpl impl = TransactionManagerImpl.MANAGERS.get();
        if(impl.isOpen())
            return (int) (mana - maybeStorage.insert(mana, impl.getCurrentUnsafe()));
        else
            return (int) (mana - maybeStorage.insert(mana, impl.openOuter()));
    }

    public static int extractEnergy(BlockEntity tileEntity, int mana) {
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

    public static int extractEnergy(BlockEntity tileEntity, int mana, Direction side) {
        EnergyStorage maybeStorage = EnergyStorage.SIDED.find(tileEntity.getLevel(), tileEntity.getBlockPos(), side);
        TransactionManagerImpl impl = TransactionManagerImpl.MANAGERS.get();
        if(impl.isOpen())
            return (int) maybeStorage.extract(mana, impl.getCurrentUnsafe());
        else
            return (int) maybeStorage.extract(mana, impl.openOuter());
    }
}
