package mod.cdv.tnpc.tactical;
import java.util.*;
/** One committed navigation order. No clock-driven strafing, stance cycling or patrol interruption in combat. */
public final class Command29 {
 static final Map<Object,State> STATES=new WeakHashMap<>();
 static final class State {String mode="hold";Object target;long until,pathAt,nextMove,aid=-1,lastHit=-1000,coverRetry;double[]point;boolean covered,crouched;}
 static State state(Object n){return STATES.computeIfAbsent(n,k->new State());}static Object c(Object n,String m,Object...a)throws Exception{return Reflect.call(n,m,a);}
 static void order(State s,String mode,double[]point,long now,int duration){s.mode=mode;s.point=point==null?null:point.clone();s.until=now+duration;s.pathAt=0;}
 static boolean committed(State s,long now){return s.point!=null&&now<s.until;}
 static boolean urgent(double d){return d<9;}
 static void hold(Object n,boolean crouch)throws Exception{c(c(n,"getNavigation"),"stop");Tactics24.stance(n,crouch,false,false);}
 static boolean move(Object n,State s,long now)throws Exception{if(!committed(s,now))return false;if(Tactics24.dist(n,s.point)<1){s.point=null;s.until=now+40;s.mode=s.covered?"cover":"hold";s.nextMove=now+60;hold(n,s.covered);return false;}Tactics24.stance(n,false,true,false);if(now>=s.pathAt){s.pathAt=now+30;Tactics24.move(n,s.point,1.05);}return true;}
 static boolean cover(Object n,Object enemy,State s,long now)throws Exception{if(now<s.coverRetry)return false;s.coverRetry=now+120;Tactics24.State old=Tactics24.state(n);old.coverAt=0;double[]p=Tactics24.spot(n,enemy,old,now);if(p==null||!old.covered)return false;s.covered=true;order(s,"cover_move",p,now,100);return true;}
 static boolean care(Object n,State s,long now)throws Exception{if(!s.covered)return false;double hp=Social.num(c(n,"getHealth")),max=Social.num(c(n,"getMaxHealth"));if(hp<max*.75&&NpcMedicine.supply(n,false)!=null){if(s.aid<0){s.aid=now;Combat26.packet(n,9);}hold(n,true);c(n,"aim",false);if(now-s.aid>=60){if(NpcMedicine.healNpc(n,5f))NpcMedicine.consume(n,false);s.aid=-1;s.until=now+20;}return true;}s.aid=-1;return false;}
 static boolean maintainCover(Object n,State s,boolean reload,boolean visible,long now)throws Exception {
 if(s.mode.equals("cover_move")){if(committed(s,now)){if(move(n,s,now))return true;}else{s.point=null;s.covered=false;s.mode="hold";return false;}}
 if(!s.mode.equals("cover")||!s.covered)return false;
 hold(n,true);if(!visible&&care(n,s,now))return true;
 if(reload){c(n,"aim",false);return true;}
 if(now<s.until){c(n,"aim",true);if(visible&&now>=Tactics24.state(n).burstAt)TeamBrain.gunTick(n);return true;}
 s.mode="hold";s.covered=false;s.aid=-1;s.nextMove=now+60;return false;
 }
 public static boolean combat(Object n){if(Field28.working(n))return true;if(!Tactics24.eligible(n))return Roles22.military(n);try{
 long now=Social.time(n);Tactics24.State nativeState=Tactics24.state(n);if(nativeState.tick==now)return true;nativeState.tick=now;State s=state(n);Object enemy=CalmBrain.state(n).target;boolean live=CalmBrain.alive(enemy),visible=live&&CalmBrain.visible(n,enemy),reload=Tactics24.reloading(n);
 double distance=visible?CalmBrain.distance(n,enemy):Double.POSITIVE_INFINITY;
 if(visible)CalmBrain.face(n,enemy);
 if(visible&&urgent(distance)){s.point=null;s.covered=false;s.aid=-1;s.mode="hold";}
 if(maintainCover(n,s,reload,visible,now))return true;
 if(!live){s.target=null;s.point=null;s.covered=false;s.aid=-1;Tactics24.stance(n,false,false,false);return Radio22.search(n,CalmBrain.state(n));}
 if(s.point!=null&&now>=s.until){s.point=null;s.covered=false;s.mode="hold";}
 if(s.target!=enemy){s.target=enemy;s.point=null;s.covered=false;s.aid=-1;s.until=now+30;s.nextMove=now+20;}
 Combat23.prepare(n);
 if(!visible){var known=CalmBrain.state(n);if(known.seen>=0&&now-known.seen<120){if(!s.mode.equals("search")){order(s,"search",new double[]{known.x,known.y,known.z},now,100);s.covered=false;}if(move(n,s,now)){c(n,"aim",true);return true;}}hold(n,true);c(n,"aim",true);return true;}
 if(now>=nativeState.shareAt){nativeState.shareAt=now+40;Tactics24.share(n,enemy,now);}
 boolean hit=((Number)Reflect.field(n,"hurtTime")).intValue()>0;
 if(!urgent(distance)&&((!committed(s,now)&&reload)||hit)&&!s.mode.equals("cover")&&!s.mode.equals("cover_move"))cover(n,enemy,s,now);
 if(committed(s,now)){if(move(n,s,now)){c(n,"aim",!reload);if(!reload&&now>=nativeState.burstAt)TeamBrain.gunTick(n);return true;}}
 if(maintainCover(n,s,reload,visible,now))return true;
 if(reload){hold(n,true);c(n,"aim",false);return true;}
 if(distance>400&&now>=s.nextMove){double[]p={Tactics24.v(enemy,"X"),Tactics24.v(enemy,"Y"),Tactics24.v(enemy,"Z")};if(Tactics24.reachable(n,p)){order(s,"advance",p,now,80);s.nextMove=now+100;move(n,s,now);}}
 else{if(distance>100)s.crouched=true;else if(distance<49)s.crouched=false;hold(n,s.crouched);}
 if(now>=nativeState.burstAt)TeamBrain.gunTick(n);else c(n,"aim",true);return true;
 }catch(Exception e){CalmBrain.error("command29 combat fallback",e);return false;}}
 public static boolean owns(Object n){try{State s=STATES.get(n);return s!=null&&(coverActive(n)||CalmBrain.alive(CalmBrain.state(n).target));}catch(Exception e){return false;}}
 public static boolean coverActive(Object n){State s=STATES.get(n);return s!=null&&(s.mode.equals("cover")||s.mode.equals("cover_move"));}
 public static boolean resume(Object n){if(!coverActive(n)||Field28.working(n))return false;combat(n);return coverActive(n);}
}
