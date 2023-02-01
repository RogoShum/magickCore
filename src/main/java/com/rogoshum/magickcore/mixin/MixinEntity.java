package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IEntityData;
import com.rogoshum.magickcore.api.event.ExtraDataEvent;
import com.rogoshum.magickcore.common.extradata.EntityExtraData;
import com.rogoshum.magickcore.api.mixin.IScaleEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.concurrent.Callable;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntityData, IScaleEntity {
    @Shadow private EntityDimensions dimensions;
    @Shadow private float eyeHeight;

    @Shadow public abstract boolean equals(Object object);

    private final HashMap<String, EntityExtraData> extraData = new HashMap<>();

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("RETURN"))
    protected void onConstructor(CallbackInfo info) {
        Entity thisEntity = (Entity)(Object)this;
        HashMap<String, Callable<EntityExtraData>> dataMap = new HashMap<>();
        ExtraDataEvent.Entity event = new ExtraDataEvent.Entity(dataMap);
        MagickCore.EVENT_BUS.post(event);
        dataMap.forEach((key, value) -> {
            try {
                EntityExtraData data = value.call();
                if(data.isEntitySuitable(thisEntity))
                    extraData.put(key, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Inject(method = "load", at = @At("HEAD"))
    public void onRead(CompoundTag compound, CallbackInfo info) {
        Entity thisEntity = (Entity)(Object)this;
        if(!compound.contains(EntityExtraData.ENTITY_DATA)) return;
        CompoundTag entityData = compound.getCompound(EntityExtraData.ENTITY_DATA);

        extraData.forEach((key, func) -> {
            if(func.isEntitySuitable(thisEntity) && entityData.contains(key))
                func.read(entityData.getCompound(key));
        });
    }

    @Inject(method = "saveWithoutId", at = @At("HEAD"))
    public void onWriteWithoutTypeId(CompoundTag compound, CallbackInfoReturnable<Boolean> info) {
        Entity thisEntity = (Entity)(Object)this;
        CompoundTag entityData = new CompoundTag();
        extraData.forEach((key, func) -> {
            if(func.isEntitySuitable(thisEntity)) {
                CompoundTag dataTag = new CompoundTag();
                func.write(dataTag);
                entityData.put(key, dataTag);
            }
        });
        compound.put(EntityExtraData.ENTITY_DATA, entityData);
    }

    @Override
    public HashMap<String, EntityExtraData> extraData() {
        return extraData;
    }

    @Override
    public void setEntityDimensions(EntityDimensions dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public EntityDimensions getEntityDimensions() {
        return this.dimensions;
    }

    @Override
    public void setEyeHeight(float eyeHeight) {
        this.eyeHeight = eyeHeight;
    }
}
