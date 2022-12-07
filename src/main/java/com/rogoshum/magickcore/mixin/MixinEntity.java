package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.api.entity.IEntityData;
import com.rogoshum.magickcore.api.event.ExtraDataEvent;
import com.rogoshum.magickcore.common.extradata.EntityExtraData;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.concurrent.Callable;

@Mixin(Entity.class)
public class MixinEntity implements IEntityData {
    private HashMap<String, EntityExtraData> extraData = new HashMap<>();

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("RETURN"), cancellable = true)
    protected void onConstructor(CallbackInfo info) {
        Entity thisEntity = (Entity)(Object)this;
        HashMap<String, Callable<EntityExtraData>> dataMap = new HashMap<>();
        ExtraDataEvent.Entity event = new ExtraDataEvent.Entity(dataMap);
        MinecraftForge.EVENT_BUS.post(event);
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

    @Inject(method = "read", at = @At("HEAD"))
    public void onRead(CompoundNBT compound, CallbackInfo info) {
        Entity thisEntity = (Entity)(Object)this;
        if(!compound.contains(EntityExtraData.ENTITY_DATA)) return;
        CompoundNBT entityData = compound.getCompound(EntityExtraData.ENTITY_DATA);

        extraData.forEach((key, func) -> {
            if(func.isEntitySuitable(thisEntity) && entityData.contains(key))
                func.read(entityData.getCompound(key));
        });
    }

    @Inject(method = "writeWithoutTypeId", at = @At("HEAD"))
    public void onWriteWithoutTypeId(CompoundNBT compound, CallbackInfoReturnable<Boolean> info) {
        Entity thisEntity = (Entity)(Object)this;
        CompoundNBT entityData = new CompoundNBT();
        extraData.forEach((key, func) -> {
            if(func.isEntitySuitable(thisEntity)) {
                CompoundNBT dataTag = new CompoundNBT();
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
}
