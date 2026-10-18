#version 150
uniform mat4 ViewProjection;
uniform vec3 From,To;
uniform vec2 Viewport;
uniform float WidthPixels,DotPixels;
uniform int Shape;
noperspective out vec2 uv;
out vec3 WorldPosition;
void main(){
 vec2 corners[6]=vec2[6](vec2(0,-1),vec2(1,-1),vec2(0,1),vec2(0,1),vec2(1,-1),vec2(1,1));vec2 q=corners[gl_VertexID];
 vec4 a=ViewProjection*vec4(From,1),b=ViewProjection*vec4(To,1);vec2 direction=(b.xy/b.w-a.xy/a.w)*Viewport;float n=length(direction);vec2 side=n>.001?vec2(-direction.y,direction.x)/n:vec2(1,0);
 if(Shape==0){vec4 p=mix(a,b,q.x);p.xy+=side*q.y*WidthPixels*2./Viewport*p.w;gl_Position=p;WorldPosition=mix(From,To,q.x);uv=vec2(q.x,q.y);}
 else{vec2 circle=vec2(q.x*2.-1.,q.y);gl_Position=b;gl_Position.xy+=circle*DotPixels*2./Viewport*b.w;WorldPosition=To;uv=circle;}
}
