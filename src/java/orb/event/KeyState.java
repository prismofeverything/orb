package orb.event;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import orb.event.Event;

public class KeyState implements Event {
  public int channel;
  public long start;
  public long stop;
  public long recent;
  public boolean on;
  public Map<Integer, Integer> tones;
  public Map<Integer, Integer> controls;
  public int pressure;
  public int pitch;

  public KeyState(int channel) {
    this.channel = channel;
    this.start = -1;
    this.stop = -1;
    this.recent = -1;
    this.tones = new HashMap<Integer, Integer>();
    this.controls = new HashMap<Integer, Integer>();
    this.pressure = 0;
    this.pitch = 0;
  }

  public boolean isOn() {
    return this.tones.size() > 0;
  }

  public List<Integer> tonesOn() {
    return new ArrayList<Integer>(this.tones.keySet());
  }

  public void on(int channel, long timestamp, int tone, int velocity) {
    this.channel = channel;
    this.start = timestamp;
    this.recent = timestamp;
    this.tones.put(tone, velocity);
  }

  public void off(int channel, long timestamp, int tone) {
    this.channel = channel;
    this.stop = timestamp;
    this.recent = timestamp;
    this.tones.remove(tone);
  }

  public void control(int channel, long timestamp, int control, int data) {
    this.channel = channel;
    this.recent = timestamp;
    if (data == 0) {
      this.controls.remove(control);
    } else {
      this.controls.put(control, data);
    }
  }

  public void pressure(int channel, long timestamp, int pressure) {
    this.channel = channel;
    this.recent = timestamp;
    this.pressure = pressure;
  }

  public void pitch(int channel, long timestamp, int pitch) {
    this.channel = channel;
    this.recent = timestamp;
    this.pitch = pitch;
  }

  public String toString() {
    String rep = "channel " + this.channel + ": ";
    if (this.isOn()) {
      List<String> toneList = new ArrayList<String>();
      for (Map.Entry<Integer, Integer> entry : this.tones.entrySet()) {
        toneList.add("" + entry.getKey() + " > " + entry.getValue());
      }
      String tonesOn = String.join(" . ", toneList);

      rep += "tones - " + tonesOn + " | ";
      rep += "pressure - " + this.pressure + " | pitch - " + this.pitch + " | ";

      if (this.controls.size() > 0) {
        List<String> controlList = new ArrayList<String>();
        for (Map.Entry<Integer, Integer> entry : this.controls.entrySet()) {
          controlList.add("" + entry.getKey() + " @ " + entry.getValue());
        }
        String controlsOn = String.join(" . ", controlList);
        rep += "controls - " + controlsOn + " | ";
      }
    } else {
      rep += "off | ";
    }

    return rep;
  }
}
