# Clojure(Script) Prototypes
   
and proofs-of-concept.

These fall broadly into two categories:

1. Explorations of ideas
2. Experimentation with different technology stacks

See also: [cljc-webstacks](https://github.com/eighttrigrams/cljc-webstacks)

## keras-mnist-clj

This one should show how data can be processed in **Clojure** and
then single batches get send via the **libpython-clj** bridge to 
train a **Keras** model in a **Python** environment.

## dlgo-clj

A Go board game implementation - written in Clojure - based on the great book Deep Learning and the Game of Go by Max Pumperla.

## gameloop

A proof of concept of a very basic 2D game engine built on top of a physics engine. 
Built to explore how a reinforcement learning testbed could be set up.

## field-proto

My first shot at a full stack Clojure web application, with `reagent` on the front end. The style of the demo is of a very typical application like we did many at [Dainst](https://github.com/dainst).

## rocketoid

An experiment with [Biff](https://biffweb.com). Attempt to reimplement [cometoid](https://github.com/eighttrigrams/cometoid).

## plain-planner

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
