package dev.zomboidmelee.boxing;
/** Asymmetric first-person guard with loading, extension, recoil and recovery in distinct phases. */
public final class View30 {
 static double track(double p,boolean heavy,double[]v){return Motion20.track(Math.max(0,Math.min(1,p)),heavy?new double[]{0,.19,.46,.55,.76,1}:new double[]{0,.10,.25,.36,.62,1},v);}
 static First24.Frame strike(int side,int kind,int combo,double phase,double breathing){int sign=side==0?1:-1;boolean active=side==Math.floorMod(combo,2),heavy=kind==Motion20.HEAVY;double x=side==0?.30:.27,y=side==0?-.56:-.50,z=side==0?-.50:-.47,pitch=side==0?-139:-145,yaw=sign*(side==0?9:12),roll=sign*(side==0?5:7),wrist=0;
  if(phase<1&&(kind==Motion20.LIGHT||heavy)){double hit=track(phase,heavy,new double[]{0,0,1,.88,.18,0}),load=track(phase,heavy,new double[]{0,1,0,0,0,0});if(active){x+=load*(heavy?.065:.025)-hit*(heavy?.25:.22);y+=hit*(heavy?.33:.37)-load*.025;z-=hit*(heavy?.38:.30)-load*.045;pitch+=hit*(heavy?42:47)-load*5;yaw+=sign*(load*(heavy?12:3)-hit*(heavy?23:7));roll+=sign*hit*(heavy?12:3);wrist=sign*hit*(heavy?25:10);}else{y+=hit*.016;z+=hit*.015;pitch-=hit*3;}}
  return new First24.Frame(sign*x,y+breathing,z,pitch,yaw,roll,wrist);}
 static First24.Frame pose(First24.Frame base,Object player,int side,int kind,double phase){if(MoveClient25.climb(player)>0||Slide28.lowVisual(player)||DoorFoot29.progress(player)>=0||kind!=Motion20.LIGHT&&kind!=Motion20.HEAVY)return base;double breath=Math.sin(BoxingClient16.CLOCK.getAsLong()/1e9*1.7+side*.45)*.0025;return strike(side,kind,Client20.state(player).combo<0?0:Client20.state(player).combo,phase,breath);}
}
