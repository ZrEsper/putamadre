package dev.zomboid.gore;
public class IntegrationChecks16{
 public static class Position{public double x=1,y=2,z=3;}
 public static class Delta{public float getGameTimeDeltaPartialTick(boolean ignored){return .5f;}}
 public static class Event{public Delta getPartialTick(){return new Delta();}}
 static int checks;static void ok(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
 public static void main(String[]args){
  Object world=new Object();Organs16.bind(world);Position p=new Position();
  for(String name:new String[]{"eyeball","heart","liver","stomach"})Organs16.spawn(p,name,name.equals("eyeball")?0:6,0);
  ok(Shards13.ACTIVE.size()==4,"all independent types spawn");for(var s:Shards13.ACTIVE)ok(!s.mesh.isEmpty(),"anatomical mesh exists");
  var s=Shards13.ACTIVE.getFirst();var b=Organs16.BODIES.get(s);b.x=3;b.px=1;Organs16.beforeRender(new Event());ok(s.x==2,"render interpolates between ticks");Organs16.afterRender();ok(s.x==3,"render does not mutate physics state");
  for(int i=0;i<100;i++)Organs16.spawn(p,"eyeball",0,0);ok(Shards13.ACTIVE.size()==24&&Organs16.BODIES.size()==24,"global piece limit includes all types");
  Organs16.bind(new Object());ok(Shards13.ACTIVE.isEmpty()&&Organs16.BODIES.isEmpty()&&Organs16.SEEN.isEmpty(),"dimension switch clears bodies and dedup cache");
  System.out.println("ORGAN_INTEGRATION_CHECKS="+checks);
 }
}
