package minecrafttransportsimulator.mcinterface;
import dev.zomboid.mts.Checks.*;import java.util.*;
public class InterfaceManager {public static Client clientInterface=new Client();public static Packets packetInterface=new Packets();public static class Client {public Player player;public Player getClientPlayer(){return player;}}public static class Packets {public List<Object> packets=new ArrayList<>();public void sendToServer(Object packet){packets.add(packet);}}}
