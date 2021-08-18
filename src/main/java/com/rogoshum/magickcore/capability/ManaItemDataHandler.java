package com.rogoshum.magickcore.capability;

import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.capabilities.Capability;

public class ManaItemDataHandler {
    public static final Capability.IStorage<IManaItemData> storage = new CapabilityManaItemData.Storage<>();

    public static void deserializeData(CompoundNBT tag, IManaItemData data){
        if (tag.contains("ELEMENT_DATA")) {
            INBT element_data = tag.get("ELEMENT_DATA");
            storage.readNBT(null, data, null, element_data);
        }
    }

    public static void serializeData(CompoundNBT tag, IManaItemData data){
        INBT element_data = storage.writeNBT(null, data, null);
        tag.put("ELEMENT_DATA", element_data);
    }

    public static IManaItemData createDate(){
        return new CapabilityManaItemData.Implementation(ModElements.getElement(LibElements.ORIGIN));
    }
}