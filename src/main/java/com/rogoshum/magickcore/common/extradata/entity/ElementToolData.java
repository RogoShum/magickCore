package com.rogoshum.magickcore.common.extradata.entity;

import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.lib.LibElementTool;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.child.ItemContext;
import com.rogoshum.magickcore.common.extradata.EntityExtraData;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

public class ElementToolData extends EntityExtraData {

    @Override
    public boolean isEntitySuitable(Entity entity) {
        return entity instanceof LivingEntity;
    }

    private float addtionDamage;
    private int tick;

    public void tick(LivingEntity entity) {
        HashMap<String, Integer> map = new HashMap<>();

        if(entity.getAllSlots() != null) {
            for (ItemStack stack : entity.getAllSlots()) {
                if (stack.hasTag() && stack.getTag().contains(LibElementTool.TOOL_ELEMENT)) {
                    CompoundTag tag = NBTTagHelper.getToolElementTable(stack);
                    if(tag.isEmpty()) {
                        stack.getTag().remove(LibElementTool.TOOL_ELEMENT);
                    }

                    try {
                        for (String key : tag.getAllKeys()) {
                            if(key != null && key.equals(LibElements.ORIGIN)) continue;
                            MagickContext context = new MagickContext(entity.level).element(MagickRegistry.getElement(key)).force(1).applyType(ApplyType.ELEMENT_TOOL);
                            context.addChild(ItemContext.create(stack));
                            MagickReleaseHelper.releaseMagick(context.noCost());
                            //MagickReleaseHelper.releaseMagick(MagickContext.create(entity.world).entity(entity).force(1).applyType(Enum));
                            map.put(key, map.containsKey(key) ? map.get(key) + 1 : 1);
                        }
                    } catch (Exception ignored) {

                    }
                }
            }
        }

        for(String key : map.keySet()) {
            if(key.equals(LibElements.ORIGIN)) continue;
            MagickContext context = new MagickContext(entity.level).element(MagickRegistry.getElement(key)).<MagickContext>force(map.get(key)).caster(entity).applyType(ApplyType.ELEMENT_TOOL);
            MagickReleaseHelper.releaseMagick(context.noCost());
        }

        if(tick > 0 && map.containsKey(LibElements.SOLAR)) {
            addtionDamage = (float) Math.pow(1.1, map.get(LibElements.SOLAR));
        }
        else
            addtionDamage = 1;


        if(tick > 0)
            tick--;
    }

    public void setAdditionDamage(int level) {
        tick = level;
    }

    public float applyAdditionDamage(float amount) {
        return amount * addtionDamage;
    }

    public void consumeElementOnTool(LivingEntity entity, String element) {
        entity.getAllSlots().forEach((s) -> NBTTagHelper.consumeElementOnTool(s, element));
    }

    @Override
    public void read(CompoundTag nbt) {

    }

    @Override
    public void write(CompoundTag nbt) {

    }
}
