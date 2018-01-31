package orb.waveform.generator;

import orb.waveform.Generator;

public class DelayGenerator extends Generator {
  public double time;
  public Generator source;
  public Generator window;
  public int maxSamples;
  public int head;
  public int cursor;
  public double[] samples;

  // window is assumed to be from zero to one
  // and it is what ratio of maxSeconds the delay consists of
  public DelayGenerator(Generator source, Generator window, double maxSeconds) {
    super(source, window);
    this.time = 0;
    this.source = source;
    this.window = window;
    this.maxSamples = (int) (maxSeconds * Generator.SAMPLING_RATE);
    this.head = 0;
    this.cursor = 0;
    this.samples = new double[this.maxSamples];
  }

  public double generate(double delta) {
    this.time += delta;

    // the source is read directly and stored at the head
    double source = this.source.read(delta);
    samples[head] = source;

    // the delay is the ratio of the window to the maximum number of samples
    double window = this.window.read(delta);
    int delay = (int) (window * (maxSamples - 1));
    if (delay >= maxSamples) delay = maxSamples - 1;

    // the cursor trails the head by delay samples
    int cursor = head - delay;
    while (cursor < 0) cursor += maxSamples;
    if (cursor == head && delay != 0) cursor = head + 1;
    while (cursor >= maxSamples) cursor -= maxSamples;

    // read the sample at the cursor
    double read = samples[cursor];
    this.cursor = cursor;

    // advance the head
    head += 1;
    if (head >= maxSamples) head = 0;

    return read;
  }
}
