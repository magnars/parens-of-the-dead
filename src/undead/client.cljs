(ns undead.client
  (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<!]]
            [undead.components :refer [render-game]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def container (.getElementById js/document "main"))

(defonce run-once ;; not every figwheel reload
  (go
    (let [{:keys [ws-channel error]} (<! (ws-ch "ws://localhost:9009/ws"))]
      (when error (throw error))

      (loop []
        (when-let [game (:message (<! ws-channel))]
          (render-game game container ws-channel)
          (cond
            (:dead? game) (set! (.-className (.-body js/document)) "game-over")
            (:safe? game) (set! (.-location js/document) "/safe.html")
            :else (recur)))))))
