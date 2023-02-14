package com.rogoshum.magickcore.common.recipe;

import java.util.function.Consumer;

public class SpawnResult {
    private final Consumer<SpawnContext> spawnContext;

    private SpawnResult(Consumer<SpawnContext> spawnContext) {
        this.spawnContext = spawnContext;
    }

    public static SpawnResult create(Consumer<SpawnContext> spawnContext) {
        return new SpawnResult(spawnContext);
    }

    public void craft(SpawnContext spawnContext) {
        this.spawnContext.accept(spawnContext);
    }
}
