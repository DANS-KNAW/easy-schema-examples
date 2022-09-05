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
* The fixture replaces the references to dans.knaw when reading the examples, the XSDs are not affected.
  Apply the following replacements to the files under `easy/easy-schema-examples/target/easy-schema`

      schemaLocation="https://easy.dans.knaw.nl/schemas
      schemaLocation="file:///<ABSOLUTE-PATH-TO>/easy-schema-examples/target/easy-schema

* Run `mvn clean install` in this project.
* Fix failing tests, e.g. `"primary examples" should "reference the last schema version"`
  means: update the version number in `xsi:schemaLocation` of the failing example.
* Merge both projects, publish schema's.
* The test ignored above should now succeed.
