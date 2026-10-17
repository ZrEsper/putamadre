package dev.zomboidmelee.boxing;
public final class InteractClient22{
 public static void show(Object p){if(p!=R.get(BoxingClient16.mc(),"player"))return;if(Client20.kind(p)==Motion20.STOMP&&Client20.phase(p)<1)return;Client20.begin(p,Motion20.INTERACT,false);}
}
