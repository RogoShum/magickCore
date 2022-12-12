package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.common.entity.PlaceableItemEntity;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Predicate;

public class MultiBlockUtil {

    public static <T> Optional<T>[][][] createBlockPosArrays(HashMap<Vector3i, T> map, Optional<T> empty) {
        if(map.isEmpty()) {
            return null;
        }

        Optional<T>[][][] arrays;
        int xMin = 0, xMax = 0, yMin = 0, yMax = 0, zMin = 0, zMax = 0;
        for (Vector3i pos : map.keySet()) {
            xMin = Math.min(xMin, pos.getX());
            yMin = Math.min(yMin, pos.getY());
            zMin = Math.min(zMin, pos.getZ());
            xMax = Math.max(xMax, pos.getX());
            yMax = Math.max(yMax, pos.getY());
            zMax = Math.max(zMax, pos.getZ());
        }
        int xLength = 1+xMax+Math.abs(xMin);
        int yLength = 1+yMax+Math.abs(yMin);
        int zLength = 1+zMax+Math.abs(zMin);

        arrays = new Optional[yLength][xLength][zLength];
        for (int y = 0; y < yLength; ++y) {
            for (int x = 0; x < xLength; ++x) {
                for (int z = 0; z < zLength; ++z) {
                    BlockPos pos = new BlockPos(x + xMin, y + yMin, z + zMin);
                    arrays[y][x][z] = map.containsKey(pos) ? Optional.ofNullable(map.get(pos)) : empty;
                }
            }
        }

        return arrays;
    }

    public static <T> Optional<T>[][] rotate(Optional<T>[][] matrix) {
        Optional<T> t = Optional.empty();
        int r = matrix.length;
        int c = matrix[0].length;
        int max = Math.max(c, r);
        int min = Math.min(c, r);
        int sub = 0;
        if(c == max)
            sub = max - min;
        Optional<T>[][] m = new Optional[c][r];

        for(int row=0;row<max;row++){
            for(int col=0;col<max;col++){
                int tempt = max-1-col - sub;
                if(m.length > row && m[row].length > col && matrix.length > tempt && matrix[tempt].length > row)
                    m[row][col] = matrix[tempt][row];
            }
        }
        return m;
    }

    public static <T> boolean correctStructure(String[][] matrix, StructurePattern<T>[] patterns, Optional<T>[][] structure) {
        for(int row=0;row<matrix.length;row++){
            for(int col=0;col<matrix[0].length;col++){
                if(structure.length > row && structure[row].length > col) {
                    String matrixPattern = matrix[row][col];
                    for (StructurePattern<T> pattern : patterns) {
                        Optional<T> optional = structure[row][col];

                        if(pattern.getPattern().equals(matrixPattern)
                                && !pattern.match(optional.orElse(null)))
                            return false;
                    }
                }
            }
        }

        return true;
    }

    public static abstract class StructurePattern<T> {
        public abstract boolean match(T type);
        public abstract String getPattern();
    }

    public static class BlockPattern extends StructurePattern<BlockState>{
        public static final BlockPattern AIR = new BlockPattern();

        public final String name;
        public final Block block;
        public final BlockState state;

        public final String pattern;

        public BlockPattern() {
            this.name = null;
            this.pattern = "";
            this.block = Blocks.AIR;
            this.state = null;
        }

        public BlockPattern(String pattern, String name) {
            this.name = name;
            this.pattern = pattern;
            this.block = null;
            this.state = null;
        }

        public BlockPattern(String pattern, Block block) {
            this.block = block;
            this.pattern = pattern;
            this.name = null;
            this.state = null;
        }

        public BlockPattern(String pattern, BlockState state) {
            this.state = state;
            this.pattern = pattern;
            this.block = null;
            this.name = null;
        }

        @Override
        public boolean equals(Object o) {
            if(!valid()) return false;
            if (this == o) return true;
            if (!(o instanceof BlockPattern)) return false;
            BlockPattern that = (BlockPattern) o;
            if(!that.valid()) return false;
            return Objects.equals(name, that.name) && Objects.equals(block, that.block) && Objects.equals(state, that.state) && pattern.equals(that.pattern);
        }

        private boolean valid() {
            if(this.pattern == null) return false;
            if(this.pattern.isEmpty()) {
                if(this.block != null && !(this.block instanceof AirBlock)) return false;
                if(this.state != null && !(state.getBlock() instanceof AirBlock)) return false;
                if(this.name != null && !name.contains("air")) return false;
            }

            return true;
        }

