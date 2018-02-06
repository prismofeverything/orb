package orb.waveform.generator;

import orb.waveform.Generator;

public class SineGenerator extends Generator {
  public double time;
  public Generator frequency;
  public Generator amplitude;

  public double position;
  public double velocity;
  public double acceleration;

  public SineGenerator(Generator frequency, Generator amplitude) {
    super(frequency, amplitude);
    this.time = 0;
    this.frequency = frequency;
    this.amplitude = amplitude;

    this.position = 1;
    this.velocity = 0;
    this.acceleration = 0;
  }

  public double generate(double delta) {
    this.time += delta;

    double frequency = this.frequency.read();
    double amplitude = this.amplitude.read();

    this.acceleration = -frequency * frequency * this.position;
    this.velocity += delta * this.acceleration;
    this.position += delta * this.velocity;

    if (this.position > 1) {
      this.velocity *= 1.0 / this.position;
      this.position = 1;
    }

    return position * amplitude;
  }
}
