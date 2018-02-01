package orb.event;

import java.util.Map;

import orb.event.Event;

public class NoteEvent implements Event {
  public void noteOn(int tone, int energy, long time) {}
  public void noteOff(int tone, long time) {}
}
