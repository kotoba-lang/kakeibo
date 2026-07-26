(ns kakeibo.topology-test
  (:require #?(:clj [clojure.test :refer [deftest is]]
               :cljs [cljs.test :refer [deftest is]])
            [kakeibo.topology :as topology]))

(def graph
  {:topology/issues
   [{:issue/id "a" :issue/status :open :issue/priority :p0
     :issue/blocked-by []}
    {:issue/id "b" :issue/status :open :issue/priority :p1
     :issue/blocked-by ["a"]}
    {:issue/id "c" :issue/status :open :issue/priority :p1
     :issue/blocked-by ["a" "b"]}]})

(deftest topology-is-queryable-and-ordered
  (is (empty? (topology/validate graph)))
  (is (= ["a" "b" "c"] (topology/topological-order graph)))
  (is (= ["a"] (mapv :issue/id (topology/runnable graph))))
  (is (= [[:issue/id "a"]]
         (:issue/blocked-by (second (topology/datoms graph))))))

(deftest completed-blockers-unlock-the-next-issue
  (let [advanced (update-in graph [:topology/issues 0]
                            assoc :issue/status :integrated)]
    (is (= ["b"] (mapv :issue/id (topology/runnable advanced))))))

(deftest cycles-and-missing-blockers-fail-closed
  (is (seq (topology/validate
            (update-in graph [:topology/issues 0]
                       assoc :issue/blocked-by ["missing"]))))
  (is (thrown? #?(:clj clojure.lang.ExceptionInfo :cljs js/Error)
               (topology/topological-order
                (-> graph
                    (assoc-in [:topology/issues 0 :issue/blocked-by] ["c"]))))))
