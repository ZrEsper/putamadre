package dev.zomboid.clans;
import java.util.*;import net.minecraft.client.Minecraft;import net.minecraft.network.chat.Component;import me.danialisntcool.factionfriction.client.ui.ModernButton;
public final class ClientClans {
 public static List<String[]> data=List.of();public static Object connection;
 public static void accept(String text){connection=Minecraft.getInstance().getConnection();data=ClanState.rows(text);}
 public static void reset(){Object c=Minecraft.getInstance().getConnection();if(c!=connection){data=List.of();connection=c;}}
 public static String[] one(String kind){for(String[] row:data)if(row[0].equals(kind))return row;return null;}
 public static List<String[]> all(String kind){List<String[]> out=new ArrayList<>();for(String[] row:data)if(row[0].equals(kind))out.add(row);return out;}
 public static Object operationTier(){return R.get(R.type("me.danialisntcool.factionfriction.faction.FactionTier"),"COLONY");}
 public static String tierLabel(Object tier){return switch(tier.toString()){case "TEAM"->"BANDA";case "COLONY"->"REFUGIO";default->"COALICIÓN";};}
 public static void addEntry(Object screen){int width=((Number)R.get(screen,"width")).intValue();R.call(screen,"addRenderableWidget",new ModernButton(Math.max(2,width-114),4,110,18,Component.literal("Cuaderno de clanes"),b->Minecraft.getInstance().setScreen(new ClanScreen()),0xff29291f,0xff454736));}
}
