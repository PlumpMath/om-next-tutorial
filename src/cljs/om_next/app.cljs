(ns om-next.app
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(enable-console-print!)

(def app-state
  (atom {:app/title "Animals"
         :animals/list [[1 "Ant"] [2 "Antelope"] [3 "Bird"] [4 "Cat"]]}))

(defmulti read (fn [_ _ params] key))

(defmethod read :default
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:value :not-found})))

(defmethod read :animals/list
  [{:keys [state] :as env} key {:keys [start end]}]
  {:value (subvec (:animals/list @state) start end)})

(defui AnimalsList
  static om/IQueryParams
  (params [this] {:start 0 :end 10})
  static om/IQuery
  (query [this] '[:app/title (:animals/list {:start ?start :end ?end})])
  Object
  (render [this]
          (let [{:keys [app/title animals/list]} (om/props this)]
            (dom/div nil
                     (dom/h2 nil title)
                     (apply dom/ul nil
                            (map (fn [[i name]]
                                   (dom/li nil (str i ". " name))))
                            list)))))

(def reconciler
  (om/reconciler {:state app-state
                  :parser (om/parser {:read read})}))

(defn init [] nil)

(om/add-root! reconciler AnimalsList (gdom/getElement "container"))
