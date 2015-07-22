(ns undead.web
  (:require [chord.http-kit :refer [with-channel]]
            [clojure.core.async :refer [<! >! go]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [resources]]
            [undead.game :refer [create-game reveal-tile]]))

(defn- ws-handler [req]
  (with-channel req ws-channel
    (go
      (loop [game (create-game)]
        (>! ws-channel game)
        (when-let [tile-index (:message (<! ws-channel))]
          (recur (reveal-tile game tile-index)))))))

(defroutes app
  (GET "/ws" [] ws-handler)
  (resources "/"))
