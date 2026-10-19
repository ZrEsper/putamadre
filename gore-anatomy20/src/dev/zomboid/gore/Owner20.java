package dev.zomboid.gore;
import java.util.*;
/** Find an actual anatomical cube below empty EMF container nodes. */
public final class Owner20 {
 record Candidate(Object part,int depth){}
 static double volume(Object part){try{Object raw=R.get(part,"cubes");if(!(raw instanceof List<?>cs))return 0;double total=0;for(Object c:cs){double x=R.num(R.get(c,"maxX"))-R.num(R.get(c,"minX")),y=R.num(R.get(c,"maxY"))-R.num(R.get(c,"minY")),z=R.num(R.get(c,"maxZ"))-R.num(R.get(c,"minZ"));if(x>0&&y>0&&z>0)total+=x*y*z;}return total;}catch(RuntimeException unsupported){return 0;}}
 public static Object resolve(Object root,String region){if(Surface19.readable(root)&&volume(root)>0)return root;double target=region.equals("head")?512:region.equals("body")?384:192,best=Double.POSITIVE_INFINITY;Object selected=null;Deque<Candidate>q=new ArrayDeque<>();q.add(new Candidate(root,0));Set<Object>seen=Collections.newSetFromMap(new IdentityHashMap<>());int visited=0;
 while(!q.isEmpty()&&visited++<128){Candidate c=q.removeFirst();if(!seen.add(c.part()))continue;double v=volume(c.part());if(v>0&&Surface19.readable(c.part())){double score=Math.abs(Math.log(v/target))+.03*c.depth();if(score<best){selected=c.part();best=score;}}
 if(c.depth()>=16)continue;try{Object children=R.get(c.part(),"children");if(children instanceof Map<?,?>m)for(var entry:m.entrySet()){String name=String.valueOf(entry.getKey()).toLowerCase(Locale.ROOT);if(name.matches(".*(hair|hat|helmet|ear|overlay|accessory|jacket|sleeve).*"))continue;if(entry.getValue()!=null)q.addLast(new Candidate(entry.getValue(),c.depth()+1));}}catch(RuntimeException unsupported){}
 }return selected;
 }
}
