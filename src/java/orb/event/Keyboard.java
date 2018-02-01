package orb.event;

import java.util.Map;
import java.util.Vector;
import java.util.HashMap;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Transmitter;
import javax.sound.midi.Receiver;
import javax.sound.midi.MidiUnavailableException;

import orb.event.Event;
import orb.event.PrintEvent;

public class Keyboard implements Receiver {
  public MidiDevice keyboard;
  public Transmitter transmit;
  public Vector<Event> events;
  public Map<Integer, Integer> on;

  public static int NOTE_ON_STATUS = 144;
  public static int NOTE_OFF_STATUS = 128;

  public Keyboard(String key) {
    try {
      MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

      for (MidiDevice.Info info: infos) {
        System.out.println(info.getName() + " ? " + key);
        if (info.getName().equals(key)) {
          MidiDevice device = MidiSystem.getMidiDevice(info);
          int transmitters = device.getMaxTransmitters();
          System.out.println(transmitters);
          if (transmitters == -1 || transmitters > 0) {
            this.keyboard = device;
          }
        }  
      }

      this.on = new HashMap<Integer, Integer>();
      this.events = new Vector<Event>();
      this.events.add(new PrintEvent());
      this.transmit = this.keyboard.getTransmitter();
      this.transmit.setReceiver(this);
    } catch (MidiUnavailableException e) {
      System.out.println("MIDI UNAVAILABLE WHAT");
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

  public void send(MidiMessage message, long timestamp) {
    HashMap<String, Integer> values = new HashMap<String, Integer>();
    int length = message.getLength();
    byte[] bytes = message.getMessage();
    values.put("length", message.getLength());
    values.put("status", message.getStatus());
    for (int i = 0; i < length; i++) {
      values.put(Integer.toString(i), (int) bytes[i]);
    }

    if (message.getStatus() == NOTE_ON_STATUS) {
      this.on.put((int) bytes[1], (int) bytes[2]);
      for (Event event: this.events) {
        event.noteOn(bytes[1], bytes[2], timestamp);
      }
    } else if (message.getStatus() == NOTE_OFF_STATUS) {
      this.on.remove((int) bytes[1]);
      for (Event event: this.events) {
        event.noteOff(bytes[1], timestamp);
      }
    }
  }
}
