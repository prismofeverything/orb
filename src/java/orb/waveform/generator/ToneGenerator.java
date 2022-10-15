package orb.waveform.generator;

import orb.waveform.Generator;
import orb.event.Event;

public class ToneGenerator extends Generator implements Event {
  public String mode;
  public int tone;
  public int pitch;
  public int velocity;

  public ToneGenerator() {
    super();

    this.mode = "tone";
    this.tone = -1;
    this.pitch = 0;
    this.velocity = 0;
  }

  public ToneGenerator(String mode) {
    super();

    this.mode = mode;
    this.tone = -1;
    this.pitch = 0;
    this.velocity = 0;
  }

  public void on(int channel, long time, int tone, int velocity) {
    this.tone = tone;
    this.velocity = velocity;
    this.pitch = 0;
  }

  public void off(int channel, long time, int tone) {
    this.tone = -1;
    this.velocity = 0;
    this.pitch = 0;
  }

  public void control(int channel, long time, int control, int data) {};
  public void pressure(int channel, long time, int pressure) {};

  public void pitch(int channel, long time, int pitch) {
    this.pitch = pitch;
  };

  public double generate(double delta) {
    if (this.mode == "velocity") {
      return this.velocity;
    } else if (this.mode == "pitch") {
      return this.pitch;
    } else {
      return this.tone;
    }
  }
}
