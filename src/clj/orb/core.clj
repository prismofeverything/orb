(ns orb.core
  (:import
   [orb.waveform Emit Generator]
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
