package orb.waveform;

import java.nio.ByteBuffer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;

public class Sine {
  public static void at(double frequency, double time) throws InterruptedException, LineUnavailableException 
  {
    final int SAMPLING_RATE = 44100;
    final int SAMPLE_SIZE = 2;

    double step = frequency / SAMPLING_RATE;
    double cursor = 0;        

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
    long totalSamples = (long) Math.floor(SAMPLING_RATE * time);

    while (totalSamples > 0) {
      buffer.clear();
      int available = line.available() / SAMPLE_SIZE;   

      for (int i = 0; i < available; i++) {
        buffer.putShort((short) (Short.MAX_VALUE * Math.sin(2 * Math.PI * cursor)));

        cursor += step;
        if (cursor > 1)
          cursor -= 1;
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

