package dev.zomboid.clans;
public final class PveRules {
 public static int rarity(int roll){return roll<60?0:roll<85?1:roll<95?2:roll<99?3:4;}
 public static int count(int rarity){return new int[]{12,20,28,36,48}[rarity];}
 public static int special(int rarity){return new int[]{0,1,2,4,6}[rarity];}
 public static int xp(int rarity){return new int[]{20,35,60,100,160}[rarity];}
 public static int level(int xp){return xp>=300?3:xp>=100?2:1;}
 public static String label(int r){return new String[]{"Común","Inusual","Rara","Épica","Legendaria"}[r];}
}
