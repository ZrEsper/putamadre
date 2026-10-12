package dev.zomboid.autopsy;
import java.util.*;
public final class Autopsy {
 static final String[] PARTS={"HEAD","BODY","LEFT_ARM","RIGHT_ARM","LEFT_LEG","RIGHT_LEG","LEFT_FOOT","RIGHT_FOOT"};
 static final Map<String,String> PART_NAMES=Map.of("HEAD","Cabeza","BODY","Tórax","LEFT_ARM","Brazo izquierdo","RIGHT_ARM","Brazo derecho","LEFT_LEG","Pierna izquierda","RIGHT_LEG","Pierna derecha","LEFT_FOOT","Pie izquierdo","RIGHT_FOOT","Pie derecho");
 static Object c(String text){return R.call(R.type("net.minecraft.network.chat.Component"),"literal",text);}
 static boolean has(Object j,String k){return R.yes(R.call(j,"has",k));}
 static Object get(Object j,String k){return R.call(j,"get",k);}
 static String str(Object j,String k){return has(j,k)?R.call(get(j,k),"getAsString").toString():"";}
 static double num(Object j,String k){return has(j,k)?R.num(R.call(get(j,k),"getAsDouble")):0;}
 static boolean flag(Object j,String k){return has(j,k)&&R.yes(R.call(get(j,k),"getAsBoolean"));}
 static List<Object> body(Object detail){List<Object> result=new ArrayList<>();if(has(detail,"body24"))for(Object p:(Iterable<?>)get(detail,"body24"))result.add(R.call(p,"getAsJsonObject"));return result;}
 static Set<String> wounds(Object part){Set<String> w=new LinkedHashSet<>();if(has(part,"wounds"))for(Object x:(Iterable<?>)get(part,"wounds"))w.add(R.call(x,"getAsString").toString());return w;}
 static double health(Object p){return num(p,"max")>0?Math.max(0,Math.min(1,num(p,"hp")/num(p,"max"))):1;}
 public static boolean serious(double hp,Set<String> wounds){return hp<=.35||wounds.contains("fracture")||wounds.contains("gunshot")||wounds.contains("bullet")||wounds.contains("bleed")||wounds.contains("bite");}
 public static String mechanism(String source,String key,boolean criticalBleed,boolean projectile,boolean critical){String evidence=(source+" "+key).toLowerCase(Locale.ROOT);if(evidence.contains("cardiac")||evidence.contains("heart_attack"))return "Paro cardíaco registrado";if(evidence.contains("bleed")||evidence.contains("hemorrhag"))return "Hemorragia fatal registrada";if(evidence.contains("drown")||evidence.contains("asphyx"))return "Asfixia registrada";if(evidence.contains("lava")||evidence.contains("fire")||evidence.contains("burn"))return "Lesión térmica fatal";if(criticalBleed)return "Hemorragia / shock hipovolémico probable";if(projectile)return "Traumatismo penetrante probable";if(critical)return "Traumatismo grave probable";return "Mecanismo no determinado";}
 static void fill(Object g,int x,int y,int w,int h,int color){if(w>0&&h>0)R.call(g,"fill",x,y,x+w,y+h,color);}
 static int text(Object g,Object f,String s,int x,int y,int width,int color){List<?> lines=(List<?>)R.call(f,"split",c(s),Math.max(20,width));for(Object l:lines){R.call(g,"drawString",f,l,x,y,color,false);y+=11;}return y;}
 static void center(Object g,Object f,String s,int x,int y,int color){R.call(g,"drawString",f,s,x-((Number)R.call(f,"width",s)).intValue()/2,y,color,false);}
 static void sprite(Object g,Object p,int x,int y,int width,int height){int index=Arrays.asList(PARTS).indexOf(str(p,"part"));if(index<0)return;double hp=health(p);float r=hp<=.35?.82f:hp<.75?.76f:.56f,green=hp<=.35?.32f:hp<.75?.61f:.66f,b=hp<=.35?.29f:hp<.75?.38f:.62f;Object render=R.type("com.mojang.blaze3d.systems.RenderSystem"),texture=R.call(R.type("net.minecraft.resources.ResourceLocation"),"fromNamespaceAndPath","survivorcreator","textures/gui/autopsy_body.png");try{R.call(render,"setShaderColor",r,green,b,1f);R.call(g,"blit",texture,x,y,width,height,(float)((index%4)*256),(float)((index/4)*448),256,448,1024,896);}finally{R.call(render,"setShaderColor",1f,1f,1f,1f);}if(serious(hp,wounds(p))){int[][] centers={{18,6},{18,23},{29,27},{7,27},{22,47},{14,47},{24,61},{12,61}};int cx=x+centers[index][0]*width/36,cy=y+centers[index][1]*height/64;fill(g,cx-3,cy-1,7,3,0xFFD97762);fill(g,cx-1,cy-3,3,7,0xFFD97762);}}

 public static boolean render(Object g,Object f,int w,int h,long elapsed,Object report,Object detail){return Minimal.render(g,f,w,h,elapsed,report,detail);}
}
