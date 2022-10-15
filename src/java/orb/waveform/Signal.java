package orb.waveform;

import java.io.File;
import java.io.FileOutputStream;

import java.nio.ByteBuffer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;

import orb.event.Event;
import orb.waveform.Generator;
import orb.waveform.generator.ConstantGenerator;

public class Signal implements Runnable {
  public final static int SAMPLE_SIZE = 2;

  // number of samples for level onset and offset ramps
  public final static double SIGNAL_STEPS = 10000;
  public final static double SIGNAL_STEP = 1.0 / SIGNAL_STEPS;

  public Generator generator;
  public boolean running;
  public boolean playing;
  public boolean writing;
  public double time;
  public double signal;
  public double crossover;
  public boolean crossing;
  public Generator previous;
  public Thread thread;
  public SourceDataLine line;
  public DataLine.Info info;
  public AudioFormat format;
  public ByteBuffer buffer;
  public FileOutputStream output;

  public Signal(Generator generator) {
    this(generator, "");
  }

  public Signal(Generator generator, String outputFilename) {
    this.generator = generator;
    this.time = 0;
    this.running = false;
    this.playing = false;
    this.writing = false;
    this.crossover = 0;
    this.crossing = false;
    this.signal = 0;
    this.previous = new ConstantGenerator(0);

    try {
      if (outputFilename != "") {
        this.output = new FileOutputStream(outputFilename);
        this.writing = true;
      }

      this.format = new AudioFormat(Generator.SAMPLING_RATE, 16, 1, true, true);
      this.info = new DataLine.Info(SourceDataLine.class, this.format);
      this.listAudioFormats();

      if (!AudioSystem.isLineSupported(this.info)) {
        System.out.println("Line matching " + this.info + " is not supported");
      }

      this.line = (SourceDataLine) AudioSystem.getLine(info);
      this.line.open(format, 4410);
      this.line.start();
      this.buffer = ByteBuffer.allocate(this.line.getBufferSize());
      System.out.printf("buffer size %d", this.line.getBufferSize());

      this.thread = new Thread(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void listAudioFormats() {
    Line.Info desired = new Line.Info(SourceDataLine.class);
    Line.Info[] infos = AudioSystem.getSourceLineInfo(desired);

    for (Line.Info info : infos) {
      if (info instanceof DataLine.Info) {
        AudioFormat[] forms = ((DataLine.Info) info).getFormats();
        for (AudioFormat format : forms) {
          System.out.println(format);
        }
      }
      else {
        System.out.println("info not an instance of Dataline.info");
        System.out.println(info);
      }
    }
  }

  public void run() {
    try {
      double sample = 0;
      double previous = 0;
      boolean crossing = false;
      this.playing = true;

      while (this.running || this.signal > 0) {
        this.buffer.clear();

        int available = this.line.available() / SAMPLE_SIZE;

        for (int i = 0; i < available; i++) {
          if (!this.playing) {
            this.signal -= SIGNAL_STEP;
            if (this.signal < 0) this.signal = 0;
          } else if (this.signal < 1) {
            this.signal += SIGNAL_STEP;
            if (this.signal > 1) this.signal = 1;
          } 

          if (this.crossing) {
            this.crossover -= SIGNAL_STEP;
            if (this.crossover < 0) {
              this.crossover = 0;
              this.crossing = false;
              this.previous = new ConstantGenerator(0);
            }
          }

          sample = this.signal * this.generator.cycle(Generator.SAMPLE_INTERVAL);
          if (this.crossing) {
            previous = this.previous.cycle(Generator.SAMPLE_INTERVAL);
            sample += this.crossover * previous;
          }

          if (sample > 1) sample = 1;
          if (sample < -1) sample = -1;

          this.buffer.putShort((short) (Short.MAX_VALUE * sample));
          this.time += Generator.SAMPLE_INTERVAL;
        }

        byte[] bytes = this.buffer.array();
        this.line.write(bytes, 0, this.buffer.position());

        if (this.writing) {
          this.output.write(bytes, 0, this.buffer.position());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void start() {
    this.running = true;
    this.thread.start();
  }

  public void stop() {
    this.running = false;
    this.playing = false;

    try {
      this.thread.interrupt();
      this.line.drain();                                         
      this.line.close();
      this.output.flush();
      this.output.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void onset() {
    this.playing = true;
  }

  public void offset() {
    this.playing = false;
  }

  public void swap(Generator generator) {
    this.previous = this.generator;
    this.generator = generator;
    this.crossover = this.signal;
    this.crossing = true;
    this.signal = 0;
  }
}

