package com.rogoshum.magickcore.common.api.enums;

public enum ParticleType {
    PARTICLE("particle"),
    MIST("mist"),
    TRAIL("trail"),
    ORB("orb");

    private String type;

    private ParticleType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
