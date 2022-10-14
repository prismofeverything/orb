package orb.waveform.generator;

import orb.waveform.Generator;
import orb.event.Event;

public class VelocityGenerator extends Generator implements Event {
  public final static int SPAN = 128;
  public final static double RECIPROCAL_SPAN = 1.0 / SPAN;

  public int tone;
  public double velocity;

  public VelocityGenerator() {
    super();
    this.tone = 0;
    this.velocity = 0;
  }

  public void on(int channel, long time, int tone, int velocity) {
    this.tone = tone;
    this.velocity = velocity * RECIPROCAL_SPAN;
  }

  public void off(int channel, long time, int tone) {
    this.tone = -1;
    this.velocity = 0;
  }

  public void control(int channel, long time, int control, int data) {};
  public void pressure(int channel, long time, int pressure) {};
  public void pitch(int channel, long time, int pitch) {};

  public double generate(double delta) {
    return this.velocity;
  }
}
