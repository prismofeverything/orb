package orb.waveform;

import java.nio.ByteBuffer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;

import orb.waveform.Generator;
import orb.event.Event;

public class Voice implements Event {
  public final static int SAMPLE_SIZE = 2;
  // number of samples for level onset and offset ramps
  public final static double SIGNAL_STEPS = 1000;
  public final static double SIGNAL_STEP = 1.0 / SIGNAL_STEPS;

  public Generator generator;
  public boolean singing;

  public Voice(Generator generator) {
    this.generator = generator;
  }

  public void noteOn(int tone, int energy, long time)
  {
    try {
      this.singing = true;

      SourceDataLine line;
      AudioFormat format = new AudioFormat(Generator.SAMPLING_RATE, 16, 1, true, true);
      DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

      if (!AudioSystem.isLineSupported(info)){
        System.out.println("Line matching " + info + " is not supported");
        return;
      }

      line = (SourceDataLine) AudioSystem.getLine(info);
      line.open(format);
      line.start();

      ByteBuffer buffer = ByteBuffer.allocate(line.getBufferSize());
      double signal = 0;
      double sample = 0;

      while (this.singing || signal > 0) {
        buffer.clear();

        if (!this.singing) {
          signal -= SIGNAL_STEP;
          if (signal < 0) signal = 0;
        } else if (signal < 1) {
          signal += SIGNAL_STEP;
          if (signal > 1) signal = 1;
        } 

        int available = line.available() / SAMPLE_SIZE;

        for (int i = 0; i < available; i++) {
          sample = generator.generate(Generator.SAMPLE_INTERVAL);
          buffer.putShort((short) (Short.MAX_VALUE * signal * sample));
          time += Generator.SAMPLE_INTERVAL;
        }

        line.write(buffer.array(), 0, buffer.position());
        while (line.getBufferSize() / 2 < line.available())
          Thread.sleep(1);
      }

      line.drain();                                         
      line.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void noteOff(int tone, long time) {
    this.singing = false;
  }
}
