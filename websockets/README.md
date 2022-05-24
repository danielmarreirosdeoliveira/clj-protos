# shadow-cljs - browser quickstart

This is a minimum template you can use as the basis for CLJS projects intended to run in the browser.

## Preparations

    $ npm i

## Getting started

    1$ clj -X:run
    2$ npx shadow-cljs server
    3$ npx shadow-cljs watch app

Open [http://localhost:8020](http://localhost:8020) or [http://localhost:3000](http://localhost:3000). 

## Packaging

    $ npx shadow-cljs release app
    $ clojure -M -m uberdeps.uberjar --deps-file deps.edn --target websockets.jar
    $ java -cp websockets.jar clojure.main -m server
