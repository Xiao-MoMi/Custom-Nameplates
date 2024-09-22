#version 150
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;

uniform sampler2D Sampler2;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float GameTime;
uniform int FogShape;
uniform vec2 ScreenSize;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
out float depthLevel;
%SHADER_0%
void main() {
    vec4 vertex = vec4(Position, 1.0);
    vertexDistance = fog_distance(Position, FogShape);
    depthLevel = Position.z;
    texCoord0 = UV0;
    %SHADER_1%%SHADER_2%%SHADER_3%
}
