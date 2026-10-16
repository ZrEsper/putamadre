package dev.zomboid.firstaid;public class Revival{public static boolean active(Object m){return m instanceof AidChecks.Model model&&model.criticalConditionActive;}}
