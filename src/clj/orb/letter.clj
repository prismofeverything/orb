(ns orb.letter
  (:import [jline.console ConsoleReader]))

(defn listen
  [letter-map]
  (print "yo")
  (flush)
  (let [reader (ConsoleReader.)]
    (loop [letter (.readCharacter reader)]
      (if-let [letterfn (get letter-map letter)]
        (do
          (letterfn)
          (recur (.readCharacter reader)))))))

(defn -main
  []
  (listen {97 (fn [] (println "key 97 woo"))}))
