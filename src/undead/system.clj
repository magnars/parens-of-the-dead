(ns undead.system
  (:require [org.httpkit.server :refer [run-server]]))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!"})

(defn -main [& args]
  (run-server app {:port 8080})
  (println "Started server on localhost:8080"))
