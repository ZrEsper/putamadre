#version 150
uniform sampler2D Scene,Depth,Albedo,Shadow;
uniform mat4 InverseViewProjection;
uniform int Count;uniform vec2 FogDistance;
uniform vec3 Origin[2],Forward[2],Right[2],Up[2];
uniform vec3 ShadowOrigin[2],ShadowForward[2],ShadowRight[2],ShadowUp[2];
uniform float Range[2],HalfTan[2];
in vec2 texCoord;out vec4 fragColor;
float shadow(int i,vec3 p){vec3 d=p-ShadowOrigin[i];float z=dot(d,ShadowForward[i]);vec2 uv=vec2(dot(d,ShadowRight[i]),dot(d,ShadowUp[i]))/(max(.01,z)*HalfTan[i]*1.4)*.5+.5;if(z<=0.0||any(lessThan(uv,vec2(0)))||any(greaterThan(uv,vec2(1))))return 0.0;float sum=0.0;for(int y=0;y<2;y++)for(int x=0;x<2;x++){vec2 q=clamp(uv+(vec2(x,y)-.5)/12.0,vec2(.5/12.),vec2(11.5/12.));q.y=(q.y+float(i))/2.0;float limit=texture(Shadow,q).r;sum+=1.-smoothstep(limit+.10,limit+.35,z);}return sum*.25;}
void main(){vec4 base=texture(Scene,texCoord);float depth=texture(Depth,texCoord).r;if(depth>=.999999||abs(base.a-251./255.)<.0015){fragColor=base;return;}vec4 h=InverseViewProjection*vec4(texCoord*2.-1.,depth*2.-1.,1);vec3 p=h.xyz/h.w;vec3 gradient=cross(dFdx(p),dFdy(p));float gradientLength=length(gradient);vec3 normal=gradientLength>.000001?gradient/gradientLength:normalize(-p);if(dot(normal,p)>0.)normal=-normal;float fog=FogDistance.y>FogDistance.x+.1?1.-smoothstep(FogDistance.x,FogDistance.y,length(p)):1.;vec4 albedo=texture(Albedo,texCoord);if(albedo.a<.01){fragColor=base;return;}vec3 light=vec3(0);float coverage=0.;for(int i=0;i<Count;i++){vec3 v=p-Origin[i];float distance=length(v);float c=dot(v/max(.001,distance),Forward[i]);float outer=inversesqrt(1.+HalfTan[i]*HalfTan[i]),inner=inversesqrt(1.+HalfTan[i]*HalfTan[i]*.40);float spot=smoothstep(outer,inner,c);float fade=pow(clamp(1.-distance/Range[i],0.,1.),1.3);float incidence=.25+.75*max(0.,dot(normal,-v/max(.001,distance)));float power=spot*fade*shadow(i,p)*incidence*fog;light+=vec3(1.,.96,.86)*power*1.35;coverage=max(coverage,power);}
vec3 response=light/(vec3(1.)+light*.35);vec3 lit=max(base.rgb,albedo.rgb*response);float a=coverage>.002&&abs(base.a-253./255.)>.0015?250./255.:base.a;fragColor=vec4(lit,a);}
