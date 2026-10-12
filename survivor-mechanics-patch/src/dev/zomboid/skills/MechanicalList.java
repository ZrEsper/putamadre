package dev.zomboid.skills;
import java.util.*;
public final class MechanicalList {public static List<String> include(List<String> original){ArrayList<String> skills=new ArrayList<>(original);if(!skills.contains("mechanics"))skills.add("mechanics");return Collections.unmodifiableList(skills);}}
