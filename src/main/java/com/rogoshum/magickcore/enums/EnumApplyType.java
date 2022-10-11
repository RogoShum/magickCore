package com.rogoshum.magickcore.enums;

import com.rogoshum.magickcore.MagickCore;

import java.util.Objects;

public class EnumApplyType {
    private static EnumApplyType[] values = {};
    public static final EnumApplyType NONE = new EnumApplyType("none", false);
    public static final EnumApplyType ATTACK = new EnumApplyType("attack", false);
    public static final EnumApplyType BUFF = new EnumApplyType("buff", true);
    public static final EnumApplyType DE_BUFF = new EnumApplyType("de_buff", false);
    public static final EnumApplyType HIT_ENTITY = new EnumApplyType("hit_entity", false);
    public static final EnumApplyType HIT_BLOCK = new EnumApplyType("hit_block", false);
    public static final EnumApplyType SPAWN_ENTITY = new EnumApplyType("spawn_entity", false);
    public static final EnumApplyType ELEMENT_TOOL = new EnumApplyType("element_tool", true);

    private final String label;
    private final boolean beneficial;

    public EnumApplyType(String label, boolean beneficial) {
        this.label = label;
        this.beneficial = beneficial;
        EnumApplyType[] values = new EnumApplyType[1 + EnumApplyType.values.length];
        int i = 0;
        for (EnumApplyType manaType : EnumApplyType.values) {
            values[i] = manaType;
            ++i;
        }
        values[i] = this;
        EnumApplyType.values = values;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public String getLabel() {
        return label;
    }

    public boolean isBeneficial() {
        return beneficial;
    }

    public static EnumApplyType getEnum(String s) {
        for (EnumApplyType type : EnumApplyType.values) {
            if (type.label.equals(s))
                return type;
        }

        return NONE;
    }

    public static EnumApplyType getRandomEnum() {
        int ran = MagickCore.rand.nextInt(values.length - 1) + 1;
        return EnumApplyType.values[ran];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnumApplyType manaType = (EnumApplyType) o;
        return Objects.equals(label, manaType.label);
    }

    public EnumApplyType[] values() {
        return values;
    }
}
