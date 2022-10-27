(ns orb.cell-test
  (:require [clojure.test :refer :all]
            [orb.cell :as cell]))

(deftest generate-rows-test
  (testing "Generates list of rows."
    (let [rows (cell/generate-rows [4 4])]
      (is (= rows '(([0 0] [1 0] [2 0] [3 0]) 
                    ([0 1] [1 1] [2 1] [3 1])
                    ([0 2] [1 2] [2 2] [3 2])
                    ([0 3] [1 3] [2 3] [3 3])
                    ))))))

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
        

;; ((deftest build-adjacencies-test
;;       (testing "Context of the test assertions"
;;         (let [adjacencies (cell/build-adjacencies [2 2])]
;;           (is (= adjacencies :#{[0 0] '([1 1] [1 0] [1 1] [0 1] [0 0] [0 1] [1 1] [1 0] [1 1]),
;;                                 [1 0] '([0 1] [0 0] [0 1] [1 1] [1 0] [1 1] [0 1] [0 0] [0 1]),
;;                                 [0 1] '([1 0] [1 1] [1 0] [0 0] [0 1] [0 0] [1 0] [1 1] [1 0]),
;;                                 [1 1] '([0 0] [0 1] [0 0] [1 0] [1 1] [1 0] [0 0] [0 1] [0 0])}))))))