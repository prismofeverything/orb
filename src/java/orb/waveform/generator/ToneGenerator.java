package orb.waveform.generator;

import orb.waveform.Generator;
import orb.event.Event;

public class ToneGenerator extends Generator implements Event {
  public int tone;
  public int energy;

  public ToneGenerator() {
    super();
    this.tone = -1;
    this.energy = 0;
  }

  public void noteOn(int tone, int energy, long time) {
    System.out.printf("TONE: note on %d", tone);
    this.tone = tone;
    this.energy = energy;
  }

  public void noteOff(int tone, long time) {
    this.tone = -1;
    this.energy = 0;
  }

  public double generate(double delta) {
    return this.tone;
  }
}
