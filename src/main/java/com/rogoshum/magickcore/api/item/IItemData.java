package com.rogoshum.magickcore.api.item;

import com.rogoshum.magickcore.api.extradata.ItemExtraData;

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
