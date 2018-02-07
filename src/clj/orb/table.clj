(ns orb.table)

(defn sine-table
  [samples]
  (let [step (/ (* Math/PI 2) samples)]
    (for [sample (range samples)]
      (Math/sin (* sample step)))))
