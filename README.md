# anomkit

A Clojure(Script) library providing utilities for working with [Cognitect's anomaly maps](https://github.com/cognitect-labs/anomalies).

## Installation 

```clojure
dev.kwill/anomkit {:mvn/version "1.0.6"}
```

## Overview

Anomkit provides a comprehensive set of functions for creating, handling, and transforming anomaly maps according to the Cognitect anomalies specification. 
Anomalies are a way to represent errors and exceptional conditions in a data-driven manner.

### Features

- Create and detect anomalies with predicates
- Convert between anomalies and exceptions
- Map HTTP status codes to anomaly categories
- Group anomaly map keys (e.g., marking keys as public)
- Identify retryable anomalies
- Throw exceptions from anomalies

## Usage

```clojure
(require '[kwill.anomkit :as ak])

;; Create an anomaly
(def not-found-error (ak/not-found "Resource not found" {:resource-id 123}))

;; Check if something is an anomaly
(ak/anomaly? not-found-error) ;; => true

;; Get anomaly properties
(ak/category not-found-error) ;; => :cognitect.anomalies/not-found
(ak/message not-found-error)  ;; => "Resource not found"

;; Convert HTTP status to anomaly category
(ak/category-from-http-status 404) ;; => :cognitect.anomalies/not-found

;; Check if an anomaly is retryable
(ak/retryable? not-found-error) ;; => false
(ak/retryable? (ak/busy "Server busy")) ;; => true

;; Convert an exception to an anomaly
(ak/ex->anomaly (ex-info "Something failed" {}))

;; Throw an exception from an anomaly
(ak/! not-found-error) ;; throws ExceptionInfo
```

## Categories

Cognitect anomaly categories:

- `:cognitect.anomalies/unavailable` - The requested resource exists but is not currently available
- `:cognitect.anomalies/interrupted` - The operation was interrupted
- `:cognitect.anomalies/incorrect` - Client error, incorrect inputs
- `:cognitect.anomalies/forbidden` - Caller not authorized for operation
- `:cognitect.anomalies/unsupported` - Operation not supported
- `:cognitect.anomalies/not-found` - Requested resource does not exist
- `:cognitect.anomalies/conflict` - Conflict with current state of resource
- `:cognitect.anomalies/fault` - Server/service error
- `:cognitect.anomalies/busy` - Service is overloaded, retry may succeed

## License

Copyright © 2022 Kenny Williams

Distributed under the MIT license. See the LICENSE file for more info.
