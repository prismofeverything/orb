(ns orb.cell
  (:require [clojure.string :as string]
            [orb.cell :as cell]))

(defn generate-rows
  "Generates a list of coordinates for each row."
  [[width height]]
  (for [y (range height)]
    (for [x (range width)]
      [x y])))

(defn generate-locations
  "Generates a 'flat' list of all locations."
  [dimensions]
  (apply
   concat
   (generate-rows dimensions)))

(def neighbor-kernel
  (apply
   concat
   (for [x (range -1 2)]
     (for [y (range -1 2)]
       [x y]))))

(defn adjacent-cells
  "Builds list of adjacent cells, including x y.
   ex (adjacent-cells [4 4] [2 2] )
   ([1 1] [1 2] [1 3] 
    [2 1] [2 2] [2 3] 
    [3 1] [3 2] [3 3])"
  [[width height] [x y]]
  (map
   (fn [[dx dy]]
     [(mod (+ x dx) width)
      (mod (+ y dy) height)])
   neighbor-kernel))

(defn build-adjacencies
  "Builds a map of cell location -> adjacent locations."
  [dimensions]
  (let [locations (generate-locations dimensions)]
    (reduce
     (fn [adjacencies location]
       (assoc adjacencies location (adjacent-cells dimensions location)))
     {}
     locations)))

(defn adjacent-states
  "Builds list of neighboring states, including given location"
  [world location]
  (let [cells (get world :cells)
        adjacencies (get world :adjacencies)
        neighbors (get adjacencies location)]
    (map
     (fn [neighbor-location]
       (get cells neighbor-location))
     neighbors)))

(defn build-cells
  "Builds map of location -> state."
  [dimensions seed-fn]
  (let [width (first dimensions)
        height (last dimensions)]
    (reduce
     (fn [cells x]
       (reduce
        (fn [cells y]
          (assoc cells [x y] (seed-fn [x y])))
        cells
        (range height)))
     {}
     (range width))))

(defn make-world
  [dimensions states seed-fn]
  (let [cells (build-cells dimensions seed-fn)
        adjacencies (build-adjacencies dimensions)]
    {:dimensions dimensions
     :states states
     :cells cells
     :adjacencies adjacencies}))

(defn seed-random-state
  [states]
  (fn [location]
    (first (shuffle states))))

(defn print-states
  [world]
  (let [dimensions (get world :dimensions)
        rows (generate-rows dimensions)
        cells (get world :cells)]
    (doseq [row rows]
      (let [states (map (fn [location] (get cells location)) row)
            line (string/join " " states)]
        (println line)))))

(defn update-world
  "Applies update rule to the current world, generating the next world state."
  [world rule]
  ;;gets keys from cell map
  (let [locations (keys (get world :cells))
        update (reduce
                (fn [states location]
                  (let [adjacent (adjacent-states world location)
                        next-state (rule adjacent)]
                    (assoc states location next-state)))
                {}
                locations)]
    (assoc world :cells update)))

(defn life-rule
  [states]
  (let [center-state (nth states 4)
         ;; * -1 center-state is to cancel out center-state (the cell being updated).
        live-adjacencies (reduce + (* -1 center-state) states)]
    (if (= center-state 0)
      (if (= live-adjacencies 3) 1 0)
      (if (#{2 3} live-adjacencies) 1 0))))

(defn exp [x n]
  (if (zero? n) 1
      (* x (exp x (dec n)))))

(defn highest-power-of-2 [power number]
  (let [power-value (exp 2 power)]
    (if (> number power-value)
      (highest-power-of-2 (+ power 1) (- number power-value))
      power)))

(defn number->binary
  [number]
  (let [powers (range (+ 1 (highest-power-of-2 0 number)))]
    (reverse (first (reduce
                     (fn [[binary-seq number-remaining] power]
                       (if (>= number-remaining (exp 2 power))
                         [(cons 1 binary-seq) (- number-remaining (exp 2 power))]
                         [(cons 0 binary-seq) number-remaining]))
                     [[] number]
                     (reverse powers))))))

(defn binary->number
  [digits]
  (first ;;Pulls decimal-number out of vector produced by reduce (only want the number, not the power)
   (reduce
    (fn [[decimal-number power] bit]
      (let [;;Convert bit and add to decimal-number 
            decimal-number-plus-bit (+ decimal-number (* bit power))
            ;;Prepare power for next iteration
            power-next-bit (* 2 power)]
        ;;Return vector for next reduce iteration
        [decimal-number-plus-bit power-next-bit]))
    ;;Init values for decimal-number and power
    [0 1]
    ;;Reverse binary string, since it will be processed left to right
    (reverse digits))))

(defn -main
  []
  (let [dimensions [16 16];;Generate a 256 cell world to represent all values of 8 bit string (from neighbor states)
        states [0 1]
        world (make-world dimensions states (seed-random-state states))]
    (print-states world)
    (println)
    (print-states (update-world world life-rule))
    (println "------")
    ;; (println (adjacent-states (update-world world life-rule) [16 16]))
    (println "-------")
    (println "==============")
    (println (number->binary 0))
    (println (number->binary 1))
    (println (number->binary 2))
    (println (number->binary 3))
    (println (number->binary 4))
    (println (number->binary 5))
    (println (number->binary 6))
    (println (number->binary 7))
    (println (number->binary 8))
    (println (number->binary 9))

    ))