        public boolean match(BlockState state) {
            if(!valid()) return false;
            if(state.equals(this.state)) return true;
            if(state.getBlock().equals(this.block)) return true;
            return state.getBlock().getRegistryName().toString().equals(this.name);
        }

        @Override
        public String getPattern() {
            return pattern;
        }

        @Override
        public int hashCode() {
            return Objects.hash(pattern);
        }
    }

    public static class ItemPattern extends StructurePattern<ItemStack> {
        public static final ItemPattern AIR = new ItemPattern();

        public final String name;
        public final Item item;
        public final ItemStack itemStack;

        public final String pattern;

        public ItemPattern() {
            this.name = null;
            this.pattern = "";
            this.item = Items.AIR;
            this.itemStack = null;
        }

        public ItemPattern(String pattern, String name) {
            this.name = name;
            this.pattern = pattern;
            this.item = null;
            this.itemStack = null;
        }

        public ItemPattern(String pattern, Item item) {
            this.item = item;
            this.pattern = pattern;
            this.name = null;
            this.itemStack = null;
        }

        public ItemPattern(String pattern, ItemStack stack) {
            this.itemStack = stack;
            this.pattern = pattern;
            this.item = null;
            this.name = null;
        }

        private boolean valid() {
            if(this.pattern == null) return false;
            if(this.pattern.isEmpty()) {
                if(this.item != null && !(this.item == Items.AIR)) return false;
                if(this.itemStack != null && !(itemStack.getItem() == Items.AIR)) return false;
                if(this.name != null && !name.contains("air")) return false;
            }

            return true;
        }

        public boolean match(ItemStack stack) {
            if(!valid()) return false;
            if(stack.equals(this.itemStack, false)) return true;
            if(stack.getItem().equals(this.item)) return true;
            return stack.getItem().getRegistryName().toString().equals(this.name);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ItemPattern)) return false;
            ItemPattern that = (ItemPattern) o;
            return Objects.equals(name, that.name) && Objects.equals(item, that.item) && Objects.equals(itemStack, that.itemStack) && pattern.equals(that.pattern);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pattern);
        }

        @Override
        public String getPattern() {
            return pattern;
        }
    }

    public static class EntityPattern extends StructurePattern<Entity>{
        public final EntityType<?> entity;
        public final String pattern;
        public final String name;

        public EntityPattern(String pattern, String name) {
            this.name = name;
            this.pattern = pattern;
            this.entity = null;
        }

        public EntityPattern(String pattern, EntityType<?> entity) {
            this.entity = entity;
            this.pattern = pattern;
            this.name = null;
        }

        private boolean valid() {
            return this.pattern != null;
        }

        public boolean match(Entity entity) {
            if(!valid()) return false;
            if(entity.getType().equals(this.entity)) return true;
            return entity.getType().getRegistryName().toString().equals(this.name);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EntityPattern)) return false;
            EntityPattern that = (EntityPattern) o;
            return Objects.equals(entity, that.entity) && pattern.equals(that.pattern) && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pattern);
        }

        @Override
        public String getPattern() {
            return pattern;
        }
    }

    public static class PredicatePattern<T> extends StructurePattern<T>{
        public final Predicate<T> predicate;
        public final String pattern;

        public PredicatePattern(String pattern, Predicate<T> predicate) {
            this.predicate = predicate;
            this.pattern = pattern;
        }

        private boolean valid() {
            return this.pattern != null && predicate != null;
        }

        public boolean match(T object) {
            if(!valid()) return false;
            return predicate.test(object);
        }

        @Override
        public String getPattern() {
            return pattern;
        }

        @Override
        public int hashCode() {
            return Objects.hash(pattern);
        }
    }

    public static class PlaceableEntityPattern extends StructurePattern<PlaceableItemEntity>{
        public final Item item;
        public final String pattern;

        public PlaceableEntityPattern(String pattern, Item item) {
            this.item = item;
            this.pattern = pattern;
        }

        private boolean valid() {
            return this.pattern != null && item != null;
        }

        @Override
        public boolean match(PlaceableItemEntity type) {
            return item == Items.AIR || (type != null && type.getItemStack().getItem() == item);
        }

        @Override
        public String getPattern() {
            return pattern;
        }

        @Override
        public int hashCode() {
            return Objects.hash(pattern);
        }
    }
}
