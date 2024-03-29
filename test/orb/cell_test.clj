(ns orb.cell-test
  (:require [clojure.test :refer :all]
            [orb.cell :as cell]))

(deftest generate-rows-test
  (testing "Generates list of rows.  Each row location is stored as [col row]."
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
     (is (= [1 1] (cell/north-south-neighbor-states [0 1 0 
                                                     0 0 0 
                                                     0 1 0]))))))

((deftest rule-key-location-test-1
   (testing "Maps rule key (index) to cells grid location ([x y])"
     (is (= [1 2] (cell/decimal-rule-key-to-location 5 [3 3]))))))


((deftest rule-key-location-test-2
   (testing "Maps rule key (index) to cells grid location ([x y])"
     (is (= [0 0] (cell/decimal-rule-key-to-location 0 [3 3]))))))


((deftest location-to-state-0-0
   (testing "Location is mapped to the correct state."
     (let [dimensions [2 2]
           states [0 1]
           world-state-init [[1 1]
                             [0 0]]
           world (cell/make-world dimensions states (fn [[x y]] (nth (nth world-state-init y) x)))
           ;; Generate a rule function, using only east and west neighbors
           state (cell/location-to-state world [0 0])]
       (is (= state 1))))))


((deftest location-to-state-0-1
   (testing "Location is mapped to the correct state."
     (let [dimensions [2 2]
           states [0 1]
           world-state-init [[1 1]
                             [0 0]]
           world (cell/make-world dimensions states (fn [[x y]] (nth (nth world-state-init y) x)))
           ;; Generate a rule function, using only east and west neighbors
           state (cell/location-to-state world [0 1])] ;; [col row]
       (is (= state 0))))))

((deftest location-to-state-1-0
   (testing "Location is mapped to the correct state."
     (let [dimensions [2 2]
           states [0 1]
           world-state-init [[1 1]
                             [0 0]]
           world (cell/make-world dimensions states (fn [[x y]] (nth (nth world-state-init y) x)))
           ;; Generate a rule function, using only east and west neighbors
           state (cell/location-to-state world [1 0])] ;; [col row]
       (is (= state 1))))))


((deftest location-to-state-1-1
   (testing "Location is mapped to the correct state."
     (let [dimensions [2 2]
           states [0 1]
           world-state-init [[1 1]
                             [0 0]]
           world (cell/make-world dimensions states (fn [[x y]] (nth (nth world-state-init y) x)))
           ;; Generate a rule function, using only east and west neighbors
           state (cell/location-to-state world [1 1])] ;; [col row]
       (is (= state 0))))))

