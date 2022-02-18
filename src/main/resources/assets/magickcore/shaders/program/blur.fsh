#version 110

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 BlurDir;
uniform float Radius;

float alpha_value( vec3 color ) {
    float alpha = 1.0;
    if ( color.r < alpha ) {
        alpha = color.r;
    }

    if ( color.g < alpha ) {
        alpha = color.g;
    }

    if ( color.b < alpha ) {
        alpha = color.b;
    }

    return alpha;
}

float max_value( vec3 color ) {
    float alpha = 0.0;
    if ( color.r > alpha ) {
        alpha = color.r;
    }

    if ( color.g > alpha ) {
        alpha = color.g;
    }

    if ( color.b > alpha ) {
        alpha = color.b;
    }

    return alpha;
}

void main() {
    vec4 blurred = vec4(0.0);
    float totalAlpha = 0.0;
    for(float r = -Radius; r <= Radius; r += 1.0) {
        vec4 sampleValue = texture2D(DiffuseSampler, texCoord + oneTexel * r * BlurDir);
        blurred = blurred + sampleValue;
        totalAlpha = alpha_value(sampleValue.rgb) + totalAlpha;
    }
    vec3 colour = blurred.rgb / (Radius * 2.0);
    gl_FragColor = vec4(colour.rgb, totalAlpha);
}
