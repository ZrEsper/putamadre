package dev.tactical.tacz;
public class ExpiryChecks{
 static int checks;
 static void ok(boolean b,String why){checks++;if(!b)throw new AssertionError(why);}
 public static class Particle{public int lifetime;public float alpha,quadSize=.13f;public boolean alive=true;public void remove(){alive=false;}public boolean isAlive(){return alive;}}
 static ImpactMarks.Mark mark(int key)throws Exception{ImpactMarks.Mark m=new ImpactMarks.Mark();m.particle=new Particle();ImpactMarks.configure(m.particle);ImpactMarks.marks.put(new ImpactMarks.Key(key,0,0,"north"),m);return m;}
 static void reset(){ImpactMarks.marks.clear();TimedMarks.born.clear();TimedMarks.clock=0;}
 public static void main(String[]args)throws Exception{
 reset();ImpactMarks.Mark first=mark(0);Particle p=(Particle)first.particle;ok(p.lifetime==1700&&p.alpha==1&&p.quadSize==.13f,"85 seconds, original category size");
 for(int tick=1;tick<1700;tick++){TimedMarks.tick();ok(p.alive&&ImpactMarks.marks.size()==1,"must remain before 85 seconds");}
 TimedMarks.tick();ok(!p.alive&&ImpactMarks.marks.isEmpty()&&TimedMarks.born.isEmpty(),"remove particle and restore record at deadline");
 for(int i=0;i<100;i++)TimedMarks.tick();ok(ImpactMarks.marks.isEmpty(),"nothing left for native replay to restore");
 reset();first=mark(0);for(int i=0;i<1400;i++)TimedMarks.tick();Particle restored=new Particle();first.particle=restored;ImpactMarks.configure(restored);for(int i=0;i<299;i++)TimedMarks.tick();ok(restored.alive,"restored mark keeps remaining original lifetime");TimedMarks.tick();ok(!restored.alive&&ImpactMarks.marks.isEmpty(),"restore cannot extend the deadline");
 reset();first=mark(0);for(int i=0;i<100;i++)TimedMarks.tick();ImpactMarks.Mark second=mark(1);for(int i=0;i<1600;i++)TimedMarks.tick();ok(!((Particle)first.particle).alive&&((Particle)second.particle).alive&&ImpactMarks.marks.size()==1,"independent creation times");for(int i=0;i<100;i++)TimedMarks.tick();ok(ImpactMarks.marks.isEmpty(),"second deadline independent");
 reset();for(int i=0;i<200;i++)mark(i);for(int i=0;i<1700;i++)TimedMarks.tick();ok(ImpactMarks.marks.isEmpty()&&TimedMarks.born.isEmpty(),"full tracker expires, no chunk or player required");
 System.out.println("IMPACT_EXPIRY_CHECKS="+checks);
 }
}
