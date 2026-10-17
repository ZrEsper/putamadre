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
  double rad=Math.toRadians(yaw),targetSpeed=Bodycam26.motion*20,dx=Bodycam26.dx*20,dz=Bodycam26.dz*20,targetForward=-dx*Math.sin(rad)+dz*Math.cos(rad),targetSide=-(dx*Math.cos(rad)+dz*Math.sin(rad)),previousForward=forward;
  speed+=(targetSpeed-speed)*a;forward+=(targetForward-forward)*a;side+=(targetSide-side)*a;double acceleration=clamp((forward-previousForward)/dt,-6,6);gait=Step31.gait();aim+=(aiming(m,p,partial)-aim)*(1-Math.exp(-18*dt));double movement=clamp(speed/4,0,1)*(R.yes(R.call(p,"onGround"))?1:.15),sprint=R.yes(R.call(p,"isSprinting"))?movement:0,slide=MoveClient25.slide(p),climb=MoveClient25.climb(p),steady=1-.75*aim;
  double cameraPitch=(Math.sin(gait*2)*.12*movement+Step31.pulse()*.12*movement-acceleration*.012+slide*.24-climb*.18)*steady;
  double cameraRoll=(Math.sin(gait)*.14*movement-side*.09-turn*.004+slide*.28)*steady;
  CAMERA[0].step(clamp(cameraPitch,-.8,.8),dt,2.1);CAMERA[1].step(clamp(cameraRoll,-1.6,1.6),dt,2.1);
  double raised=Equipment29.raised(partial),restWeight=(1-.60*aim)*(1-raised),gunWeight=(1-.60*aim)*(1-.65*raised);
  double mantle=Mantle31.weight(p);double[] target={(clamp(-turn*.00065-side*.010+Math.sin(gait)*.023*movement,-.14*(1-aim)-.04*aim,.14*(1-aim)+.04*aim))*gunWeight,-.15*(1-aim)*restWeight+(-Step31.pulse()*.018*movement-.020*sprint)*gunWeight-.10*slide-.25*mantle,-.018*(1-aim)*restWeight+(-acceleration*.007-.020*sprint+.035*slide+.04*mantle)*gunWeight,(-tilt*.022-4*sprint+.30*Math.sin(gait*2)*movement)*gunWeight-10*slide+12*mantle,(-turn*.050-side*.22)*gunWeight,(-side*1.05-turn*.022+Math.sin(gait)*.90*movement)*gunWeight-12*slide-8*mantle};
  for(int i=0;i<GUN.length;i++){if(i>=3&&slide<.01&&mantle<.01)target[i]=clamp(target[i],-(9*(1-aim)+1.6*aim),9*(1-aim)+1.6*aim);GUN[i].step(target[i],dt,i<3?3.3:2.8);}
 }
 public static void camera(Object e){if(!active()){reset(null,BoxingClient16.CLOCK.getAsLong());return;}sample(0);R.call(e,"setPitch",(float)(R.num(R.call(e,"getPitch"))+CAMERA[0].value));R.call(e,"setRoll",(float)(R.num(R.call(e,"getRoll"))+CAMERA[1].value));}
 public static void weapon(com.mojang.blaze3d.vertex.PoseStack pose,float partial){sample(partial);pose.translate(GUN[0].value,GUN[1].value,GUN[2].value);pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees((float)GUN[3].value));pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees((float)GUN[4].value));pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees((float)GUN[5].value));}
}
