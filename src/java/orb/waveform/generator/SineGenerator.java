package orb.waveform.generator;

import orb.waveform.Generator;

public class SineGenerator implements Generator {
  public double time;
  public double frequency;

  public double position;
  public double velocity;
  public double acceleration;

  public SineGenerator(double frequency, double amplitude) {
    this.time = 0;
    this.frequency = frequency;

    this.position = amplitude;
    this.velocity = 0;
    this.acceleration = 0;
  }

  public double generate(double time) {
    double delta = time - this.time;
    this.time = time;

    this.acceleration = -this.frequency * this.frequency * this.position;
    this.velocity += delta * this.acceleration;
    this.position += delta * this.velocity;
    return position;
  }

  public String toString() {
    return String.format("p = %f, v = %f, a = %f", this.position, this.velocity, this.acceleration);
  }
}
