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
package nl.knaw.dans.easy.schemaExamples

import better.files.File
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

class DefaultsSpec extends AnyFlatSpec with Matchers with TableDrivenPropertyChecks {
  "last local XSD" should "equal not qualified XSD" in {
    val xsds = Table(
      ("path", "file"),
      ("md/emd", "emd.xsd"),
      ("md/emd", "eas.xsd"),
      ("md/emd", "qdc.xsd"),
      ("md/emd", "sdc.xsd"),
      ("md/emd", "xml.xsd"),
      ("bag/metadata/afm", "afm.xsd"),
      ("bag/metadata/amd", "amd.xsd"),
      ("bag/metadata/files", "files.xsd"),
      ("bag/metadata/agreements", "agreements.xsd"),
    )
    forEvery(xsds) { (path, file) =>
      File(lastLocalXsd(path, file))
        .contentAsString shouldBe (schemaDir / path / file).contentAsString
    }
  }

  "last local ddm.XSD" should "equal not qualified ddm.xsd" in {
    // an exception to the table driven tests:
    // different path for version and version-less
    File(lastLocalXsd("md", "ddm.xsd"))
      .contentAsString shouldBe (schemaDir / "md/ddm/ddm.xsd").contentAsString
  }

  "SchemaSpec classes" should "test all examples" in {
    exampleDir
      .listRecursively()
      .count(file => !file.isDirectory && file.name.endsWith(".xml")) shouldBe
      File("src/test/scala/nl/knaw/dans/easy/schemaExamples").listRecursively().collect {
        case f: File if f.name.endsWith("SchemaSpec.scala") => countTestedFiles(f)
      }.sum
  }

  private def countTestedFiles(file: File) = {
    file.contentAsString
      .split("\n")
      .count(_.endsWith(""".xml","""))
  }
}
