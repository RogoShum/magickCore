#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

float near = 0.1;
float far  = 1000.0;

float LinearizeDepth(float depth)
{
    float z = depth * 2.0 - 1.0;
    return (near * far) / (far + near - z * (far - near));
}

void main() {
    float depth = texture(DiffuseDepthSampler, texCoord).r;
    vec4 tex = texture(DiffuseSampler, texCoord);
    float col = 1.0-(1.0-depth)*500.0;
    fragColor = vec4(vec3(col), tex.a);
}
