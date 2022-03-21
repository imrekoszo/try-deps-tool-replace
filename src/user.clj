(ns user
  (:require
   [clojure.java.classpath :as cp]
   [clojure.java.io :as io]
   [clojure.pprint :as pp]
   [clojure.string :as str]))

(defn some-end-with? [end strings]
  (some #(str/ends-with? % end) strings))

(defn debug [& {:keys [verbose]}]
  (let [cp (mapv #(.getCanonicalPath %) (cp/classpath))]
    (pp/print-table
     [:name :paths :deps]
     [{:name  'default
       :paths (some-end-with? "/default" cp)
       :deps  (some-end-with? "/data.json-2.4.0.jar" cp)}
      {:name  'replace
       :paths (some-end-with? "/replace" cp)
       :deps  (some-end-with? "/data.csv-1.0.0.jar" cp)}
      {:name  'extra
       :paths (some-end-with? "/extra" cp)
       :deps  (some-end-with? "/data.xml-0.0.8.jar" cp)}
      {:name  'nothing
       :paths (some-end-with? "/nothing" cp)
       :deps  (some-end-with? "/data.priority-map-1.1.0.jar" cp)}
      {:name  'tool
       :paths (some-end-with? (.getCanonicalPath (io/file ".")) cp)}])
    (when (true? verbose)
      (println)
      (run! println cp))))
