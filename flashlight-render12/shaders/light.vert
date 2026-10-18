#version 150
out vec2 texCoord;
void main(){vec2 p[3]=vec2[3](vec2(-1,-1),vec2(3,-1),vec2(-1,3));gl_Position=vec4(p[gl_VertexID],0,1);texCoord=p[gl_VertexID]*.5+.5;}
