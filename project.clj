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
                 [cjohansen/dumdom "2019.02.03-3"]
                 [jarohen/chord "0.8.1"]
                 [org.clojure/core.async "0.4.500"]]
  :profiles {:dev {:dependencies [[reloaded.repl "0.2.4"]
                                  [com.bhauman/figwheel-main "0.2.3"]
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]
                                  [cider/piggieback "0.4.1"]
                                  [lambdaisland/kaocha "0.0-529"]
                                  [kaocha-noyoda "2019-06-03"]]
                   :source-paths ["dev"]
                   :resource-paths ["target"]
                   :clean-targets ^{:protect false} ["target"]
                   :repl-options {:init-ns user}}}
  :aliases {"kaocha" ["run" "-m" "kaocha.runner"]
            "fig" ["trampoline" "run" "-m" "figwheel.main"]})
