#version 110

uniform sampler2D DiffuseSampler;
uniform sampler2D CopySampler;

varying vec2 texCoord;

void main() {
    vec4 center = texture2D(CopySampler, texCoord);
    if(center.a <= 0.0) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
    } else
        gl_FragColor = texture2D(DiffuseSampler, texCoord);
}
