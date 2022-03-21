# try-deps-tool-replace

A small demo project to test how different `:deps` and `:paths` keys work in aliases when used alone or together, in different contexts.

Prints a summary table showing what paths/deps are included in the current execution.

Got the idea to try this out after I found the use of `:deps` and `:paths` inside aliases confusing and asked about it on [Clojurians](https://clojurians.slack.com/archives/C6QH853H8/p1647626564105159).

## Usage

Invoke `clojure -X:common` or `clojure -T:common`, adding any other aliases you want to test with. Specify `:verbose true` to have all the classpath entries printed in addition to the summary table.

## Learnings

1. `:deps`/`:paths` work just like `:replace-deps`/`:replace-paths` in all contexts, just as Alex Miller said in the thread on Clojurians
2. `:replace-*` only gets rid of stuff defined at the top, not in other aliases that are used. Even when combining aliases of which some use `:replace-*` (or `:deps`/`:paths`, see 1.), all deps/paths from those aliases will be available.
3. As `-T:alias` implies `:replace-deps {} :replace-paths ["."]` and multiple `:replace-paths` keys don't override each other (see 2.), `"."` will _always_ be on the classpath when using `-T:alias`, there is no way around it.

## Conclusions

1. If the intended use of an alias is both as a `-T` tool _and_ something to use, say, in a repl for the project, my suggestion would be to prefer `:extra-deps`/`paths` in its alias. `-T` will ensure isolation when run as a tool, and it won't bin your top-level deps when you use it in the project repl (in case that's desired)
2. If for some reason you _really_ don't want `"."` to be on the classpath, don't use `-T`. Work around it by combining `-X` with an alias that does the `replace` trick in case you don't want the top-level deps to be on the classpath for some reason.

## Examples

```shell
; clojure -version
Clojure CLI version 1.10.3.1087
```

### Function execution

```shell
; clojure -X:common

|   :name | :paths | :deps |
|---------+--------+-------|
| default |   true |  true |
| replace |        |       |
|   extra |        |       |
| nothing |        |       |
|    tool |        |       |

; clojure -X:common:extra

|   :name | :paths | :deps |
|---------+--------+-------|
| default |   true |  true |
| replace |        |       |
|   extra |   true |  true |
| nothing |        |       |
|    tool |        |       |

; clojure -X:common:replace

|   :name | :paths | :deps |
|---------+--------+-------|
| default |        |       |
| replace |   true |  true |
|   extra |        |       |
| nothing |        |       |
|    tool |        |       |

; clojure -X:common:nothing

|   :name | :paths | :deps |
|---------+--------+-------|
| default |        |       |
| replace |        |       |
|   extra |        |       |
| nothing |   true |  true |
|    tool |        |       |
```

### Function execution, combining aliases

```shell
; clojure -X:common:extra:replace

|   :name | :paths | :deps |
|---------+--------+-------|
| default |        |       |
| replace |   true |  true |
|   extra |   true |  true |
| nothing |        |       |
|    tool |        |       |

; clojure -X:common:extra:nothing

|   :name | :paths | :deps |
|---------+--------+-------|
| default |        |       |
| replace |        |       |
|   extra |   true |  true |
| nothing |   true |  true |
|    tool |        |       |

; clojure -X:common:nothing:replace

|   :name | :paths | :deps |
|---------+--------+-------|
| default |        |       |
| replace |   true |  true |
|   extra |        |       |
| nothing |   true |  true |
|    tool |        |       |

; clojure -X:common:extra:nothing:replace

|   :name | :paths | :deps |
|---------+--------+-------|
| default |        |       |
| replace |   true |  true |
|   extra |   true |  true |
| nothing |   true |  true |
|    tool |        |       |
```

### Tool execution

```shell
; clojure -T:common

|   :name | :paths | :deps |
|---------+--------+-------|
| default |        |       |
| replace |        |       |
|   extra |        |       |
| nothing |        |       |
|    tool |   true |       |

; clojure -T:common:extra:nothing:replace

|   :name | :paths | :deps |
|---------+--------+-------|
| default |        |       |
| replace |   true |  true |
|   extra |   true |  true |
| nothing |   true |  true |
|    tool |   true |       |
```

### clojure.main execution

```shell
; clojure -M:common:extra:replace -e '(debug)'

|   :name | :paths | :deps |
|---------+--------+-------|
| default |        |       |
| replace |   true |  true |
|   extra |   true |  true |
| nothing |        |       |
|    tool |        |       |
```
