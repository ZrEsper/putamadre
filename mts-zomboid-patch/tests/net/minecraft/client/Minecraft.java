package net.minecraft.client;
public class Minecraft {static final Minecraft INSTANCE=new Minecraft();public Object screen;public static Minecraft getInstance(){return INSTANCE;}public Window getWindow(){return new Window();}public static class Window {public long getWindow(){return 42;}}}
