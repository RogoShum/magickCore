package com.rogoshum.magickcore.common.magick.context;

import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.context.child.ChildContext;
import com.rogoshum.magickcore.common.registry.MagickRegistry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unchecked")
public class ItemSpellContext extends SpellContext {
    public final ItemStack stack;

    private ItemSpellContext(ItemStack stack) {
        this.stack = stack;
        this.deserialize(getDataTag());
    }

    public static ItemSpellContext create(ItemStack stack) {
        return new ItemSpellContext(stack);
    }

    private CompoundTag getDataTag() {
        if(stack.hasTag() && stack.getTag().contains(LibRegistry.ITEM_DATA))
            return stack.getTag().getCompound(LibRegistry.ITEM_DATA);
        CompoundTag tag = new CompoundTag();
        stack.getOrCreateTag().put(LibRegistry.ITEM_DATA, tag);
        return tag;
    }

    @Override
    public ItemSpellContext applyType(ApplyType applyType) {
        this.applyType = applyType;
        save();
        return this;
    }

    @Override
    public ItemSpellContext element(MagickElement element) {
        this.element = element;
        save();
        return this;
    }

    @Override
    public ItemSpellContext tick(int tick) {
        this.tick = tick;
        save();
        return this;
    }

    @Override
    public ItemSpellContext range(float range) {
        this.range = range;
        save();
        return this;
    }

    @Override
    public ItemSpellContext force(float force) {
        this.force = force;
        save();
        return this;
    }

    @Override
    public ItemSpellContext post(SpellContext context) {
        CompoundTag tag = new CompoundTag();
        context.serialize(tag);
        this.postContext = SpellContext.create(tag);
        save();
        return this;
    }

    @Override
    public void copy(SpellContext context) {
        CompoundTag tag = new CompoundTag();
        context.serialize(tag);
        this.clear();
        this.deserialize(tag);
        save();
    }

    @Override
    public <T extends SpellContext> T copy() {
        SpellContext context = new SpellContext();
        CompoundTag tag = new CompoundTag();
        this.serialize(tag);
        context.deserialize(tag);
        return (T) context;
    }

    @Override
    public ItemSpellContext merge(SpellContext context) {
        this.tick += context.tick;
        this.range += context.range;
        this.force += context.force;
        if(context.applyType != ApplyType.NONE)
            this.applyType = context.applyType;
        context.childContexts.values().forEach((child) -> this.childContexts.put(child.getName(), child));
        save();
        return this;
    }

    @Override
    public ItemSpellContext addChild(ChildContext child) {
        this.childContexts.put(child.getName(), child);
        save();
        return this;
    }

    @Override
    public ItemSpellContext replenishChild(ChildContext child) {
        if(!containChild(child.getName()))
            this.childContexts.put(child.getName(), child);
        return this;
    }

    @Override
    public boolean containChild(String s) {
        return this.childContexts.containsKey(s);
    }

    @Override
    public <T extends ChildContext> T getChild(String s) {
        return (T) this.childContexts.get(s);
    }

    @Override
    public boolean valid() {
        for (ChildContext childContext : this.childContexts.values()) {
            if(!childContext.valid())
                return false;
        }
        return true;
    }

    @Override
    public void clear() {
        this.element = MagickRegistry.getElement(LibElements.ORIGIN);
        this.applyType = ApplyType.NONE;
        this.tick = 0;
        this.range = 0;
        this.force = 0;
        this.childContexts.clear();
        this.postContext = null;
        save();
    }

    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        save();
    }

    public void save() {
        serialize(getDataTag());
    }
}
