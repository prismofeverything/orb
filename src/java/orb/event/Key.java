package orb.event;

import orb.event.Event;
import orb.event.Keyboard;
import orb.waveform.Generator;
import orb.waveform.generator.ToneGenerator;
import orb.waveform.generator.VelocityGenerator;

public class Key implements Event {
  public boolean on;
  public ToneGenerator tone;
  public VelocityGenerator velocity;

  public Key() {
    super();
    this.on = false;
    this.tone = new ToneGenerator();
    this.velocity = new VelocityGenerator();
  }

  public void on(int channel, long time, int tone, int velocity) {
    this.on = true;
    this.tone.on(channel, time, tone, velocity);
    this.velocity.on(channel, time, tone, velocity);
  }

  public void off(int channel, long time, int tone) {
    this.on = false;
    this.tone.off(channel, time, tone);
    this.velocity.off(channel, time, tone);
  }

  public void control(int channel, long time, int control, int data) {};
  public void pressure(int channel, long time, int pressure) {};
  public void pitch(int channel, long time, int base, int detail) {};
}
