#version 130

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D BloomSampler;
uniform sampler2D BloomDepthSampler;
uniform mat4 ProjMat;
uniform vec2 InSize;
uniform vec2 OutSize;
uniform vec2 ScreenSize;
uniform float _FOV;

in vec2 texCoord;
out vec4 FragColor;

vec4 color_main;
float depth_main;

vec3 blend( vec3 dst, vec4 src, float depth ) {
    return ( dst * ( 1.0 - depth ) ) + src.rgb;
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

    return alpha * alpha;
}

vec4 depth_test( vec4 color, float depth ) {
    vec4 blank = vec4(0, 0, 0, 0);
    float deviation = 0.0001;
    if ( color.a == 0.0 ) {
        return blank;
    }
    float depths = abs(depth - depth_main);
    if(depths <= deviation) {
        vec3 result = blend( color_main.rgb, color, color.a);
        vec4 final_color = vec4(result.rgb, max_value(color_main.rgb));
        return final_color;
    }

   return blank;
}

float near = 0.1;
float far  = 1000.0;
float LinearizeDepth(float depth)
{
    float z = depth * 2.0 - 1.0;
    return (near * far) / (far + near - z * (far - near));
}

const float exposure = 2.;
const float AOE = 15.;
void main(){
    float depth = LinearizeDepth(texture2D(DiffuseDepthSampler, texCoord).r);
    float distance = length(vec3(1., (2.*texCoord - 1.) * vec2(1.,1.) * tan(radians(70 / 2.))) * depth);
    //float distance = depth / 2;

    //vec2 uv = texCoord;
    //float d = sqrt(pow((uv.x - 0.5),2.0) + pow((uv.y - 0.5),2.0));
    //d = exp(-(d * AOE)) * exposure / (distance*0.15);
    //*clamp(1.0 + d,0.1,10.0)
    FragColor = vec4(blend(texture2D(DiffuseSampler, texCoord).rgb, texture2D(BloomSampler,texCoord), distance), 1.0);
}

/*void main() {
    color_main = vec4( texture2D( DiffuseSampler, texCoord ).rgb, 1.0 );
    depth_main = texture2D( DiffuseDepthSampler, texCoord ).r;
    gl_FragColor = depth_test(texture2D( BloomSampler, texCoord ), texture2D( BloomDepthSampler, texCoord ).r);
}*/
