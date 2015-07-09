(ns user
  (:require [reloaded.repl :refer [system reset stop]]
            [undead.system]))

(reloaded.repl/set-init! #'undead.system/create-system)
