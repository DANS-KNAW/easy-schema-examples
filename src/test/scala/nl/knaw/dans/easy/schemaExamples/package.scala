/**
 * Copyright (C) 2019 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
      .maxBy(_.toUpperCase.map(digitsToLowerCase)) // digits sort after letters
  }

  private def digitsToLowerCase(c: Char): Char = {
    if (c.isDigit) ('a' + c).toChar
    else c
  }
}
