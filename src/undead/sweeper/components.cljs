(ns undead.sweeper.components
  (:require [cljs.core.async :refer [put!]]
            [dumdom.core :as d]))

(d/defcomponent Cell [cell]
  [:div.cell
   (cond
     (nil? cell)
     [:div.void]

     (= :wall cell)
     [:div {:className "tile wall"}]

     :else
     [:div {:className (str "tile"
                            (when (:concealed? cell)
                              " concealed")
                            (when (:zombie? cell)
                              " zombie"))}
      (:zombie-count cell)])])


(d/defcomponent Board [game]
  [:div.board.clearfix.offset-y0
   (for [line (:board game)]
     [:div.line
      (for [cell line]
        [Cell cell])])
   [:div {:className (str "figure x" (-> game :player :x)
                          " y" (-> game :player :y))}]
   [:div.top-edge-open]
   [:div.right-edge-open]
   [:div.bottom-edge-open]
   [:div.left-edge-open]])

(d/defcomponent ProgressBar [bar]
  [:div {:className (str "progress-bar " (name (:kind bar)))}
   [:div.progress {:style {:width (str (-> bar :percentage (* 100) int) "%")}}]
   [:div.label
    (:text bar)
    [:p.note (:note bar)]]])

(d/defcomponent ActionIndicator [action]
  [:div.action-indicator
   [:div.label
    (:text action)
    [:p.note
     (:note action)]]])

(d/defcomponent Game [game]
  [:div
   [Board game]
   [:div.action-bar
    [ProgressBar (-> game :player :health)]
    (when-let [action (:available-action game)]
      [ActionIndicator action])
    (when-let [action (:ongoing-action game)]
      [ProgressBar action])]])

(defn render-game [game container]
  (d/render (Game game) container))
