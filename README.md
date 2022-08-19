easy-schema-examples
====================
[![Build Status](https://travis-ci.org/DANS-KNAW/easy-schema-examples.svg?branch=master)](https://travis-ci.org/DANS-KNAW/easy-schema-examples)

A collection of examples of the XML schema's used by EASY. These schema's are published at 
[https://easy.dans.knaw.nl/schemas/](https://easy.dans.knaw.nl/schemas/)

Development cycle
-----------------

* Make changes to your local [easy-schema](https://github.com/DANS-KNAW/easy-schema).
* Run `mvn clean install` in `easy-schema`.
* Update `<easy.schema.version>` to the SNAPSHOT version of `easy-schema` in `easy-schema-examples.pom`.
* Run `mvn clean generate-test-resources` in this project. 
* The fixture replaces the references to dans.knaw to local file, but not recursively.
  When changes are made to the files under `easy-schema/src/main/resources/extern`
  apply the following replacements to the files under `easy/easy-schema-examples/target/easy-schema`

      schemaLocation="https://easy.dans.knaw.nl/schemas
      schemaLocation="file:///<HOME>/git/service/easy/easy-schema-examples/target/easy-schema

* Update the examples and fix failing tests.
* ~~Some tests will be ignored because of `assume(lastLocalIsPublic)`, reported with `...lastLocalIsPublic was false` messages.~~
  Seems explicitly [requested](https://github.com/DANS-KNAW/easy-schema-examples/pull/1/files#r265880649) but is dropped in the next PR
* Merge both projects, publish schema's.
* The test ignored above should now succeed.
