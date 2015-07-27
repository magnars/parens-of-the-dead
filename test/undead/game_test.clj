(ns undead.game-test
  (:require [expectations :refer :all]
            [undead.game :refer :all]))

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

(expect [:h1 :h2]
        (->> (create-game)
             (reveal-one :h1)
             (reveal-one :h2)
             (reveal-one :h3)
             :tiles
             (filter :revealed?)
             (map :face)
             (sort)))

(expect [:h1 :h1]
        (->> (create-game)
             (reveal-one :h1)
             (reveal-one :h1)
             :tiles
             (filter :matched?)
             (map :face)))

(expect [:h3]
        (->> (create-game)
             (reveal-one :h1)
             (reveal-one :h1)
             (reveal-one :h3)
             :tiles
             (filter :revealed?)
             (map :face)))

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

;; prep

(expect {nil 16}
        (->> (create-game) prep :tiles (map :face) frequencies))

(expect {nil 15, :h1 1}
        (->> (create-game) (reveal-one :h1)
             prep :tiles (map :face) frequencies))

(expect {nil 14, :h1 2}
        (->> (create-game) (reveal-one :h1) (reveal-one :h1)
             prep :tiles (map :face) frequencies))

(expect (range 0 16)
        (->> (create-game) prep :tiles (map :id)))

;; tick - concealment

(expect 2 (->> (create-game)
               (reveal-one :h1)
               (reveal-one :h2)
               tick tick
               :tiles (filter :revealed?) count))

(expect 0 (->> (create-game)
               (reveal-one :h1)
               (reveal-one :h2)
               tick tick tick
               :tiles (filter :revealed?) count))

(expect {nil 14, :h1 1, :h2 1}
        (->> (create-game) (reveal-one :h1) (reveal-one :h2)
             tick tick tick tick
             prep :tiles (map :face) frequencies))

;; tick - time to die

(defn tick-n [n game]
  (first (drop n (iterate tick game))))

(expect [:gone :remaining]
        (->> (create-game)
             (tick-n 5)
             :sand (take 2)))

(expect {:gone 30}
        (->> (create-game)
             (tick-n 155)
             :sand frequencies))

(expect (->> (create-game)
             (tick-n 151)
             :dead?))

;; getting out alive

(defn reveal-two [face game]
  (->> game (reveal-one face) (reveal-one face)))

(defn reveal-all-houses [game]
  (->> game
       (reveal-two :h1)
       (reveal-two :h2)
       (reveal-two :h3)
       (reveal-two :h4)
       (reveal-two :h5)))

(expect (not (->> (create-game)
                  (reveal-all-houses)
                  tick tick
                  :safe?)))

(expect empty? (->> (create-game)
                    (reveal-all-houses)
                    tick tick tick
                    :tiles (filter :matched?)))

(expect 60 (->> (create-game)
                (reveal-all-houses) tick tick tick
                :sand count))

(expect 90 (->> (create-game)
                (reveal-all-houses) tick tick tick
                (reveal-all-houses) tick tick tick
                :sand count))

(expect (->> (create-game)
             (reveal-all-houses) tick tick tick
             (reveal-all-houses) tick tick tick
             (reveal-all-houses) tick tick tick
             :safe?))
