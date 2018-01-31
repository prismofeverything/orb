package orb.waveform.generator;

import orb.waveform.Generator;

public class MultiplyGenerator extends Generator {
  public double time;
  public Generator x;
  public Generator y;

  public MultiplyGenerator(Generator x, Generator y) {
    super(x, y);
    this.time = 0;
    this.x = x;
    this.y = y;
  }

  public double generate(double delta) {
    this.time += delta;

    double x = this.x.read(delta);
    double y = this.y.read(delta);

    return x * y;
  }
}
