package dev.zomboid.gore;
import java.util.*;
public class OwnerChecks20 {
 public static class Vec{public float x,y,z;Vec(float x,float y,float z){this.x=x;this.y=y;this.z=z;}}
 public static class Vertex{public Vec pos=new Vec(0,0,0);public float u,v;}
 public static class Face{public Object[]vertices={new Vertex(),new Vertex(),new Vertex(),new Vertex()};public Vec normal=new Vec(0,0,-1);}
 public static class Cube{public float minX,minY,minZ,maxX,maxY,maxZ;public Object[]polygons={new Face()};Cube(float w,float h,float d){maxX=w;maxY=h;maxZ=d;}}
 public static class Part{public List<Object>cubes=new ArrayList<>();public Map<String,Part>children=new LinkedHashMap<>();}
 public static class Model{public Part head=new Part();}
 static int n;static void ok(boolean b,String why){n++;if(!b)throw new AssertionError(why);}
 public static void main(String[]args){Model m=new Model();Part owner=new Part(),hair=new Part(),plane=new Part();owner.cubes.add(new Cube(8,8,8));hair.cubes.add(new Cube(9,9,9));plane.cubes.add(new Cube(8,8,0));m.head.children.put("hair",hair);m.head.children.put("model",owner);m.head.children.put("plane",plane);ok(Owner20.resolve(m.head,"head")==owner,"empty head container resolves actual cube, not hair or flat overlay");Model14.bind(m);ok(Model14.ROOTS.containsKey(owner),"actual EMF geometry becomes wound owner");ok(!Model14.ROOTS.containsKey(hair)&&!Model14.ROOTS.containsKey(plane),"decorative pieces do not duplicate skull");Model14.part(owner);ok(Model14.internal()&&Model14.skin(),"selected child renders anatomy and original skin");ok(Anatomy14.build(List.of(SurfaceChecks19.cube()),0,3).stream().anyMatch(q->q.material()==2),"selected nested owner produces visible bones rather than native-only fallback");Model14.end();Part nativePart=new Part();nativePart.cubes.add(new Cube(8,12,4));ok(Owner20.resolve(nativePart,"body")==nativePart,"native nonempty root preserved");Part cycle=new Part();cycle.children.put("loop",cycle);ok(Owner20.resolve(cycle,"head")==null,"cyclic unsupported hierarchy terminates");System.out.println("EMF_GEOMETRY_OWNER_CHECKS="+n);}
}
