#version 110

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform float BlurScale;
uniform float Radius;
uniform float Alpha;

void main() {
    vec4 center = texture2D(DiffuseSampler, texCoord);
    float size = Radius * 2.0 + 2.0;
    float empty = 1.0;
    vec2 bulrDir0 = vec2(BlurScale, 0.0);
    vec2 bulrDir1 = vec2(0.0, BlurScale);
    bool hasPixel = false;
    for(float r = -Radius; r <= Radius; r += 1.0) {
        vec4 blurSample0 = texture2D(DiffuseSampler, texCoord + oneTexel * r * bulrDir0);
        if(blurSample0.a <= 0.0)
            empty += 1.0;
        else
            hasPixel = true;
        vec4 blurSample1 = texture2D(DiffuseSampler, texCoord + oneTexel * r * bulrDir1);
        if(blurSample1.a <= 0.0)
            empty += 1.0;
        else
            hasPixel = true;
    }

    /*
    if(empty < 2.0) {
        empty = Radius * 0.5;
    }
*/
    size = (empty + 1.0) / Radius;

    empty = 1.0;

    vec4 blurred = vec4(0.0);
    float count = 1.0;
    bulrDir0 = vec2(max(BlurScale * size * 5.0, 1.0), 0.0);
    bulrDir1 = vec2(0.0, max(BlurScale * size * 5.0, 1.0));
    blurred = texture2D(DiffuseSampler, texCoord);
    float totalAlpha = center.a;
    bool nonEmpty = false;
    for(float r = -empty; r <= empty; r += 1.0) {
        vec4 sampleValue0 = texture2D(DiffuseSampler, texCoord + oneTexel * r * bulrDir0);
        vec4 sampleValue1 = texture2D(DiffuseSampler, texCoord + oneTexel * r * bulrDir1);

        count += 2.0;
        blurred += sampleValue0;
        blurred += sampleValue1;
        if(sampleValue0.a > 0.0) {
            totalAlpha += sampleValue0.a;
            nonEmpty = true;
        }

        if(sampleValue1.a > 0.0){
            totalAlpha += sampleValue1.a;
            nonEmpty = true;
        }
    }
    float alpha = totalAlpha / count;
    alpha = alpha * alpha * center.a;
    gl_FragColor = vec4(blurred.rgb / count, alpha);
}
