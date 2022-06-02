(defproject fullstack "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/clojurescript "1.11.54"]
                 [compojure/compojure "1.6.3"]
                 [ring/ring-json "0.5.1"]
                 [reagent "1.1.1" :exclusions [cljsjs/react cljsjs/react-dom cljsjs/react-dom-server]]
                 [reagent-utils "0.3.3"]
                 [cljs-ajax "0.8.4"]
                 [clj-http "3.12.3"]
                 [secretary "1.2.3"]
                 [com.fasterxml.jackson.core/jackson-core "2.13.3"]
                 [venantius/accountant "0.2.5"]
                 [buddy/buddy-auth "3.0.323"]
                 [ring/ring-jetty-adapter "1.9.5"]
                 [derekchiang/ring-proxy "1.0.1"]]

  :plugins [[lein-ring "0.12.5"]]

  :ring {:handler fullstack.api/app}

  :source-paths ["src/clj" "src/cljc" "test/clj" "src/cljs" "test/cljs"]
  
  :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
            "fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]
            "fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" "fullstack.test-runner"]}

  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.2.18"]
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]]
                   :resource-paths ["target"]
                   :clean-targets ^{:protect false} ["target" "resources/public/cljs-out"]}})
