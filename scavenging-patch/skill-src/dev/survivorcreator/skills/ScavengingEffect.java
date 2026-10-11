package dev.survivorcreator.skills;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
public final class ScavengingEffect implements SkillEffect {
 public void apply(ServerPlayer p,int level,ResourceLocation id){}
 public Component describe(int level){int n=Math.max(0,Math.min(10,level));return Component.literal("Búsqueda: "+String.format(java.util.Locale.ROOT,"%.1f s",(30-2*n)/20.0)+" · +"+(n/2)+" hallazgos · +"+String.format(java.util.Locale.ROOT,"%.2f",n*.75)+" suerte · mejores rarezas · +"+(n/3)+" unidades/hallazgo · -"+(n*2.5)+"% desgaste de herramientas");}
}
