(ns user)

(try
  (require 'reloader.core)
  ((resolve 'reloader.core/start) ["src"])
  (catch Exception _
    (require 'clojure.tools.namespace.repl)
    (defn refresh [] ((resolve 'clojure.tools.namespace.repl/refresh)))
    nil))
  