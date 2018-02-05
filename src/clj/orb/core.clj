(ns orb.core
  (:require
   [orb.midi :as midi])
  (:import
   [orb.waveform Emit Signal Generator]
   [orb.event Keyboard Event PrintEvent]
   [orb.waveform.generator
    AddGenerator
    SineGenerator
    DelayGenerator
    MultiplyGenerator
    ConstantGenerator]))

(defn const?
  [x]
  (if (number? x)
    (ConstantGenerator. x)
    x))

(defn sine
  [freq amp]
  (SineGenerator. (const? freq) (const? amp)))

(defn add
  [a b]
  (AddGenerator. (const? a) (const? b)))

(defn multiply
  [x y]
  (MultiplyGenerator. (const? x) (const? y)))

(defn const
  [value]
  (ConstantGenerator. value))

(defn delay
  [source window max]
  (DelayGenerator. (const? source) (const? window) max))

(defn emit
  [generator t]
  (Emit/at generator t))

(defn signal
  [generator]
  (let [signal (Signal. generator)]
    (.start signal)
    signal))

(defn signal-on
  [signal]
  (.onset signal))

(defn signal-off
  [signal]
  (.offset signal))

(defn signal-stop
  [signal]
  (.stop signal))

(defn swap-generator
  [signal generator]
  (.swap signal generator))

(defn keyboard
  [voices]
  (Keyboard. "Digital Piano" voices))

(defn key
  [keyboard voice]
  (.key keyboard voice))

(defn tone
  [keyboard voice]
  (.tone (.key keyboard voice)))

(defn energy
  [keyboard voice]
  (.energy (.key keyboard voice)))
