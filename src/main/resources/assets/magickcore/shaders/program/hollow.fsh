#version 110

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D ShapeSampler;
uniform sampler2D ShapeDepthSampler;

varying vec2 texCoord;

vec4 color_main;
float depth_main;

vec4 hollow_out( vec4 color) {
    vec4 oc = color;
    if ( oc.a > 0.0) {
        return oc;
    }
    
   return color_main;
}

void main() {
    color_main = vec4( texture2D( DiffuseSampler, texCoord ).rgb, 1.0 );
    depth_main = texture2D( DiffuseDepthSampler, texCoord ).r;
    gl_FragColor = hollow_out(texture2D( ShapeSampler, texCoord ));
}
