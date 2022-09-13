(ns orb.cell)

(defn build-cells 
  [dimensions initial-state]
  (let [width (first dimensions) 
        height (last dimensions)]
    (reduce 
     (fn [cells x]
       (reduce 
        (fn [cells y]
          (assoc cells [x y] initial-state))
        cells
        (range height))) 
     {}
     (range width))))
  
(defn make-world 
  [dimensions states]
  (let [cells (build-cells dimensions (first states))]
    {:dimensions dimensions
     :states states
     :cells cells}))

(defn -main
  []
  (println
   (make-world [5 5] [0 1])))