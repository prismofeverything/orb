package orb.waveform.generator;

import orb.waveform.Generator;

public class SineGenerator implements Generator {
  public double time;
  public double mass;
  public double force;
  public double ratio;

  public double position;
  public double velocity;
  public double acceleration;

  public SineGenerator(double mass, double force, double position) {
    this.time = 0;
    this.mass = mass;
    this.force = force;
    this.ratio = this.force / this.mass;

    this.position = position;
    this.velocity = 0;
    this.acceleration = 0;
  }

  public double generate(double time) {
    double delta = time - this.time;
    this.time = time;

    this.acceleration = -1 * this.ratio * this.position;
    this.velocity += delta * this.acceleration;
    this.position += delta * this.velocity;
    return position;
  }

  public String toString() {
    return String.format("p = %f, v = %f, a = %f", this.position, this.velocity, this.acceleration);
  }
}
