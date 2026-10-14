package dev.zomboid.hordes;
import java.util.*;
/** Bounded, same-floor room graph: doorways separate rooms even when open. */
public final class IndoorGraph {
 public record Cell(int x,int y,int z){public Cell add(int dx,int dz){return new Cell(x+dx,y,z+dz);}}
 static final int[][] DIRECTIONS={{1,0},{-1,0},{0,1},{0,-1}};
 public interface Grid {boolean inside(Cell p);boolean door(Cell p);}
 public static Set<Cell> room(Grid grid,Cell start,int budget){return fill(grid,start,budget,false);}
 public static Set<Cell> building(Grid grid,Cell start,int budget){return fill(grid,start,budget,true);}
 static Set<Cell> fill(Grid grid,Cell start,int budget,boolean doors){
  Set<Cell> seen=new HashSet<>(),visited=new HashSet<>();ArrayDeque<Cell> queue=new ArrayDeque<>();queue.add(start);
  while(!queue.isEmpty()&&visited.size()<budget){Cell p=queue.remove();if(!visited.add(p)||Math.abs(p.x-start.x)>24||Math.abs(p.z-start.z)>24||!grid.inside(p)||!doors&&grid.door(p))continue;
   seen.add(p);for(int[] d:DIRECTIONS)queue.add(p.add(d[0],d[1]));
  }
  // Never treat a truncated room as separate from unexplored parts of the same room.
  if(!queue.isEmpty())return Set.of();return seen;
 }
 public static List<Cell> candidates(Grid grid,Cell player){Set<Cell> building=building(grid,player,1600),room=room(grid,player,1600);if(building.isEmpty()||room.isEmpty())return List.of();return building.stream().filter(p->!room.contains(p)&&!grid.door(p)&&(p.x-player.x)*(p.x-player.x)+(p.z-player.z)*(p.z-player.z)>=36).toList();}
 public static boolean roll(boolean enabled,double chance,double random){return enabled&&Double.isFinite(chance)&&chance>0&&random<Math.min(100,chance)/100;}
}
