package com.rogoshum.magickcore.common.api.itemstack;

import com.rogoshum.magickcore.common.magick.extradata.ItemExtraData;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Consumer;

public interface IItemData {
    HashMap<String, ItemExtraData> extraData();

    @SuppressWarnings("unchecked")
    default <T extends ItemExtraData> void execute(String dataID, Consumer<T> consumer) {
        if (extraData().containsKey(dataID))
            consumer.accept((T) extraData().get(dataID));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    default <T extends ItemExtraData> T get(String dataID) {
        if(extraData().containsKey(dataID))
            return (T)extraData().get(dataID);
        return null;
    }
}
