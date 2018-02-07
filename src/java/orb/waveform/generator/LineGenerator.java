package orb.waveform.generator;

import orb.waveform.Generator;

public class LineGenerator extends Generator {
  public Generator step;
  public double point;

  public LineGenerator(Generator step) {
    super(step);
    this.step = step;
    this.point = 0;
  }

  public double generate(double delta) {
    double step = this.step.read();
    this.point += step;
    this.point -= Math.floor(this.point);
    return this.point;
  }
}
