package dev.zomboid.hordes;
import java.util.*;
/** Text shown when hovering every control. */
public final class PanelHelp {
 static final Map<String,String> HELP=Map.ofEntries(
 Map.entry("pool_tontos","Cuantos zombis torpes salen al aparecer. 80/15/5 = 80% tontos, 15% brutos y 5% astutos. 0 = no sale este tipo. Solo nuevos zombis."),
 Map.entry("pool_brutos","Peso de la personalidad intermedia. No significa el monstruo mutant_brute. Los tres pesos se reparten solos."),
 Map.entry("pool_astutos","Peso de los zombis listos, que reaccionan mejor. 0 = ninguno. No cambia el tipo de los que ya existen."),
 Map.entry("ia_inteligencia","Que rapido reaccionan y cuanto recuerdan. 0.5 = mas despacio; 1 = original; 2 = mas rapido. Se aplica a todos los tipos."),
 Map.entry("ia_agresividad","Que insistentes son: mas alto = ataques mas frecuentes y te buscan mas tiempo. No aumenta el dano del golpe."),
 Map.entry("ia_oido","Que lejos oyen tus ruidos. 0 = sordos; 0.5 = mitad; 1 = normal; 2 = doble, respetando el limite de cada sonido."),
 Map.entry("agresivos","Porcentaje de zombis con el perfil extra rapido y fuerte. 0 = ninguno; 100 = todos; -1 = comportamiento original."),
 Map.entry("frecuencia","Cada cuanto aparecen y se reponen zombis. 0.5 = mas lento; 1 = normal; 2 = mas rapido. No crea zombis encima de ti."),
 Map.entry("densidad","Cantidad objetivo por chunk (zona de 16x16 bloques). 0.5 = mitad; 1 = normal; 2 = doble. Respeta el limite de zona."),
 Map.entry("mutantes","Porcentaje de mutantes en los selectores compatibles de hordas. 0 = ninguno. No convierte los zombis que ya existen."),
 Map.entry("limite_mutantes","Tope de mutantes del selector de Zomboid Hordes. No controla los spawns independientes de otros mods."),
 Map.entry("vision","Hasta cuantos bloques pueden verte. 0 = sin vision; -1 = la vision original de cada tipo. Paredes y oscuridad siguen contando."),
 Map.entry("asediadores","Porcentaje de nuevos zombis con IA de asedio. Maximo 1%. Pueden usar sus comportamientos originales de romper o construir."),
 Map.entry("romper_todos","Permite romper bloques a todos los zombis compatibles. Sigue respetando mobGriefing y proteccion de inventarios."),
 Map.entry("construir_todos","Permite la construccion limitada de los zombis compatibles. Respeta mobGriefing."),
 Map.entry("limite_zona","Maximo de zombis activos en una zona fija de 6x6 chunks. Los demas esperan en reserva. Normal: 32."),
 Map.entry("despawn_zombis_todos","AL APLICAR: retira zombis de la poblacion controlada y pausa apariciones. No da loot. Para volver a poblar, usa Regenerar."),
 Map.entry("regenerar_zombis_todos","AL APLICAR: retira la poblacion actual y solicita nuevos zombis con tus ajustes. Reactiva las apariciones. No ocurre al cambiar un preset."),
 Map.entry("limpiar_restos_todos","AL APLICAR: elimina cadaveres y restos, con lo que tengan dentro. Incluye los pendientes en chunks descargados."),
 Map.entry("reiniciar_loot_todos","AL APLICAR: reinicia los cofres y barriles registrados por Mosslorn. El contenido nuevo se genera al abrirlos."),
 Map.entry("poblacion_pausada_todos","Pausada = no crea ni repone zombis. Los que ya existen siguen moviendose y atacando."),
 Map.entry("limpieza_activa_todos","Activa la retirada automatica de objetos tirados. No limpia cofres ni inventarios de jugadores."),
 Map.entry("limpieza_min_minutos","Tiempo minimo entre limpiezas. Se elige un tiempo al azar entre minimo y maximo."),
 Map.entry("limpieza_max_minutos","Tiempo maximo entre limpiezas. Debe ser igual o mayor que el minimo. No carga chunks."),
 Map.entry("limpieza_edad_minutos","Cuanto debe llevar un objeto en el suelo para borrarlo. 1 minuto protege lo recien tirado."),
 Map.entry("limpieza_aviso_todos","Muestra un aviso antes de borrar objetos y el resultado al terminar."),
 Map.entry("limpieza_aviso_segundos","Cuantos segundos antes aparece el aviso. Solo se usa si activaste Avisar en chat."),
 Map.entry("limpiar_objetos_todos","AL APLICAR: hace una limpieza de objetos del suelo, respetando edad y aviso. No se repite por quedar activado."),
 Map.entry("bandidos","Porcentaje de candidatos de TacZ NPCs que se dejan aparecer. 0 = ninguno; 100 = todos los candidatos, sin superar sus limites."),
 Map.entry("zombis_militares","Porcentaje de nuevos zombis con ropa militar compatible. Requiere la integracion del pack."),
 Map.entry("bandidos_militares","Porcentaje de nuevos NPC con atuendo militar. Mantiene las profesiones que ya tenian."),
 Map.entry("epicos","Probabilidad base de loot epico en sistemas compatibles. Si epico+legendario+mitico supera 100, se reparte proporcionalmente."),
 Map.entry("legendarios","Probabilidad base de loot legendario. No cambia objetos ya generados ni armas garantizadas de militares."),
 Map.entry("miticos","Probabilidad de la categoria superior: loot legendario con suministros extra en cadaveres."),
 Map.entry("vision_nocturna","Probabilidad de gafas de vision nocturna compatibles al registrar un cadaver, si hay espacio. No mejora la vision del zombi."),
 Map.entry("ropa_militar","Probabilidad de una pieza militar extra en el cadaver, si existe en el pack y cabe."),
 Map.entry("accesorios_armas","Porcentaje de armas nuevas de Zombie Remains con hasta tres accesorios compatibles. No cambia las que ya tienes."),
 Map.entry("armas_cargadas","Porcentaje de armas nuevas con algo de municion en el cargador. Las demas salen vacias."));
 public static String loot(String category){return switch(category){
 case "armas"->"Armas para combatir: pistolas y rifles TaCZ, cuchillos y melee del catalogo.";
 case "medicinas"->"Para curarte: vendas, apositos, tela, analgesicos, ferulas e inyectores de First Aid; kits medicos LR Tactical, compresas frias y sales compatibles. Su efecto depende del mod.";
 case "comida"->"Para comer: verduras, bocadillos y platos preparados del catalogo. No cura lesiones por si sola.";
 case "herramientas"->"Para trabajar y sobrevivir: hachas, picos, palas, azadas, cubos, canas, reloj, brujula, tijeras, mechero y antorchas. Tambien equipo util compatible.";
 case "ropa"->"Para vestirte y protegerte: ropa, uniformes, calzado, cascos y armaduras. Puede incluir mochilas compatibles.";
 case "municion"->"Balas y cartuchos TaCZ. Cada calibre sirve para armas compatibles; no es un arma completa.";
 case "accesorios"->"Piezas para mejorar armas: miras, empunaduras, silenciadores, compensadores y cargadores compatibles.";
 case "materiales"->"Ingredientes para fabricar: lingotes, polvora, piezas, semillas y otros materiales del catalogo. No son medicinas ni comida lista.";
 default->"Objetos del catalogo seleccionado.";};}
 public static String[][] hints(String[][] keys,String[][] old){String[][] out=new String[keys.length][];for(int p=0;p<keys.length;p++){out[p]=new String[keys[p].length];for(int i=0;i<keys[p].length;i++){String key=keys[p][i],text=HELP.getOrDefault(key,old[p][i]);
  for(String prefix:List.of("cofres","drops","cadaveres"))if(key.startsWith(prefix+"_")){
   String k=key.substring(prefix.length()+1);text=k.equals("loot_todos")?"Original = deja las tablas del pack. Personalizado = usa las categorias de esta pestaña. Solo cambia loot nuevo.":k.equals("cantidad")?"Cuantas selecciones de loot se hacen: 0 = nada; 1 = normal; 2 = el doble, si hay espacio. No asegura objetos concretos.":loot(k)+" Peso al sortear: 0 excluye esta categoria. Los pesos se normalizan; no tienen que sumar 100. Ver ejemplo muestra una simulacion, no una promesa.";
  }
  if(key.startsWith("velocidad_"))text="Que rapido se mueve este tipo de zombi. 0 = quieto; 0.5 = mitad; 1 = original; 2 = doble. No cambia los demas tipos.";
  if(key.startsWith("dano_"))text="Cuanto pega este tipo en cuerpo a cuerpo. 0 = sin dano; 0.5 = mitad; 1 = original; 2 = doble. Ataques especiales de otros mods pueden usar sus propias reglas.";
  out[p][i]=text+" Los cambios se guardan al pulsar Aplicar.";
 }}return out;}
}
