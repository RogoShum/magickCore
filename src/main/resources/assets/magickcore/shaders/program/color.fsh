#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D ColorSampler;
uniform sampler2D ColorDepthSampler;

in vec2 texCoord;

out vec4 fragColor;

float near = 0.1;
float far  = 1000.0;

float LinearizeDepth(float depth)
{
    float z = depth * 2.0 - 1.0;
    return (near * far) / (far + near - z * (far - near));
}

void main() {
    vec4 mc = texture(DiffuseSampler, texCoord);
    vec4 c = texture(ColorSampler, texCoord);
    if(c.a <= 0.0) {
        fragColor = vec4(mc.rgb, 0.0);
        return;
    }

    float mcD = LinearizeDepth(texture(DiffuseDepthSampler, texCoord).r);
    float cD = LinearizeDepth(texture(ColorDepthSampler, texCoord).r);

    float dis = abs(mcD - cD);
    float alpha = clamp(dis/15.0, 0.0, 1.0);
    fragColor = vec4(c.rgb, 1.0 - alpha);
}
