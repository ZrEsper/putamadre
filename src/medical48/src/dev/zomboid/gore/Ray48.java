package dev.zomboid.gore;
/** Match the centered rendered skin volume, not a bone-only approximation. */
public final class Ray48 {
 public static double scale(Object packet,int part){if(!R.yes(R.get(packet,"isBaby")))return 1;return part==1?.75:.5;}
 public static double trace(double[] start,double[] end,int part,Object packet){double s=scale(packet,part);double[] a=start.clone(),b=end.clone();for(int i=0;i<3;i++){a[i]/=s;b[i]/=s;}return Ray45.trace(a,b,part,packet);}
 // Keep a last authoritative pose across impulse invalidation. A new stream replaces it.
 public static void remember(Object body){Object p=R.get(body,"settledTransforms");if(p==null)p=R.get(body,"latestStreamTransforms");if(p!=null)R.set(body,"gorePose13",p);}
 public static Object anchor(Object body){Object p=R.get(body,"settledTransforms");if(p==null)p=R.get(body,"latestStreamTransforms");if(p==null)p=R.get(body,"gorePose13");if(p instanceof Object[] ar&&ar.length>0&&ar[0]!=null){Object v=R.get(ar[0],"position");return R.make("net.minecraft.world.phys.Vec3",R.num(R.get(v,"x")),R.num(R.get(v,"y")),R.num(R.get(v,"z")));}Object stream=R.get(body,"streamAnchor");return stream==null?R.get(body,"deathPosition"):stream;}
}