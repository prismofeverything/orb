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
      (testing "Builds list of neighboring (given location) states, including given location"
        (let [
              location [2 2]
              state 5
              dimensions [6 6]
              cells (cell/build-cells dimensions (fn [location] state))
              adjacent-states (cell/adjacent-states { :cells cells
                                                      :adjacencies (cell/build-adjacencies dimensions)} location)]
          (is (= adjacent-states '(5 5 5 5 5 5 5 5 5 )))))))

((deftest binary->number
      (testing "Context of the test assertions"
        (is (= (cell/binary->number [1 0 1 1]) 11)))))