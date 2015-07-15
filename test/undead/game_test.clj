(ns undead.game-test
  (:require [undead.game :refer :all]
            [expectations :refer :all]))

(defn- find-face-index [game face]
  (first (keep-indexed (fn [index tile]
                         (when (and (= face (:face tile))
                                    (not (:revealed? tile)))
                           index))
                       (:tiles game))))

(defn reveal-one [face game]
  (reveal-tile game (find-face-index game face)))

;; create-game

(expect {:h1 2 :h2 2 :h3 2 :h4 2 :h5 2
         :fg 2 :zo 3 :gy 1}
        (->> (create-game) :tiles (map :face) frequencies))

(expect #(< 10 %) (count (set (repeatedly 100 create-game))))

(expect {:remaining 30} (frequencies (:sand (create-game))))

;; reveal-tile

(expect 1 (->> (reveal-tile (create-game) 0)
               :tiles (filter :revealed?) count))

(expect #{{:face :h1 :revealed? true}
          {:face :h2 :revealed? true}}
        (->> (create-game)
             (reveal-one :h1)
             (reveal-one :h2)
             (reveal-one :h3)
             :tiles
             (filter :revealed?)
             (set)))

(expect [{:face :h1 :matched? true}
         {:face :h1 :matched? true}]
        (->> (create-game)
             (reveal-one :h1)
             (reveal-one :h1)
             :tiles
             (filter :matched?)))

(expect #{{:face :h3 :revealed? true}}
        (->> (create-game)
             (reveal-one :h1)
             (reveal-one :h1)
             (reveal-one :h3)
             :tiles
             (filter :revealed?)
             (set)))

(expect (->> (create-game)
             (reveal-one :fg)
             (reveal-one :fg)
             :foggy?))

(expect [:zombie :zombie :zombie :remaining]
        (->> (create-game)
             (reveal-one :zo)
             (reveal-one :zo)
             :sand
             (take 4)))

(expect {:h1 2 :h2 2 :h3 2 :h4 2 :h5 2
         :fg 2 :zo 4}
        (->> (create-game)
             (reveal-one :zo)
             (reveal-one :zo)
             :tiles (map :face) frequencies))
