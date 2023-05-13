# api

FIXME: my new application.

## Installation

Requirements:
clojure
For testing
docker 
babashka
    $ bash < <(curl -s https://raw.githubusercontent.com/babashka/babashka/master/install)

## Usage


Run the project directly, via `:main-opts` (`-m goals.api`):

    $ clojure -M:run-m
    Hello, World!

Integration tests require a test db. To run them run:
    bb test

Run the project's CI pipeline and build an uberjar (this will fail until you edit the tests to pass):

    $ clojure -T:build ci

This will produce an updated `pom.xml` file with synchronized dependencies inside the `META-INF`
directory inside `target/classes` and the uberjar in `target`. You can update the version (and SCM tag)
information in generated `pom.xml` by updating `build.clj`.

If you don't want the `pom.xml` file in your project, you can remove it. The `ci` task will
still generate a minimal `pom.xml` as part of the `uber` task, unless you remove `version`
from `build.clj`.

Run that uberjar:

    $ java -jar target/api-0.1.0-SNAPSHOT.jar

If you remove `version` from `build.clj`, the uberjar will become `target/api-standalone.jar`.


## License

Copyright © 2023 Agatasumowska

_EPLv1.0 is just the default for projects generated by `clj-new`: you are not_
_required to open source this project, nor are you required to use EPLv1.0!_
_Feel free to remove or change the `LICENSE` file and remove or update this_
_section of the `README.md` file!_

Distributed under the Eclipse Public License version 1.0.

Completed:
Authentication

Next steps:
Authorisation - change the include request to user_id so that a goal can be saved for the user that is authenticated
Retrieve goals for user
update a specific goal / how to?
