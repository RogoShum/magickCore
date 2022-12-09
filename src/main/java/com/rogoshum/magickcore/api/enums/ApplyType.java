package com.rogoshum.magickcore.api.enums;

import com.rogoshum.magickcore.MagickCore;

import java.util.Objects;

public class ApplyType {
    private static ApplyType[] values = {};
    public static final ApplyType NONE = new ApplyType("none", Beneficial.HARMLESS, false, false);
    public static final ApplyType ATTACK = new ApplyType("attack", Beneficial.HARMFUL, false, true);
    public static final ApplyType BUFF = new ApplyType("buff", Beneficial.BENEFICIAL, false, true);
    public static final ApplyType DE_BUFF = new ApplyType("de_buff", Beneficial.HARMFUL, false, true);
    public static final ApplyType HIT_ENTITY = new ApplyType("hit_entity", Beneficial.HARMFUL, false, false);
    public static final ApplyType HIT_BLOCK = new ApplyType("hit_block", Beneficial.HARMLESS, false, false);
    public static final ApplyType SPAWN_ENTITY = new ApplyType("spawn_entity", Beneficial.HARMFUL, true, false);
    public static final ApplyType ELEMENT_TOOL = new ApplyType("element_tool", Beneficial.BENEFICIAL, false, false);
    public static final ApplyType DIFFUSION = new ApplyType("diffusion", Beneficial.HARMLESS, false, true);
    public static final ApplyType AGGLOMERATE = new ApplyType("agglomerate", Beneficial.HARMLESS, false, true);
    public static final ApplyType SUPER = new ApplyType("super", Beneficial.BENEFICIAL, true, false);
    public static final ApplyType POTION = new ApplyType("potion", Beneficial.HARMLESS, false, true);

    private final String label;
    private final Beneficial beneficial;
    private final boolean form;
    private final boolean continueCast;

    public ApplyType(String label, Beneficial beneficial, boolean isForm, boolean continueCast) {
        this.label = label;
        this.beneficial = beneficial;
        this.form = isForm;
        this.continueCast = continueCast;
        ApplyType[] values = new ApplyType[1 + ApplyType.values.length];
        int i = 0;
        for (ApplyType manaType : ApplyType.values) {
            values[i] = manaType;
            ++i;
        }
        values[i] = this;
        ApplyType.values = values;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public String getLabel() {
        return label;
    }

    public Beneficial getBeneficial() {
        return beneficial;
    }

    public boolean isForm() {
        return form;
    }

    public boolean continueCast() {
        return continueCast;
    }

    public static ApplyType getEnum(String s) {
        for (ApplyType type : ApplyType.values) {
            if (type.label.equals(s))
                return type;
        }

        return NONE;
    }

    public static ApplyType getRandomEnum() {
        int ran = MagickCore.rand.nextInt(values.length - 1) + 1;
        return ApplyType.values[ran];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplyType manaType = (ApplyType) o;
        return Objects.equals(label, manaType.label);
    }

    public static ApplyType[] values() {
        return values;
    }

    public enum Beneficial {
        BENEFICIAL,
        HARMFUL,
        HARMLESS
    }
}
