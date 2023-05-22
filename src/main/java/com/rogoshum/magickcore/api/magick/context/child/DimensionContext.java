package com.rogoshum.magickcore.api.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class DimensionContext extends ChildContext {
    public static final Type<DimensionContext> TYPE = new Type<>(LibContext.DIMENSION);
    public ResourceKey<Level> dimension = Level.OVERWORLD;

    public static DimensionContext create(Entity entity) {
        DimensionContext context = new DimensionContext();
        context.dimension = entity.getLevel().dimension();
        return context;
    }

    public static DimensionContext create(Level level) {
        DimensionContext context = new DimensionContext();
        context.dimension = level.dimension();
        return context;
    }

    public static DimensionContext create(ResourceLocation res) {
        DimensionContext context = new DimensionContext();
        context.dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, res);
        return context;
    }

    public static DimensionContext create(BlockEntity blockEntity) {
        DimensionContext context = new DimensionContext();
        context.dimension = blockEntity.getLevel().dimension();
        return context;
    }

    @Override
    public void serialize(CompoundTag tag) {
        tag.putString("level", dimension.location().toString());
    }

    public Level getLevel(Level level) {
        return level.getServer().getLevel(dimension);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        if(tag.contains("level"))
            dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(tag.getString("level")));
    }

    @Override
    public boolean valid() {
        return dimension != null;
    }

    @Override
    public Type<DimensionContext> getType() {
        return TYPE;
    }

    @Override
    public String getString(int tab) {
        return new TranslatableComponent(dimension.location().toString()).getString();
    }
}
