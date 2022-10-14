package orb.event;

import java.util.Map;

public class PrintEvent implements Event {
  public PrintEvent() {}

  public void on(int channel, long time, int tone, int velocity) {
    System.out.println("on!       " + time + " - " + channel + ": " + tone + " > " + velocity);
  }

  public void off(int channel, long time, int tone) {
    System.out.println("off!      " + time + " - " + channel + ": " + tone);
  }

  public void control(int channel, long time, int control, int data) {
    System.out.println("control!  " + time + " - " + channel + ": " + control + " > " + data);
  }

  public void pressure(int channel, long time, int pressure) {
    System.out.println("pressure! " + time + " - " + channel + ": " + pressure);
  }

  public void pitch(int channel, long time, int base, int detail) {
    System.out.println("pitch!    " + time + " - " + channel + ": " + base + " + " + detail);
  }
}
