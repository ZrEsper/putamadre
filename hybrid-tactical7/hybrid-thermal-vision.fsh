#version 150
uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;
uniform float Time;
in vec2 texCoord;
out vec4 fragColor;
float hash(vec2 p){return fract(sin(dot(p,vec2(127.1,311.7)))*43758.5453);}
void main(){
 vec3 source=texture(DiffuseSampler,texCoord).rgb;
 // Reserved mesh signature. Broad enough for quantization and modest fog;
 // ordinary red blocks (no blue) and purple/magenta (too much blue) stay cold.
 float ratio=source.b/max(source.r,0.001);
 float hot=step(0.025,source.r)*step(0.36,ratio)*(1.0-step(0.88,ratio))*(1.0-step(0.15*source.r+0.006,source.g));
 float luma=dot(source,vec3(0.2126,0.7152,0.0722));
 float detail=pow(clamp(luma,0.0,1.0),0.70);
 vec3 cold=mix(vec3(0.015,0.045,0.070),vec3(0.24,0.57,0.70),detail);
 vec3 heat=mix(vec3(0.88,0.10,0.07),vec3(1.0,0.29,0.23),clamp(source.r,0.0,1.0));
 vec2 pixel=floor(texCoord*OutSize);
 float grain=(hash(pixel+floor(Time*1200.0))-0.5)*0.006;
 float scan=sin(pixel.y*3.14159265+Time*60.0)*0.0015;
 vec2 centered=texCoord*2.0-1.0;
 float vignette=1.0-0.17*smoothstep(0.30,1.40,dot(centered,centered));
 fragColor=vec4(clamp(mix(cold,heat,hot)*vignette+grain+scan,0.0,1.0),1.0);
}
