(ns undead.web
  (:require [chord.http-kit :refer [with-channel]]
            [clojure.core.async :refer [<! put!]]
            [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [resources]]
            [undead.memo.game-loop :as memo]
            [undead.sweeper.game-loop :as sweeper]))

(def current-game :sweeper)

(defn- ws-handler [req]
  (with-channel req ws-channel
    (put! ws-channel current-game)
    (cond
      (= :memo current-game)
      (memo/start-game-loop ws-channel)

      (= :sweeper current-game)
      (sweeper/start-game-loop ws-channel))))

(defroutes app
  (GET "/ws" [] ws-handler)
  (GET "/" [] (slurp (io/resource "public/index.html")))
  (resources "/"))
