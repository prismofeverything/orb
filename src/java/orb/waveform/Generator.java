package orb.waveform;

import java.util.function.Function;
import java.util.function.Consumer;

public abstract class Generator {
  public final static int SAMPLING_RATE = 44100;
  public final static double SAMPLE_INTERVAL = 1.0 / SAMPLING_RATE;

  public boolean mark;
  public double sample;
  public double next;
  public Generator[] dependencies;

  public Generator(Generator... dependencies) {
    this.dependencies = dependencies;
    this.mark = false;
    this.sample = 0;
    this.next = 0;
  }

  abstract public double generate(double delta);

  public void iterate(Consumer<Generator> operator) {
    if (!this.mark) {
      this.mark = true;
      operator.accept(this);
      for (Generator dependency: this.dependencies) {
        dependency.iterate(operator);
      }
    }
  }

  public void reset() {
    if (this.mark) {
      this.mark = false;
      for (Generator dependency: this.dependencies) {
        dependency.reset();
      }
    }
  }

  public void apply(Consumer<Generator> operator) {
    this.iterate(operator);
    this.reset();
  }

  public void trigger(double delta) {
    this.next = this.generate(delta);
  }

  public void sync() {
    this.sample = this.next;
  }

  public double read() {
    return this.sample;
  }

  public double cycle(double delta) {
    this.apply(generator -> generator.trigger(delta));
    this.apply(generator -> generator.sync());

    return this.read();
  }
}
