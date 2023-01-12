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
                        next-state (rule adjacent world)]
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
  [binary]
  ;; Pulls decimal-number out of vector produced by reduce (only want the number, not the power).
  (first 
   (reduce
    (fn [[;; Cumulative decimal number being reduced.
          decimal-number
          ;; Current binary power being processed.
          power]
         ;; Current bit being processed.
         bit]
      (let [;; Convert bit and add to decimal-number.
            decimal-number-plus-bit (+ decimal-number (* bit power))
            ;; Prepare power for next iteration.
            power-next-bit (* 2 power)]
        ;; Return vector for next reduce iteration.
        [decimal-number-plus-bit power-next-bit]))
    ;; Init values for [decimal-number power].
    [0 1]
    ;; Reverse binary string, since it will be processed left to right.
    (reverse 
     ;; Binary string as vector ex [1 1 0 1].
     binary))))

(defn all-neighbors-states
  [states]
  (let [
        ;; Converts states list to vector.
        states-vec (into [] states)]
    (into (subvec states-vec 0 4) (subvec states-vec (inc 4)))))

(defn east-west-neighbor-states
  [states]
  [(nth states 3) (nth states 5)])

(defn north-south-neighbor-states
  [states]
  [(nth states 1) (nth states 7)])

;; Maps rule key (index) to cells grid location ([x y]). 
;;
;; Index into rows data structure.  2D rows list must be 'unwrapped', in order to index into it
;; using rule-key.  Unwrapps 'column-wise' (counts down entire first column, proceeds to second..). 
;; Returns a [col row] location.
(defn decimal-rule-key-to-location 
  [rule-key dimensions]
  (let [col (quot rule-key (second dimensions))
        row (mod rule-key (second dimensions))]
    [col row]))

(defn location-to-state
  [world location]
  (let [cells (get world :cells)]
    (get cells location)))

(defn self-ref-rule-factory
  [neighbor-states-filter]
  (fn [neighbor-states world]
    (let [dimensions (get world :dimensions)
        ;; Removes neighbor states unused by rule.
          states-filtered (neighbor-states-filter neighbor-states)
        ;; Converts neigboring states binary str to decimal index, for use as rule key.
          rule-key (binary->number states-filtered)
          location (decimal-rule-key-to-location rule-key dimensions)]
      (location-to-state world location))))

(defn -main
  []
  (let [;; Generate a 256 cell world to represent all values of 8 bit string (from neighbor states).
        cols 32
        rows 16
        dimensions [cols rows]
        states [0 1]
        ;; world-init-state (make-world dimensions states (seed-random-state states))
        world-init-state (make-world dimensions states (fn [location] (if (#{[0 9] [3 3] [4 3]} location) 1 0)))
        ;; Number of generations to render.
        generations 1
        ;; Generate a rule function, using all neighbors
        all-neighbors-self-ref-rule (self-ref-rule-factory identity)]
        
    (println "***Init***")
    (print-states world-init-state)
    (reduce (fn [world-current-state generation]
              (let [world-next-state (update-world world-current-state all-neighbors-self-ref-rule)]
                (println)
                (println (str "***Generation: " generation))
                (println)
                (print-states world-next-state)
                world-next-state))
            world-init-state
                            ;; Generations collection to reduce.
            (range generations))
    ))



