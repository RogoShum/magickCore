#version 110

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D BloomSampler;
uniform sampler2D BloomDepthSampler;

varying vec2 texCoord;

vec4 color_main;
float depth_main;
float near = 0.1; 
float far  = 100.0;

vec3 blend( vec3 dst, vec4 src ) {
    return ( dst * ( 1.0 - src.a ) ) + src.rgb;
}

vec4 depth_test( vec4 color, float depth ) {
    vec4 blank = vec4(0, 0, 0, 0);
    if ( color.a == 0.0 ) {
        return blank;
    }

    if(depth <= depth_main) {
        vec3 result = blend( color_main.rgb, color );
        vec4 final_color = vec4(result.rgb, color.a);
        return final_color;
    }

    vec3 result = blend( color.rgb, color_main );
    vec4 final_color = vec4(result.rgb, color_main.a);
    return final_color;
}

void main() {
    color_main = vec4( texture2D( DiffuseSampler, texCoord ).rgb, 1.0 );
    depth_main = texture2D( DiffuseDepthSampler, texCoord ).r;
    gl_FragColor = depth_test(texture2D( BloomSampler, texCoord ), texture2D( BloomDepthSampler, texCoord ).r);
}
