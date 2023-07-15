(ns kwill.anomkit
  "Utility functions for working with anomaly maps."
  (:refer-clojure :exclude [some])
  (:require
    [kwill.anomkit.key-groups :as kgroups]))

(def categories
  "Set of all anomaly categories."
  #{:cognitect.anomalies/unavailable
    :cognitect.anomalies/interrupted
    :cognitect.anomalies/incorrect
    :cognitect.anomalies/forbidden
    :cognitect.anomalies/unsupported
    :cognitect.anomalies/not-found
    :cognitect.anomalies/conflict
    :cognitect.anomalies/fault
    :cognitect.anomalies/busy})

(defn category
  "Returns the anomaly category of `x`."
  [x]
  (:cognitect.anomalies/category x))

(defn message
  "Returns the anomaly message of `x`."
  [x]
  (:cognitect.anomalies/message x))

(defn with-public-ks
  "Adds `ks` to the set of public keys for `anom-data`."
  [anom-data ks]
  (kgroups/with-keys anom-data ::public-keys ks))

(defn public-data
  "Returns a map of the public data from `anom-data`."
  [anom-data]
  (kgroups/select anom-data ::public-keys))

(defn anomaly?
  "Returns true if `x` is an anomaly."
  [x]
  (boolean (category x)))

(defn ex
  [anomaly]
  (-> anomaly meta :ex))

(defn some
  "Returns x when x is an anomaly, else nil."
  [x]
  (when (anomaly? x) x))

(defn !
  "Throw `anomaly` as an `ExceptionInfo`."
  [anomaly]
  (throw
    (if-let [cause (ex anomaly)]
      (ex-info (:cognitect.anomalies/message anomaly) anomaly cause)
      (ex-info (:cognitect.anomalies/message anomaly) anomaly))))

(defn ?!
  "Throw `x` if it is an anomaly, else `x`."
  ([x] (?! x nil))
  ([x & {:keys [extra-data]}]
   (if (anomaly? x)
     (! (merge x extra-data))
     x)))

(defn with-ex
  "Associates ex with the anomaly."
  [anomaly ex]
  (vary-meta anomaly assoc :ex ex))

(defn anomaly
  "Returns an anomaly map constructed from `category` and, optionally, `message`."
  ([category] (anomaly category nil))
  ([category message] (anomaly category message {}))
  ([category message data] (anomaly category message data nil))
  ([category message data cause-ex]
   ;; Always ensure metadata on data is maintained since upstream code relies on it.
   (cond-> (assoc data :cognitect.anomalies/category category)
     message (assoc :cognitect.anomalies/message message)
     cause-ex (with-ex cause-ex))))

(defn unavailable?
  [x]
  (identical? :cognitect.anomalies/unavailable (category x)))

(defn unavailable
  "make sure callee healthy"
  ([message] (unavailable message {}))
  ([message data] (unavailable message data nil))
  ([message data cause-ex]
   (anomaly :cognitect.anomalies/unavailable message data cause-ex)))

(defn unavailable!
  "make sure callee healthy, throwing"
  ([message] (unavailable! message {}))
  ([message data] (unavailable! message data nil))
  ([message data cause-ex]
   (! (unavailable message data cause-ex))))

(defn interrupted?
  [x]
  (identical? :cognitect.anomalies/interrupted (category x)))

(defn interrupted
  "stop interrupting"
  ([message] (interrupted message {}))
  ([message data] (interrupted message data nil))
  ([message data cause-ex]
   (anomaly :cognitect.anomalies/interrupted message data cause-ex)))

(defn interrupted!
  "stop interrupting, throwing"
  ([message] (interrupted! message {}))
  ([message data] (interrupted! message data nil))
  ([message data cause-ex]
   (! (interrupted message data cause-ex))))

(defn incorrect?
  [x]
  (identical? :cognitect.anomalies/incorrect (category x)))

(defn incorrect
  "fix caller bug"
  ([message] (incorrect message {}))
  ([message data] (incorrect message data nil))
  ([message data cause-ex]
   (anomaly :cognitect.anomalies/incorrect message data cause-ex)))

(defn incorrect!
  "fix caller bug"
  ([message] (incorrect! message {}))
  ([message data] (incorrect! message data nil))
  ([message data cause-ex]
   (! (incorrect message data cause-ex))))

(defn forbidden?
  [x]
  (identical? :cognitect.anomalies/forbidden (category x)))

(defn forbidden
  "fix caller creds"
  ([message] (forbidden message {}))
  ([message data] (forbidden message data nil))
  ([message data cause-ex]
   (anomaly :cognitect.anomalies/forbidden message data cause-ex)))

(defn forbidden!
  "fix caller creds, throwing"
  ([message] (forbidden! message {}))
  ([message data] (forbidden! message data nil))
  ([message data cause-ex]
   (! (forbidden message data cause-ex))))

(defn unsupported?
  [x]
  (identical? :cognitect.anomalies/unsupported (category x)))

(defn unsupported
  "fix caller verb"
  ([message] (unsupported message {}))
  ([message data] (unsupported message data nil))
  ([message data cause-ex]
   (anomaly :cognitect.anomalies/unsupported message data cause-ex)))

(defn unsupported!
  "fix caller verb, throwing"
  ([message] (unsupported! message {}))
  ([message data] (unsupported! message data nil))
  ([message data cause-ex]
   (! (unsupported message data cause-ex))))

(defn not-found?
  [x]
  (identical? :cognitect.anomalies/not-found (category x)))

(defn not-found
  "fix caller noun"
  ([message] (not-found message {}))
  ([message data] (not-found message data nil))
  ([message data cause-ex]
   (anomaly :cognitect.anomalies/not-found message data cause-ex)))

