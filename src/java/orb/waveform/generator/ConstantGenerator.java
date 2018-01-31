package orb.waveform.generator;

import orb.waveform.Generator;

public class ConstantGenerator extends Generator {
  public double time;
  public double value;

  public ConstantGenerator(double value) {
    super();
    this.time = 0;
    this.value = value;
  }

  public double generate(double time) {
    double delta = time - this.time;
    this.time = time;
    return this.value;
  }
}
