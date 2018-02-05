package orb.waveform.generator;

import orb.waveform.Generator;

public class EnergyGenerator extends Generator {
  public int tone;
  public int energy;

  public EnergyGenerator() {
    super();
    this.tone = 0;
    this.energy = 0;
  }

  public void intone(int tone, int energy) {
    this.tone = tone;
    this.energy = energy;
  }

  public void cease(int tone) {
    this.tone = 0;
    this.energy = 0;
  }

  public double generate(double delta) {
    return this.energy;
  }
}
