package orb.waveform.generator;

import orb.waveform.Generator;
import orb.event.Event;

public class ToneGenerator extends Generator implements Event {
  public int tone;
  public int velocity;

  public ToneGenerator() {
    super();
    this.tone = -1;
    this.velocity = 0;
  }

  public void on(int channel, long time, int tone, int velocity) {
    this.tone = tone;
    this.velocity = velocity;
  }

  public void off(int channel, long time, int tone) {
    this.tone = -1;
    this.velocity = 0;
  }

  public void control(int channel, long time, int control, int data) {};
  public void pressure(int channel, long time, int pressure) {};
  public void pitch(int channel, long time, int pitch) {};

  public double generate(double delta) {
    return this.tone;
  }
}
