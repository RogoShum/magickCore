package com.rogoshum.magickcore.api;

import com.rogoshum.magickcore.capability.IManaData;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IMagickElementObject {
    @Nullable
    public IManaData getManaData();

    public IManaElement getElement();
    public void setElement(IManaElement manaElement);

    public float getRange();
    public void setRange(float range);

    public float getForce();
    public void setForce(float force);

    public EnumTargetType getTargetType();
    public void setTargetType(EnumTargetType targetType);

    public EnumManaType getManaType();
    public void setManaType(EnumManaType manaType);

    public int getTickTime();
    public void setTickTime(int tick);

    public UUID getTraceTarget();
    public void setTraceTarget(UUID traceTarget);

    public void hitMixing(IMagickElementObject a);
}
