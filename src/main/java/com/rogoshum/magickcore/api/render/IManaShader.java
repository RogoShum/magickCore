package com.rogoshum.magickcore.api.render;

import com.mojang.blaze3d.shaders.Uniform;

public interface IManaShader {
    Uniform getIViewProjMat();
    Uniform getViewMat();
    Uniform getIViewMat();
    Uniform getModelMat();
    Uniform getIModelMat();
    Uniform getPosScale();
    Uniform getCameraPos();
    Uniform getCameraDirection();
    Uniform getCameraOrientation();

}
