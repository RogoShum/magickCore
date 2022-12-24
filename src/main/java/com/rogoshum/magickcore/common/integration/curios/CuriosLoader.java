package com.rogoshum.magickcore.common.integration.curios;

import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.client.item.ItemExtractorRenderer;
import com.rogoshum.magickcore.common.event.magickevent.MagickLogicEvent;
import com.rogoshum.magickcore.common.init.ModBlocks;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModKeyBind;
import com.rogoshum.magickcore.common.integration.AdditionLoader;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.item.SpiritCrystalRingItem;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.network.CSpellSwapPack;
import com.rogoshum.magickcore.common.network.Networking;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
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
    public void doClientStuff(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(ModKeyBind.CAST_KEY);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyboardInput(InputEvent.KeyInputEvent event) {
        if (ModKeyBind.CAST_KEY.isPressed() && Minecraft.getInstance().player != null) {
            if(!press)
                Networking.INSTANCE.send(
                    PacketDistributor.SERVER.noArg(), new CCastSpellPack(Minecraft.getInstance().player.getEntityId()));
            press = true;
        } else
            press = false;
    }
}
