(ns user
  (:require [reloaded.repl :refer [system reset stop]]
            [figwheel.main]
            [undead.system]))

(reloaded.repl/set-init! #'undead.system/create-system)

(defn cljs []
  (figwheel.main/start :dev))
