(ns orb.core
  (:require
   [orb.tonality :as tonality]
   [orb.table :as table]
   [orb.midi :as midi])
  (:import
   [orb.tonality Tonality]
   [orb.waveform Emit Signal Generator]
   [orb.event Keyboard Event PrintEvent]
   [orb.waveform.generator
    AddGenerator
    MixGenerator
    LineGenerator
    SineGenerator
    DelayGenerator
    TableGenerator
    DivideGenerator
    SmoothGenerator
    ConstantGenerator
    MultiplyGenerator
    TonalityGenerator]))

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

(defn divide
  [x y]
  (DivideGenerator. (const? x) (const? y)))

(defn const
  [value]
  (ConstantGenerator. value))

(defn line
  [step]
  (LineGenerator. (const? step)))

(defn delay
  [source window max]
  (DelayGenerator. (const? source) (const? window) max))

(defn mix
  [channels]
  (MixGenerator. channels))

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

(defn key
  [keyboard voice]
  (.key keyboard voice))

(defn tone
  [keyboard voice]
  (.tone (.key keyboard voice)))

(defn energy
  [keyboard voice]
  (.energy (.key keyboard voice)))

(defn tonality
  [tones root frequency]
  (Tonality. tones root frequency))

(defn table
  [position samples]
  (TableGenerator. position samples))

(defn key-tonality
  [keyboard tonality]
  (let [voices
        (for [key (.keys keyboard)]
          (let [tones (TonalityGenerator. tonality (.tone key))
                energy (SmoothGenerator. (.energy key) Signal/SIGNAL_STEP)
                index (line (multiply tones Generator/SAMPLE_INTERVAL))
                sine-table (table/sine-table 1024)]
            ;; (sine tones energy)
            (multiply (table index sine-table) energy)))]
    (mix voices)))

(defn keyboard
  [voices]
  (let [board (Keyboard. "Digital Piano" voices)]
    (.power board)
    board))

(def nineteen
  (tonality
   (tonality/equal-temperament 19)
   40 100.0))

(defn run
  []
  (let [board (keyboard 13)
        channels (key-tonality board nineteen)]
    (signal channels)))

(defn -main
  []
  (run)
  @(promise))
