package com.rogoshum.magickcore.client.event;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.model.EntityHunterModel;
import com.rogoshum.magickcore.client.item.OrbBottleRenderer;
import com.rogoshum.magickcore.client.item.model.OrbBottleModel;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = MagickCore.MOD_ID)
public class ModelRegisterEvent {

    @SubscribeEvent
    public static void onModelRegister(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EntityHunterModel.LAYER_LOCATION, EntityHunterModel::createBodyLayer);
    }
}
