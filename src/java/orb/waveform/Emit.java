package orb.waveform;

import java.nio.ByteBuffer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;

import orb.waveform.Generator;

public class Emit {
  public final static int SAMPLE_SIZE = 2;
  // number of samples for level onset and offset ramps
  public final static double SIGNAL_STEPS = 1000;
  public final static double SIGNAL_STEP = 1.0 / SIGNAL_STEPS;

  public static void at(Generator generator, double seconds)
    throws InterruptedException, LineUnavailableException 
  {
    double time = 0;

    SourceDataLine line;
    AudioFormat format = new AudioFormat(Generator.SAMPLING_RATE, 16, 1, true, true);
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

    if (!AudioSystem.isLineSupported(info)){
      System.out.println("Line matching " + info + " is not supported.");
      throw new LineUnavailableException();
    }

    line = (SourceDataLine) AudioSystem.getLine(info);
    line.open(format);
    line.start();

    ByteBuffer buffer = ByteBuffer.allocate(line.getBufferSize());
    long totalSamples = (long) Math.floor(Generator.SAMPLING_RATE * seconds);
    double signal = 0;
    double sample = 0;

    while (totalSamples > 0) {
      buffer.clear();
      int available = line.available() / SAMPLE_SIZE;

      for (int i = 0; i < available; i++) {
        sample = generator.generate(Generator.SAMPLE_INTERVAL);
        buffer.putShort((short) (Short.MAX_VALUE * signal * sample));
        time += Generator.SAMPLE_INTERVAL;

        // minimal attack and decay to avoid pops
        if (totalSamples - i < SIGNAL_STEPS) {
          signal -= SIGNAL_STEP;
          if (signal < 0) signal = 0;
        } else if (signal < 1) {
          signal += SIGNAL_STEP;
        }
      }

      line.write(buffer.array(), 0, buffer.position());
      totalSamples -= available;

      while (line.getBufferSize() / 2 < line.available())
        Thread.sleep(1);
    }

    line.drain();                                         
    line.close();
  }
}

