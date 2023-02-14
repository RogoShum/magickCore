package com.rogoshum.magickcore.common.magick;

public class ManaFactor {
    public static final ManaFactor NON_MANA = ManaFactor.create(0.0f, 0.0f, 0.0f);
    public static final ManaFactor DEFAULT = ManaFactor.create(1.0f, 1.0f, 1.0f);
    public static final ManaFactor POINT_DEFAULT = ManaFactor.create(0.2f, 1.0f, 0.2f);
    public static final ManaFactor RADIATE_DEFAULT = ManaFactor.create(0.5f, 1.0f, 1.0f);
    public final float force;
    public final float range;
    public final float tick;

    private ManaFactor(float force, float range, float tick) {
        this.force = force;
        this.range = range;
        this.tick = tick;
    }

    public static ManaFactor create(float force, float range, float tick) {
        return new ManaFactor(force, range, tick);
    }
}
