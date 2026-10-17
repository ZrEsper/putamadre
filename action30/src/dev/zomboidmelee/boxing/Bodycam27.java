package dev.zomboidmelee.boxing;
/** One critically damped camera, sampled per render rather than jumping between 20 Hz poses. */
public final class Bodycam27 {
 public static final class Spring {
  public double value,velocity;
  public void step(double target,double dt,double frequency){double w=2*Math.PI*frequency,d=value-target,j=(velocity+w*d)*dt,e=Math.exp(-w*dt);value=target+(d+j)*e;velocity=(velocity-w*j)*e;}
  void clear(){value=velocity=0;}
 }
 static Object owner,world;static long sampled;static double gait,speed,forward,side,lastYaw,lastPitch,aim;static final Spring[] CAMERA={new Spring(),new Spring()},GUN={new Spring(),new Spring(),new Spring(),new Spring(),new Spring(),new Spring()};
 static double clamp(double v,double min,double max){return Math.max(min,Math.min(max,v));}static double wrap(double a){a=(a+180)%360;if(a<0)a+=360;return a-180;}
 public static boolean active(){Object m=BoxingClient16.mc(),p=R.get(m,"player");return p!=null&&R.get(m,"screen")==null&&R.yes(R.call(p,"isAlive"))&&!R.yes(R.call(p,"isSleeping"))&&Action19.firstPerson(m);}
 static double aiming(Object m,Object p,float partial){try{Object op=R.call(R.type("com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator"),"fromLocalPlayer",p);return clamp(R.num(R.call(op,"getClientAimingProgress",partial)),0,1);}catch(IllegalStateException unavailable){return R.yes(R.call(R.get(R.get(m,"options"),"keyUse"),"isDown"))?1:0;}}
 static void reset(Object p,long now){owner=p;world=Bodycam26.world;sampled=now;gait=speed=forward=side=aim=0;for(Spring s:CAMERA)s.clear();for(Spring s:GUN)s.clear();if(p!=null){lastYaw=R.num(R.call(p,"getYRot"));lastPitch=R.num(R.call(p,"getXRot"));}}
 static void sample(float partial){Object m=BoxingClient16.mc(),p=R.get(m,"player");long now=BoxingClient16.CLOCK.getAsLong();if(p!=owner||p!=Bodycam26.owner||world!=Bodycam26.world){reset(p,now);return;}double elapsed=(now-sampled)/1e9;if(elapsed<.004)return;if(elapsed>.25){reset(p,now);return;}sampled=now;double dt=Math.min(.05,elapsed),a=1-Math.exp(-12*dt),yaw=R.num(R.call(p,"getYRot")),pitch=R.num(R.call(p,"getXRot"));double turn=clamp(wrap(yaw-lastYaw)/dt,-300,300),tilt=clamp((pitch-lastPitch)/dt,-220,220);lastYaw=yaw;lastPitch=pitch;
  double rad=Math.toRadians(yaw),targetSpeed=Bodycam26.motion*20,dx=Bodycam26.dx*20,dz=Bodycam26.dz*20,targetForward=-dx*Math.sin(rad)+dz*Math.cos(rad),targetSide=dx*Math.cos(rad)+dz*Math.sin(rad),previousForward=forward;
  speed+=(targetSpeed-speed)*a;forward+=(targetForward-forward)*a;side+=(targetSide-side)*a;double acceleration=clamp((forward-previousForward)/dt,-6,6);gait+=speed*dt*2.2;aim+=(aiming(m,p,partial)-aim)*(1-Math.exp(-18*dt));double movement=clamp(speed/4,0,1)*(R.yes(R.call(p,"onGround"))?1:.15),sprint=R.yes(R.call(p,"isSprinting"))?movement:0,slide=MoveClient25.slide(p),climb=MoveClient25.climb(p),steady=1-.92*aim;
  double cameraPitch=(Math.sin(gait*2)*.035*movement-acceleration*.012+slide*.24-climb*.18)*steady;
  double cameraRoll=(Math.sin(gait)*.035*movement-side*.045-turn*.002+slide*.28)*steady;
  CAMERA[0].step(clamp(cameraPitch,-.45,.45),dt,2.1);CAMERA[1].step(clamp(cameraRoll,-1.1,1.1),dt,2.1);
  double gunWeight=(1-aim)*(1-Equipment29.raised(partial));
  double[] target={(-turn*.00012+Math.sin(gait)*.006*movement)*gunWeight,(-.025-Math.abs(Math.sin(gait))*.006*movement-.035*sprint-.06*slide)*gunWeight,(-.018-acceleration*.003-.020*sprint)*gunWeight,(-tilt*.009-6*sprint+.30*Math.sin(gait*2)*movement-4*slide)*gunWeight,(-turn*.014)*gunWeight,(-side*.28-turn*.006+Math.sin(gait)*.25*movement+2*slide)*gunWeight};
  for(int i=0;i<GUN.length;i++)GUN[i].step(target[i],dt,i<3?2.2:1.8);
 }
 public static void camera(Object e){if(!active()){reset(null,BoxingClient16.CLOCK.getAsLong());return;}sample(0);R.call(e,"setPitch",(float)(R.num(R.call(e,"getPitch"))+CAMERA[0].value));R.call(e,"setRoll",(float)(R.num(R.call(e,"getRoll"))+CAMERA[1].value));}
 public static void weapon(com.mojang.blaze3d.vertex.PoseStack pose,float partial){sample(partial);pose.translate(GUN[0].value,GUN[1].value,GUN[2].value);pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees((float)GUN[3].value));pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees((float)GUN[4].value));pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees((float)GUN[5].value));}
}
