package dev.zomboidmelee.boxing;
/** Footstep-driven gait: one half-cycle per nearby local footstep, never an independent wall-clock oscillator. */
public final class Step31{
 static Object owner,world;static long at,interval=350_000_000L;static int count;
 static void tick(Object p,Object level){if(p!=owner||level!=world){owner=p;world=level;at=0;count=0;interval=350_000_000L;}}
 public static void sound(Object event){Object sound=R.call(event,"getSound");R.call(BoxingClient16.mc(),"execute",(Runnable)()->heard(sound));}
 static void heard(Object sound){if(sound==null||owner==null||Bodycam26.motion<.015||!R.yes(R.call(owner,"onGround")))return;if(!"PLAYERS".equals(R.call(sound,"getSource").toString()))return;String id=R.call(sound,"getLocation").toString();if(!(id.endsWith(".step")||id.endsWith("_step")))return;double dx=R.num(R.call(sound,"getX"))-R.coord(owner,"X"),dy=R.num(R.call(sound,"getY"))-R.coord(owner,"Y"),dz=R.num(R.call(sound,"getZ"))-R.coord(owner,"Z");if(dx*dx+dz*dz>.81||Math.abs(dy)>2)return;mark(BoxingClient16.CLOCK.getAsLong());}
 static void mark(long now){if(at!=0&&now-at<140_000_000L)return;if(at!=0)interval=Math.max(180_000_000L,Math.min(650_000_000L,now-at));at=now;count++;}
 static double gait(){if(at==0)return 0;double t=Math.min(.95,Math.max(0,(BoxingClient16.CLOCK.getAsLong()-at)/(double)interval));return (count-1+t)*Math.PI;}
 static double pulse(){if(at==0||owner==null||Bodycam26.motion<.015||!R.yes(R.call(owner,"onGround")))return 0;double t=(BoxingClient16.CLOCK.getAsLong()-at)/(double)interval;return t<1.3?Math.sin(Math.min(1,t)*Math.PI):0;}
}
