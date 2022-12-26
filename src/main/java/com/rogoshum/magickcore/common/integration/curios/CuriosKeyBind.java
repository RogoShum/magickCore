package com.rogoshum.magickcore.common.integration.curios;

import com.rogoshum.magickcore.MagickCore;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

public class CuriosKeyBind {
    public static final KeyBinding CAST_KEY = new KeyBinding(MagickCore.MOD_ID+".key.cast",
            KeyConflictContext.IN_GAME,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.category." + MagickCore.MOD_ID);

    public static final KeyBinding TAKE_OFF_KEY = new KeyBinding(MagickCore.MOD_ID+".key.take_off",
            KeyConflictContext.IN_GAME,
            KeyModifier.CONTROL,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.category." + MagickCore.MOD_ID);
}
