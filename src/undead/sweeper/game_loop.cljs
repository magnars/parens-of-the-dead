(ns undead.sweeper.game-loop
  (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<!]]
            [undead.sweeper.components :refer [render-game]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn start [container ws-channel]
  (js/console.log "started")
  (go
    (loop []
      (when-let [game (:message (<! ws-channel))]
        (render-game game container)
        (cond
          (:dead? game) (set! (.-className (.-body js/document)) "game-over")
          (:safe? game) (set! (.-location js/document) "/safe.html")
          :else (recur))))))
