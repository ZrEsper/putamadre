package dev.zomboid.quietly;
public class Checks {
 public static class Callback {double value;int updates;Callback(double value){this.value=value;}public Double getReturnValue(){return value;}public void setReturnValue(Object value){this.value=((Number)value).doubleValue();updates++;}}
 public static void main(String[] args){Callback survival=new Callback(4.5);Reach.extend(survival);if(survival.value!=5||survival.updates!=1)throw new AssertionError("survival reach");Callback creative=new Callback(5);Reach.extend(creative);if(creative.value!=5||creative.updates!=0)throw new AssertionError("creative reach");Callback longer=new Callback(7);Reach.extend(longer);if(longer.value!=7||longer.updates!=0)throw new AssertionError("extended reach must remain");if(Reach.minimum(3)!=5)throw new AssertionError("reduced reach");System.out.println("Passed four reach and callback checks (API doubles)");}
}
