package dev.tactical.tacz;
/** Restoring a decal is not a new hit. Keep its original age and suppress the fresh glow. */
public final class OnceEffects {
 public static void configure(Object particle)throws Exception{TimedMarks.configure(particle);if(!ImpactMarks.replay)return;long age=5;for(Object mark:ImpactMarks.marks.values())if(Reflect.field(mark,"particle")==particle){Long born=TimedMarks.born.get(mark);if(born!=null)age=Math.max(5,TimedMarks.clock-born);break;}ShotAlignment.set(particle,"age",(int)Math.min(TimedMarks.LIFE,age));}
}
