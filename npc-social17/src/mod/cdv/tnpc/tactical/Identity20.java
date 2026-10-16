package mod.cdv.tnpc.tactical;
import java.util.*;
/** Fictional combinations; independent seed keeps existing personalities unchanged. */
public final class Identity20 {
 static final String[] FIRST={"Milo","Lena","Dario","Nora","Ivo","Alma","Bruno","Vera","Elio","Mara","Gael","Lia","Axel","Iris","Noel","Cora","Teo","Zoe","Rene","Ada","Hugo","Nina","Leon","Eva","Silas","Mina","Luca","Ayla","Aron","Yara","Nico","Ines"};
 static final String[] LAST={"Varela","Montel","Arven","Valden","Rovira","Belmar","Soler","Neral","Calden","Virel","Orlan","Dalen","Miret","Lorian","Sorel","Navar","Brelan","Torven","Alven","Rendal","Montal","Leroux","Venn","Noren","Morlan","Valera","Dorel","Ferran","Almar","Vidal","Cervan","Ravel"};
 static void init(Object n){Object tag=Social.tag(n);if(!Social.s(tag,"soc_full_name20").isEmpty())return;Random r=new Random(Social.uuid(n).getMostSignificantBits()^Social.uuid(n).getLeastSignificantBits()^0x5a87be291L);int female=r.nextBoolean()?1:0;String name=FIRST[2*r.nextInt(FIRST.length/2)+female]+" "+LAST[r.nextInt(LAST.length)];Social.c(tag,"putString","soc_full_name20",name);Social.c(tag,"putInt","soc_age20",18+r.nextInt(55));Social.c(tag,"putString","soc_gender20",female==1?"female":"male");Appearance20.assign(n,2*r.nextInt(24)+female);Social.c(n,"setCustomName",Social.c(Social.t("net.minecraft.network.chat.Component"),"literal",name));}
 static String name(Object n){init(n);return Social.s(Social.tag(n),"soc_full_name20");}static int age(Object n){init(n);return Social.i(Social.tag(n),"soc_age20");}
 static String attitude(Object n,Object p){if(Social.member(n,p))return "friendly";if(Social.kind(n)==0||Social.trust(n,p)<=-35||Social.s(Social.tag(n),"soc_enemy").equals(Social.identity(p).toString())&&Social.l(Social.tag(n),"soc_enemy_until")>Social.time(n))return Social.kind(n)==2?"afraid":"enemy";if(Social.kind(n)==2||Social.trust(n,p)>=15)return "friendly";return "neutral";}
}
