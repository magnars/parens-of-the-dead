(ns undead.game-test
  (:require [clojure.test :refer [deftest is testing]]
            [undead.game :refer :all]))

(defn- find-face-index [game face]
  (first (keep-indexed (fn [index tile]
                         (when (and (= face (:face tile))
                                    (not (:revealed? tile)))
                           index))
                       (:tiles game))))

(defn reveal-one [face game]
  (reveal-tile game (find-face-index game face)))

(deftest create-game-test
  (is (= (->> (create-game) :tiles (map :face) frequencies)
         {:h1 2 :h2 2 :h3 2 :h4 2 :h5 2
          :fg 2 :zo 3 :gy 1}))

  (is (< 10 (count (set (repeatedly 100 create-game)))))

  (is (= (:sand (create-game)) {:total 30})))

(deftest reveal-tile-test
  (is (= (->> (reveal-tile (create-game) 0)
              :tiles (filter :revealed?) count)
         1))

  (is (= (->> (create-game)
              (reveal-one :h1)
              (reveal-one :h2)
              (reveal-one :h3)
              :tiles
              (filter :revealed?)
              (map :face)
              (sort))
         [:h1 :h2]))

  (is (= (->> (create-game)
              (reveal-one :h1)
              (reveal-one :h1)
              :tiles
              (filter :matched?)
              (map :face))
         [:h1 :h1]))

  (is (= (->> (create-game)
              (reveal-one :h1)
              (reveal-one :h1)
              (reveal-one :h3)
              :tiles
              (filter :revealed?)
              (map :face))
         [:h3]))

  (is (->> (create-game)
           (reveal-one :fg)
           (reveal-one :fg)
           :foggy?))

  (is (= (->> (create-game)
              (reveal-one :zo)
              (reveal-one :zo)
              :sand)
         {:gone [:zombie :zombie :zombie]
          :total 30}))

  (is (= (->> (create-game)
              (reveal-one :zo)
              (reveal-one :zo)
              :tiles (map :face) frequencies)
         {:h1 2 :h2 2 :h3 2 :h4 2 :h5 2
          :fg 2 :zo 4})))

(defn tick-n [n game]
  (first (drop n (iterate tick game))))

(deftest prep-test
  (is (= (->> (create-game) prep :tiles (map :face) frequencies)
         {nil 16}))

  (is (= (->> (create-game) (reveal-one :h1)
              prep :tiles (map :face) frequencies)
         {nil 15, :h1 1}))

  (is (= (->> (create-game) (reveal-one :h1) (reveal-one :h1)
              prep :tiles (map :face) frequencies)
         {nil 14, :h1 2}))

  (is (= (->> (create-game) prep :tiles (map :id))
         (range 0 16)))

  (testing "sand"
    (is (= (->> (create-game) prep :sand frequencies)
           {:remaining 30}))

    (is (= (->> (create-game) (tick-n 5) prep :sand frequencies)
           {:remaining 29
            :gone 1}))

    (is (= (->> (create-game) (tick-n 5) prep :sand (take 2))
           [:gone :remaining]))))

(deftest tick-test
  (testing "concealment"
    (is (= (->> (create-game)
                (reveal-one :h1)
                (reveal-one :h2)
                tick tick
                :tiles (filter :revealed?) count) 2))

    (is (= (->> (create-game)
                (reveal-one :h1)
                (reveal-one :h2)
                tick tick tick
                :tiles (filter :revealed?) count) 0))


    (is (= (->> (create-game) (reveal-one :h1) (reveal-one :h2)
                tick tick tick tick
                prep :tiles (map :face) frequencies)
           {nil 14, :h1 1, :h2 1})))

  (testing "time to die"
    (is (= (->> (create-game)
                (tick-n 5)
                :sand)
           {:gone [:gone]
            :total 30}))

    (is (= (->> (create-game)
                (tick-n 151)
                :sand
                :gone
                frequencies) ;; fix with extra zombies
           {:gone 30}))

    (is (->> (create-game)
             (tick-n 151)
             :dead?))))

(defn reveal-two [face game]
  (->> game (reveal-one face) (reveal-one face)))

(defn reveal-all-houses [game]
  (->> game
       (reveal-two :h1)
       (reveal-two :h2)
       (reveal-two :h3)
       (reveal-two :h4)
       (reveal-two :h5)))

(deftest getting-out-alive-test
  (is (not (->> (create-game)
                (reveal-all-houses)
                tick tick
                :safe?)))

  (is (= (->> (create-game)
              (reveal-all-houses)
              tick tick tick
              :tiles
              (filter :matched?))
         []))

  (is (= (->> (create-game)
              (reveal-all-houses) tick tick tick
              :sand :total)
         60))

  (is (= (->> (create-game)
              (reveal-all-houses) tick tick tick
              (reveal-all-houses) tick tick tick
              :sand :total)
         90))

  (is (->> (create-game)
           (reveal-all-houses) tick tick tick
           (reveal-all-houses) tick tick tick
           (reveal-all-houses) tick tick tick
           :safe?)))
