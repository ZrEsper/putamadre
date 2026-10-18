#version 150
uniform sampler2D WorldDepth;
uniform mat4 InverseViewProjection;
uniform vec2 Viewport;
uniform vec3 Color;
uniform int Shape;
noperspective in vec2 uv;
in vec3 WorldPosition;
out vec4 fragColor;
void main(){
 vec2 pixel=gl_FragCoord.xy/Viewport;float depth=texture(WorldDepth,pixel).r;
 if(depth<.999999){vec4 h=InverseViewProjection*vec4(pixel*2.-1.,depth*2.-1.,1);float wall=length(h.xyz/h.w),beam=length(WorldPosition);if(beam>wall+.04+.0003*beam)discard;}
 float radius=Shape==0?abs(uv.y):length(uv);if(radius>1.)discard;
 float glow=exp(-radius*radius*4.),core=1.-smoothstep(.12,.5,radius);float strength=max(core,glow*.4);if(strength<.03)discard;
 fragColor=vec4(Color*strength,251./255.);
}
