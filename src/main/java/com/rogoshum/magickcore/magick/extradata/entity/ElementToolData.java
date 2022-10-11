package com.rogoshum.magickcore.magick.extradata.entity;

import com.rogoshum.magickcore.api.entity.IManaMob;
import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.lib.LibElementTool;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.context.child.ItemContext;
import com.rogoshum.magickcore.magick.extradata.EntityExtraData;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;

public class ElementToolData extends EntityExtraData {

    @Override
    public boolean isEntitySuitable(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof IManaMob;
    }

    private float addtionDamage;
    private int tick;

    public void tick(LivingEntity entity) {
        HashMap<String, Integer> map = new HashMap<>();

        if(entity.getEquipmentAndArmor() != null) {
            for (ItemStack stack : entity.getEquipmentAndArmor()) {
                if (stack.hasTag() && stack.getTag().contains(LibElementTool.TOOL_ELEMENT)) {
                    CompoundNBT tag = NBTTagHelper.getToolElementTable(stack);
                    if(tag.isEmpty()) {
                        stack.getTag().remove(LibElementTool.TOOL_ELEMENT);
                    }

                    for (String key : tag.keySet()) {
                        MagickContext context = new MagickContext(entity.world).force(1).applyType(EnumApplyType.ELEMENT_TOOL);
                        context.addChild(ItemContext.create(stack));
                        MagickReleaseHelper.releaseMagick(context);
                        //MagickReleaseHelper.releaseMagick(MagickContext.create(entity.world).entity(entity).force(1).applyType(Enum));
                        map.put(key, map.containsKey(key) ? map.get(key) + 1 : 1);
                    }
                }
            }
        }

        for(String key : map.keySet()) {
            MagickContext context = new MagickContext(entity.world).<MagickContext>force(map.get(key)).caster(entity).applyType(EnumApplyType.ELEMENT_TOOL);
            MagickReleaseHelper.releaseMagick(context);
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
        entity.getEquipmentAndArmor().forEach((s) -> NBTTagHelper.consumeElementOnTool(s, element));
    }

    @Override
    public void read(CompoundNBT nbt) {

    }

    @Override
    public void write(CompoundNBT nbt) {

    }
}
