(ns kwill.anomkit-test
  (:require
    [clojure.test :refer :all]
    [kwill.anomkit :as ak])
  (:import (clojure.lang ExceptionInfo)))

(deftest anomaly?-test
  (is (= true (ak/anomaly? (ak/fault "a"))))
  (is (= false (ak/anomaly? {}))))

(deftest some-test
  (is (= {:cognitect.anomalies/category :cognitect.anomalies/incorrect
          :cognitect.anomalies/message  ""}
        (ak/some (ak/incorrect ""))))
  (is (= nil (ak/some {}))))

(deftest anomaly-ex
  (let [ex (ex-info "x" {:a 1})
        ex-anom (-> (ak/fault "") (ak/with-ex ex))]
    (is (= ex (ak/ex ex-anom))
      "Original exception is returned")
    (is (thrown? ExceptionInfo (ak/! ex-anom))
      "Exception thrown")
    (is (= 1 (ak/?! 1))
      "Exception not thrown when arg is not an anomaly")))

(def anomaly-category-fns
  (map (fn [category]
         {:cognitect.anomalies/category
          category
          ::pred     @(requiring-resolve (symbol (str 'kwill.anomkit) (str (name category) "?")))
          ::map-fn   @(requiring-resolve (symbol (str 'kwill.anomkit) (name category)))
          ::throw-fn @(requiring-resolve (symbol (str 'kwill.anomkit) (str (name category) "!")))})
    ak/categories))

(deftest anomaly-category-ops
  (doseq [{:cognitect.anomalies/keys [category]
           ::keys                    [map-fn pred throw-fn]} anomaly-category-fns]
    (let [anom (map-fn "msg")]
      (is (= category (ak/category anom))
        "anomaly category set")
      (is (= true (pred anom))
        "anomaly predicate returns true for an anomaly constructed by its map fn")
      (is (= false (pred "asd"))
        "anomaly predicate returns false otherwise")
      (is (thrown-with-msg? ExceptionInfo #"msg" (throw-fn "msg")
            "anomaly exception thrown")))))

(deftest category-from-http-status-test
  (is (= :cognitect.anomalies/incorrect (ak/category-from-http-status 400))))

(deftest ex->anomaly-test
  (is (= {:cognitect.anomalies/category :cognitect.anomalies/fault
          :cognitect.anomalies/message  "asd"}
        (ak/ex->anomaly (ex-info "asd" {}))))
  (is (= {:cognitect.anomalies/category :cognitect.anomalies/incorrect}
        (ak/ex->anomaly (ex-info "asd" {:cognitect.anomalies/category :cognitect.anomalies/incorrect})))))
