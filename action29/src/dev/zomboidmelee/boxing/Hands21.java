package dev.zomboidmelee.boxing;
public final class Hands21{
 public static boolean offEmpty(Object p){return R.yes(R.call(R.call(p,"getOffhandItem"),"isEmpty"));}
 /** Preserve vanilla offhand item rendering. The main hand RenderHandEvent draws the fist. */
 public static boolean early(float partial,Object pose,Object buffers,Object p,int light){return offEmpty(p)&&BoxingClient16.renderFirstPerson(partial,pose,buffers,p,light);}
 public static boolean empty(Object p){return p!=null&&R.yes(R.call(R.call(p,"getMainHandItem"),"isEmpty"));}
}
