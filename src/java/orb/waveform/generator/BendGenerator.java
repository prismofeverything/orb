package orb.waveform.generator;

import orb.waveform.Generator;

public class BendGenerator extends Generator {
  public double scale;
  public Generator pitch;
  public Generator bend;

  public BendGenerator(double scale, Generator pitch, Generator bend) {
    super(pitch, bend);

    this.scale = scale;
    this.pitch = pitch;
    this.bend = bend;
  }

  public double generate(double delta) {
    double pitch = this.pitch.read();
    double bend = pitch * this.bend.read() * this.scale;

    return pitch + bend;
  }
}
