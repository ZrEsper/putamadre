package dev.zomboid.reactive;
public class Checks {
 static int count;static void check(boolean c,String name){if(!c)throw new AssertionError(name);count++;}
 public static void main(String[] args){
  check(Rules.steps(true,false,false,false,false)>Rules.steps(false,false,false,false,false),"running louder");
  check(Rules.steps(false,true,false,false,false)<Rules.steps(false,false,false,false,false),"crouching quieter");
  check(Math.abs(Rules.steps(true,false,false,true,false)/Rules.steps(true,false,false,false,false)-.55)<.001,"elusive reduces running steps by 45 percent");
  check(Rules.steps(true,false,true,false,false)>Rules.steps(true,false,false,false,false),"splash noise");
  check(Rules.vision(2,false,false)>Rules.vision(1,false,false)&&Rules.vision(1,false,false)>Rules.vision(0,false,false),"vision variation");
  check(Rules.hearing(0,0)>=.85&&Rules.hearing(2,1)<=1.45,"no near deaf zombies");
  check(Rules.hearingRange(18,1,true)<Rules.hearingRange(18,1,false),"walls attenuate hearing");
  check(Rules.dodge(98,100,100)&&!Rules.dodge(95,100,100)&&!Rules.dodge(101,100,100)&&!Rules.dodge(98,100,101),"timed dodge, no late input or cooldown spam");
  check(Rules.ambient("minecraft:entity.zombie.ambient",1)==0&&Rules.ambient("minecraft:block.grass.step",1)==0,"avoid zombie feedback and unfiltered footsteps");
  check(Rules.ambient("minecraft:block.chest.open",1)==8&&Rules.ambient("minecraft:entity.generic.explode",1)==72,"environment noise categories");
  check(Rules.pace(.99)==3&&Rules.speed(3)>Rules.speed(0),"runners faster");
  System.out.println(count+" core perception/noise/dodge rule cases passed");
 }
}
