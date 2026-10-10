package dev.zomboid.danger;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import dev.zomboid.hordes.ChunkPopulationRules.Pair;
public final class Zones {
 private static final int[][] BOUNDS=load();
 private static int[][] load(){
  List<int[]> a=new ArrayList<>();
  try(InputStream in=Zones.class.getResourceAsStream("/mosslorn-danger-zones.csv")){
   if(in==null)throw new IllegalStateException("missing zones resource");
   for(String line:new String(in.readAllBytes(),StandardCharsets.UTF_8).split("\\R")){
    if(line.isBlank())continue;String[] fields=line.split(",");if(fields.length!=5)throw new IllegalStateException("invalid zone");int[] row=new int[5];for(int i=0;i<5;i++)row[i]=Integer.parseInt(fields[i]);a.add(row);
   }
  }catch(IOException e){throw new IllegalStateException(e);}return a.toArray(int[][]::new);
 }
 public static int multiplier(int x,int z){for(int[] b:BOUNDS)if(x>=b[0]&&x<=b[1]&&z>=b[2]&&z<=b[3])return b[4];return 1;}
 public static int missing(int alive,int base,Pair pair){int target=Math.max(1,Math.min(5,base));return Math.max(0,target*multiplier(pair.x()*16+8,pair.z()*16+8)-Math.max(0,alive));}
}
