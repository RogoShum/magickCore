package com.rogoshum.magickcore.api;

public enum EnumParticleType {
    PARTICLE("particle"),
    MIST("mist"),
    TRAIL("trail"),
    ORB("orb");

    private String type;

    private EnumParticleType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
