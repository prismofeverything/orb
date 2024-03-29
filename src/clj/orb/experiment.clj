(ns orb.experiment
  (:require
   [orb.letter :as letter]
   [orb.core :as core]
   [orb.tonality :as tonality]))

(defn mix-sines
  [frequencies]
  (let [generators (map core/make-sine frequencies)]
    (core/mix generators)))

(defn -main
  []
  (core/signal! (mix-sines [(core/add (core/make-sine 1) 400) 880 660]))
  @(promise))

