package dev.zomboid.firstaid;
import net.minecraft.server.level.ServerPlayer;import net.minecraft.world.entity.monster.Zombie;import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
/** Real Wounds45 and real TreatmentV33; game boundary types are minimal fixtures. */
public class BiteRegression47 {
 public static class State {public Object[] wounds=new Object[8];}
 public static class Part {public float currentHealth;Part(float h){currentHealth=h;}}
 public static class Model {public int syncs;public float health;public Model(float h){health=h;}public Object getFromEnum(Object part){return new Part(part==EnumPlayerPart.HEAD?health:10);}public void scheduleResync(){syncs++;}}
 public static class Source {public Object entity;Source(Object e){entity=e;}public Object getDirectEntity(){return entity;}public Object getEntity(){return entity;}}
 public static void main(String[]args){var player=new ServerPlayer(1);var before=new Model(10);var after=new Model(9);var state=new State();for(int i=0;i<8;i++)state.wounds[i]=R.make("ichttt.mods.firstaid.survival.ClinicalState$Wound");ichttt.mods.firstaid.survival.ClinicalData.current=state;
  // Previously any server-player hit looked up the nonexistent class before testing its source.
  Wounds45.bite(after,player,new Source(null),before);if(after.syncs!=0)throw new AssertionError("non-zombie source created bite mark");
  R.set(state.wounds[0],"bitten",true);R.set(state.wounds[0],"healingLeft",2400);Wounds45.bite(after,player,new Source(new Zombie()),before);int[]marks=(int[])R.get(state.wounds[0],"marks45");if(marks.length!=5||marks[3]!=2||after.syncs!=1)throw new AssertionError("accepted zombie bite did not create and synchronize one mark");if(marks[0]!=191||marks[1]!=170||marks[2]!=0)throw new AssertionError("mark used fallback bounds instead of actual TaCZ head box: "+java.util.Arrays.toString(marks));for(int i=1;i<8;i++)if(((int[])R.get(state.wounds[i],"marks45")).length!=0)throw new AssertionError("uninjured limb received mark");
  System.out.println("REAL_WOUNDS45_AND_REAL_TREATMENTV33_NULL_SOURCE_ZOMBIE_BITE_AND_REGION=PASS");
 }
}
