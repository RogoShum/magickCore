#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler3;

uniform vec4 ColorModulator;
uniform float GameTime;
uniform float FogStart;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord2;
in vec2 noiseUV;

out vec4 fragColor;

void main() {
    vec2 distuv = vec2(noiseUV.x + GameTime * 2.0, noiseUV.y + GameTime * 2.0);

    vec2 offsetUV = texture(Sampler3, distuv).rb;
    offsetUV = ((offsetUV * 2.0) - 1.0) * FogStart;

    vec4 color = texture(Sampler0, texCoord0 + offsetUV) * vertexColor;
    fragColor = color * ColorModulator;
}
