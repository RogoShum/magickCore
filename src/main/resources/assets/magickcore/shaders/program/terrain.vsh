#version 150 compatibility

out vec3 position;
out vec4 lcolor;
out float intens;
out float dist2Obj;
out float darkElement;

struct Light
{
    vec4 color;
    vec3 position;
    float radius;
};

uniform int chunkX;
uniform int chunkY;
uniform int chunkZ;
uniform int lightCount;
uniform Light lights[256];

void main()
{
    vec4 pos = gl_ModelViewProjectionMatrix * gl_Vertex;
    position = gl_Vertex.xyz + vec3(chunkX, chunkY, chunkZ);
    gl_TexCoord[0] = gl_TextureMatrix[0] * gl_MultiTexCoord0;
    gl_TexCoord[2] = gl_TextureMatrix[2] * gl_MultiTexCoord2;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    //gl_Position = ftransform();
    gl_FrontColor = gl_Color;
    lcolor = vec4(0, 0, 0, 1.0f);
    dist2Obj = length(gl_Position);

    float sumR = 0;
    float sumG = 0;
    float sumB = 0;
    float count = 0;
    float maxIntens = 0;
    float totalIntens = 0;
    darkElement = 2.0;
    for(int i = 0; i < lightCount; i++)
    {
        Light l = lights[i];
        float radius = l.radius;
        if(radius < 0.0)
            radius = -radius;
        float intensity = pow(max(0, 1.0f - distance(l.position, position) / radius), 2);
        if(l.radius < 0.0 && intensity > 0.0) {
            float dark = 1.0f-intensity;
            darkElement = min(darkElement, dark * dark);
        }
        totalIntens += intensity;
        maxIntens = max(maxIntens, intensity);
    }
    for(int i = 0; i < lightCount; i++)
    {
        Light l = lights[i];
        float radius = l.radius;
        if(radius < 0.0)
            radius = -radius;
        float intensity = pow(max(0, 1.0f - distance(l.position, position) / radius), 2);
        sumR += l.color.r * (intensity / totalIntens) * l.color.a;
        sumG += l.color.g * (intensity / totalIntens) * l.color.a;
        sumB += l.color.b * (intensity / totalIntens) * l.color.a;
    }
    lcolor = vec4(max(sumR, 0.0f), max(sumG, 0.0f), max(sumB, 0.0f), 1.0f);
    intens = min(1.0f, maxIntens);
}
