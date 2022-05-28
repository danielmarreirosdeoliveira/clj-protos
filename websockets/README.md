# shadow-cljs - browser quickstart

This is a minimum template you can use as the basis for CLJS projects intended to run in the browser.

## Preparations

    $ npm i

## Getting started

    1$ clj -X:run-http-kit
    1$ clj -X:run-ring-undertow # alternatively
    2$ npx shadow-cljs server
    3$ npx shadow-cljs watch app

Open [http://localhost:4000](http://localhost:4000). 

## Packaging

    $ npx shadow-cljs release app
    $ clojure -M -m uberdeps.uberjar --target websockets.jar
    $ java -cp websockets.jar clojure.main -m server-http-kit
    $ java -cp websockets.jar clojure.main -m server-ring-undertow # alternatively
