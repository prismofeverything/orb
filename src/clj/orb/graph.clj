(ns orb.graph
  (:require [clojure.spec.alpha :as s]))

{:freq 110
 :amp 0.3
 :phase 0.1}

(defn node
  [id inputs transform]
  {:inputs inputs
   :transform transform})

(defn path-to
  [[node port]]
  [:nodes node :inputs port])

(defn empty-graph
  [config]
  {:config config
   :nodes {}})

(defn node-in
  [graph id]
  (get (:nodes graph) id))

(defn add-node
  [graph node]
  (update graph :nodes assoc (:id node) node))

(defn connect-node
  [graph [from-node from-port :as from] [to-node to-port]]
  (assoc-in
   graph
   [:nodes to-node :inputs to-port]
   from))

(defn disconnect-node
  [graph [node port]]
  (assoc-in graph [:nodes node :inputs port] nil))

(defn insert-node
  [graph
   [node in-port out-port]
   [after-node after-port]]
  (let [[before-node before-port :as before]
        (get-in graph [:nodes after-node :inputs after-port])]
    (-> graph
        (assoc-in [:nodes node :inputs in-port] before)
        (assoc-in [:nodes after-node :inputs after-port] [node out-port]))))

(s/def ::buffer-size pos-int?)
(s/def ::config (s/keys :req-un [::buffer-size]))

(defn start
  [config]
  {:config config})
