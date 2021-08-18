package com.rogoshum.magickcore.enums;

import com.rogoshum.magickcore.MagickCore;

import java.util.Objects;

public class EnumManaType {
    private static EnumManaType[] values = {};
    public static final EnumManaType NONE = new EnumManaType("none");
    public static final EnumManaType ATTACK = new EnumManaType("attack");
    public static final EnumManaType BUFF = new EnumManaType("buff");
    public static final EnumManaType DEBUFF = new EnumManaType("debuff");
    public static final EnumManaType HIT = new EnumManaType("hit");

    private String label;

    public EnumManaType(String label) {
        this.label = label;
        EnumManaType[] values = new EnumManaType[1 + EnumManaType.values.length];
        int i = 0;
        for (EnumManaType manaType : EnumManaType.values) {
            values[i] = manaType;
            ++i;
        }
        values[i] = this;
        EnumManaType.values = values;
    }

    public String getLabel() {
        return label;
    }

    public static EnumManaType getEnum(String s) {
        for (EnumManaType type : EnumManaType.values) {
            if (type.label.equals(s))
                return type;
        }

        return null;
    }

    public static EnumManaType getRandomEnum() {
        int ran = MagickCore.rand.nextInt(values.length - 1) + 1;
        return EnumManaType.values[ran];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnumManaType manaType = (EnumManaType) o;
        return Objects.equals(label, manaType.label);
    }

    public EnumManaType[] values() {
        return values;
    }
}
