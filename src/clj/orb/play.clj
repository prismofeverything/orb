(ns orb.play
  (:require
   [orb.letter :as letter]
   [orb.core :as core]))

;; if on, remove from on-letters
;; if off, add to on-letters
;; rebuild generators from new on-letters
;; swap new generator into signal

(defn handle-letter
  [signal on-letters letter]
  (let [on (deref on-letters)
        current (get on letter)]
    (if current
      (swap! on-letters dissoc letter))))

(defn generate-letter-map
  [signal on-letters]
  (reduce
   (fn [letter-map letter]
     (assoc letter-map letter (handle-letter signal on-letters letter)))
   {}
   (range 97 123)))

(defn play-letters
  []
  (let [signal (core/signal! 0)
        on-letters (atom {})
        letter-map (generate-letter-map signal on-letters)]))

(defn -main
  [])
