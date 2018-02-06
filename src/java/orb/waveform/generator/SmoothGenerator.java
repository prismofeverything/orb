package orb.waveform.generator;

import orb.waveform.Generator;

public class SmoothGenerator extends Generator {
  public Generator source;
  public double step;
  public double path;

  public SmoothGenerator(Generator source, double step) {
    super(source);
    this.source = source;
    this.step = step;
    this.path = 0;
  }

  public double generate(double delta) {
    double sample = this.source.read();

    if (Math.abs(sample - this.path) < this.step * 0.8) {
      this.path = sample;
    } else if (sample > this.path) {
      this.path += this.step;
    } else if (sample < this.path) {
      this.path -= this.step;
    }

    // double gulf = sample - this.path;
    // this.path += gulf * this.step;
    return this.path;
  }
}
