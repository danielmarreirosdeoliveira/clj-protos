# fullstack-cljc-mount

A json api using mount, jwt.

## Getting started

Copy and edit config file templates

    $ cp env/prod/config.edn.template env/prod/config.edn
    $ cp dev-config.edn.template dev-config.edn

## Run the application

This

    $ clj -X:run

will run a webserver at `localhost:3000`.

### Run tests 

#### ... with the Cognitect test-runner

See [test-runner](https://github.com/cognitect-labs/test-runner)

    $ clj -X:test:runner '{:dirs ["test"]}'

#### ... continuously with test-refresh

See [test-refresh](https://github.com/jakemcc/test-refresh)

    $ clj -M:test:refresh

### Develop

Run

    $ clj -M:dev

This

```
(require 'go)
(require 'mount.core)
(mount.core/start)
```

will get you a webserver running at `localhost:3000`.

#### ... with rebel-readline

See [rebel-readline](https://github.com/bhauman/rebel-readline) .

    $ clj -M:dev:rebel

## Usage

Post `{ "name": "dan", "pass": "key" }` to `localhost:3000` to obtain a token.

Use token and post `{ "function": "list-resources", "args": { "q": "" }}` to `localhost:3000` to see available and matching resources.

Use token and post `{ "function": "show-permissions" }` to `localhost:3000` to see actual permissions.