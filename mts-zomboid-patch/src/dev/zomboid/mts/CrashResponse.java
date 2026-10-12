package dev.zomboid.mts;
import java.util.*;
public final class CrashResponse {
 static final class State {double x,z,dx,dz;boolean blocked,pending;}
 static final Map<Object,State> states=new WeakHashMap<>();
 public static void begin(Object v){State s=new State();Object m=R.get(v,"motion");s.x=R.num(R.get(m,"x"));s.z=R.num(R.get(m,"z"));states.put(v,s);}
 public static void blocked(Object v,Object box,boolean blocked){State s=states.get(v);if(s==null)return;s.blocked=blocked;if(blocked){Object d=R.get(box,"currentCollisionDepth");s.dx=R.num(R.get(d,"x"));s.dz=R.num(R.get(d,"z"));}}
 public static double injury(double strength){return strength<2?0:Math.min(8,(strength-1)*1.6);}
 public static double[] rebound(double x,double z,double dx,double dz,double factor){double length=Math.hypot(dx,dz);if(length<1e-8)return new double[]{x,z};double nx=dx/length,nz=dz/length,dot=x*nx+z*nz;if(dot<0){nx=-nx;nz=-nz;dot=-dot;}double bounce=Math.min(dot*.22,.15/Math.max(.05,factor));return new double[]{(x-dot*nx)*.65-bounce*nx,(z-dot*nz)*.65-bounce*nz};}
 public static void impact(Object v,double strength){State s=states.get(v);if(s!=null&&s.blocked&&strength>=2)s.pending=true;double amount=injury(strength);if(amount==0)return;Set<Object> seen=Collections.newSetFromMap(new IdentityHashMap<>());Object language=null;for(Object part:(Collection<?>)R.get(v,"allParts")){Object rider=R.get(part,"rider");if(rider!=null&&seen.add(rider)){if(language==null)language=R.get(R.type("minecrafttransportsimulator.systems.LanguageSystem"),"DEATH_CRASH_NULL");R.call(rider,"attack",R.make("minecrafttransportsimulator.baseclasses.Damage",amount,null,v,null,language));}}}
 public static void finish(Object v){State s=states.remove(v);if(s==null||!s.pending||R.yes(R.call(R.get(v,"world"),"isClient")))return;double[] result=rebound(s.x,s.z,s.dx,s.dz,R.num(R.get(v,"speedFactor")));Object motion=R.get(v,"motion");R.set(motion,"x",result[0]);R.set(motion,"z",result[1]);}
}
