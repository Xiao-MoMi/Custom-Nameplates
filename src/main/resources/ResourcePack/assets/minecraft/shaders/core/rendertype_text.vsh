#version 150
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;

uniform sampler2D Sampler0,Sampler2;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;
uniform float GameTime;
uniform int FogShape;
uniform vec2 ScreenSize;

out float vertexDistance;
flat out vec4 vertexColor;
out vec2 texCoord0;

void main(){
    vec4 vertex = vec4(Position, 1.0);
    vertexDistance = fog_distance(ModelViewMat, IViewRotMat * Position, FogShape);
    texCoord0 = UV0;
    {IA}
    if (Color.xyz == vec3(255., 254., 253.) / 255.) {
        vertexColor = Color*texelFetch(Sampler2, UV2 / 16, 0);
        vertex.y+= 1;
        vertex.x+= 1;
        gl_Position = ProjMat * ModelViewMat * vertex;
    } else {
        vertexColor = Color*texelFetch(Sampler2, UV2 / 16, 0);
        gl_Position = ProjMat * ModelViewMat * vertex;
    }
    {hide}
}
