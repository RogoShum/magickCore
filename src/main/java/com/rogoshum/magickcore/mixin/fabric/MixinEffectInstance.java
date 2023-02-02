package com.rogoshum.magickcore.mixin.fabric;

import com.mojang.blaze3d.shaders.Program;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.living.LivingHealEvent;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EffectInstance.class)
public class MixinEffectInstance {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;)V"))
    public String onInit(String string) {
        string = string.replace("shaders/program/", "");
        string = string.replace(".json", "");
        ResourceLocation rl = ResourceLocation.tryParse(string);
        string = rl.getNamespace() + ":" + "shaders/program/" + rl.getPath() + ".json";
        return string;
    }

    @ModifyArg(method = "getOrCreate", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;)V"))
    private static String onGetOrCreate(String string) {
        string = string.replace("shaders/program/", "");
        Program.Type type = Program.Type.FRAGMENT;
        if(string.contains(".fsh"))
            string = string.replace(".fsh", "");
        else if(string.contains(".vsh")) {
            string = string.replace(".vsh", "");
            type = Program.Type.VERTEX;
        }
        ResourceLocation rl = ResourceLocation.tryParse(string);
        string = rl.getNamespace() + ":" + "shaders/program/" + rl.getPath() + type.getExtension();
        return string;
    }
}
