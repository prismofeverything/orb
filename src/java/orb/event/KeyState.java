package orb.event;

import java.util.Map;
import java.util.HashMap;

public class KeyState {
  public int channel;
  public long timestamp;
  public Map<Integer, Integer> keys;
  public Map<Integer, Integer> controls;
  public int pressureValue;
  public int pitch;

  public KeyState(int channel, long timestamp) {
    this.channel = channel;
    this.timestamp = timestamp;
    this.keys = new HashMap<Integer, Integer>();
    this.controls = new HashMap<Integer, Integer>();
    this.pressureValue = 0;
    this.pitch = 0;
  }

  public void on(int key, int velocity) {
    this.keys.put(key, velocity);
  }

  public void off(int key) {
    this.keys.remove(key);
  }

  public void control(int control, int data) {
    if (data == 0) {
      this.controls.remove(control);
    } else {
      this.controls.put(control, data);
    }
  }

  public void pressure(int pressure) {
    this.pressureValue = pressure;
  }

  public void pitch(int pitch) {
    this.pitch = pitch;
  }
}
