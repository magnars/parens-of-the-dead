(ns undead.web
  (:require [chord.http-kit :refer [with-channel]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [resources]]
            [undead.game-loop :refer [start-game-loop]]))

(defn- ws-handler [req]
  (with-channel req ws-channel
    (start-game-loop ws-channel)))

(defroutes app
  (GET "/ws" [] ws-handler)
  (resources "/"))
