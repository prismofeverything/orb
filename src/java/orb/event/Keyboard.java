package orb.event;

import java.util.Map;
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
import orb.event.PrintEvent;
import orb.waveform.generator.KeyGenerator;

public class Keyboard implements Receiver, Event {
  public MidiDevice keyboard;
  public Transmitter transmit;
  public Vector<Key> keys;
  public Vector<Event> events;
  public Map<Integer, Integer> state;
  public Pattern pattern;

  public static int ON_STATUS = 9;
  public static int OFF_STATUS = 8;
  public static int CONTROL_STATUS = 11;
  public static int PRESSURE_STATUS = 13;
  public static int PITCH_STATUS = 14;

  public Keyboard(String key, int voices) {
    this.pattern = Pattern.compile(key, Pattern.CASE_INSENSITIVE);

    this.keys = new Vector<Key>();
    for (int voice = 0; voice < voices; voice++) {
      this.keys.add(new Key());
    }

    this.state = new HashMap<Integer, Integer>();
    this.events = new Vector<Event>();
    this.events.add(new PrintEvent());

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
      this.state.put(control, data);
      this.on(channel, timestamp, control, data);
      for (Event event: this.events) {
        event.on(channel, timestamp, control, data);
      }
    } else if (status == OFF_STATUS) {
      this.state.remove(control);
      this.off(channel, timestamp, control);
      for (Event event: this.events) {
        event.off(channel, timestamp, control);
      }
    } else if (status == CONTROL_STATUS) {
      // this.state.remove(control);
      this.control(channel, timestamp, control, data);
      for (Event event: this.events) {
        event.control(channel, timestamp, control, data);
      }
    } else if (status == PRESSURE_STATUS) {
      // this.state.remove(control);
      this.pressure(channel, timestamp, control);
      for (Event event: this.events) {
        event.pressure(channel, timestamp, control);
      }
    } else if (status == PITCH_STATUS) {
      // this.state.remove(control);
      this.pitch(channel, timestamp, control, data);
      for (Event event: this.events) {
        event.pitch(channel, timestamp, control, data);
      }
    }
  }

  public void on(int channel, long time, int tone, int velocity) {
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
    for (Key key: this.keys) {
      if (key.tone.tone == tone) {
        key.off(channel, time, tone);
      }
    }
  }

  public void control(int channel, long time, int control, int data) {};
  public void pressure(int channel, long time, int pressure) {};
  public void pitch(int channel, long time, int base, int detail) {};

  public Key key(int key) {
    return this.keys.get(key);
  }
}
