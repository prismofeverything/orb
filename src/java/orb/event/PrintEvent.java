package orb.event;

import java.util.Map;

public class PrintEvent implements Event {
  public PrintEvent() {}

  public void noteOn(int tone, int energy, long time) {
    System.out.println("on! " + time + " - " + tone + ": " + energy);
  }

  public void noteOff(int tone, long time) {
    System.out.println("off " + time + " - " + tone + ": 0");
  }
}
