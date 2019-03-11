package nl.knaw.dans.easy

import better.files.File

package object schemaExamples {

  val schemaDir = File("target/easy-schema")
  val exampleDir = File("src/main/assembly/dist/examples")

  def lastLocalXsd(dir: String, file: String): String = {
    (schemaDir / dir)
      .walk()
      .map(_.toString())
      .filter(_.matches(".*/[0-9/]{5,}+" + file)) // end with: /YYYY/xxx or /YYYY/MM/xxx
      .maxBy(_.toUpperCase.map(c => digitsToLowerCase(c))) // digits sort after letters
  }

  private def digitsToLowerCase(c: Char): Char = {
    if (c.isDigit) ('a' + c).toChar
    else c
  }
}
