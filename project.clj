(defproject parens-of-the-dead "0.1.0-SNAPSHOT"
  :description "A series of zombie-themed games written with Clojure and ClojureScript."
  :url "http://www.parens-of-the-dead.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main undead.system
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"]
                 [http-kit "2.3.0"]
                 [com.stuartsierra/component "0.4.0"]
                 [compojure "1.6.1"]
                 [quiescent "0.3.2"]
                 [expectations "2.1.10"]
                 [jarohen/chord "0.8.1"]
                 [org.clojure/core.async "0.4.500"]]
  :profiles {:dev {:plugins [[lein-cljsbuild "1.1.7"]
                             [lein-figwheel "0.5.19"]
                             [lein-expectations "0.0.8"]
                             [lein-autoexpect "1.9.0"]]
                   :dependencies [[reloaded.repl "0.2.4"]]
                   :repl-options {:init-ns user}
                   :source-paths ["dev"]
                   :cljsbuild {:builds [{:source-paths ["src" "dev"]
                                         :figwheel true
                                         :compiler {:output-to "target/classes/public/app.js"
                                                    :output-dir "target/classes/public/out"
                                                    :main "undead.client"
                                                    :asset-path "/out"
                                                    :optimizations :none
                                                    :recompile-dependents true
                                                    :source-map true}}]}}})
