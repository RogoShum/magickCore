package com.rogoshum.magickcore.enums;

public enum EnumTargetType {
    NONE("none"),
    PROJECTILE("paojectile"),
    POINT("point"),
    SIGHT("sight"),
    SEIF("seif");

    private String label;

    private EnumTargetType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static EnumTargetType getEnum(String s)
    {
        for(EnumTargetType type : EnumTargetType.values())
        {
            if(type.label.equals(s))
                return type;
        }

        return null;
    }
}
