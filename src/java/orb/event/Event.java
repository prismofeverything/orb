package orb.event;

import java.util.Map;

public interface Event {
  public void noteOn(int tone, int energy, long time);
  public void noteOff(int tone, long time);
}
