package orb.tonality;

import java.util.Collection;
import java.util.ArrayList;

public class Tonality {
  public ArrayList<Double> tones;
  public int root;
  public double frequency;

  public Tonality(Collection<Double> tones, int root, double frequency) {
    this.tones = new ArrayList<Double>(tones);
    this.root = root;
    this.frequency = frequency;
  }

  public double tone(int key) {
    int level = key - this.root;
    int octave = 0;
    int size = this.tones.size();

    while (level < 0) {
      level += size;
      octave -= 1;
    }

    while (level >= size) {
      level -= size;
      octave += 1;
    }

    double factor = Math.pow(2, octave);
    double ratio = this.tones.get(level);
    double tone = this.frequency * ratio * factor;
    return tone;
  }
}
