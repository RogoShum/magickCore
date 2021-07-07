package com.rogoshum.magickcore.api;

import com.rogoshum.magickcore.MagickCore;

public enum EnumManaType {
    NONE("none"),
    ATTACK("attack"),
    BUFF("buff"),
    DEBUFF("debuff");

    private String label;

    private EnumManaType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static EnumManaType getEnum(String s)
    {
        for(EnumManaType type : EnumManaType.values())
        {
            if(type.label.equals(s))
                return type;
        }

        return null;
    }

    public static EnumManaType getRandomEnum()
    {
        int ran = MagickCore.rand.nextInt(EnumManaType.values().length - 1) + 1;
        return EnumManaType.values()[ran];
    }
}
