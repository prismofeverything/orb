package orb.waveform.generator;

import java.util.Collection;
import java.util.ArrayList;

import orb.waveform.Generator;

public class MixGenerator extends Generator {
  public ArrayList<Generator> channels;
  public double factor;

  public MixGenerator(Collection<Generator> channels) {
    super();
    this.channels = new ArrayList<Generator>(channels);
    this.dependencies = this.channels.toArray(new Generator[this.channels.size()]);
    this.factor = 1.0 / this.channels.size();
  }

  public double generate(double delta) {
    double mix = 0;
    for (Generator channel: this.channels) {
      mix += this.factor * channel.read();
    }

    return mix;
  }
}
