package com.rogoshum.magickcore.api.magick.context;

import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.magick.context.child.ChildContext;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;

@SuppressWarnings("unchecked")
public class TagSpellContext extends SpellContext {
    public CompoundTag tag;

    private TagSpellContext(CompoundTag tag) {
        this.tag = tag;
    }

    public static TagSpellContext create(ItemStack stack) {
        return new TagSpellContext(stack.getOrCreateTagElement(LibRegistry.ITEM_DATA));
    }

    public static TagSpellContext create(CompoundTag tag) {
        return new TagSpellContext(tag);
    }

    @Override
    public TagSpellContext applyType(ApplyType applyType) {
        tag.putString("APPLY_TYPE", applyType.getLabel());
        return this;
    }

    public ApplyType applyType() {
        return ApplyType.getEnum(tag.getString("APPLY_TYPE"));
    }

    @Override
    public TagSpellContext element(MagickElement element) {
        tag.putString("ELEMENT", element.type());
        return this;
    }

    public MagickElement element() {
        return MagickRegistry.getElement(tag.getString("ELEMENT"));
    }

    @Override
    public TagSpellContext tick(int tick) {
        tag.putInt("TICK", tick);
        return this;
    }

    public int tick() {
        return tag.getInt("TICK");
    }

    @Override
    public TagSpellContext range(float range) {
        tag.putFloat("RANGE", range);
        return this;
    }

    public float range() {
        return tag.getFloat("RANGE");
    }

    @Override
    public TagSpellContext force(float force) {
        tag.putFloat("FORCE", force);
        return this;
    }

    public float force() {
        return tag.getFloat("FORCE");
    }

    @Override
    public TagSpellContext post(SpellContext context) {
        if(context == null) {
            tag.remove("POST");
            return this;
        }
        CompoundTag postTag = new CompoundTag();
        context.serialize(postTag);
        tag.put("POST", postTag);
        return this;
    }

    public SpellContext postContext() {
        if(!tag.contains("POST"))
            return null;
        return new TagSpellContext(tag.getCompound("POST"));
    }

    @Override
    public void copy(SpellContext context) {
        CompoundTag tag = new CompoundTag();
        context.serialize(tag);
        this.clear();
        this.deserialize(tag);
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
    public TagSpellContext merge(SpellContext context) {
        tag.putInt("TICK", this.tick() + context.tick());
        tag.putFloat("RANGE", this.range() + context.range());
        tag.putFloat("FORCE", this.force() + context.force());
        if(context.applyType() != ApplyType.NONE)
            tag.putString("APPLY_TYPE", context.applyType().getLabel());
        CompoundTag childTags = tag.getCompound("CHILD_CONTEXT");
        context.childContexts().forEach((s, child) -> {
            CompoundTag childTag = new CompoundTag();
            child.serialize(childTag);
            childTags.put(child.getType().name(), childTag);
        });
        tag.put("CHILD_CONTEXT", childTags);
        return this;
    }

    @Override
    public TagSpellContext addChild(ChildContext child) {
        CompoundTag childTags = tag.getCompound("CHILD_CONTEXT");
        CompoundTag childTag = new CompoundTag();
        child.serialize(childTag);
        childTags.put(child.getType().name(), childTag);
        tag.put("CHILD_CONTEXT", childTags);
        return this;
    }

    public void removeChild(String s) {
        tag.getCompound("CHILD_CONTEXT").remove(s);
    }

    protected HashMap<String, ChildContext> childContexts() {
        NBTTagHelper tagHelper = new NBTTagHelper(tag);
        HashMap<String, ChildContext> childContexts = new HashMap<>();
        tagHelper.ifContainNBT("CHILD_CONTEXT", (nbt) -> nbt.getAllKeys().forEach(s -> {
            ChildContext context = MagickRegistry.getChildContext(s);
            if(context != null) {
                CompoundTag childTag = nbt.getCompound(s);
                context.deserialize(childTag);
                childContexts.put(s, context);
            }
        }));
        return childContexts;
    }

    @Override
    public TagSpellContext replenishChild(ChildContext child) {
        if(!containChild(child.getType().name()))
            addChild(child);
        return this;
    }

    @Override
    public boolean containChild(String s) {
        return this.childContexts().containsKey(s);
    }

    @Override
    public <T extends ChildContext> T getChild(String s) {
        return (T) this.childContexts().get(s);
    }

    @Override
    public boolean valid() {
        for (ChildContext childContext : this.childContexts().values()) {
            if(!childContext.valid())
                return false;
        }
        return true;
    }

    @Override
    public void clear() {
        for(String key : tag.getAllKeys().toArray(new String[0])) {
            tag.remove(key);
        }
    }

    @Override
    public void deserialize(CompoundTag tag, int depth) {
        super.deserialize(tag, depth);
    }

    @Override
    public void serialize(CompoundTag tag) {
        super.serialize(tag);
    }
}
