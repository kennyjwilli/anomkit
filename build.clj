(ns build
  (:require
    [deps-deploy.deps-deploy :as dd]
    [clojure.tools.build.api :as b]))

(def lib 'dev.kwill/anomkit)
(def version (format "1.0.%s" (b/git-count-revs nil)))
(def class-dir "target/classes")

(defn- pom-template [version]
  [[:description "A Clojure(Script) library providing utilities for working with Cognitect's anomaly maps."]
   [:url "https://github.com/kennyjwilli/anomkit"]
   [:licenses
    [:license
     [:name "MIT License"]
     [:url "https://mit-license.org/"]]]
   [:developers
    [:developer
     [:name "Kenny Williams"]]]
   [:scm
    [:url "https://github.com/kennyjwilli/anomkit"]
    [:connection "scm:git:https://github.com/kennyjwilli/anomkit"]
    [:developerConnection "scm:git:ssh:git@github.com:kennyjwilli/anomkit.git"]
    [:tag (str "v" version)]]])

(defn jar-opts
  [opts]
  (assoc opts
    :lib lib
    :version version
    :jar-file (format "target/%s-%s.jar" lib version)
    :basis (b/create-basis {})
    :class-dir class-dir
    :target "target"
    :src-dirs ["src"]
    :pom-data (pom-template version)))

(defn jar
  "Build lib jar."
  [opts]
  (b/delete {:path "target"})
  (let [opts (jar-opts opts)]
    (b/write-pom opts)
    (b/jar opts))
  opts)

(defn deploy
  "Deploy the JAR to Clojars."
  [opts]
  (let [{:keys [jar-file] :as opts} (jar-opts opts)]
    (dd/deploy {:installer :remote
                :artifact  (b/resolve-path jar-file)
                :pom-file  (b/pom-path (select-keys opts [:lib :class-dir]))}))
  opts)
