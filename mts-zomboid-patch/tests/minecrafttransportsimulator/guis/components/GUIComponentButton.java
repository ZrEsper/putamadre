package minecrafttransportsimulator.guis.components;
import minecrafttransportsimulator.baseclasses.ColorRGB;
public class GUIComponentButton extends GUIComponentCutout {public GUIComponentButton(AGUIBase gui,int x,int y,int w,int h,String label,boolean center,ColorRGB color,boolean background){super(gui,x,y,w,h,0,0,w,h);text=label;}public void onClicked(boolean left){}}
