#version 150
#moj_import<fog.glsl>
uniform sampler2D Sampler0;uniform vec4 ColorModulator;uniform float FogStart,FogEnd;uniform vec4 FogColor;in float vertexDistance;flat in vec4 vertexColor;in vec2 texCoord0;out vec4 fragColor;void main(){vec4 v=texture(Sampler0,texCoord0)*vertexColor*ColorModulator;if(v.a<.1){discard;}fragColor=linear_fog(v,vertexDistance,FogStart,FogEnd,FogColor);}