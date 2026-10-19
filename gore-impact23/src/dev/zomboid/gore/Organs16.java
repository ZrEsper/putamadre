package dev.zomboid.gore;
import java.util.*;
/** Client visual rigid pieces. Uses existing shaded debris renderer, not game entities. */
public final class Organs16 {
 static final int CAP=24;static Object world;static long tick=Long.MIN_VALUE,soundAt=-100;static boolean reported;
 static final Random random=new Random(16);static final Map<Shards13.Shard,OrganMotion16.Body>BODIES=new IdentityHashMap<>();
 static final LinkedHashMap<Integer,Integer>SEEN=new LinkedHashMap<>();static boolean interpolated;
 static Object client(){return R.call(R.type("net.minecraft.client.Minecraft"),"getInstance");}
 static boolean bind(Object w){if(world!=w){world=w;tick=Long.MIN_VALUE;BODIES.clear();SEEN.clear();Shards13.ACTIVE.clear();Shards13.world=w;interpolated=false;}return w!=null;}
 static boolean near(Object p){Object player=R.get(client(),"player");if(player==null)return false;double dx=R.num(R.get(p,"x"))-R.num(R.call(player,"getX")),dy=R.num(R.get(p,"y"))-R.num(R.call(player,"getY")),dz=R.num(R.get(p,"z"))-R.num(R.call(player,"getZ"));return dx*dx+dy*dy+dz*dz<=24*24;}
 static void observe(int id,int details,int state,Object head,Object torso){
 if(!near(torso))return;int mask=SEEN.getOrDefault(id,0);
 if(Trauma12.stage(details,0)>=3&&(mask&1)==0){mask|=1;spawn(head,"eyeball",0,-.055);spawn(head,"eyeball",0,.055);}
 String[]names={"heart","liver","stomach"};for(int i=0;i<3;i++){int bit=2<<i,region=i==2?6:7;if(Trauma12.stage(details,region)>=3&&(mask&bit)==0){mask|=bit;spawn(torso,names[i],6,0);}}
 SEEN.put(id,mask);while(SEEN.size()>512)SEEN.remove(SEEN.keySet().iterator().next());
 }
 public static void living(Object event){try{Object n=R.call(event,"getEntity");if(!Gore.zombie(n))return;Object w=R.call(n,"level");if(!R.yes(R.get(w,"isClientSide"))||!bind(w))return;Object p=R.call(n,"position");double x=R.num(R.get(p,"x")),y=R.num(R.get(p,"y")),z=R.num(R.get(p,"z")),h=R.num(R.call(n,"getBbHeight"));observe(((Number)R.call(n,"getId")).intValue(),Trauma12.details(n),Gore.state(n),R.make("net.minecraft.world.phys.Vec3",x,y+h*.86,z),R.make("net.minecraft.world.phys.Vec3",x,y+h*.58,z));}catch(RuntimeException e){report(e);}}
 public static void ragdoll(Object rag){try{Object w=R.get(client(),"level");if(!bind(w))return;Object torso=R.call(rag,"getSmoothedTorsoPos"),head=torso;Object t=R.call(rag,"getSmoothedTransform",R.get(R.type("com.raiiiden.ragdollified.RagdollPart"),"HEAD"));if(t!=null)head=R.get(t,"position");observe(((Number)R.get(rag,"originalEntityId")).intValue(),((Number)R.get(rag,"goreDetails12")).intValue(),((Number)R.get(rag,"goreState12")).intValue(),head,torso);}catch(RuntimeException e){report(e);}}
 static void spawn(Object position,String organ,int region,double side){
 if(Shards13.ACTIVE.size()>=CAP)return;double radius=region==0?.06:organ.equals("liver")?.13:organ.equals("heart")?.10:.085;
 Shards13.Shard shard=new Shards13.Shard();shard.x=R.num(R.get(position,"x"))+side;shard.y=R.num(R.get(position,"y"))+.04;shard.z=R.num(R.get(position,"z"));shard.radius=(float)radius;
 List<Mesh12.Quad>mesh=new ArrayList<>();
 if(region==0){Mesh12.box(mesh,new Mesh12.Box(-.55f,-.55f,-.55f,.55f,.55f,.55f),5,null);Mesh12.box(mesh,new Mesh12.Box(-.23f,-.23f,-.59f,.23f,.23f,-.55f),4,null);}
 else{float[]scale={8,12,4};for(var piece:Anatomy14.anatomy(region))if(piece.name().equals(organ)){var b=piece.box();float centerX=organ.equals("heart")?.475f:organ.equals("liver")?.455f:.735f;float centerY=organ.equals("heart")?.435f:organ.equals("liver")?.64f:.68f;float centerZ=organ.equals("heart")?.38f:organ.equals("liver")?.46f:.465f;Mesh12.box(mesh,new Mesh12.Box((b.x0()-centerX)*scale[0],(b.y0()-centerY)*scale[1],(b.z0()-centerZ)*scale[2],(b.x1()-centerX)*scale[0],(b.y1()-centerY)*scale[1],(b.z1()-centerZ)*scale[2]),piece.material(),null);}}
 shard.mesh=List.copyOf(mesh);OrganMotion16.Body body=new OrganMotion16.Body(shard.x,shard.y,shard.z,radius);body.vx=(random.nextDouble()-.5)*.10;body.vy=.07+random.nextDouble()*.06;body.vz=(random.nextDouble()-.5)*.10;body.spin=(random.nextDouble()-.5)*.4;BODIES.put(shard,body);Shards13.ACTIVE.add(shard);
 }
 static double[] collide(double x,double y,double z,double r,double dx,double dy,double dz){Object block=R.call(R.type("net.minecraft.core.BlockPos"),"containing",x+dx,y+dy,z+dz);if(!R.yes(R.call(world,"hasChunkAt",block)))return null;Object box=R.make("net.minecraft.world.phys.AABB",x-r,y-r,z-r,x+r,y+r,z+r),motion=R.make("net.minecraft.world.phys.Vec3",dx,dy,dz);Object moved=R.call(R.type("net.minecraft.world.entity.Entity"),"collideBoundingBox",null,motion,box,world,List.of());return new double[]{R.num(R.get(moved,"x")),R.num(R.get(moved,"y")),R.num(R.get(moved,"z"))};}
 public static void tick(Object ignored){try{Object c=client();if(!bind(R.get(c,"level"))||R.yes(R.call(c,"isPaused")))return;long now=((Number)R.call(world,"getGameTime")).longValue();if(now==tick)return;tick=now;afterRender();for(var it=Shards13.ACTIVE.iterator();it.hasNext();){var shard=it.next();var b=BODIES.get(shard);boolean wasLanded=b!=null&&b.landed;if(b==null||!OrganMotion16.step(b,Organs16::collide)){it.remove();BODIES.remove(shard);continue;}shard.x=b.x;shard.y=b.y;shard.z=b.z;shard.angle=(float)b.angle;if(!wasLanded&&b.landed&&now-soundAt>=10){soundAt=now;R.call(world,"playLocalSound",b.x,b.y,b.z,Artery14.makeSound("wet_impact14"),R.get(R.type("net.minecraft.sounds.SoundSource"),"HOSTILE"),.12f,1.2f,false);}}
 }catch(RuntimeException e){report(e);Shards13.ACTIVE.clear();BODIES.clear();}}
 public static void beforeRender(Object event){afterRender();if(Shards13.ACTIVE.isEmpty())return;double alpha=1;try{alpha=R.num(R.call(R.call(event,"getPartialTick"),"getGameTimeDeltaPartialTick",false));}catch(RuntimeException optional){}alpha=Math.max(0,Math.min(1,alpha));for(var e:BODIES.entrySet()){var s=e.getKey();var b=e.getValue();s.x=b.px+(b.x-b.px)*alpha;s.y=b.py+(b.y-b.py)*alpha;s.z=b.pz+(b.z-b.pz)*alpha;}interpolated=true;}
 public static void afterRender(){if(!interpolated)return;for(var e:BODIES.entrySet()){var s=e.getKey();var b=e.getValue();s.x=b.x;s.y=b.y;s.z=b.z;}interpolated=false;}
 static void report(RuntimeException e){if(!reported){reported=true;System.err.println("[Gore16] Organ physics: "+e);}}
}
