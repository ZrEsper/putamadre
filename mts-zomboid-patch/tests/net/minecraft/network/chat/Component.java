package net.minecraft.network.chat;public class Component {public String text;private Component(String t){text=t;}public static Component literal(String t){return new Component(t);}}
