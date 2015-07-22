(defproject parens-of-the-dead "0.1.0-SNAPSHOT"
  :description "A series of zombie-themed games written with Clojure and ClojureScript."
  :url "http://www.parens-of-the-dead.com"
  :license {:name "GNU General Public License"
            :url "http://www.gnu.org/licenses/gpl.html"}
  :jvm-opts ["-XX:MaxPermSize=256m"]
  :main undead.system
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "0.0-3308"]
                 [http-kit "2.1.18"]
                 [com.stuartsierra/component "0.2.3"]
                 [compojure "1.3.4"]
                 [quiescent "0.2.0-RC2"]
                 [expectations "2.1.2"]
                 [jarohen/chord "0.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]
  :profiles {:dev {:plugins [[lein-cljsbuild "1.0.6"]
                             [lein-figwheel "0.3.7"]]
                   :dependencies [[reloaded.repl "0.1.0"]]
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
