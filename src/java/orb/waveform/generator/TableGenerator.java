package orb.waveform.generator;

import java.util.ArrayList;
import java.util.Collection;

import orb.waveform.Generator;

public class TableGenerator extends Generator {
  public Generator position;
  public ArrayList<Double> table;
  public double step;

  public TableGenerator(Generator position, Collection<Double> table) {
    super(position);
    this.position = position;
    this.table = new ArrayList<Double>(table);
    this.step = 1.0 / this.table.size();
  }

  public double generate(double delta) {
    double position = this.table.size() * this.position.read();

    long below = (long) Math.floor(position);
    long above = (long) Math.ceil(position);
    if (above == this.table.size()) above = 0;

    double before = this.table.get((int) below);
    double after = this.table.get((int) above);
    double between = position - below;
    double within = after - before;

    return before + (between * within);
  }
}
