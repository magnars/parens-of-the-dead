(ns undead.game-loop
  (:require [clojure.core.async :refer [>! <! alts! chan close! go go-loop timeout]]
            [undead.game :refer [create-game prep reveal-tile tick]]))

(defn tick-every [ms]
  (let [c (chan)]
    (go-loop []
      (<! (timeout ms))
      (when (>! c :tick)
        (recur)))
    c))

(defn game-on? [{:keys [safe? dead?]}]
  (not (or safe? dead?)))

(defn start-game-loop [ws-channel]
  (go
    (let [tick-ch (tick-every 200)]
      (loop [game (create-game)]
        (>! ws-channel (prep game))
        (if (game-on? game)
          (when-let [[value port] (alts! [ws-channel tick-ch])]
            (condp = port
              ws-channel (recur (reveal-tile game (:message value)))
              tick-ch (recur (tick game))))
          (do
            (close! tick-ch)
            (close! ws-channel)))))))
