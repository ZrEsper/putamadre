package dev.zomboid.gore;
import java.util.*;
/** Test-only renderer container; real game's original shaded renderer stays in the JAR. */
public class Shards13 {
 static Object world;static final List<Shard> ACTIVE=new ArrayList<>();
 static class Shard{double x,y,z,vx,vy,vz;float radius,angle;int age;boolean bone;List<Mesh12.Quad>mesh;}
}
