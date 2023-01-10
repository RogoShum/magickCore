package com.rogoshum.magickcore.common.item.tool;

import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.HashSet;

public class WandItem extends BaseItem {
    public static final String SET_KEY = "blockSet";
    public WandItem() {
        super(properties());
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        CompoundNBT tag = context.getItem().getOrCreateChildTag(SET_KEY);
        int count = 1;
        PlayerEntity player = context.getPlayer();
        EntityStateData state = null;
        if(player != null)
            state = ExtraDataUtil.entityStateData(player);
        if(state != null)
            count = (int) (state.getMaxManaValue() / 39);
        HashSet<Vector3d> vector3ds = NBTTagHelper.getVectorSet(tag);
        if(vector3ds.size() < count)
            NBTTagHelper.addOrDeleteVector(tag, Vector3d.copyCentered(context.getPos()));
        return super.onItemUse(context);
    }
}
