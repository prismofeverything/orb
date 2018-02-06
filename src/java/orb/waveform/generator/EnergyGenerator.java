package orb.waveform.generator;

import orb.waveform.Generator;
import orb.event.Event;

public class EnergyGenerator extends Generator implements Event {
  public final static int SPAN = 128;
  public final static double RECIPROCAL_SPAN = 1.0 / SPAN;

  public int tone;
  public double energy;

  public EnergyGenerator() {
    super();
    this.tone = 0;
    this.energy = 0;
  }

  public void noteOn(int tone, int energy, long time) {
    this.tone = tone;
    this.energy = energy * RECIPROCAL_SPAN;
  }

  public void noteOff(int tone, long time) {
    this.tone = 0;
    this.energy = 0;
  }

  public double generate(double delta) {
    return this.energy;
  }
}
