package com.rogoshum.magickcore.api.entity;

import com.rogoshum.magickcore.api.extradata.EntityExtraData;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Consumer;

public interface IEntityData {
    HashMap<String, EntityExtraData> extraData();

    @SuppressWarnings("unchecked")
    default <T extends EntityExtraData> void execute(String dataID, Consumer<T> consumer) {
        if(extraData().containsKey(dataID))
            consumer.accept((T)extraData().get(dataID));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    default <T extends EntityExtraData> T get(String dataID) {
        if(extraData().containsKey(dataID))
            return (T)extraData().get(dataID);
        return null;
    }
}
