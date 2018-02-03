package orb.waveform;

import java.nio.ByteBuffer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;

import orb.waveform.Generator;
import orb.event.Event;

public class Signal implements Runnable {
  public final static int SAMPLE_SIZE = 2;

  // number of samples for level onset and offset ramps
  public final static double SIGNAL_STEPS = 1000;
  public final static double SIGNAL_STEP = 1.0 / SIGNAL_STEPS;

  public Generator generator;
  public boolean running;
  public double time;
  public Thread thread;
  public SourceDataLine line;
  public DataLine.Info info;
  public AudioFormat format;
  public ByteBuffer buffer;

  public Signal(Generator generator) {
    this.generator = generator;
    this.time = 0;
    this.running = false;

    try {
      this.format = new AudioFormat(Generator.SAMPLING_RATE, 16, 1, true, true);
      this.info = new DataLine.Info(SourceDataLine.class, this.format);

      if (!AudioSystem.isLineSupported(this.info)) {
        System.out.println("Line matching " + this.info + " is not supported");
      }

      this.line = (SourceDataLine) AudioSystem.getLine(info);
      this.line.open(format);
      this.line.start();
      this.buffer = ByteBuffer.allocate(line.getBufferSize());

      this.thread = new Thread(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void run() {
    try {
      double signal = 0;
      double sample = 0;

      // while (this.running || signal > 0) {
      while (true) {
        this.buffer.clear();

        if (!this.running) {
          signal -= SIGNAL_STEP;
          if (signal < 0) signal = 0;
        } else if (signal < 1) {
          signal += SIGNAL_STEP;
          if (signal > 1) signal = 1;
        } 

        int available = this.line.available() / SAMPLE_SIZE;

        for (int i = 0; i < available; i++) {
          sample = this.generator.cycle(Generator.SAMPLE_INTERVAL);
          this.buffer.putShort((short) (Short.MAX_VALUE * signal * sample));
          this.time += Generator.SAMPLE_INTERVAL;
        }

        this.line.write(this.buffer.array(), 0, this.buffer.position());
        while (this.line.getBufferSize() / 2 < this.line.available())
          Thread.sleep(1);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void start() {
    this.thread.start();
  }

  public void stop() {
    try {
      this.thread.interrupt();
      this.line.drain();                                         
      this.line.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void onset() {
    this.running = true;
  }

  public void offset() {
    this.running = false;
  }
}

