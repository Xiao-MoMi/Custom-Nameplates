#version 150
in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;

uniform sampler2D Sampler0,Sampler2;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float GameTime;

out float vertexDistance;
flat out vec4 vertexColor;
out vec2 texCoord0;

void main(){
    vec4 vertex=vec4(Position, 1.0);
    if(Color.xyz==vec3(255., 255., 254.)/ 255.){
        (gl_Position=ProjMat*ModelViewMat*vertex);
        (vertexColor=((.6+.6*cos(6.*(gl_Position.x+GameTime*1000.)+vec4(0, 23, 21, 1)))+vec4(0., 0., 0., 1.))*texelFetch(Sampler2, UV2 / 16, 0));
    }else if(Color.xyz==vec3(255., 255., 253.)/ 255.){
        (gl_Position=ProjMat*ModelViewMat*vertex);
        (vertexColor=Color*texelFetch(Sampler2, UV2 / 16, 0));
        (gl_Position.y=gl_Position.y+sin(GameTime*12000.+(gl_Position.x*6))/ 150.);
    }else if(Color.xyz==vec3(255., 255., 252.)/ 255.){
        (gl_Position=ProjMat*ModelViewMat*vertex);
        (vertexColor=((.6+.6*cos(6.*(gl_Position.x+GameTime*1000.)+vec4(0, 23, 21, 1)))+vec4(0., 0., 0., 1.))*texelFetch(Sampler2, UV2 / 16, 0));
        (gl_Position.y=gl_Position.y+sin(GameTime*12000.+(gl_Position.x*6))/ 150.);
    }else if(Color.xyz==vec3(255., 255., 251.)/ 255.){
        (vertexColor=Color*texelFetch(Sampler2, UV2 / 16, 0));
        float vertexId = mod(gl_VertexID, 4.0);
        if(vertex.z <= 0.){
            if(vertexId==3.||vertexId==0.)vertex.y += cos(GameTime*12000. / 4)*0.1;
            vertex.y += max(cos(GameTime*12000. / 4)*0.1, 0.);
        }else{
            if(vertexId==3.||vertexId==0.)vertex.y-=cos(GameTime*12000. / 4)*3;
            vertex.y-=max(cos(GameTime*12000. / 4)*4, 0.);
        }
        (gl_Position=ProjMat*ModelViewMat*vertex);
    }else if(Color.xyz==vec3(255., 254., 254.)/ 255.){
        float vertexId = mod(gl_VertexID, 4.0);
        if(vertex.z <= 0.){
            if(vertexId==3.||vertexId==0.)vertex.y += cos(GameTime*12000. / 4)*0.1;
            vertex.y += max(cos(GameTime*12000. / 4)*0.1, 0.);
        }else{
            if(vertexId==3.||vertexId==0.)vertex.y-=cos(GameTime*12000. / 4)*3;
            vertex.y-=max(cos(GameTime*12000. / 4)*4, 0.);
        }
        (vertexColor=((.6+.6*cos(6.*(gl_Position.x+GameTime*1000.)+vec4(0, 23, 21, 1)))+vec4(0., 0., 0., 1.))*texelFetch(Sampler2, UV2 / 16, 0));
        (gl_Position=ProjMat*ModelViewMat*vertex);
    }else if(Color.xyz==vec3(255., 254., 253.)/ 255.){
        (vertexColor=Color*texelFetch(Sampler2, UV2 / 16, 0));
        vertex.y+= 1;
        vertex.x+= 1;
        (gl_Position=ProjMat*ModelViewMat*vertex);
    }else{
        (gl_Position=ProjMat*ModelViewMat*vertex);
        (vertexColor=Color*texelFetch(Sampler2, UV2 / 16, 0));
    }
    vertexDistance=length((ModelViewMat*vertex).xyz);
    texCoord0=UV0;
}