(defn not-found!
  "fix caller noun, throwing"
  ([message] (not-found! message {}))
  ([message data] (not-found! message data nil))
  ([message data cause-ex]
   (! (not-found message data cause-ex))))

(defn conflict?
  [x]
  (identical? :cognitect.anomalies/conflict (category x)))

(defn conflict
  "coordinate with callee"
  ([message] (conflict message {}))
  ([message data] (conflict message data nil))
  ([message data cause-ex]
   (anomaly :cognitect.anomalies/conflict message data cause-ex)))

(defn conflict!
  "coordinate with callee, throwing"
  ([message] (conflict! message {}))
  ([message data] (conflict! message data nil))
  ([message data cause-ex]
   (! (conflict message data cause-ex))))

(defn fault?
  [x]
  (identical? :cognitect.anomalies/fault (category x)))

(defn fault
  "fix callee bug"
  ([message] (fault message {}))
  ([message data] (fault message data nil))
  ([message data cause-ex]
   (anomaly :cognitect.anomalies/fault message data cause-ex)))

(defn fault!
  "fix callee bug, throwing"
  ([message] (fault! message {}))
  ([message data] (fault! message data nil))
  ([message data cause-ex]
   (! (fault message data cause-ex))))

(defn busy?
  [x]
  (identical? :cognitect.anomalies/busy (category x)))

(defn busy
  "backoff and retry"
  ([message] (busy message {}))
  ([message data] (busy message data nil))
  ([message data cause-ex]
   (anomaly :cognitect.anomalies/busy message data cause-ex)))

(defn busy!
  "backoff and retry, throwing"
  ([message] (busy! message {}))
  ([message data] (busy! message data nil))
  ([message data cause-ex]
   (! (busy message data cause-ex))))

(def retryable-categories
  "Set of anomaly categories that are retryable."
  #{:cognitect.anomalies/busy
    :cognitect.anomalies/interrupted
    :cognitect.anomalies/unavailable})

(defn retryable?
  "Returns true if a replay of the same activity might reasonably lead to a
  different outcome."
  [anomaly]
  (contains? retryable-categories (category anomaly)))

(def http-status->category
  "Map of HTTP status code to anomaly category."
  {400 :cognitect.anomalies/incorrect
   401 :cognitect.anomalies/forbidden
   403 :cognitect.anomalies/forbidden
   404 :cognitect.anomalies/not-found
   405 :cognitect.anomalies/unsupported
   409 :cognitect.anomalies/conflict
   429 :cognitect.anomalies/busy

   501 :cognitect.anomalies/unsupported
   503 :cognitect.anomalies/busy
   504 :cognitect.anomalies/unavailable
   505 :cognitect.anomalies/unsupported})

(def category->http-status
  "A default mapping from anomaly category to an HTTP status code. Note that a
   single category can map to multiple HTTP status codes."
  {:cognitect.anomalies/unavailable 503
   :cognitect.anomalies/interrupted 500
   :cognitect.anomalies/incorrect   400
   :cognitect.anomalies/forbidden   403
   :cognitect.anomalies/unsupported 400
   :cognitect.anomalies/not-found   404
   :cognitect.anomalies/conflict    409
   :cognitect.anomalies/fault       500
   :cognitect.anomalies/busy        503})

(defn category-from-http-status
  ([http-status] (category-from-http-status http-status nil))
  ([http-status default-category]
   (get http-status->category http-status default-category)))

(defmulti ex-type->anomaly (fn [{:keys [throwable]}] (type throwable)))
(defmethod ex-type->anomaly :default [_] nil)

(def default-ex->anomaly-converters
  [ex-type->anomaly
   #?(:clj
      ;; Treat connection resets as retriable anomalies.
      (fn connection-reset-by-peer
        [{:keys [throwable]}]
        (when (and
                (instance? java.io.IOException throwable)
                (ex-message throwable)
                (= "Connection reset by peer" (ex-message throwable)))
          (interrupted "Connection reset by peer" {} throwable))))])

(defn default-ex-anomaly
  [{:keys [throwable]}]
  (fault (ex-message throwable) {} throwable))

(defn ex->anomaly
  "Returns an anomaly map from the given `throwable`. Optionally takes an argument
  map of the following keys:
    converters: Seq of functions where each function takes a map containing the
      throwable under the `:throwable` key and returns an anomaly map or nil if
      no anomaly can be produced from the converter. Defaults to [[default-ex->anomaly-converters]].
    default-converter: The converter function called when no converter in `:converters`
      returns an anomaly map. Defaults to [[default-ex-anomaly]]."
  ([throwable] (ex->anomaly throwable nil))
  ([throwable {:keys [converters
                      default-converter]}]
   (let [converters (or converters default-ex->anomaly-converters)
         default-converter (or default-converter default-ex-anomaly)
         argm {:throwable throwable}]
     (or
       (clojure.core/some #(% argm) converters)
       (default-converter argm)))))

(defmethod ex-type->anomaly #?(:clj clojure.lang.ExceptionInfo :cljs js/Error)
  [{:keys [throwable]}]
  (if (anomaly? (ex-data throwable))
    ;; We have the option to set the default anomaly message key to the ex-message
    ;; if no anomaly message is already set. Unclear if that is a desirable behavior
    ;; so leaving it out until a use case arises.
    (with-ex (ex-data throwable) throwable)
    (fault (ex-message throwable) (ex-data throwable) throwable)))

#?(:clj
   (defmethod ex-type->anomaly java.util.concurrent.CompletionException
     [{:keys [throwable]}]
     (when-let [cause-ex (ex-cause throwable)]
       (with-ex (ex->anomaly cause-ex) throwable))))
