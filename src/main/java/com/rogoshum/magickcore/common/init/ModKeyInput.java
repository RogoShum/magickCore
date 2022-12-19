package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.client.gui.SpellSwapBoxGUI;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.network.CSpellSwapPack;
import com.rogoshum.magickcore.common.network.Networking;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ModKeyInput {
    public static int press = 0;
    public static final KeyBinding MESSAGE_KEY = new KeyBinding("key.message",
            KeyConflictContext.IN_GAME,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.category." + MagickCore.MOD_ID);

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
        if (MESSAGE_KEY.isPressed() && Minecraft.getInstance().player != null) {
            PlayerEntity player = Minecraft.getInstance().player;
            if(press >= 0) {
                press++;
                ParticleUtil.spawnBlastParticle(player.world, player.getPositionVec().add(0, player.getEyeHeight(), 0), 1, ModElements.ORIGIN, ParticleType.PARTICLE);
            }
            ItemStack mainHand = player.getHeldItemMainhand();
            ItemStack offHand = player.getHeldItemOffhand();
            if(mainHand.getItem() instanceof IManaContextItem || offHand.getItem() instanceof IManaContextItem) {
                press = -1;
                Networking.INSTANCE.send(
                        PacketDistributor.SERVER.noArg(), CSpellSwapPack.openGUI(player.getEntityId()));
            } else if(press > 20 && (mainHand.getItem() instanceof MagickContextItem || offHand.getItem() instanceof MagickContextItem)) {
                press = -1;
                Networking.INSTANCE.send(
                        PacketDistributor.SERVER.noArg(), CSpellSwapPack.pushItem(player.getEntityId()));
            } else if(press > 20 && offHand.getItem() instanceof MagickContextItem) {
                press = -1;
                Networking.INSTANCE.send(
                        PacketDistributor.SERVER.noArg(), CSpellSwapPack.pushItem(player.getEntityId()));
            } else if(press > 20) {
                press = -1;
                if(player.isSneaking()) {
                    for(int i = 0; i < 3; ++i) {
                        Networking.INSTANCE.send(
                                PacketDistributor.SERVER.noArg(), CSpellSwapPack.popItem(player.getEntityId()));
                    }
                } else {
                    Networking.INSTANCE.send(
                            PacketDistributor.SERVER.noArg(), CSpellSwapPack.popItem(player.getEntityId()));
                }
            }
        } else
            press = 0;
    }
}
