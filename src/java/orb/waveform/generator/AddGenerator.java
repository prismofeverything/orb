package orb.waveform.generator;

import orb.waveform.Generator;

public class AddGenerator extends Generator {
  public double time;
  public Generator a;
  public Generator b;

  public AddGenerator(Generator a, Generator b) {
    super(a, b);
    this.time = 0;
    this.a = a;
    this.b = b;
  }

  public double generate(double delta) {
    this.time += delta;

    double a = this.a.read();
    double b = this.b.read();

    return a + b;
  }
}
