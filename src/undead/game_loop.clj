(ns undead.game-loop
  (:require [clojure.core.async :refer [>! <! alts! chan go go-loop timeout]]
            [undead.game :refer [create-game prep reveal-tile tick]]))

(defn tick-every [ms]
  (let [c (chan)]
    (go-loop []
      (<! (timeout ms))
      (when (>! c :tick)
        (recur)))
    c))

(defn start-game-loop [ws-channel]
  (go
    (let [tick-ch (tick-every 200)]
      (loop [game (create-game)]
        (>! ws-channel (prep game))
        (when-let [[value port] (alts! [ws-channel tick-ch])]
          (condp = port
            ws-channel (recur (reveal-tile game (:message value)))
            tick-ch (recur (tick game))))))))
