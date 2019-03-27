easy-schema-examples
====================
[![Build Status](https://travis-ci.org/DANS-KNAW/easy-schema-examples.svg?branch=master)](https://travis-ci.org/DANS-KNAW/easy-schema-examples)

A collection of examples of the XML schema's used by EASY. These schema's are published at 
[https://easy.dans.knaw.nl/schemas/](https://easy.dans.knaw.nl/schemas/)

Development cycle
-----------------

* Add new version of XSD to https://github.com/DANS-KNAW/easy-schema.
* Run `mvn clean install` in `easy-schema`.
* Run `mvn clean generate-test-resources` in this project.
* Update the examples and fix failing tests.
* Some tests will be ignored because of `assume(lastLocalIsPublic)`, reported with `...lastLocalIsPublic was false` messages.
* Merge both projects, publish schema's.
* The test ignored above should now succeed.
