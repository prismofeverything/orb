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
    BendGenerator
    MixGenerator
    LineGenerator
    SineGenerator
    DelayGenerator
    PluckGenerator
    TableGenerator
    DivideGenerator
    SmoothGenerator
    ConstantGenerator
    ConvolveGenerator
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

(defn convolve
  [source kernel]
  (ConvolveGenerator. source kernel))

(defn mix
  [channels]
  (MixGenerator. channels))

(defn emit
  [generator t]
  (Emit/at generator t))

(defn make-signal
  ([generator] (make-signal generator ""))
  ([generator output-path]
   (let [signal (Signal. generator output-path)]
     signal)))

(defn signal!
  ([generator] (signal! generator ""))
  ([generator output-path]
   (let [signal (make-signal generator output-path)]
     (.start signal)
     signal)))

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

(defn velocity
  [keyboard voice]
  (.velocity (.key keyboard voice)))

(defn tonality
  [tones root frequency]
  (Tonality. (map double tones) root frequency))

(defn table
  [position samples]
  (TableGenerator. position samples))

(defn pluck
  [position samples]
  (PluckGenerator. position samples))

(defn key-tonality
  [keyboard tonality]
  (let [voices
        (for [key (.keys keyboard)]
          (let [tones (TonalityGenerator. tonality (.tone key))
                bend (BendGenerator. 0.0002 tones (.pitch key))
                velocity (SmoothGenerator. (.velocity key) Signal/SIGNAL_STEP)
                index (line (multiply bend Generator/SAMPLE_INTERVAL))
                noise-table (table/noise-table 1024)
                pluck-index (pluck index noise-table)
                ;; sine-table (table/sine-table 1024)
                ;; sine-index (table index sine-table)
                ;; voice (convolve sine-index [1.0])
                voice (convolve pluck-index [1.0])]
            ;; (sine tones velocity)
            (multiply voice velocity)))]
    (mix voices)))

(defn keyboard
  [device voices]
  (let [board (Keyboard. device voices)]
    (.power board)
    board))

(def nineteen
  (tonality
   tonality/overtone-nineteen
   ;; (map double tonality/pure-nineteen)
   ;; (tonality/equal-temperament 19)
   19 100.0))

(defn run-keyboard
  ([device] (run-keyboard device nil))
  ([device output-path]
   (let [board (keyboard device 13)
         channels (key-tonality board nineteen)]
     (if output-path
       (signal! channels output-path)
       (signal! channels)))))

(defn make-sine
  [frequency]
  (let [index (line (* Generator/SAMPLE_INTERVAL frequency))
        sine-table (table/sine-table 1024)
        sine-index (table index sine-table)]
    sine-index))

(defn play-sine
  [frequency]
  (signal! (make-sine frequency)))

(defn -main
  [& args]
  ;; (play-sine 440)
  (let [device (or (first args) "MIDI")
        output-path (second args)]
    (if output-path
      (run-keyboard device output-path)
      (run-keyboard device))
    @(promise)))

;; to convert raw pcm output to wav file:
;; > ffmpeg -f s16be -ar 44.1k -ac 1 -i out/signal.pcm out/signal.wav
