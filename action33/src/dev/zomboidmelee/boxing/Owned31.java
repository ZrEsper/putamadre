package dev.zomboidmelee.boxing;
public final class Owned31{
 public static boolean active(Object p){return p!=null&&(Slide28.lowVisual(p)||Parkour20.active(p)||MoveClient25.slide(p)>0||Mantle31.weight(p)>0);}
 public static boolean local(){return active(R.get(BoxingClient16.mc(),"player"));}
 public static boolean event(Object e){return active(R.call(e,"getEntity"));}
}
