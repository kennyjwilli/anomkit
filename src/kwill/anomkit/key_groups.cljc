(ns kwill.anomkit.key-groups
  "Categorize top-level map keys. Useful for marking a particular set of keys
  as public.")

(defn set-keys
  "Adds `ks` to key `group` for `m`."
  [m group ks]
  (vary-meta m assoc-in [::group->kset group] (set ks)))

(defn with-keys
  "Merges `ks` to key `group` for `m`."
  [m group ks]
  (vary-meta m update-in [::group->kset group] (fnil into #{}) ks))

(defn keyset
  "Returns the set of keys for `group`."
  [m group]
  (-> m meta (get-in [::group->kset group])))

(defn select
  "Returns a map of the `group` keys in `m`."
  [m group]
  (select-keys m (keyset m group)))
