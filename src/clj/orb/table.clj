(ns orb.table)

(defn sine-table
  [samples]
  (let [step (/ (* Math/PI 2) samples)]
    (for [sample (range samples)]
      (Math/sin (* sample step)))))

(defn random-table
  [samples]
  (for [sample (range samples)]
    (- (* 2 (rand)) 1)))

(defn noise-table
  [samples]
  (for [sample (range samples)]
    (if (< (rand) 0.5)
      -1.0
      1.0)))
