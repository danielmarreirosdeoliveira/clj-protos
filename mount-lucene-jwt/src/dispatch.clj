(ns dispatch
  (:require resources))

(defn handle-request
  [login:fn
   {permissions                        :permissions
    {function :function
     args     :args} :body}]
  (case function
    "login"
    (login:fn args)
    "show-permissions"
    {:permissions permissions}
    "list-resources"
    (resources/get-resources permissions args)
    {:msg "invalid function"}))