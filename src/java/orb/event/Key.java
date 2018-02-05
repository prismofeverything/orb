package orb.event;

import orb.event.Event;
import orb.event.Keyboard;
import orb.waveform.Generator;
import orb.waveform.generator.ToneGenerator;
import orb.waveform.generator.EnergyGenerator;

public class Key implements Event {
  public boolean on;
  public ToneGenerator tone;
  public EnergyGenerator energy;

  public Key() {
    super();
    this.on = false;
    this.tone = new ToneGenerator();
    this.energy = new EnergyGenerator();
  }

  public void noteOn(int tone, int energy, long time) {
    this.on = true;
    this.tone.intone(tone, energy);
    this.energy.intone(tone, energy);
  }

  public void noteOff(int tone, long time) {
    this.on = false;
    this.tone.cease(tone);
    this.energy.cease(tone);
  }
}
