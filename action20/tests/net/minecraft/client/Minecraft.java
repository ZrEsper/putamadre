package net.minecraft.client;
import dev.zomboidmelee.boxing.Checks16.*;import net.minecraft.world.entity.player.Player;
public class Minecraft{public static final Minecraft INSTANCE=new Minecraft();public Player player;public Object level;public Object screen;public Hit hitResult=new Hit("MISS");public Options options=new Options();public Dispatcher dispatcher=new Dispatcher();public boolean focused=true;public static Minecraft getInstance(){return INSTANCE;}public boolean isWindowActive(){return focused;}public Dispatcher getEntityRenderDispatcher(){return dispatcher;}}
