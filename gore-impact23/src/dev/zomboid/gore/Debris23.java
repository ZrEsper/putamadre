package dev.zomboid.gore;
import java.util.*;
/** Small detached tissue fragments share the existing bounded, interpolated organ physics. */
public final class Debris23 {
 static final ThreadLocal<Object> CURRENT=new ThreadLocal<>();static final LinkedHashMap<Integer,int[]>STAGES=new LinkedHashMap<>();static boolean reported;static Object world;static long soundAt=-1000;static final Random RANDOM=new Random(23);
 static int[] stages(int id){int[] v=STAGES.computeIfAbsent(id,k->new int[8]);while(STAGES.size()>512)STAGES.remove(STAGES.keySet().iterator().next());return v;}
 public static void ragdoll(Object rag){try{Object w=R.get(R.call(R.type("net.minecraft.client.Minecraft"),"getInstance"),"level");if(!Organs16.bind(w))return;if(world!=w){world=w;STAGES.clear();soundAt=-1000;}int[] old=stages(((Number)R.get(rag,"originalEntityId")).intValue());int details=((Number)R.get(rag,"goreDetails12")).intValue(),made=0;
 String[]parts={"HEAD","HEAD","LEFT_ARM","RIGHT_ARM","LEFT_LEG","RIGHT_LEG","TORSO","TORSO"};for(int region:new int[]{0,6,7,2,3,4,5}){int level=Trauma12.stage(details,region),before=old[region];old[region]=level;if(level<2||level<=before||made>=3)continue;Object t=R.call(rag,"getSmoothedTransform",R.get(R.type("com.raiiiden.ragdollified.RagdollPart"),parts[region]));Object p=t==null?R.call(rag,"getSmoothedTorsoPos"):R.get(t,"position");double x=R.num(R.get(p,"x")),y=R.num(R.get(p,"y")),z=R.num(R.get(p,"z"));for(int i=0;i<3;i++)fragment(x,y,z,material(region==0,level,i));made++;long now=((Number)R.call(w,"getGameTime")).longValue();wet(w,x,y,z,now);}
 }catch(RuntimeException optional){}}
 static void wet(Object w,double x,double y,double z,long now){if(now-soundAt<3)return;soundAt=now;R.call(w,"playLocalSound",x,y,z,Artery14.makeSound("wet_impact14"),R.get(R.type("net.minecraft.sounds.SoundSource"),"HOSTILE"),.6f,.93f,false);}
 public static void enter(Object event){CURRENT.set(R.call(event,"getEntity"));}public static void end(){CURRENT.remove();}
 public static int material(boolean head,int stage,int fragment){return fragment==2&&stage>=3?2:head&&stage>=3?3:1;}
 static void fragment(double x,double y,double z,int material){if(Shards13.ACTIVE.size()>=Organs16.CAP){var old=Shards13.ACTIVE.removeFirst();Organs16.BODIES.remove(old);}Shards13.Shard s=new Shards13.Shard();s.x=x;s.y=y;s.z=z;s.radius=.035f+RANDOM.nextFloat()*.025f;s.bone=material==2;
 float r=s.radius*16;List<Mesh12.Quad>mesh=new ArrayList<>();Mesh12.box(mesh,new Mesh12.Box(-r,-r*.5f,-r*.65f,r,r*.5f,r*.65f),material,null);if(material!=2)Mesh12.box(mesh,new Mesh12.Box(-r*.7f,-r*.8f,-r*.4f,r*.4f,-r*.4f,r*.5f),material,null);s.mesh=List.copyOf(mesh);
 var b=new OrganMotion16.Body(x,y,z,s.radius);b.vx=(RANDOM.nextDouble()-.5)*.12;b.vy=.035+RANDOM.nextDouble()*.06;b.vz=(RANDOM.nextDouble()-.5)*.12;b.spin=(RANDOM.nextDouble()-.5)*.5;Shards13.ACTIVE.add(s);Organs16.BODIES.put(s,b);
 }
 public static void burst(Object w,double x,double y,double z,int requested,boolean head){try{if(!Organs16.bind(w))return;if(world!=w){world=w;STAGES.clear();soundAt=-1000;}Object entity=CURRENT.get();int stage=3,previous=0;
 if(entity!=null){int region=head?0:6;try{region=((Number)R.call(R.call(entity,"getEntityData"),"get",Trauma13.impactAccessor)).intValue()&7;}catch(RuntimeException optional){}stage=Trauma12.stage(Trauma12.details(entity),region);int[]seen=stages(((Number)R.call(entity,"getId")).intValue());previous=seen[region];seen[region]=stage;}
 long now=((Number)R.call(w,"getGameTime")).longValue();wet(w,x,y,z,now);
 if(stage<2||entity!=null&&stage<=previous)return;for(int i=0;i<3;i++)fragment(x,y,z,material(head,stage,i));
 }catch(RuntimeException e){if(!reported){reported=true;System.err.println("[Gore23] Tissue fragment: "+e);}}}
}
