(ns undead.components
  (:require [cljs.core.async :refer [put!]]
            [quiescent.core :as q]
            [quiescent.dom :as d]))

(q/defcomponent Cell [tile reveal-ch]
  (d/div {:className "cell"}
         (d/div {:className (str "tile"
                                 (when (:revealed? tile) " revealed")
                                 (when (:matched? tile) " matched"))
                 :onClick (fn [e]
                            (.preventDefault e)
                            (put! reveal-ch (:id tile)))}
                (d/div {:className "front"})
                (d/div {:className (str "back " (when (:face tile)
                                                  (name (:face tile))))}))))

(q/defcomponent Line [tiles reveal-ch]
  (apply d/div {:className "line"}
         (map #(Cell % reveal-ch) tiles)))

(q/defcomponent Board [tiles reveal-ch]
  (apply d/div {:className "board clearfix"}
         (map #(Line % reveal-ch) (partition 4 tiles))))

(q/defcomponent Timer [{:keys [sand index]}]
  (apply d/div {:className (str "timer timer-" index)}
         (map #(d/div {:className (str "sand " (name %))}) sand)))

(q/defcomponent Timers [sand]
  (apply d/div {}
         (map-indexed #(Timer {:index %1 :sand %2}) (partition 30 sand))))

(q/defcomponent Game [game reveal-ch]
  (d/div {:className (when (:foggy? game) "foggy")}
         (Board (:tiles game) reveal-ch)
         (Timers (:sand game))))

(defn render-game [game container reveal-ch]
  (q/render (Game game reveal-ch) container))
