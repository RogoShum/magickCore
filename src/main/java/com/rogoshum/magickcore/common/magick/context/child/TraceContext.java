package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TraceContext extends ChildContext {
    public static final Type<TraceContext> TYPE = new Type<>(LibContext.TRACE);
    public UUID uuid = MagickCore.emptyUUID;
    public Entity entity = null;

    public static TraceContext create(@Nonnull Entity entity) {
        TraceContext traceContext = new TraceContext();
        traceContext.entity = entity;
        traceContext.uuid = entity.getUUID();
        return traceContext;
    }

    public static TraceContext create(@Nonnull UUID uuid) {
        TraceContext traceContext = new TraceContext();
        traceContext.uuid = uuid;
        return traceContext;
    }

    @Override
    public void serialize(CompoundTag tag) {
        if(entity != null) {
            tag.putUUID("UUID", entity.getUUID());
        } else if(uuid != null && !uuid.equals(MagickCore.emptyUUID))
            tag.putUUID("UUID", uuid);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        new NBTTagHelper(tag).ifContainUUID("UUID", (uuid) -> this.uuid = uuid);
    }

    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public Type<TraceContext> getType() {
        return TYPE;
    }

    @Override
    public String getString(int tab) {
        return "";
    }
}
