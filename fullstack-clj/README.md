# fullstack-clj

Proof of concept for a fullstack `Clojure`/`ClojureScript` (`reagent`) 
application with JWT-authentication. 

Code can be shared between API and UI, the delopment conveniently done with hot-code-reloading (via `ring-server` and `figwheel-main`). The test suites include tests against a running Jetty with full authentication/authorization.

## Other features

- The browser's back button works as expected with the help of `secretary` and `accountant`.

## Design

The general idea for the API architecture is that except for `/api/login` everything else is done via 
`/api/` directly. Maps are used to communicate between front- and backend instead of url parameters or different endpoints. For example `{:query-type "resources" :query {:q "t"}}` will yield results for a prefix-search of resources. `clj/fullstack/dispatch.clj` will delegate calls to other modules, here the resources module, where the handler there gets the query and permissions passed as its context.

## Getting started

Install react

	$ npm i

For development of the API, run

	$ lein ring server-headless

This provides hot-code-reload for the `clj` and `cljc` artifacts 
out of the box. Save a file and make another request to see changes.
The UI will use this api automaically via a proxy.

To start and develop the UI, run

	$ lein fig:build   # serves the single page application under `localhost:9500` 

This will provide hot-code reload for `cljs` and `cljc` artifacts.

You will see that everything is connected by getting a REPL in the shell from which you started the command. It
will prompt `cljs.user=>`. From there you could verify by entering `(js/alert "Hi")`. You can also, when in development mode
via `fig:build`, verify a successfully established connection by inspecting the messages in the browser console.

Note that while for a successful connection to of the figwheel REPL using `localhost:9500` is necessary,
the `localhost:3000` route can also then be called in the browser. Doing this allows for example calling `localhost:3000/login` directly, which is the final and desired behaviour of the app when packaged.

## Testing

### API

* Test-Suite against a real jetty, mainly for testing authentication/authorization
* Unit tests
* Test against `clj` and `cljc` artifacts

Run

	$ lein test

### UI

* Unit tests via infrastructure provided by figwheel
* Test against `cljs` and `cljc` artifacts

Tests are available
at `http://localhost:9500/figwheel-extra-main/auto-testing`
or via

	$ lein fig:test

## Packaging

```
$ lein clean
$ lein fig:min
$ lein ring uberjar # The packaged jar is self contained 
					# and contains API and UI, including npm-dependencies
$ java -jar target/fullstack-0.1.0-SNAPSHOT-standalone.jar
```

## API Usage

The backend is reachable via `localhost:3000`.

Post `{ "name": "user-1", "pass": "pass-1" }` to `/login` to obtain a token.

Use token and post `{ "query-type": "resources", "query": { "q": "" }}` to `/api` to see available and matching resources.

Use token and post `{ "query-type": "permissions" }` to `/api` to see actual permissions.