;; INTEGRATION TESTS
;;
;; The following tests show that units tested above can be integrated to produce a self referential cellular autata
;;
;; Test 1a (simple) 
;; Only the neighbors affect the next generation (the seeded 'alive' rule key value has no affect)
;;
;;sets up an initial world state:
;;
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;
;; and shows that in the next generation, the state will correctly be updated to:
;;
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;
;; Cell [3 2] is alive in gen 2 since in gen 1, its neighbor states bit string produces: 
;; [0 0 0 0 0 1 0 0 1].  In decimal, this is 9.  The cell at index 9 is alive in gen 1.
;;
((deftest self-ref-rule-1a
   (testing "Updates world state to next generation correctly."
     (let [;; Generate a 512 cell world to represent all values of 9 bit string (the state of the cell being updated, and all of it's neighbors).
           cols 32
           rows 16
           dimensions [cols rows]
           states [0 1]
           seeded-gen-1-alive-rule-key [0 9] ;; maps to world index 9, in binary 000001001
           seeded-gen-1-alive-neighbors [[3 3] [4 3]] ;; arrange neighbors to map to 000001001
           seeded-gen-1-alive-locations (into #{seeded-gen-1-alive-rule-key} seeded-gen-1-alive-neighbors)
           seeded-gen-2-expected-alive [3 2]
           world-gen-1 (cell/make-world dimensions states (fn [location] (if (seeded-gen-1-alive-locations location) 1 0)))
           all-neighbors-self-ref-rule (cell/self-ref-rule-factory identity)
           world-gen-2 (cell/update-world world-gen-1 all-neighbors-self-ref-rule)
           expected-gen-2 (cell/make-world dimensions states (fn [location] (if (#{seeded-gen-2-expected-alive} location) 1 0)))]
       (is (= world-gen-2 expected-gen-2))))))

;; Test 1b sets up the same configuration, transposed to a different part of the world
;;
((deftest self-ref-rule-1b
   (testing "Updates world state to next generation correctly."
     (let [;; Generate a 512 cell world to represent all values of 9 bit string (the state of the cell being updated, and all of it's neighbors).
           cols 32
           rows 16
           dimensions [cols rows]
           states [0 1]
           seeded-gen-1-alive-rule-key [0 9] ;; maps to world index 9, in binary 000001001
           seeded-gen-1-alive-neighbors [[5 8] [6 8]] ;; arrange neighbors to map to 000001001
           seeded-gen-1-alive-locations (into #{seeded-gen-1-alive-rule-key} seeded-gen-1-alive-neighbors)
           seeded-gen-2-expected-alive [5 7]
           world-gen-1 (cell/make-world dimensions states (fn [location] (if (seeded-gen-1-alive-locations location) 1 0)))
           all-neighbors-self-ref-rule (cell/self-ref-rule-factory identity)
           world-gen-2 (cell/update-world world-gen-1 all-neighbors-self-ref-rule)
           expected-gen-2 (cell/make-world dimensions states (fn [location] (if (#{seeded-gen-2-expected-alive} location) 1 0)))]
       (is (= world-gen-2 expected-gen-2))))))

;; Test 2 (a bit of chaos).  
;;
;; The seeded alive value affects the next generation.
;;
;; Initial config:
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;
;; Gen 2:
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
((deftest self-ref-rule-2a
   (testing "Updates world state to next generation correctly."
     (let [;; Generate a 512 cell world to represent all values of 9 bit string (the state of the cell being updated, and all of it's neighbors).
           cols 32
           rows 16
           dimensions [cols rows]
           states [0 1]
           seeded-gen-1-alive-rule-key [0 4] ;; maps to world index 4, in binary 000000100
                                             ;; arrange neighbors to map to 001010101  
                                             ;; In our mapping scheme: 
                                             ;; 0 0 1 
                                             ;; 0 0 0
                                             ;; 0 0 0
           seeded-gen-1-alive-neighbors [[10 10]]
           seeded-gen-1-alive-locations (into #{seeded-gen-1-alive-rule-key} seeded-gen-1-alive-neighbors)
           seeded-gen-2-expected-alive [9 11] ;; SE of single seeded-gen-1-alive-neighbor
           inadvertently-alive [31 5] ;; SE of seeded-gen-1-alive-rule-key
           world-gen-1 (cell/make-world dimensions states (fn [location] (if (seeded-gen-1-alive-locations location) 1 0)))
           all-neighbors-self-ref-rule (cell/self-ref-rule-factory identity)
           world-gen-2 (cell/update-world world-gen-1 all-neighbors-self-ref-rule)
           expected-gen-2 (cell/make-world dimensions states (fn [location] (if (#{seeded-gen-2-expected-alive inadvertently-alive} location) 1 0)))]
       (is (= world-gen-2 expected-gen-2))))))

;;Test 3 (a bit more chaos)
;; ((deftest self-ref-rule-3a
;;    (testing "Updates world state to next generation correctly."
;;      (let [;; Generate a 512 cell world to represent all values of 9 bit string (the state of the cell being updated, and all of it's neighbors).
;;            cols 32
;;            rows 16
;;            dimensions [cols rows]
;;            states [0 1]
;;            seeded-gen-1-alive-rule-key [5 5] ;; maps to world index 85, in binary 001010101
;;                                              ;; arrange neighbors to map to 001010101  
;;                                              ;; In our mapping scheme: 
;;                                              ;; 0 0 1 
;;                                              ;; 0 1 0
;;                                              ;; 1 0 1
;;            seeded-gen-1-alive-neighbors [[10 8] [9 9] [8 10] [10 10]]
;;            seeded-gen-1-alive-locations (into #{seeded-gen-1-alive-rule-key} seeded-gen-1-alive-neighbors)
;;            seeded-gen-2-expected-alive [9 9]
;;            inadvertently-alive [10 8]
;;            world-gen-1 (cell/make-world dimensions states (fn [location] (if (seeded-gen-1-alive-locations location) 1 0)))
;;            all-neighbors-self-ref-rule (cell/self-ref-rule-factory identity)
;;            world-gen-2 (cell/update-world world-gen-1 all-neighbors-self-ref-rule)
;;            expected-gen-2 (cell/make-world dimensions states (fn [location] (if (#{seeded-gen-2-expected-alive inadvertently-alive} location) 1 0)))]
;;        (cell/print-states world-gen-1)
;;        (println)
;;        (cell/print-states world-gen-2)
;;        (println)
;;        (cell/print-states expected-gen-2)
;;        (is (= 1 1))))))
