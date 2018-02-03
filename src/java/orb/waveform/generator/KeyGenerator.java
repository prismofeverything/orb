package orb.waveform.generator;

import orb.waveform.Generator;
import orb.event.Keyboard;

public class KeyGenerator extends Generator {
  public Keyboard keyboard;
  public int voice;
  public double time;

  public KeyGenerator(Keyboard keyboard, int voice) {
    super();
    this.keyboard = keyboard;
    this.voice = voice;
    this.time = 0;
  }

  public double generate(double delta) {
    return this.keyboard.read(this.voice);
  }
}
