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
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{ FlatSpec, Matchers }

class DefaultsSpec extends FlatSpec with Matchers with TableDrivenPropertyChecks{
  "last local XSD" should "equal unqualified xsd" in {
    val xsds = Table (
      ("path", "file"),
      ("md/emd", "emd.xsd"),
      ("md/emd", "eas.xsd"),
      ("md/emd", "qdc.xsd"),
      ("md/emd", "sdc.xsd"),
      ("md/emd", "xml.xsd"),
      ("bag/metadata/files", "files.xsd"),
    )
    forEvery(xsds) { (path, file) =>
      File(lastLocalXsd(path, file))
        .contentAsString shouldBe (schemaDir / path / file).contentAsString
    }
  }

  it should "equal unqualified ddm.xsd" in {
    // an exception to the table driven tests:
    // different path for version and version-less
    File(lastLocalXsd("md", "ddm.xsd"))
      .contentAsString shouldBe (schemaDir / "md/ddm/ddm.xsd").contentAsString
  }

  it should "equal unqualified agreements.xsd" in pendingUntilFixed {
    // TODO ignore or fix white space differences; then move to table driven test
    File(lastLocalXsd("bag/metadata/agreements", "agreements.xsd"))
      .contentAsString shouldBe (schemaDir / "bag/metadata/agreements/agreements.xsd").contentAsString
  }
}
