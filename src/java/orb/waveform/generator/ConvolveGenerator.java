package orb.waveform.generator;

import java.util.ArrayList;
import java.util.Collection;

import orb.waveform.Generator;

public class ConvolveGenerator extends Generator {
  public Generator signal;
  public int kernelLength;
  public double[] kernel;
  public double[] samples;
  public int cursor;

  public ConvolveGenerator(Generator signal, Collection<Double> kernel) {
    super(signal);

    ArrayList<Double> k = new ArrayList<Double>(kernel);

    this.signal = signal;
    this.kernelLength = kernel.size();
    this.kernel = new double[this.kernelLength];
    this.samples = new double[this.kernelLength];
    this.cursor = 0;

    for (int sample = 0; sample < this.kernelLength; sample++) {
      this.kernel[sample] = k.get(sample);
      this.samples[sample] = 0.0;
    }
  }

  public double generate(double delta) {
    double sample = this.signal.read();
    double output = 0.0;

    this.cursor += 1;
    if (this.cursor >= this.kernelLength) {
      this.cursor = 0;
    }

    this.samples[this.cursor] = sample;
    for (int head = 0; head < this.kernelLength; head++) {
      int previous = this.cursor - head;
      while (previous < 0) {
        previous += this.kernelLength;
      }

      output += this.samples[previous] * this.kernel[head];
    }

    return output;
  }
}
