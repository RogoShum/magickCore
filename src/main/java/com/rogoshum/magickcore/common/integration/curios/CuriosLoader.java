package com.rogoshum.magickcore.common.integration.curios;

import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.integration.AdditionLoader;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.item.SpiritCrystalRingItem;
import com.rogoshum.magickcore.common.network.Networking;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.curios.api.SlotTypeMessage;

public class CuriosLoader extends AdditionLoader {
    public static boolean press = false;
    public static final RegistryObject<Item> RING = ModItems.ITEMS.register("spirit_crystal_ring", () -> new SpiritCrystalRingItem(BaseItem.properties()));


    @Override
    public void onLoad(IEventBus eventBus) {
        Networking.INSTANCE.messageBuilder(CCastSpellPack.class, Networking.nextID())
                .encoder(CCastSpellPack::toBytes)
                .decoder(CCastSpellPack::new)
                .consumer(CCastSpellPack::handler)
                .add();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void inter(InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, ()-> new SlotTypeMessage.Builder("spirit_crystal_ring").build());
    }

    @Override
    public void setup(FMLCommonSetupEvent event) {}

    @Override
    public void doClientStuff(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(CuriosKeyBind.CAST_KEY);
        ClientRegistry.registerKeyBinding(CuriosKeyBind.TAKE_OFF_KEY);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyboardInput(InputEvent.KeyInputEvent event) {
        if(Minecraft.getInstance().player == null) return;

        ItemStack ring = CuriosHelper.getSpiritRing(Minecraft.getInstance().player);
        if (CuriosKeyBind.TAKE_OFF_KEY.isPressed() && ring != null) {
            ring.shrink(1);
            Networking.INSTANCE.send(
                    PacketDistributor.SERVER.noArg(), CCastSpellPack.take(Minecraft.getInstance().player.getEntityId()));
        }

        if (CuriosKeyBind.CAST_KEY.isPressed()) {
            if(!press)
                Networking.INSTANCE.send(
                    PacketDistributor.SERVER.noArg(), CCastSpellPack.cast(Minecraft.getInstance().player.getEntityId()));
            press = true;
        } else
            press = false;
    }
}
