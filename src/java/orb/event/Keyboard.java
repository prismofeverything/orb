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
import orb.event.PrintEvent;
import orb.waveform.generator.KeyGenerator;

public class Keyboard implements Receiver, Event {
  public MidiDevice keyboard;
  public Transmitter transmit;
  public Vector<Key> keys;
  public Vector<Event> events;
  public Map<Integer, Integer> on;
  public Pattern pattern;

  public static int NOTE_ON_STATUS = 144;
  public static int NOTE_OFF_STATUS = 128;

  public Keyboard(String key, int voices) {
    this.pattern = Pattern.compile(key, Pattern.CASE_INSENSITIVE);

    this.keys = new Vector<Key>();
    for (int voice = 0; voice < voices; voice++) {
      this.keys.add(new Key());
    }

    this.on = new HashMap<Integer, Integer>();
    this.events = new Vector<Event>();
    this.events.add(new PrintEvent());

    try {
      MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

      System.out.println("looking for " + key);
      for (MidiDevice.Info info: infos) {
        System.out.println("MIDI info:");
        System.out.println(info.getName());
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

    if (message.getStatus() == NOTE_ON_STATUS) {
      this.on.put((int) bytes[1], (int) bytes[2]);
      this.noteOn(bytes[1], bytes[2], timestamp);
      for (Event event: this.events) {
        event.noteOn(bytes[1], bytes[2], timestamp);
      }
    } else if (message.getStatus() == NOTE_OFF_STATUS) {
      this.on.remove((int) bytes[1]);
      this.noteOff(bytes[1], timestamp);
      for (Event event: this.events) {
        event.noteOff(bytes[1], timestamp);
      }
    }
  }

  public void noteOn(int tone, int energy, long time) {
    int keyIndex = 0;
    Key key = this.key(keyIndex);
    while (key.on && keyIndex < this.keys.size() - 1) {
      keyIndex++;
      key = this.key(keyIndex);
    }

    if (!key.on) {
      key.noteOn(tone, energy, time);
    }
  }

  public void noteOff(int tone, long time) {
    for (Key key: this.keys) {
      if (key.tone.tone == tone) {
        key.noteOff(tone, time);
      }
    }
  }

  public Key key(int key) {
    return this.keys.get(key);
  }
}
