#version 150
uniform sampler2D Scene,Depth,Albedo,Shadow,AlbedoDepth;
uniform mat4 InverseViewProjection,ViewProjection;
uniform int Count;uniform vec2 FogDistance;
uniform vec3 Origin[2],Forward[2],Right[2],Up[2];
uniform vec3 ShadowOrigin[2],ShadowForward[2],ShadowRight[2],ShadowUp[2];
uniform float Range[2],HalfTan[2],NearFill[2];
in vec2 texCoord;out vec4 fragColor;
// Current-frame depth resolves contact shadows; old ray-map edges never cut off a newly turned beam.
float screenShadow(vec3 origin,vec3 p){float visibility=1.;for(int step=1;step<=8;step++){vec3 q=mix(origin,p,float(step)/9.);vec4 clip=ViewProjection*vec4(q,1.);if(clip.w<=.001)continue;vec3 ndc=clip.xyz/clip.w;vec2 uv=ndc.xy*.5+.5;if(any(lessThan(uv,vec2(.002)))||any(greaterThan(uv,vec2(.998))))continue;float d=texture(Depth,uv).r;if(d>=.999999)continue;vec4 h=InverseViewProjection*vec4(ndc.xy,d*2.-1.,1.);vec3 surface=h.xyz/h.w;vec3 fromSource=surface-origin;if(length(fromSource.xz)<1.35&&abs(fromSource.y)<2.1&&d<texture(AlbedoDepth,uv).r-.000001)continue;float delta=length(q)-length(surface);visibility=min(visibility,1.-smoothstep(.08,.20,delta));}return visibility;}
// Bilinear percentage-closer filtering avoids the old 12x12 stepping pattern.
float shadow(int i,vec3 p,vec3 normal){
 float current=screenShadow(Origin[i],p);vec3 d=p-ShadowOrigin[i];float z=dot(d,ShadowForward[i]);
 vec2 uv=vec2(dot(d,ShadowRight[i]),dot(d,ShadowUp[i]))/(max(.01,z)*HalfTan[i]*1.4)*.5+.5;
 if(z<=0.||any(lessThan(uv,vec2(0)))||any(greaterThan(uv,vec2(1))))return current;
 vec2 grid=uv*12.-.5,cell=floor(grid),blend=fract(grid);float sum=0.;
 for(int y=0;y<2;y++)for(int x=0;x<2;x++){
  vec2 q=(clamp(cell+vec2(x,y),vec2(0),vec2(11))+.5)/12.;
  vec3 ray=ShadowForward[i]+ShadowRight[i]*((q.x*2.-1.)*HalfTan[i]*1.4)+ShadowUp[i]*((q.y*2.-1.)*HalfTan[i]*1.4);
  float denom=dot(normal,ray),compareDepth=z;
  if(abs(denom)>.025){float planeDepth=dot(normal,d)/denom;if(planeDepth>0.&&planeDepth<Range[i]*2.)compareDepth=planeDepth;}
  q.y=(q.y+float(i))/2.;float limit=texture(Shadow,q).r;
  float weight=(x==0?1.-blend.x:blend.x)*(y==0?1.-blend.y:blend.y);
  sum+=weight*(1.-smoothstep(limit+.12,limit+.40,compareDepth));
 }
 float edge=min(min(uv.x,uv.y),min(1.-uv.x,1.-uv.y));return min(current,mix(1.,sum,smoothstep(0.,.12,edge)));
}

void main(){vec4 base=texture(Scene,texCoord);float depth=texture(Depth,texCoord).r;if(depth>=.999999||abs(base.a-251./255.)<.0015){fragColor=base;return;}vec4 h=InverseViewProjection*vec4(texCoord*2.-1.,depth*2.-1.,1);vec3 p=h.xyz/h.w;vec3 gradient=cross(dFdx(p),dFdy(p));float gradientLength=length(gradient);vec3 normal=gradientLength>.000001?gradient/gradientLength:normalize(-p);if(dot(normal,p)>0.)normal=-normal;float fog=FogDistance.y>FogDistance.x+.1?1.-smoothstep(FogDistance.x,FogDistance.y,length(p)):1.;vec4 albedo=texture(Albedo,texCoord);float captureDepth=texture(AlbedoDepth,texCoord).r;vec4 capturePosition=InverseViewProjection*vec4(texCoord*2.-1.,captureDepth*2.-1.,1.);float separation=length(capturePosition.xyz/capturePosition.w-p);bool matching=captureDepth<.999999&&separation<max(.06,length(p)*.002)&&albedo.a>.01;if(!matching){float luminance=dot(base.rgb,vec3(.2126,.7152,.0722));float peak=max(base.r,max(base.g,base.b));float gain=min(1./(.15+.85*luminance),1./max(peak,.001));albedo=vec4(base.rgb*gain,1.);}vec3 light=vec3(0);float coverage=0.;for(int i=0;i<Count;i++){vec3 v=p-Origin[i];float distance=length(v);vec3 direction=normalize(Forward[i]-vec3(0.,.35*NearFill[i],0.));float aperture=HalfTan[i]*mix(1.,1.35,NearFill[i]);float c=dot(v/max(.001,distance),direction);float outer=inversesqrt(1.+aperture*aperture),inner=inversesqrt(1.+aperture*aperture*.40);float spot=smoothstep(outer,inner,c);float fade=pow(clamp(1.-distance/Range[i],0.,1.),1.3);float beamPower=spot*fade;if(distance>=Range[i]||beamPower<.0001)continue;float incidence=.25+.75*max(0.,dot(normal,-v/max(.001,distance)));float power=beamPower*shadow(i,p,normal)*incidence*fog;light+=vec3(1.,.96,.86)*power*1.35;coverage=max(coverage,power);}
vec3 response=light/(vec3(1.)+light*.35);vec3 lit=max(base.rgb,albedo.rgb*response);float a=coverage>.002&&abs(base.a-253./255.)>.0015?250./255.:base.a;fragColor=vec4(lit,a);}
