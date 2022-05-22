# text-editor-cljs

## Install

    npm i

## Start

    1$ npx shadow-cljs server
    2$ npx shadow-cljs watch app
    Visit localhost:8020

Click on the textarea and press some keyboard buttons, including modifier keys.
The output is visible in the developer console of the browser, namely the listing
of which keys are pressed.

## Publish

    $ npx shadow-cljs release library
    $ npm version patch
    $ npm publish

Use

    $ npm i text-editor-cljs

    import {Editor} from "text-editor-cljs"
    console.log(Editor.hello("dan"))

## Test

Run

    1$ npx shadow-cljs watch test
    2$ npx node test-main.js
    3$ npx shadow-cljs cljs-repl test

Run

    => (require '[cljs-run-test :refer [run-test]])
    => (run-test editor-test/base-case)

Hot code reloading works for both the tests and for the code, that is, 
you should be able to get an up to date result of `(run-test editor-test/base-case)`
after saving a file.
