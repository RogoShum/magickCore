package com.rogoshum.magickcore.api.magick.context;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.magick.context.child.ChildContext;
import com.rogoshum.magickcore.api.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.util.ToolTipHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class SpellContext {
    public MagickElement element = MagickRegistry.getElement(LibElements.ORIGIN);
    public ApplyType applyType = ApplyType.NONE;
    public SpellContext postContext = null;
    public int tick;
    public float range;
    public float force;
    protected final HashMap<String, ChildContext> childContexts = new HashMap<>();

    public static SpellContext create(CompoundTag tag) {
        SpellContext spellContext = create();
        spellContext.deserialize(tag);
        return spellContext;
    }

    public static SpellContext create(CompoundTag tag, int depth) {
        SpellContext spellContext = create();
        spellContext.deserialize(tag, depth);
        return spellContext;
    }

    public static SpellContext create() {
        return new SpellContext();
    }

    public <T extends SpellContext> T applyType(ApplyType applyType) {
        this.applyType = applyType;
        return (T) this;
    }

    public <T extends SpellContext> T element(MagickElement element) {
        this.element = element;
        return (T) this;
    }

    public <T extends SpellContext> T tick(int tick) {
        this.tick = tick;
        return (T) this;
    }

    public <T extends SpellContext> T range(float range) {
        this.range = range;
        return (T) this;
    }

    public <T extends SpellContext> T force(float force) {
        this.force = force;
        return (T) this;
    }

    public <T extends SpellContext> T post(SpellContext context) {
        CompoundTag tag = new CompoundTag();
        context.serialize(tag);
        this.postContext = SpellContext.create(tag);
        return (T) this;
    }

    public void copy(SpellContext context) {
        CompoundTag tag = new CompoundTag();
        context.serialize(tag);
        this.clear();
        this.deserialize(tag);
    }

    public <T extends SpellContext> T copy() {
        SpellContext context = new SpellContext();
        CompoundTag tag = new CompoundTag();
        this.serialize(tag);
        context.deserialize(tag);
        return (T) context;
    }

    public <T extends SpellContext> T merge(SpellContext context) {
        this.tick += context.tick;
        this.range += context.range;
        this.force += context.force;
        if(context.applyType != ApplyType.NONE)
            this.applyType = context.applyType;
        context.childContexts.values().forEach((child) -> this.childContexts.put(child.getType().name(), child));
        return (T) this;
    }

    public <T extends SpellContext> T addChild(ChildContext child) {
        childContexts.put(child.getType().name(), child);
        return (T) this;
    }

    public <T extends SpellContext> T replenishChild(ChildContext child) {
        if(!containChild(child.getType().name()))
            childContexts.put(child.getType().name(), child);
        return (T) this;
    }

    public boolean containChild(String s) {
        return childContexts.containsKey(s);
    }

    public boolean containChild(ChildContext.Type<?> s) {
        return childContexts.containsKey(s.name());
    }

    public void removeChild(String s) {
        childContexts.remove(s);
    }

    public <T extends ChildContext> T getChild(String s) {
        return (T) childContexts.get(s);
    }

    public <T extends ChildContext> T getChild(ChildContext.Type<T> s) {
        return (T) childContexts.get(s.name());
    }

    public boolean valid() {
        for (ChildContext childContext : childContexts.values()) {
            if(!childContext.valid())
                return false;
        }
        return true;
    }

    public void clear() {
        this.element = MagickRegistry.getElement(LibElements.ORIGIN);
        this.applyType = ApplyType.NONE;
        this.tick = 0;
        this.range = 0;
        this.force = 0;
        this.childContexts.clear();
        this.postContext = null;
    }

    public void serialize(CompoundTag tag) {
        tag.putString("ELEMENT", element.type());
        tag.putString("APPLY_TYPE", applyType.getLabel());
        tag.putInt("TICK", tick);
        tag.putFloat("RANGE", range);
        tag.putFloat("FORCE", force);
        CompoundTag childTags = new CompoundTag();
        childContexts.forEach((s, child) -> {
            CompoundTag childTag = new CompoundTag();
            child.serialize(childTag);
            childTags.put(child.getType().name(), childTag);
        });
        tag.put("CHILD_CONTEXT", childTags);

        CompoundTag postTag = new CompoundTag();
        if(postContext != null) {
            postContext.serialize(postTag);
        }
        tag.put("POST", postTag);
    }

    public void deserialize(CompoundTag tag) {
        deserialize(tag, -1);
    }

    public void deserialize(CompoundTag tag, int depth) {
        NBTTagHelper tagHelper = new NBTTagHelper(tag);
        tagHelper.ifContainString("ELEMENT", (s) -> element = MagickRegistry.getElement(s));
        tagHelper.ifContainString("APPLY_TYPE", (s) -> applyType = ApplyType.getEnum(s));
        tagHelper.ifContainInt("TICK", (i) -> tick = i);
        tagHelper.ifContainFloat("RANGE", (i) -> range = i);
        tagHelper.ifContainFloat("FORCE", (f) -> force = f);
        tagHelper.ifContainNBT("CHILD_CONTEXT", (nbt) -> nbt.getAllKeys().forEach(s -> {
            ChildContext context = MagickRegistry.getChildContext(s);
            if(context != null) {
                CompoundTag childTag = nbt.getCompound(s);
                context.deserialize(childTag);
                childContexts.put(s, context);
            }
        }));

        if(depth != 0) {
            tagHelper.ifContainNBT("POST", (nbt) -> {
                if(!nbt.isEmpty())
                    post(SpellContext.create(nbt, depth-1));
            });
        }
    }

    @Override
    public String toString() {
        return getString(false, this.element);
    }

    public String toStringSample() {
        return ToolTipHelper.DEEP_GREY + new TranslatableComponent(MagickCore.MOD_ID + ".description.press_sneak").getString() + getString(true, this.element);
    }

    public String toString(MagickElement playerElement) {
        return getString(false, playerElement);
    }

    public String toStringSample(MagickElement playerElement) {
        return ToolTipHelper.DEEP_GREY + new TranslatableComponent(MagickCore.MOD_ID + ".description.press_sneak").getString() + getString(true, playerElement);
    }

    public String getString(boolean simple, MagickElement playerElement) {
        MagickElement element = this.element;
        if(element == ModElements.ORIGIN)
            element = playerElement;

        ToolTipHelper toolTip = new ToolTipHelper();
        int lightTab = applyType == null ? 0 : applyType.isForm() ? 0 : 1;
        int progressiveTab = lightTab + 1;
        toolTip.tab = lightTab;
        if(containChild(LibContext.SPAWN)) {
            toolTip.nextLine();
            toolTip.prefix();
            ChildContext context = getChild(LibContext.SPAWN);
            toolTip.builder.append("§5◈ ");
            toolTip.builder.append(ToolTipHelper.PURPLE+ToolTipHelper.UNDERLINE).append(new TranslatableComponent(MagickCore.MOD_ID + ".description." + context.getType().name()).getString()).append(": ").append(ToolTipHelper.GREY_UNDERLINE).append(context.getString(toolTip.tab));
            toolTip.tab = progressiveTab;
        }

        if(element != ModElements.ORIGIN && applyType != ApplyType.NONE && !applyType.isForm()) {
            toolTip.nextTrans(LibItem.FUNCTION, new TranslatableComponent(MagickCore.MOD_ID + ".function." + element.type() + "." + applyType).getString(), ToolTipHelper.PINK, ToolTipHelper.GREY);
            toolTip.builder.append("§l <- ");
            toolTip.builder.append(new TranslatableComponent(MagickCore.MOD_ID + ".description." + element.type()).getString());
            if(applyType != ApplyType.NONE && !applyType.isForm()) {
                toolTip.builder.append(" ");
                toolTip.builder.append(new TranslatableComponent(MagickCore.MOD_ID + ".context." + applyType).getString());
            }
        } else {
            if(element != ModElements.ORIGIN)
                toolTip.nextTrans(LibItem.ELEMENT, new TranslatableComponent(MagickCore.MOD_ID + ".description." + element.type()).getString(), ToolTipHelper.PINK, ToolTipHelper.GREY);
            if(applyType != ApplyType.NONE && !applyType.isForm())
                toolTip.nextTrans(LibItem.MANA_TYPE, new TranslatableComponent(MagickCore.MOD_ID + ".context." + applyType).getString(), ToolTipHelper.PINK, ToolTipHelper.GREY);
        }

        toolTip.tab = progressiveTab;
        if(!simple) {
            if(force > 0 || range > 0 || tick > 0) {
                toolTip.nextLine();
                toolTip.builder.append(ToolTipHelper.GREY);
                for(int i = 0; i < toolTip.tab; i++) {
                    if(i == 0) {
                        toolTip.builder.append("|  ");
                    } else {
                        toolTip.builder.append("  ◇");
                    }
                }
            }
            boolean prefix = false;
            if(force > 0) {
                prefix = true;
                toolTip.trans(LibItem.FORCE, force, ToolTipHelper.BLUE, ToolTipHelper.GREY);
            }

            if(range > 0) {
                if(!prefix) {
                    prefix = true;
                } else
                    toolTip.builder.append(" ");
                toolTip.trans(LibItem.RANGE, range, ToolTipHelper.BLUE, ToolTipHelper.GREY);
            }
            if(tick > 0) {
                if(prefix)
                    toolTip.builder.append(" ");
                toolTip.trans(LibItem.TICK, tick / 20f, ToolTipHelper.BLUE, ToolTipHelper.GREY);
            }
        }

        childContexts.values().forEach((context) -> {
            if((!simple || context.getLinkType() == applyType) && !(context instanceof SpawnContext)) {
                toolTip.nextTrans(MagickCore.MOD_ID + ".description." + context.getType().name(), context.getString(toolTip.tab), ToolTipHelper.PURPLE, ToolTipHelper.GREY);
            }
        });

        if(postContext != null) {
            toolTip.builder.append(postContext.getString(simple, playerElement));
        }
        return toolTip.getString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpellContext that = (SpellContext) o;
        return tick == that.tick && range == that.range && Float.compare(that.force, force) == 0 && Objects.equals(element, that.element) && Objects.equals(applyType, that.applyType) && childContexts.equals(that.childContexts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, applyType, tick, range, force, childContexts);
    }
}
