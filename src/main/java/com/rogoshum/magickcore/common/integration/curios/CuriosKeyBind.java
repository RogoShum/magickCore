package com.rogoshum.magickcore.common.integration.curios;

import com.rogoshum.magickcore.MagickCore;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

public class CuriosKeyBind {
    public static final KeyMapping CAST_KEY = new KeyMapping(MagickCore.MOD_ID+".key.cast",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.category." + MagickCore.MOD_ID);

    public static final KeyMapping TAKE_OFF_KEY = new KeyMapping(MagickCore.MOD_ID+".key.take_off",
            KeyConflictContext.IN_GAME,
            KeyModifier.CONTROL,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.category." + MagickCore.MOD_ID);
}
