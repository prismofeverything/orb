package orb.waveform;

public abstract class Generator {
  public final static int SAMPLING_RATE = 44100;
  public final static double SAMPLE_INTERVAL = 1.0 / SAMPLING_RATE;

  public boolean mark;
  public double sample;
  public Generator[] dependencies;

  public Generator(Generator... dependencies) {
    this.dependencies = dependencies;
    this.mark = false;
    this.sample = 0;
  }

  abstract public double generate(double delta);

  public void clear() {
    if (this.mark) {
      this.mark = false;
      for (Generator dependency: this.dependencies) {
        dependency.clear();
      }
    }
  }

  public void apply() {

  }

  public double read(double delta) {
    if (!this.mark) {
      this.mark = true;
      this.sample = this.generate(delta);
      this.mark = false;
    }

    return this.sample;
  }
}
