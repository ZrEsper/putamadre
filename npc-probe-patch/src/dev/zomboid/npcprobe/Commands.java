package dev.zomboid.npcprobe;
import java.util.*;import java.lang.reflect.Proxy;import java.util.function.Predicate;
public final class Commands {
 static Object literal(String name){return R.call(R.type("net.minecraft.commands.Commands"),"literal",name);}
 static Object execute(String action){Class<?> command=R.type("com.mojang.brigadier.Command");return Proxy.newProxyInstance(command.getClassLoader(),new Class<?>[]{command},(p,m,a)->{if(m.getName().equals("run")){try{return run(R.call(a[0],"getSource"),action);}catch(RuntimeException e){R.warn("Comando NPC",e);Object source=R.call(a[0],"getSource");R.call(source,"sendFailure",R.call(R.type("net.minecraft.network.chat.Component"),"literal","No se pudo completar: "+e.getMessage()));return 0;}}if(m.getName().equals("toString"))return "NPC prueba "+action;if(m.getName().equals("hashCode"))return System.identityHashCode(p);return p==a[0];});}
 public static void register(Object event){Object root=literal("npcprueba");R.call(root,"requires",(Predicate<Object>)s->R.yes(R.call(s,"hasPermission",2)));R.call(root,"executes",execute("ayuda"));
  for(String action:List.of("crear","quieto","aliado","enemigo","equipo","disparar","recargar","melee","derribar","reanimar","curar","estado","reiniciar","retirar","matar")){Object child=literal(action);R.call(child,"executes",execute(action));R.call(root,"then",child);}
  Object damage=literal("herir");for(int i=0;i<8;i++){Object child=literal(Parts.NAMES[i]);R.call(child,"executes",execute("herir_"+i));R.call(damage,"then",child);}R.call(root,"then",damage);R.call(R.call(event,"getDispatcher"),"register",root);
 }
 static Object selected(Object player){Object world=R.call(player,"level"),box=R.call(R.call(player,"getBoundingBox"),"inflate",32d);String owner=String.valueOf(R.call(player,"getUUID"));List<?> list=(List<?>)R.call(world,"getEntitiesOfClass",R.type(Probe.NPC),box,(Predicate<Object>)n->Probe.marked(n)&&owner.equals(R.call(Probe.tag(n),"getString","np15_owner"))&&R.yes(R.call(n,"isAlive")));return list.stream().min(Comparator.comparingDouble(n->R.num(R.call(player,"distanceToSqr",n)))).orElse(null);}
 static void say(Object p,String text){R.call(p,"sendSystemMessage",R.call(R.type("net.minecraft.network.chat.Component"),"literal",text));}
 static void equipment(Object npc,Object p){for(String key:List.of("MAINHAND","OFFHAND","HEAD","CHEST","LEGS","FEET")){Object slot=R.get(R.type("net.minecraft.world.entity.EquipmentSlot"),key);R.call(npc,"setItemSlot",slot,R.call(R.call(p,"getItemBySlot",slot),"copy"));}R.set(npc,"drawn",false);}
 static Object create(Object p){Object world=R.call(p,"level");Map<?,?> templates=(Map<?,?>)R.get(R.type("mod.cdv.tnpc.data.TemplateDataManager"),"TEMPLATES");if(templates.isEmpty())throw new IllegalArgumentException("Faltan plantillas NPC. Ejecuta /tacznpcs_reload.");String key=templates.keySet().stream().map(String::valueOf).filter(k->k.toLowerCase(Locale.ROOT).contains("bandit")).findFirst().orElse(String.valueOf(templates.keySet().iterator().next()));Object npc=R.call(R.type(Probe.NPC),"create",world,key);
  double yaw=Math.toRadians(R.num(R.call(p,"getYRot"))),x=R.coord(p,"X")-Math.sin(yaw)*3,z=R.coord(p,"Z")+Math.cos(yaw)*3;R.call(npc,"moveTo",x,R.coord(p,"Y"),z,(float)R.num(R.call(p,"getYRot")),0f);if(!R.yes(R.call(world,"noCollision",npc)))throw new IllegalArgumentException("No hay sitio delante de ti. Usa un lugar despejado.");
  Object tag=Probe.tag(npc);R.call(tag,"putBoolean","np15_test",true);R.call(tag,"putString","np15_owner",String.valueOf(R.call(p,"getUUID")));R.call(tag,"putString","np15_mode","quieto");
  Object skin=R.call(R.call(npc,"getEntityData"),"get",R.get(R.type(Probe.NPC),"SYNCED_SKIN"));skin=R.call(skin,"copy");R.call(skin,"putBoolean","np15_test",true);R.call(R.call(npc,"getEntityData"),"set",R.get(R.type(Probe.NPC),"SYNCED_SKIN"),skin);
  R.call(npc,"setPersistenceRequired");R.call(npc,"setCanPickUpLoot",false);equipment(npc,p);Parts.init(npc,p);Parts.sync(npc);Probe.prepare(npc);if(!R.yes(R.call(world,"addFreshEntity",npc)))throw new IllegalArgumentException("El mundo rechazo el NPC.");return npc;
 }
 static int run(Object source,String action){if(!R.yes(R.call(source,"hasPermission",2)))return 0;Object p=R.call(source,"getPlayerOrException");if(action.equals("ayuda")){say(p,"NPC: /npcprueba crear | quieto | aliado | enemigo | equipo | estado | derribar | curar | reiniciar | matar | retirar. Heridas: /npcprueba herir cabeza (u otra parte). Reanimación natural: clic derecho, mano vacía, 8 segundos cerca. Curación: apunta a una parte con venda/apósito y clic derecho.");return 1;}
  Object npc=selected(p);if(action.equals("crear")){if(npc!=null){say(p,"Ya tienes un NPC de pruebas cerca. Usa reiniciar, o retiralo antes de crear otro.");return 0;}create(p);say(p,"NPC creado en modo quieto. Copia de tu equipo; solo para pruebas de administrador.");return 1;}
  if(npc==null){say(p,"No hay un NPC tuyo a 32 bloques. Usa /npcprueba crear.");return 0;}
  if(List.of("quieto","aliado","enemigo").contains(action)){R.call(Probe.tag(npc),"putString","np15_mode",action);Probe.name(npc);say(p,"Modo: "+action+". Enemigo solo atacara a su propietario en supervivencia.");}
  else if(action.equals("equipo")){equipment(npc,p);say(p,"Equipo copiado al NPC, sin quitar el tuyo.");}
  else if(action.equals("estado")){say(p,Parts.summary(npc));say(p,"Estado: "+(Probe.down(npc)?"derribado":"activo")+" | modo "+Probe.mode(npc));}
  else if(action.startsWith("herir_")){Parts.apply(npc,Integer.parseInt(action.substring(6)),4);say(p,Parts.summary(npc));}
  else if(action.equals("derribar")){Parts.apply(npc,1,Parts.maxima(npc)[1]);say(p,"Derribado. Prueba la reanimación con mano vacía.");}
  else if(action.equals("curar")||action.equals("reiniciar")){Parts.reset(npc);say(p,"Salud de todas las partes restaurada.");}
  else if(action.equals("reanimar")){if(Probe.down(npc))Parts.revive(npc);say(p,"Reanimación administrativa; para probar los 8 segundos, usa clic derecho.");}
  else if(action.equals("matar"))Probe.finish(npc,R.call(R.call(npc,"damageSources"),"genericKill"));
  else if(action.equals("retirar"))R.call(npc,"discard");
  else if(action.equals("recargar"))R.call(npc,"reload");
  else if(action.equals("disparar")||action.equals("melee")){if(!Probe.mode(npc).equals("enemigo")||R.yes(R.call(p,"isCreative"))||R.yes(R.call(p,"isSpectator"))){say(p,"Usa modo enemigo y supervivencia para probar sus ataques contra ti.");return 0;}if(action.equals("disparar"))Probe.fire(npc,p);else Probe.melee(npc,p);}
  return 1;
 }
}
