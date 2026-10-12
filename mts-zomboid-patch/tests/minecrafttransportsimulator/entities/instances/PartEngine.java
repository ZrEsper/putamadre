package minecrafttransportsimulator.entities.instances;
import dev.zomboid.mts.Checks.*;import java.util.*;
public class PartEngine extends APart {public Definition definition=new Definition();public boolean running;public double rpm=800,temp=70,pressure=50;public Variable currentGearVar=new Variable(0),isAutomaticVar=new Variable(0),damageVar=new Variable(0),hoursVar=new Variable(0),shiftUpVar=new Variable(0),shiftDownVar=new Variable(0);Map<String,Variable> vars=new HashMap<>();public Variable getOrCreateVariable(String n){return vars.computeIfAbsent(n,k->new Variable(0));}}
