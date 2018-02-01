(ns orb.midi
  (:import
   [javax.sound.midi
    MidiSystem
    MidiDevice
    MidiMessage
    Transmitter
    Receiver
    MidiUnavailableException]))

(defn midi-device-info
  []
  (MidiSystem/getMidiDeviceInfo))

(defn midi-device
  [info]
  (MidiSystem/getMidiDevice info))
