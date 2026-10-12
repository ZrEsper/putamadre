package minecrafttransportsimulator.guis.components;
import java.util.*;
public class AGUIBase {public int guiLeft,guiTop;public Object background=new GUIComponentCutout(this,0,0,620,370,0,0,620,370);public List<Object> components=new ArrayList<>();public void setupComponents(){}public void setStates(){}public void close(){}protected void addComponent(Object o){components.add(o);}protected String getTexture(){return "";}}
