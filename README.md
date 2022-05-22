# Clojure(Script) Prototypes
   
and proofs-of-concept

## keras-mnist-clj

This one should show how data can be processed in **Clojure** and
then single batches get send via the **libpython-clj** bridge to 
train a **Keras** model in a **Python** environment.

## dlgo-clj

A Go board game implementation - written in Clojure - based on the great book Deep Learning and the Game of Go by Max Pumperla.

## gameloop-clj

A proof of concept of a very basic 2D game engine built on top of a physics engine. 
Built to explore how a reinforcement learning testbed could be set up.

## field-proto-clj

My first shot at a full stack Clojure web application, with `reagent` on the front end. The style of the demo is of a very typical application like we did many at [Dainst](https://github.com/dainst).

## fullstack-clj

Another web application. The main theme of the backend is a layered data-driven architecture. The latter is implemented in terms of a single API endpoint and then a dispatch mechanism based on the data structure supplied via the request body. 

Uses `jwt`-based authentication.

## defn-over-http-demo

The same as fullstack-clj, but this time using [defn-over-http](https://github.com/eighttrigrams/defn-over-http)
to mediate the communication between frontend and backend.

## mount-lucene-jwt

In part this was created, amongst other things, to understand a luminus starter project which used `mount` and
to dabble a bit with `lucene`. Apart from that, I took the approach of `fullstack-clj` a step further insofar as
now authorization also gets done over the now single endpoint.

## plain-planner-clj

A project planning tool. Apart from it being another exploration of web technologies, the general idea here was to build a timeline based
on issue tracking information.

## tsot-editor

This was to explore a new concept for a text editor for writing, where one would work with different snippets, possibly quotes, which should help composing a text. I used the opportunity
to explore the `re-frame` state management library. What was technically interesting was the solution of the state of the highly interactive search field as a state machine. The search field allows to switch, create and delete contexts.

## melmac

A standalone cross-platform desktop app (based on `electron`) which could act as a database of book quotes, for example.

## text-editor-cljs

This was mainly to demonstrate how one could write a `ClojureScript` library for comsumption in `javascript`. As a specific example I used a hypothetical text editor based on the common `textarea`, similar to what I did for the [cometoid](https://github.com/danielmarreirosdeoliveira/cometoid/tree/main/assets) text editor.

## voctrain27-clojure

Just for fun. Can be used to learn foreign language vocabulary. Has a scheduling mechanism which 
pushes words one knows further and further back such they are tested less and less often. Words one doesn't one the translation of get tested again earlier.
