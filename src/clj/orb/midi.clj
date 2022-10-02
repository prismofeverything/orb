(ns orb.midi
  (:import
   [javax.sound.midi
    MidiSystem
    MidiDevice
    MidiMessage
    Transmitter
    Receiver
    MidiUnavailableException]
   [orb.event Keyboard Event PrintEvent]))

(defn midi-device-info
  []
  (MidiSystem/getMidiDeviceInfo))

(defn midi-device
  [info]
  (MidiSystem/getMidiDevice info))

(defn find-device-names
  []
  (for [info (midi-device-info)]
    (.getName info)))

(defn find-devices
  []
  (for [info (midi-device-info)]
    (midi-device info)))

(defn -main
  []
  (println "Available midi devices are:")
  (doseq [device-name (find-device-names)]
    (println device-name)))
