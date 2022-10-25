(ns orb.cell-test
  (:require [clojure.test :refer :all]
            [orb.cell :as cell]))

(deftest generate-rows-test
  (testing "Generates rows correctly."
    (let [rows (cell/generate-rows [2 2])]
      (is (= rows '(([0 0] [1 0]) ([0 1] [1 1])))))))
