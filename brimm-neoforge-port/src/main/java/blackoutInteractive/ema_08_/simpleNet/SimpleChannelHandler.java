package blackoutInteractive.ema_08_.simpleNet;
import java.util.List;
public class SimpleChannelHandler extends AbstractChannelHandler {
 public SimpleChannelHandler(String id,String name,String version,List<Class<? extends APacket>> packets){super(id,name,version);packets.forEach(this::register);}
}
