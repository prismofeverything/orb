package orb.event;

import java.util.Map;

public interface Event {
  public void on(int channel, long time, int tone, int velocity);
  public void off(int channel, long time, int tone);
  public void control(int channel, long time, int control, int data);
  public void pressure(int channel, long time, int pressure);
  public void pitch(int channel, long time, int base, int detail);
}
