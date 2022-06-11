(ns orb.play
  (:require
   [orb.letter :as letter]
   [orb.core :as core]
   [orb.tonality :as tonality]))

(defn build-generator
  [on letter]
  (let [tones (tonality/tonality [1 9/8 5/4 3/2 5/3] 100 97)
        on-tones (map (fn [on-letter] (core/make-sine (tones on-letter))) on)]
    (core/mix on-tones)))

(defn handle-letter
  [signal on-letters letter]
  (fn []
    (let [on (deref on-letters)
          current (get on letter)]
      (if current
        (swap! on-letters dissoc letter)
        (swap! on-letters assoc letter true))
      (let [generator (build-generator (keys (deref on-letters)) letter)]
        (core/swap-generator signal generator)))))

(defn generate-letter-map
  [signal on-letters]
  (reduce
   (fn [letter-map letter]
     (assoc letter-map letter (handle-letter signal on-letters letter)))
   {}
   (range 97 123)))

(defn play-letters
  []
  (let [signal (core/signal! (core/const 0))
        on-letters (atom {})
        letter-map (generate-letter-map signal on-letters)]
    (letter/listen letter-map)))

;; make way to stop the listen

(defn -main
  []
  (play-letters))
