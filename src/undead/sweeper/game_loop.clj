(ns undead.sweeper.game-loop
  (:require [clojure.core.async :refer [<! >! go timeout]]))

(def example
  {:available-action {:text "A medical cabinet is standing here"
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
           (repeat 14 nil)]})

(defn start-game-loop [ws-channel]
  (go
    (loop [player {:x 0 :y 6
                   :health #_{:percentage 0.66
                              :kind :health
                              :text "You are hurt."
                              :note "Find some bandages"}
                   {:percentage 0.30
                    :kind :attack
                    :text "The zombie is biting you!"
                    :note "Run! RUN! NOW!"}}]
      (>! ws-channel (assoc example :player player))
      (when (< (:x player) 13)
        (<! (timeout 500))
        (recur (update player :x inc))))))
