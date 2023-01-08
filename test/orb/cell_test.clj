(ns orb.cell-test
  (:require [clojure.test :refer :all]
            [orb.cell :as cell]))

(deftest generate-rows-test
  (testing "Generates list of rows."
    (let [rows (cell/generate-rows [4 4])]
      (is (= rows '(([0 0] [1 0] [2 0] [3 0]) 
                    ([0 1] [1 1] [2 1] [3 1])
                    ([0 2] [1 2] [2 2] [3 2])
                    ([0 3] [1 3] [2 3] [3 3])))))))
                    

((deftest generate-locations-test
      (testing "Generates flat location list."
        (let [locations (cell/generate-locations [2 2])]
          (is (= locations '([0 0] [1 0] 
                             [0 1] [1 1])))))))

((deftest adjacent-cells-test
      (testing "Generates neighbors, including given location."
        (let [adjacent-cells (cell/adjacent-cells [4 4] [2 2])]
          (is (= adjacent-cells '([1 1] [1 2] [1 3] 
                                  [2 1] [2 2] [2 3] 
                                  [3 1] [3 2] [3 3])))))))
        

((deftest build-adjacencies-test
      (testing "Builds map of location -> adjacent locations."
        (let [adjacencies (cell/build-adjacencies [2 2])]
          (is (= adjacencies {[0 0] '([1 1] [1 0] [1 1] [0 1] [0 0] [0 1] [1 1] [1 0] [1 1]),
                              [1 0] '([0 1] [0 0] [0 1] [1 1] [1 0] [1 1] [0 1] [0 0] [0 1]),
                              [0 1] '([1 0] [1 1] [1 0] [0 0] [0 1] [0 0] [1 0] [1 1] [1 0]),
                              [1 1] '([0 0] [0 1] [0 0] [1 0] [1 1] [1 0] [0 0] [0 1] [0 0])}))))))

((deftest build-cells-test
      (testing "Builds map of location -> state."
        (let [location [2 2]
              state 5
              state-seed-func (fn [location] state)
              cells (cell/build-cells location state-seed-func)]
        (is (= cells {[0 0] state, [0 1] state, 
                      [1 0] state, [1 1] state}))))))

((deftest adjacent-states-test
      (testing "Builds list of neighboring (given location) states, including given location."
        (let [
              location [2 2]
              state 5
              dimensions [6 6]
              cells (cell/build-cells dimensions (fn [location] state))
              adjacent-states (cell/adjacent-states { :cells cells
                                                      :adjacencies (cell/build-adjacencies dimensions)} location)]
          (is (= adjacent-states '(5 5 5 5 5 5 5 5 5 )))))))

((deftest binary->number-test
      (testing "Converts binary to digital."
        (is (= (cell/binary->number [1 0 1 1]) 11)))))

((deftest number->binary-test
      (testing "Converts digital to binary."
        (is (= (cell/number->binary 11) [1 0 1 1])))))

;;paramatized tests?
((deftest number->binary->number-9-test
      (testing "Converts digital to binary, and back to digital."
        (is (= (cell/binary->number (cell/number->binary 9)) 9)))))

((deftest number->binary->number-0-test
   (testing "Converts digital to binary, and back to digital."
     (is (= (cell/binary->number (cell/number->binary 0)) 0)))))

((deftest number->binary->number-1-test
   (testing "Converts digital to binary, and back to digital."
     (is (= (cell/binary->number (cell/number->binary 1)) 1)))))

((deftest all-neighbors-states-test
      (testing "Filters to all neightbors (removes center, which is being updated)."
        (is (= [0 0 0 1 1 0 0 0] (cell/all-neighbors-states [0 0 0 1 1 1 0 0 0]))))) )

((deftest east-west-neighbors-states-test
   (testing "Filters to E and W neightbors."
     (is (= [1 1] (cell/east-west-neighbor-states [0 0 0 1 1 1 0 0 0]))))))

((deftest north-south-neighbors-states-test
   (testing "Filters to N and S neightbors."
     (is (= [1 1] (cell/north-south-neighbor-states [0 1 0 0 0 0 0 1 0]))))))

((deftest rule-key-location-test-1
   (testing "Maps rule key (index) to cells grid location ([x y])"
     (is (= [1 2] (cell/rule-key-location 5 [3 3]))))))


((deftest rule-key-location-test-2
   (testing "Maps rule key (index) to cells grid location ([x y])"
     (is (= [0 0] (cell/rule-key-location 0 [3 3]))))))

;; ((deftest self-ref-rule-east-west-neighbors-states-test-case-1
;;    (testing "Filters to all neighbors (removes center, which is being updated)."
;;      (let [;; Generate a 256 cell world to represent all values of 8 bit string (from neighbor states).
;;            dimensions [2 2]
;;            states [0 1]
;;            world-state-init [[1 0]
;;                              [0 1]]
;;            world-init (cell/make-world dimensions states (fn [[x y]] (nth (nth world-state-init y) x)))
;;            ;; Generate a rule function, using only east and west neighbors
;;            rule (cell/self-ref-rule-factory cell/east-west-neighbor-states)
;;            world-next-state (cell/update-world world-init rule)]
;;       ;;  (println world-next-state)
;;        (is (= (vals (get world-next-state :cells)) '(1 1 
;;                                                      1 1)))))))

;; ((deftest self-ref-rule-east-west-neighbors-states-test-case-2
;;    (testing "Filters to all neighbors (removes center, which is being updated)."
;;      (let [;; Generate a 256 cell world to represent all values of 8 bit string (from neighbor states).
;;            dimensions [2 2]
;;            states [0 1]
;;            world-state-init [[1 1]
;;                              [0 0]]
;;            world-init (cell/make-world dimensions states (fn [[x y]] (nth (nth world-state-init y) x)))
;;            ;; Generate a rule function, using only east and west neighbors
;;            rule (cell/self-ref-rule-factory cell/east-west-neighbor-states)
;;            world-next-state (cell/update-world world-init rule)]
;;       ;;  (println world-next-state)
;;        (is (= (vals (get world-next-state :cells)) '(0 0 1 1)))))))

;; ((deftest self-ref-rule-north-south-neighbors-states-test-case-1
;;    (testing "Filters to all neighbors (removes center, which is being updated)."
;;      (let [;; Generate a 256 cell world to represent all values of 8 bit string (from neighbor states).
;;            dimensions [2 2]
;;            states [0 1]
;;            world-state-init [[1 0]
;;                              [0 1]]
;;            world-init (cell/make-world dimensions states (fn [[x y]] (nth (nth world-state-init y) x)))
;;            ;; Generate a rule function, using only east and west neighbors
;;            rule (cell/self-ref-rule-factory cell/north-south-neighbor-states)
;;            world-next-state (cell/update-world world-init rule)]
;;       ;;  (println world-next-state)
;;        (is (= (vals (get world-next-state :cells)) '(1 1
;;                                                        1 1)))))))

;; ((deftest self-ref-rule-east-west-neighbors-states-test-case-2
;;    (testing "Filters to all neighbors (removes center, which is being updated)."
;;      (let [;; Generate a 256 cell world to represent all values of 8 bit string (from neighbor states).
;;            dimensions [2 2]
;;            states [0 1]
;;            world-state-init [[1 1]
;;                              [0 0]]
;;            world-init (cell/make-world dimensions states (fn [[x y]] (nth (nth world-state-init y) x)))
;;            ;; Generate a rule function, using only east and west neighbors
;;            rule (cell/self-ref-rule-factory cell/north-south-neighbor-states)
;;            world-next-state (cell/update-world world-init rule)]
;;       ;;  (println world-next-state)
;;        (is (= (vals (get world-next-state :cells)) '(1 1 0 0)))))))
