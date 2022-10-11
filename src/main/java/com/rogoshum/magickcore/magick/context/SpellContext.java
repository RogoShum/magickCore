package com.rogoshum.magickcore.magick.context;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibItem;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.magick.context.child.ChildContext;
import com.rogoshum.magickcore.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.registry.MagickRegistry;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import com.rogoshum.magickcore.tool.RoguelikeHelper;
import com.rogoshum.magickcore.tool.ToolTipHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class SpellContext {
    public MagickElement element = MagickRegistry.getElement(LibElements.ORIGIN);
    public EnumApplyType applyType = EnumApplyType.NONE;
    public SpellContext postContext = null;
    public int tick;
    public float range;
    public float force;
    private final HashMap<String, ChildContext> childContexts = new HashMap<>();

    public static SpellContext create(CompoundNBT tag) {
        SpellContext spellContext = create();
        spellContext.deserialize(tag);
        return spellContext;
    }

    public static SpellContext create() {
        return new SpellContext();
    }

    public <T extends SpellContext> T applyType(EnumApplyType applyType) {
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
        CompoundNBT tag = new CompoundNBT();
        context.serialize(tag);
        this.postContext = SpellContext.create(tag);
        return (T) this;
    }

    public void copy(SpellContext context) {
        CompoundNBT tag = new CompoundNBT();
        context.serialize(tag);
        this.clear();
        this.deserialize(tag);
    }

    public <T extends SpellContext> T copy() {
        SpellContext context = new SpellContext();
        CompoundNBT tag = new CompoundNBT();
        this.serialize(tag);
        context.deserialize(tag);
        return (T) context;
    }

    public <T extends SpellContext> T merge(SpellContext context) {
        this.tick += context.tick;
        this.range += context.range;
        this.force += context.force;
        if(context.applyType != EnumApplyType.NONE)
            this.applyType = context.applyType;
        context.childContexts.values().forEach((child) -> this.childContexts.put(child.getName(), child));
        return (T) this;
    }

    public <T extends SpellContext> T addChild(ChildContext child) {
        childContexts.put(child.getName(), child);
        return (T) this;
    }

    public <T extends SpellContext> T replenishChild(ChildContext child) {
        if(!containChild(child.getName()))
            childContexts.put(child.getName(), child);
        return (T) this;
    }

    public boolean containChild(String s) {
        return childContexts.containsKey(s);
    }

    public <T extends ChildContext> T getChild(String s) {
        return (T) childContexts.get(s);
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
        this.applyType = EnumApplyType.NONE;
        this.tick = 0;
        this.range = 0;
        this.force = 0;
        this.childContexts.clear();
        this.postContext = null;
    }

    public void serialize(CompoundNBT tag) {
        tag.putString("ELEMENT", element.type());
        tag.putString("APPLY_TYPE", applyType.getLabel());
        tag.putInt("TICK", tick);
        tag.putFloat("RANGE", range);
        tag.putFloat("FORCE", force);
        CompoundNBT childTags = new CompoundNBT();
        childContexts.forEach((s, child) -> {
            CompoundNBT childTag = new CompoundNBT();
            child.serialize(childTag);
            childTags.put(child.getName(), childTag);
        });
        if(!childTags.isEmpty())
            tag.put("CHILD_CONTEXT", childTags);

        if(postContext != null) {
            CompoundNBT postTag = new CompoundNBT();
            postContext.serialize(postTag);
            tag.put("POST", postTag);
        }
    }

    public void deserialize(CompoundNBT tag) {
        NBTTagHelper tagHelper = new NBTTagHelper(tag);
        tagHelper.ifContainString("ELEMENT", (s) -> element = MagickRegistry.getElement(s));
        tagHelper.ifContainString("APPLY_TYPE", (s) -> applyType = EnumApplyType.getEnum(s));
        tagHelper.ifContainInt("TICK", (i) -> tick = i);
        tagHelper.ifContainFloat("RANGE", (i) -> range = i);
        tagHelper.ifContainFloat("FORCE", (f) -> force = f);
        tagHelper.ifContainNBT("CHILD_CONTEXT", (nbt) -> nbt.keySet().forEach(s -> {
            ChildContext context = MagickRegistry.getChildContext(s);
            if(context != null) {
                CompoundNBT childTag = nbt.getCompound(s);
                context.deserialize(childTag);
                childContexts.put(s, context);
            }
        }));
        tagHelper.ifContainNBT("POST", (nbt) -> post(SpellContext.create(nbt)));
    }

    @Override
    public String toString() {
        ToolTipHelper toolTip = new ToolTipHelper();
        if(element != ModElements.ORIGIN)
            toolTip.nextTrans(LibItem.ELEMENT, new TranslationTextComponent(MagickCore.MOD_ID + ".description." + element.type()).getString(), ToolTipHelper.PINK, ToolTipHelper.GREY);
        if(applyType != EnumApplyType.NONE)
            toolTip.nextTrans(LibItem.MANA_TYPE, applyType, ToolTipHelper.PINK, ToolTipHelper.GREY);
        if(force > 0)
            toolTip.nextTrans(LibItem.FORCE, force, ToolTipHelper.PINK, ToolTipHelper.GREY);
        if(range > 0)
            toolTip.nextTrans(LibItem.RANGE, range, ToolTipHelper.PINK, ToolTipHelper.GREY);
        if(tick > 0)
            toolTip.nextTrans(LibItem.TICK, tick / 20f, ToolTipHelper.PINK, ToolTipHelper.GREY);

        childContexts.values().forEach((context) -> {
            toolTip.push();
            toolTip.nextTrans(MagickCore.MOD_ID + ".description." + context.getName(), context.getString(toolTip.tab), ToolTipHelper.PINK, ToolTipHelper.GREY);
            toolTip.pop();
        });

        if(postContext != null)
            toolTip.builder.append(postContext.getString(1));

        return toolTip.getString();
    }

    public String getString(int tab) {
        ToolTipHelper toolTip = new ToolTipHelper();
        toolTip.tab = tab;
        toolTip.nextLine("{");
        if(element != ModElements.ORIGIN)
            toolTip.nextTrans(LibItem.ELEMENT, new TranslationTextComponent(MagickCore.MOD_ID + ".description." + element.type()).getString(), ToolTipHelper.PINK, ToolTipHelper.GREY);
        if(applyType != EnumApplyType.NONE)
            toolTip.nextTrans(LibItem.MANA_TYPE, applyType, ToolTipHelper.PINK, ToolTipHelper.GREY);
        if(force > 0)
            toolTip.nextTrans(LibItem.FORCE, force, ToolTipHelper.PINK, ToolTipHelper.GREY);
        if(range > 0)
            toolTip.nextTrans(LibItem.RANGE, range, ToolTipHelper.PINK, ToolTipHelper.GREY);
        if(tick > 0)
            toolTip.nextTrans(LibItem.TICK, tick / 20f, ToolTipHelper.PINK, ToolTipHelper.GREY);

        childContexts.values().forEach((context) -> {
            toolTip.push();
            toolTip.nextTrans(MagickCore.MOD_ID + ".description." + context.getName(), context.getString(toolTip.tab), ToolTipHelper.PINK, ToolTipHelper.GREY);
            toolTip.pop();
        });

        if(postContext != null)
            toolTip.builder.append(postContext.getString(tab + 1));

        toolTip.nextLine("}");
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
