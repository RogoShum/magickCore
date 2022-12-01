package com.rogoshum.magickcore.common.recipe;

import com.rogoshum.magickcore.common.entity.PlaceableItemEntity;
import com.rogoshum.magickcore.common.util.MultiBlockUtil;

import java.util.Optional;

public class MagickCraftingRecipe {
    private final String[][][] recipe;
    private final MultiBlockUtil.PlaceableEntityPattern[] pattern;
    private final SpawnResult spawnResult;

    public MagickCraftingRecipe(String[][][] recipe, MultiBlockUtil.PlaceableEntityPattern[] pattern, SpawnResult spawnResult) {
        this.recipe = recipe;
        this.pattern = pattern;
        this.spawnResult = spawnResult;
    }

    public String[][][] getRecipe() {
        return recipe;
    }

    public MultiBlockUtil.PlaceableEntityPattern[] getPattern() {
        return pattern;
    }

    public boolean match(Optional<PlaceableItemEntity>[][][] structure) {
        if(structure.length < 1 || structure[0].length < 1 || structure[0][0].length < 1) return false;
        if(structure.length != recipe.length) return false;
        if(structure[0].length != recipe[0].length && structure[0].length != recipe[0][0].length) return false;
        if(structure[0][0].length != recipe[0][0].length && structure[0][0].length != recipe[0].length) return false;

        int[][] matchedDirection = new int[recipe.length][4];
        int[] rightDirection = new int[4];

        for (int y = 0; y < structure.length; ++y) {
            for (int i = 0; i < 4; ++i) {
                Optional<PlaceableItemEntity>[][] rotated = structure[y];
                for (int r = 0; r < i; ++r) {
                    rotated = MultiBlockUtil.rotate(rotated);
                }

                if(MultiBlockUtil.correctStructure(recipe[y], pattern, rotated)) {
                    matchedDirection[y][i] = 1;
                    rightDirection[i] = 1;
                }
            }
        }

        if(rightDirection[0] == 0 && rightDirection[1] == 0 && rightDirection[2] == 0 && rightDirection[3] == 0) return false;

        for (int i = 0; i < 4; ++i) {
            if(rightDirection[i] == 0) continue;
            boolean levelMatch = true;
            for (int[] array : matchedDirection) {
                if (array[i] == 0) {
                    levelMatch = false;
                    break;
                }
            }
            if (levelMatch)
                return true;
        }

        return false;
    }

    public void craft(SpawnContext spawnContext) {
        spawnResult.craft(spawnContext);
    }
}
