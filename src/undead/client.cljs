(ns undead.client
  (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<!]]
            [undead.memo.game-loop :as memo]
            [undead.sweeper.game-loop :as sweeper])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def container (.getElementById js/document "main"))

(defonce run-once ;; not every figwheel reload
  (go
    (let [{:keys [ws-channel error]} (<! (ws-ch "ws://localhost:9009/ws"))]
      (when error (throw error))
      (let [current-game (:message (<! ws-channel))]
        (cond
          (= :memo current-game)
          (memo/start container ws-channel)

          (= :sweeper current-game)
          (sweeper/start container ws-channel))))))
