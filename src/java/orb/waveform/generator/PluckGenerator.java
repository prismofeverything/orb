package orb.waveform.generator;

import java.util.ArrayList;
import java.util.Collection;

import orb.waveform.Generator;

public class PluckGenerator extends Generator {
  public ArrayList<Double> table;
  public int size;
  public Generator position;
  public int previous;
  public double step;

  public PluckGenerator(Generator position, Collection<Double> table) {
    super(position);

    this.table = new ArrayList<Double>(table);
    this.size = this.table.size();
    this.position = position;
    this.previous = (int) Math.ceil(this.size * position.read());
    this.step = 1.0 / this.size;
  }

  public double generate(double delta) {
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

      double mix = (this.table.get(index) + this.table.get(back)) * 0.5;
      this.table.set(index, mix);

      back = index;
    }

    int below = above - 1;
    if (below == -1) below = this.size - 1;

    double before = this.table.get(below);
    double after = this.table.get(above);
    double between = position - below;
    double within = after - before;

    this.previous = above;

    // linear interpolation
    return before + (between * within);
  }
}
