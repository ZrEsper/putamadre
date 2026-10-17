package dev.zomboidmelee.boxing;
public class SlideCrouchRegression26 {
 public static void main(String[]args){var p=new ParkourChecks25.Player();p.sprinting=true;Slide20.tick(p);p.physics.time++;p.pz+=.2;Slide20.tick(p);p.physics.time++;p.sprinting=false;p.pz+=.03;Slide20.tick(p);Move25.request(p,2);if(!Slide20.STATES.containsKey(p))throw new AssertionError("crouch deceleration incorrectly discards running slide");System.out.println("RUN_TO_CROUCH_DECELERATION_REGRESSION_PASS");}
}
