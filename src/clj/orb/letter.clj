(ns orb.letter
  (:require
   [lanterna.terminal :as terminal]))

(defn listen
  [letter-map]
  (let [terminal (terminal/get-terminal)]
    (loop [letter (terminal/get-key-blocking terminal)]
      (do 
        (println letter)
        (terminal/stop terminal)
        letter))))

(defn -main
  []
  (listen {}))
