package com.rogoshum.magickcore.common.magick.condition;

import com.rogoshum.magickcore.api.enums.TargetType;
import com.rogoshum.magickcore.common.lib.LibConditions;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class BlockOnlyCondition extends BlockCondition {
    HashSet<Block> blocks = new HashSet<>();
    @Override
    public String getName() {
        return LibConditions.BLOCK_ONLY;
    }

    @Override
    public TargetType getType() {
        return TargetType.TARGET;
    }


    @Override
    public boolean test(Block block) {
        if(blocks.isEmpty()) return true;
        return blocks.contains(block);
    }

    public void addBlock(Block block) {
        blocks.add(block);
    }

    @Override
    protected void serialize(CompoundTag tag) {
        CompoundTag blocks = new CompoundTag();
        this.blocks.forEach(block -> {
            if(block != null && block.getRegistryName() != null)
                blocks.putByte(block.getRegistryName().toString(), (byte) 0);
        });
        tag.put("blocks", blocks);
    }

    @Override
    protected void deserialize(CompoundTag tag) {
        if(tag.contains("blocks")) {
            CompoundTag blocks = tag.getCompound("blocks");
            blocks.getAllKeys().forEach(registryName -> {
                Block forgeBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName));
                if(forgeBlock != null && !(forgeBlock instanceof AirBlock))
                    this.blocks.add(forgeBlock);
            });
        }
    }

    @Override
    public String toString() {
        if(blocks.isEmpty())
            return "";
        StringBuilder s = new StringBuilder();
        for (Block block : blocks) {
            s.append("§9").append(new TranslatableComponent(block.getDescriptionId()).getString()).append("\n");
        }
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
