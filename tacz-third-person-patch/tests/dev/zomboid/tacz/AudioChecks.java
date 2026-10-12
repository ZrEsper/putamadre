package dev.zomboid.tacz;
import java.util.*;
public class AudioChecks {
 static int n;static void check(boolean b){n++;if(!b)throw new AssertionError(n);}
 public static class Level {boolean sky=true;List<?> all=List.of("near","far");public boolean canSeeSky(Object p){return sky;}public List<?> players(){return all;}}
 public static class Entity {Level l=new Level();public Level level(){return l;}public double getX(){return 10;}public double getY(){return 65;}public double getZ(){return -3;}public Object blockPosition(){return new Object();}}
 public static void main(String[] args){Entity e=new Entity();List<?> tracked=List.of("near");check(Audio.range(64,"shoot_3p")==192);check(Audio.range(16,"silence_3p")==40);check(Audio.range(16,"reload_empty")==16);check(Audio.range(0,"shoot")==0);check(Audio.range(Integer.MAX_VALUE,"shoot")==32767);check(Math.abs(Audio.volume(.8f,e)-1.16f)<.0001);e.l.sky=false;check(Math.abs(Audio.volume(.8f,e)-1.2528f)<.0001);check(Audio.listeners(tracked,e,"shoot_3p")==e.l.all);check(Audio.listeners(tracked,e,"reload_empty")==tracked);check(!Audio.shot("melee_stock")&&Audio.shot("silence"));String encoded=Audio.positioned("shoot_3p",e);check(Audio.name(encoded).equals("shoot_3p"));check(Audio.shot(encoded)&&Audio.listeners(tracked,e,encoded)==e.l.all);check(Audio.positioned("reload_empty",e).equals("reload_empty"));System.out.println("Passed "+n+" sound range, indoor gain and server recipient checks (API doubles)");}
}
