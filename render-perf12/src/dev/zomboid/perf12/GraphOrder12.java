package dev.zomboid.perf12;
import java.util.*;import java.lang.reflect.*;
/** Bounded weighted Kahn ordering. Keeps every layer and the original transparency categories. */
public final class GraphOrder12 {
 static final Map<Class<?>,Access> METHODS=new java.util.concurrent.ConcurrentHashMap<>();static boolean warned;
 static final class Access{Method vertices,targets,get,count;Access(Class<?>c)throws Exception{vertices=c.getMethod("vertices");targets=c.getMethod("targets",Object.class);get=c.getMethod("get",Object.class,Object.class);count=c.getMethod("getVertexCount");}}
 static Access access(Object graph)throws Exception{Access a=METHODS.get(graph.getClass());if(a==null){a=new Access(graph.getClass());METHODS.put(graph.getClass(),a);}return a;}
 static final class Edge{int target,weight;Edge(int t,int w){target=t;weight=w;}}
 public static Object order(Object manager){try{Field field=manager.getClass().getDeclaredField("types");field.setAccessible(true);Collection<?>graphs=((Map<?,?>)field.get(manager)).values();int total=0;for(Object g:graphs)total+=((Number)access(g).count.invoke(g)).intValue();if(total<=8)return null;List<Object> result=new ArrayList<>(total);for(Object g:graphs)result.addAll(sort(g));return result;}catch(Exception e){if(!warned){warned=true;System.err.println("[RenderPerf12] Iris ordering contract unavailable; retaining native ordering: "+e);}return null;}}
 static List<Object> sort(Object graph)throws Exception{Access a=access(graph);ArrayList<Object>v=new ArrayList<>();for(Object x:(Iterable<?>)a.vertices.invoke(graph))v.add(x);int n=v.size();if(n<2||n>2048)return v;Map<Object,Integer>index=new HashMap<>(n*2);for(int i=0;i<n;i++)index.put(v.get(i),i);List<List<Edge>>edges=new ArrayList<>(n);for(int i=0;i<n;i++)edges.add(new ArrayList<>());int[]in=new int[n];long[]weight=new long[n];int ec=0;
 for(int i=0;i<n;i++)for(Object target:(Iterable<?>)a.targets.invoke(graph,v.get(i))){Integer j=index.get(target);if(j==null||j==i)continue;if(++ec>32768)return v;Object value=a.get.invoke(graph,v.get(i),target);int w=value instanceof OptionalInt o?o.orElse(1):1;w=Math.max(1,Math.min(w,1000000));edges.get(i).add(new Edge(j,w));in[j]++;weight[j]+=w;}
 return ordered(v,edges,in,weight);}
 static List<Object> ordered(List<Object>v,List<List<Edge>>edges,int[]in,long[]weight){int n=v.size();boolean[]done=new boolean[n];PriorityQueue<Integer>ready=new PriorityQueue<>();for(int i=0;i<n;i++)if(in[i]==0)ready.add(i);List<Object>out=new ArrayList<>(n);
 while(out.size()<n){int selected=-1;while(!ready.isEmpty()&&selected<0){int x=ready.remove();if(!done[x])selected=x;}if(selected<0)for(int i=0;i<n;i++)if(!done[i]&&(selected<0||weight[i]<weight[selected]))selected=i;done[selected]=true;out.add(v.get(selected));for(Edge e:edges.get(selected))if(!done[e.target]){weight[e.target]-=e.weight;if(--in[e.target]==0)ready.add(e.target);}}
 return out;}
}
