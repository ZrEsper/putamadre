package net.neoforged.neoforge.event.entity.player;
public class PlayerContainerEvent {
    final Object player,container;
    public PlayerContainerEvent(Object player,Object container){this.player=player;this.container=container;}
    public Object getEntity(){return player;}public Object getContainer(){return container;}
    public static class Open extends PlayerContainerEvent{public Open(Object player,Object container){super(player,container);}}
}
