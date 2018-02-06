package orb.waveform.generator;

import orb.tonality.Tonality;
import orb.waveform.Generator;

public class TonalityGenerator extends Generator {
  public Tonality tonality;
  public Generator tone;

  public TonalityGenerator(Tonality tonality, Generator tone) {
    super(tone);
    this.tonality = tonality;
    this.tone = tone;
  }

  public double generate(double delta) {
    int tone = (int) Math.round(this.tone.read());
    return this.tonality.tone(tone);
  }
}
