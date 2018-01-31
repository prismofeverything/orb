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

    double frequency = this.frequency.read(delta);
    double amplitude = this.amplitude.read(delta);

    this.acceleration = -frequency * frequency * this.position;
    this.velocity += delta * this.acceleration;
    this.position += delta * this.velocity;

    return position * amplitude;
  }

  public String toString() {
    return String.format("p = %f, v = %f, a = %f", this.position, this.velocity, this.acceleration);
  }
}
