package dev.zomboid.clans;
import java.util.*;import java.lang.ref.WeakReference;
public final class ClanState {
 public WeakReference<Object> server=new WeakReference<>(null);
 public final Set<UUID> pausedOperations=new HashSet<>();
 public final Map<UUID,Long> requests=new HashMap<>();
 public final Map<UUID,Profile> profiles=new HashMap<>();public final Map<UUID,War> wars=new HashMap<>();
 public Profile profile(UUID id){return profiles.computeIfAbsent(id,k->new Profile());}
 public War war(UUID clan){for(War w:wars.values())if(w.a.equals(clan)||w.b.equals(clan))return w;return null;}
 public static final class Profile {public boolean nexusIssued,nexusPlaced;public long nexusPos;public int nexusUpgrade;public int pveXp,pveWins,offer=-1;public long pveNext,offerEnd;public String motto="",badge="RADIO",doctrine="Supervivientes",lastResult="Sin conflictos terminados";public final Map<String,Integer> rewards=new LinkedHashMap<>();public final Map<UUID,Long> cooldowns=new HashMap<>();}
 public static final class War {public UUID id=UUID.randomUUID(),a,b;public boolean siege;public long ready,end;public int scoreA,scoreB;public final Map<UUID,Long> lastVictim=new HashMap<>();public final Map<UUID,Integer> victimCount=new HashMap<>();public final Set<String> objectives=new HashSet<>();public UUID enemy(UUID clan){return a.equals(clan)?b:a;}public int score(UUID clan){return a.equals(clan)?scoreA:scoreB;}public void add(UUID clan,int points){if(a.equals(clan))scoreA=Math.min(100000,scoreA+points);else if(b.equals(clan))scoreB=Math.min(100000,scoreB+points);}}
 public static int rewardBundles(int own,int other,boolean won,ClanConfig cfg){if(own<cfg.killPoints)return 0;return Math.min(cfg.maxRewardBundles,1+own/25+(won?2:0));}
 public static String clean(String s,int limit){return s.replaceAll("[\\p{Cntrl}§]","").trim().substring(0,Math.min(limit,s.replaceAll("[\\p{Cntrl}§]","").trim().length()));}
 public static String row(String...parts){return String.join("\t",Arrays.stream(parts).map(v->Base64.getUrlEncoder().withoutPadding().encodeToString(v.getBytes(java.nio.charset.StandardCharsets.UTF_8))).toArray(String[]::new))+"\n";}
 public static List<String[]> rows(String text){List<String[]> out=new ArrayList<>();for(String line:text.split("\n")){if(line.isEmpty())continue;out.add(Arrays.stream(line.split("\t",-1)).map(s->new String(Base64.getUrlDecoder().decode(s),java.nio.charset.StandardCharsets.UTF_8)).toArray(String[]::new));}return out;}
}
