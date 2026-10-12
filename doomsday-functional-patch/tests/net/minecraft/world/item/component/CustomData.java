package net.minecraft.world.item.component;
import dev.zomboid.decor.Checks.Tag;
public class CustomData {Tag tag;public CustomData(Tag t){tag=t;}public Tag copyTag(){return tag;}public static CustomData of(Tag t){return new CustomData(t);}}
