(ns net.eighttrigrams.rocketoid
  (:require [com.biffweb :as biff]
            [cheshire.core :as json]
            [net.eighttrigrams.rocketoid.web.router :as router]
            [net.eighttrigrams.rocketoid.datastore :as datastore]
            [net.eighttrigrams.rocketoid.datastore.search :as search]
            [clojure.tools.logging :as log]
            [ring.middleware.anti-forgery :as anti-forgery]
            [nrepl.cmdline :as nrepl-cmd]))

(def features
  [router/routes])

(defn get-value [req val]
  (get (:multipart-params req) val))

(defn- get-state [handler]
  (fn [req]
    (let [state (json/parse-string (get-value req "state") true)
          value (try
                  (or 
                   (json/parse-string (get-value req "value") true)
                   "")
                  (catch Exception _e (get-value req "value")))
          save? (get-value req "save?")]
      (handler (assoc req 
                      :state state
                      :value value
                      :save? (boolean (Boolean. save?)))))))

(def routes [["" {:middleware [anti-forgery/wrap-anti-forgery
                               biff/wrap-anti-forgery-websockets
                               biff/wrap-render-rum
                               get-state]}
              (keep :routes features)]
             (keep :api-routes features)])

(def handler (-> (biff/reitit-handler {:routes routes})
                 (biff/wrap-inner-defaults {})))

(def static-pages (apply biff/safe-merge (map :static features)))

(defn generate-assets! [_sys]
  (biff/export-rum static-pages "target/resources/public")
  (biff/delete-old-files {:dir "target/resources/public"
                          :exts [".html"]}))

(defn on-save [sys]
  (biff/add-libs)
  (biff/eval-files! sys)
  (generate-assets! sys)
  #_(datastore/init-node (:biff.xtdb/node sys)))

(def components
  [biff/use-config
   biff/use-random-default-secrets
   biff/use-xt
   biff/use-queues
   biff/use-tx-listener
   biff/use-outer-default-middleware
   biff/use-jetty
   biff/use-chime
   (biff/use-when
    :net.eighttrigrams.rocketoid/enable-beholder
    biff/use-beholder)])

(defn- init-datastore [ds]
  #_(datastore/init-node (:biff.xtdb/node @biff/system))
  #_(prn "state at start" @node)
  (if (= '() (search/search-issues ds ""))
    (do
      (prn "creating some items ...")
      (datastore/insert-issue ds {:title "title1" :description  "desc1"})
      (datastore/insert-issue ds {:title "title2" :description  "desc2"})
      (datastore/insert-issue ds {:title "title3" :description  "desc3"}))
    (prn "got items:" (count (search/search-issues ds "")))))

(defn start []
  (let [ds (datastore/ds)]
    (biff/start-system
     {:net.eighttrigrams.rocketoid/chat-clients (atom #{})
      :net.eighttrigrams.rocketoid/ds           ds
      :biff/features #'features
      :biff/after-refresh `start
      :biff/handler #'handler
      :biff.beholder/on-save #'on-save
      :biff.xtdb/tx-fns biff/tx-fns
      :biff/config "config.edn"
      :biff/components components})
    
    (init-datastore ds)
    
    (generate-assets! @biff/system)
    (log/info "Go to" (:biff/base-url @biff/system))))

(defn -main [& args]
  (start)
  (apply nrepl-cmd/-main args))
