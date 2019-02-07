(ns clj-echo-server.core
  (:import [java.net ServerSocket]
           [java.io InputStream OutputStream])
  (:require [clojure.java.io :as io]
            [clojure.string :as s])
  (:refer-clojure :exclude [read-line write-line])
  (:gen-class))


(defn read-line
  [^InputStream input-stream]
   (let [ch (.read input-stream)]
     (if (and (not (= ch -1))
              (not (= ch (int \newline))))
       (cons ch (read-line input-stream)))))

(defn write-line
  [^OutputStream output-stream string]
  (doseq [ch (char-array string)]
    (.write output-stream (int ch)))
  (.write output-stream (int \return))
  (.write output-stream (int \newline))
  (.flush output-stream))

(defn bytes->str
  [lat]
  (apply str (filter #(not (= % \return)) (map #(char %) lat))))

(defn quit
  [line]
  (or (= line "quit")
      (= line "bye")))

(defn echo-server
  [^Integer port-number]
  (println "Please enter command: telnet localhost" port-number)
  (with-open [server (ServerSocket. port-number)
              socket (.accept server)
              input (io/input-stream socket)
              output (io/output-stream socket)]
    (println "Connect!!")
    (write-line output "")
    (write-line output "Hello!! This is Echo Server!!")
    (write-line output "To exist, Press 'bye' or 'quit'.")
    (write-line output "")
    (loop [line (bytes->str (read-line input))]
      (write-line output "> ")
      (when (not (quit line))
        (write-line output (str "Echo: " line))
        (recur (bytes->str (read-line input)))))
    (write-line output "Bye!!")
    (println "Done!!")
    (.close socket)))

(defn -main
  [port-number]
  (echo-server (Integer. port-number)))
