(ns orb.cell)

(defn generate-locations
  [[width height]]
  (apply 
   concat
   (for [x (range width)]
     (for [y (range height)]
       [x y]))))

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

(defn build-adjacencies
  [dimensions]
  (let [locations (generate-locations dimensions)]
    (reduce 
     (fn [adjacencies location]
       (assoc adjacencies location (adjacent-cells dimensions location)))
     {} 
     locations)))

(defn adjacent-states
  [world location]
  (let [cells (get world :cells)
        adjacencies (get world :adjacencies)
        neighbors (get adjacencies location)]
    (map 
     (fn [neighbor-location]
       (get cells neighbor-location))
     neighbors)))
  

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
  (let [cells (build-cells dimensions (first states))
        adjacencies (build-adjacencies dimensions)]
    {:dimensions dimensions
     :states states
     :cells cells
     :adjacencies adjacencies}))

(defn -main
  []
  (let [dimensions [6 6]
        world (make-world dimensions [0 1])]
    (println world)
    (println (adjacent-cells dimensions [5 5]))))
   