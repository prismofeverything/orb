package orb.waveform;

import java.nio.ByteBuffer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;

public class Emit {
  public final static int SAMPLING_RATE = 44100;
  public final static int SAMPLE_SIZE = 2;
  public final static double SAMPLE_TIME = 1.0 / SAMPLING_RATE;

  public static void at(Generator generator, double seconds) throws InterruptedException, LineUnavailableException 
  {
    double time = 0;

    SourceDataLine line;
    AudioFormat format = new AudioFormat(SAMPLING_RATE, 16, 1, true, true);
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

    if (!AudioSystem.isLineSupported(info)){
      System.out.println("Line matching " + info + " is not supported.");
      throw new LineUnavailableException();
    }

    line = (SourceDataLine) AudioSystem.getLine(info);
    line.open(format);
    line.start();

    ByteBuffer buffer = ByteBuffer.allocate(line.getBufferSize());   
    long totalSamples = (long) Math.floor(SAMPLING_RATE * seconds);

    while (totalSamples > 0) {
      buffer.clear();
      int available = line.available() / SAMPLE_SIZE;   

      for (int i = 0; i < available; i++) {
        buffer.putShort((short) (Short.MAX_VALUE * generator.generate(time)));
        time += SAMPLE_TIME;
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

