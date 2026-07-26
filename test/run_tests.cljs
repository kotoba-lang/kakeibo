#!/usr/bin/env nbb
;; nbb test runner. ClojureScript-on-Node is kakeibo's first-class runtime
;; (CLAUDE.md runtime priority: kotoba wasm > clojurewasm > ClojureScript >
;; nbb, with the JVM last), so this — not a JVM suite — is the gate.
;;
;;   nbb --classpath "src:test:../banking/src" test/run_tests.cljs
;;
;; Exits non-zero on any failure or error so CI and `tamaki exec` see the truth
;; without parsing output.
(ns run-tests
  (:require [cljs.test :as t]
            [kakeibo.amount-test]
            [kakeibo.tx-test]
            [kakeibo.dedup-test]
            [kakeibo.category-test]
            [kakeibo.ledger-test]
            [kakeibo.topology-test]
            [kakeibo.rollup-test]
            [kakeibo.core-test]))

(defmethod t/report [:cljs.test/default :end-run-tests] [m]
  (println)
  (if (t/successful? m)
    (println "kakeibo: all tests passed")
    (do (println "kakeibo: FAILED")
        (js/process.exit 1))))

(t/run-tests 'kakeibo.amount-test
             'kakeibo.tx-test
             'kakeibo.dedup-test
             'kakeibo.category-test
             'kakeibo.ledger-test
             'kakeibo.topology-test
             'kakeibo.rollup-test
             'kakeibo.core-test)
