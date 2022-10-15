package orb.waveform.generator;

import java.util.ArrayList;
import java.util.Collection;

import orb.waveform.Generator;

public class PluckGenerator extends Generator {
  public Generator position;
  public Generator state;

  public boolean reset;
  public int size;
  public double[] table;
  public int previous;
  public double step;

  public PluckGenerator(Generator position, Generator state, int size) {
    super(position, state);

    this.position = position;
    this.state = state;

    this.size = size;
    this.table = new double[size];
    this.previous = (int) Math.ceil(this.size * position.read());
    this.step = 1.0 / this.size;

    this.initializeTable();
    this.reset = true;
  }

  public void initializeTable() {
    for (int index = 0; index < this.size; index++) {
      // sample = Math.random() * 2 - 1;
      sample = -1;
      if (Math.random() > 0.5) {
        sample = 1;
      }
      this.table[index] = sample;
    }
  }

  public double generate(double delta) {
    boolean off = this.state.read() < 0;
    if (off && !this.reset) {
      this.reset = true;
    } else if (!off && this.reset) {
      this.initializeTable();
      this.reset = false;
    }

    double position = this.size * this.position.read();
    int above = (int) Math.ceil(position);
    while (above >= this.size) {
      above -= this.size;
    }

    int previous = this.previous;
    while (previous > above) {
      previous -= this.size;
    }
    int step = above - previous;

    int back = this.previous;
    int index = 0;
    for (int sample = 1; sample < step + 1; sample++) {
      index = this.previous + sample;
      while (index >= this.size) {
        index -= this.size;
      }

      double mix = (this.table[index] + this.table[back]) * 0.5;
      this.table[index] = mix;

      back = index;
    }

    int below = above - 1;
    if (below == -1) below = this.size - 1;

    double before = this.table[below];
    double after = this.table[above];
    double between = position - below;
    double within = after - before;

    this.previous = above;

    // linear interpolation
    return before + (between * within);
  }
}
