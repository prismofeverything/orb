package orb.event;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Vector;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Transmitter;
import javax.sound.midi.Receiver;
import javax.sound.midi.MidiUnavailableException;

import orb.event.Event;
import orb.event.Key;
import orb.event.KeyState;
import orb.event.PrintEvent;
import orb.waveform.generator.KeyGenerator;

public class Keyboard implements Receiver, Event {
  public MidiDevice keyboard;
  public Transmitter transmit;
  public Vector<Key> keys;
  public Vector<Event> events;
  public Map<Integer, KeyState> state;
  public Pattern pattern;

  public static int ON_STATUS = 9;
  public static int OFF_STATUS = 8;
  public static int CONTROL_STATUS = 11;
  public static int PRESSURE_STATUS = 13;
  public static int PITCH_STATUS = 14;
  public static int TOTAL_CHANNELS = 8;

  public Keyboard(String key, int voices) {
    this.pattern = Pattern.compile(key, Pattern.CASE_INSENSITIVE);

    this.keys = new Vector<Key>();
    for (int voice = 0; voice < voices; voice++) {
      this.keys.add(new Key());
    }

    this.state = new HashMap<Integer, KeyState>();
    for (int channel = 0; channel < TOTAL_CHANNELS; channel++) {
      this.state.put(channel, new KeyState(channel));
    }

    this.events = new Vector<Event>();
    // this.events.add(new PrintEvent());

    try {
      MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

      System.out.println("looking for " + key);
      for (MidiDevice.Info info: infos) {
        
        String name = info.getName();
        Matcher matcher = pattern.matcher(name);
        boolean match = matcher.find();
        System.out.println(name);

        if (match) {
          MidiDevice device = MidiSystem.getMidiDevice(info);
          int transmitters = device.getMaxTransmitters();
          System.out.println(transmitters);
          if (transmitters == -1 || transmitters > 0) {
            this.keyboard = device;
          }
        }  
      }

      if (this.keyboard == null) {
        throw new MidiUnavailableException("no devices with the name " + key);
      }

      this.transmit = this.keyboard.getTransmitter();
      this.transmit.setReceiver(this);
    } catch (MidiUnavailableException e) {
      System.out.println("MIDI UNAVAILABLE WHAT");
      e.printStackTrace();
    }
  }

  public void registerEvent(Event event) {
    this.events.add(event);
  }

  public void power() {
    try {
      this.keyboard.open();
    } catch (MidiUnavailableException e) {
      System.out.println("MIDI UNAVAILABLE WHAT");
    }
  }

  public void close() {
    this.keyboard.close();
  }

  public double read(int voice) {
    return 0;
  }

  public int translatePitch(int major, int minor) {
    int pitch = 0;
    if (major < 64) {
      pitch = major * 128;
    } else {
      pitch = (major - 128) * 128;
    }
    pitch += minor;

    return pitch;
  }

  public void send(MidiMessage message, long timestamp) {
    byte[] bytes = message.getMessage();
    int messageLength = message.getLength();

    // String messageOutput = "";
    // for (int segment = 0; segment < messageLength; segment++) {
    //   messageOutput += bytes[segment] + " ";
    // }
    
    // System.out.println("message received: " + message.getStatus() + " - " + messageOutput);

    int code = message.getStatus();
    int status = code / 16;
    int channel = code % 16;
    int control = bytes[1];
    int data = 0;

    if (messageLength > 2) {
      data = bytes[2];
    }

    if (status == ON_STATUS) {
      this.on(channel, timestamp, control, data);
      for (Event event: this.events) {
        event.on(channel, timestamp, control, data);
      }
    } else if (status == OFF_STATUS) {
      this.off(channel, timestamp, control);
      for (Event event: this.events) {
        event.off(channel, timestamp, control);
      }
    } else if (status == CONTROL_STATUS) {
      this.control(channel, timestamp, control, data);
      for (Event event: this.events) {
        event.control(channel, timestamp, control, data);
      }
    } else if (status == PRESSURE_STATUS) {
      this.pressure(channel, timestamp, control);
      for (Event event: this.events) {
        event.pressure(channel, timestamp, control);
      }
    } else if (status == PITCH_STATUS) {
      int pitch = this.translatePitch(data, control);
      this.pitch(channel, timestamp, pitch);
      for (Event event: this.events) {
        event.pitch(channel, timestamp, pitch);
      }
    }

    System.out.println(this.state.get(channel).toString());
  }

  public List<Key> findKeys(List<Integer> tones) {
    ArrayList<Key> keys = new ArrayList<Key>();
    for (Key key: this.keys) {
      if (tones.contains(key.tone.tone)) {
        keys.add(key);
      }
    }

    return keys;
  }

  public void on(int channel, long time, int tone, int velocity) {
    this.state.get(channel).on(channel, time, tone, velocity);

    int keyIndex = 0;
    Key key = this.key(keyIndex);
    while (key.on && keyIndex < this.keys.size() - 1) {
      keyIndex++;
      key = this.key(keyIndex);
    }

    if (!key.on) {
      key.on(channel, time, tone, velocity);
    }
  }

  public void off(int channel, long time, int tone) {
    this.state.get(channel).off(channel, time, tone);

    List<Key> keys = this.findKeys(Arrays.asList(tone));
    for (Key key: keys) {
      key.off(channel, time, tone);
    }
  }

  public void control(int channel, long time, int control, int data) {
    this.state.get(channel).control(channel, time, control, data);
  };

  public void pressure(int channel, long time, int pressure) {
    this.state.get(channel).pressure(channel, time, pressure);

    List<Integer> tones = this.state.get(channel).tonesOn();
    List<Key> keys = this.findKeys(tones);
    for (Key key: keys) {
      key.pressure(channel, time, pressure);
    }
  };

  public void pitch(int channel, long time, int pitch) {
    this.state.get(channel).pitch(channel, time, pitch);

    List<Integer> tones = this.state.get(channel).tonesOn();
    List<Key> keys = this.findKeys(tones);
    for (Key key: keys) {
      key.pitch(channel, time, pitch);
    }
  };

  public Key key(int key) {
    return this.keys.get(key);
  }
}
