package com.rogoshum.magickcore.client.init;

import com.mojang.blaze3d.platform.InputConstants;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.network.CSpellSwapPack;
import com.rogoshum.magickcore.common.network.Networking;
import com.rogoshum.magickcore.common.network.SimpleChannel;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class ModKeyBind {
    public static int press = 0;
    public static final KeyMapping SWAP_KEY = new KeyMapping(MagickCore.MOD_ID+".key.spell",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.category." + MagickCore.MOD_ID);

    public static void onKeyboardInput() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (SWAP_KEY.isDown()) {
                if (SWAP_KEY.consumeClick() && Minecraft.getInstance().player != null) {
                    Player player = Minecraft.getInstance().player;
                    if(press >= 0) {
                        press++;
                        ParticleUtil.spawnBlastParticle(player.level, player.position().add(0, player.getEyeHeight(), 0), 1, ModElements.ORIGIN, ParticleType.PARTICLE);
                    }
                    ItemStack mainHand = player.getMainHandItem();
                    ItemStack offHand = player.getOffhandItem();
                    if(mainHand.getItem() instanceof IManaContextItem || offHand.getItem() instanceof IManaContextItem) {
                        press = -1;
                        Networking.INSTANCE.send(SimpleChannel.SendType.client(), CSpellSwapPack.openGUI(player.getId()));
                    } else if(press > 20 && (mainHand.getItem() instanceof MagickContextItem || offHand.getItem() instanceof MagickContextItem)) {
                        press = -1;
                        Networking.INSTANCE.send(
                                SimpleChannel.SendType.client(), CSpellSwapPack.pushItem(player.getId()));
                    } else if(press > 20 && offHand.getItem() instanceof MagickContextItem) {
                        press = -1;
                        Networking.INSTANCE.send(
                                SimpleChannel.SendType.client(), CSpellSwapPack.pushItem(player.getId()));
                    } else if(press > 20) {
                        press = -1;
                        if(player.isShiftKeyDown()) {
                            for(int i = 0; i < 3; ++i) {
                                Networking.INSTANCE.send(
                                        SimpleChannel.SendType.client(), CSpellSwapPack.popItem(player.getId()));
                            }
                        } else {
                            Networking.INSTANCE.send(
                                    SimpleChannel.SendType.client(), CSpellSwapPack.popItem(player.getId()));
                        }
                    }
                } else
                    press = 0;
            }
        });
    }
}
