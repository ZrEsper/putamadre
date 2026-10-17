package dev.zomboidmelee.boxing;
/** Exact entry point used by TaCZ .19 MovementCompat20 before its prone toggle. */
public final class Bodycam20 {
 public static boolean proneKey(int key,int scan,int action){if(action!=1)return false;boolean matches=Keys26.slide!=null&&R.yes(R.call(Keys26.slide,"matches",key,scan));if(!matches)try{matches=R.yes(R.call(R.get(R.type("com.locknar.taczammocheck.prone.ProneClient"),"KEY"),"matches",key,scan));}catch(IllegalStateException absent){}if(!matches)return false;Object m=BoxingClient16.mc(),p=R.get(m,"player");if(p==null||R.get(m,"screen")!=null||!R.yes(R.call(m,"isWindowActive")))return false;if(Slide20.CLIENT.containsKey(p))return true;return MoveClient25.trySlide(p,BoxingClient16.CLOCK.getAsLong());}
}
