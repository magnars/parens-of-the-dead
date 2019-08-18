(ns undead.web
  (:require [chord.http-kit :refer [with-channel]]
            [clojure.core.async :refer [put!]]
            [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [resources]]
            [undead.game-loop :refer [start-game-loop]]))

(def current-game :sweeper)

(defn- ws-handler [req]
  (with-channel req ws-channel
    (put! ws-channel current-game)
    (cond
      (= :memo current-game)
      (start-game-loop ws-channel)

      (= :sweeper current-game)
      (put! ws-channel {:player {:x 1 :y 6
                                 :health #_{:percentage 0.66
                                            :kind :health
                                            :text "You are hurt."
                                            :note "Find some bandages"}
                                 {:percentage 0.30
                                  :kind :attack
                                  :text "The zombie is biting you!"
                                  :note "Run! RUN! NOW!"}}
                        :available-action {:text "A medical cabinet is standing here"
                                           :note "Press [enter] to search through it"}

                        #_:ongoing-action #_{:kind :action
                                             :text "Searching through medical cabinet"
                                             :percentage 0.3}
                        :board [(repeat 14 nil)
                                (repeat 14 :wall)
                                (repeat 14 {:concealed? true})
                                (repeat 14 {:zombie? true})
                                (repeat 14 {:zombie-count "3"})
                                (repeat 14 {})
                                (repeat 14 {})
                                (repeat 14 :wall)
                                (repeat 14 nil)
                                (repeat 14 nil)
                                (repeat 14 nil)
                                (repeat 14 nil)
                                (repeat 14 nil)
                                (repeat 14 nil)]}))))

(defroutes app
  (GET "/ws" [] ws-handler)
  (GET "/" [] (slurp (io/resource "public/index.html")))
  (resources "/"))
