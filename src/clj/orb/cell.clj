(ns orb.cell)

(def neighbor-kernel
  (apply 
   concat 
   (for [x (range -1 2)] 
     (for [y (range -1 2)] 
       [x y]))))

(defn adjacent-cells
  [[width height] [x y]]
  (map
   (fn [[dx dy]]
     [(mod (+ x dx) width)
      (mod (+ y dy) height)])
   neighbor-kernel))

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
  (let [dimensions [3 3]
        world (make-world dimensions [0 1])]
    (println world)
    (println (adjacent-cells dimensions [0 1]))
   ))