package com.rogoshum.magickcore.api.magick;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.ElementRenderer;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.common.lib.LibElements;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;
import java.util.UUID;

public class ManaData {
    private MagickElement element = MagickRegistry.getElement(LibElements.ORIGIN);
    private UUID traceTarget = MagickCore.emptyUUID;
    public BlockState block;
    public ItemStack stack = ItemStack.EMPTY;
    private boolean trace;
    private float force;
    private int range;
    private int tick;
    private ApplyType applyType = ApplyType.NONE;
    private ApplyType spawnType = ApplyType.NONE;
    public EntityType<? extends Entity> spawnEntity;

    public static ManaData create() {
        return new ManaData();
    }

    public MagickElement getElement() {
        return element;
    }

    public void setElement(MagickElement element) {
        this.element = element;
    }

    public BlockState getBlock() {
        return block;
    }

    public void setBlock(BlockState blockState) {
        this.block = blockState;
    }

    public ItemStack getItem() {
        return stack;
    }

    public void setItem(ItemStack stack) {
        this.stack = stack;
    }

    public UUID getTraceTarget() {
        return traceTarget;
    }

    public void setTraceTarget(UUID uuid) {
        this.traceTarget = uuid;
    }

    public float getForce() {
        return force;
    }

    public void setForce(float force) {
        this.force = force;
    }

    public int getTickTime() {
        return tick;
    }

    public void setTickTime(int tick) {
        this.tick = tick;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public ApplyType getApplyType() {
        return applyType;
    }

    public void setApplyType(ApplyType type) {
        applyType = type;
    }

    public ApplyType getSpawnType() {
        return spawnType;
    }

    public void setSpawnType(ApplyType type) {
        spawnType = type;
    }

    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    public boolean isTrace() {
        return trace;
    }

    public CompoundTag serialize(CompoundTag tag) {
        CompoundTag manaData = new CompoundTag();
        manaData.putString("ELEMENT", element.type());
        manaData.putUUID("TRACE_TARGET", traceTarget);
        manaData.putBoolean("TRACE", trace);
        manaData.putFloat("FORCE", force);
        manaData.putInt("RANGE", range);
        manaData.putInt("TICK", tick);
        manaData.putString("APPLY_TYPE", applyType.getLabel());
        manaData.putString("SPAWN_TYPE", spawnType.getLabel());
        if(spawnEntity != null)
            manaData.putString("ENTITY_TYPE", EntityType.getKey(spawnEntity).toString());
        manaData.put("ITEM_STACK", stack.save(new CompoundTag()));

        tag.put("ManaData", manaData);
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        if(!tag.contains("ManaData")) return;
        tag = tag.getCompound("ManaData");
        if(tag.contains("ELEMENT"))
            this.element = MagickRegistry.getElement(tag.getString("ELEMENT"));
        if(tag.contains("TRACE_TARGET"))
            this.traceTarget = tag.getUUID("TRACE_TARGET");
        if(tag.contains("FORCE"))
            this.force = tag.getFloat("FORCE");
        if(tag.contains("RANGE"))
            this.range = tag.getInt("RANGE");
        if(tag.contains("TICK"))
            this.tick = tag.getInt("TICK");
        if(tag.contains("APPLY_TYPE"))
            this.applyType = ApplyType.getEnum(tag.getString("APPLY_TYPE"));
        if(tag.contains("SPAWN_TYPE"))
            this.spawnType = ApplyType.getEnum(tag.getString("SPAWN_TYPE"));
        if(tag.contains("TRACE"))
            this.trace = tag.getBoolean("TRACE");
        Optional<EntityType<?>> typeOptional = EntityType.byString(tag.getString("ENTITY_TYPE"));
        stack = ItemStack.of(tag.getCompound("ITEM_STACK"));
        spawnEntity = typeOptional.orElse(null);
    }

    public ElementRenderer getRenderer() {
        return MagickCore.proxy.getElementRender(getElement().type());
    }
}
