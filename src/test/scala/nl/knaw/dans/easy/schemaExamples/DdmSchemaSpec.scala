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
import org.scalatest.prop.TableFor1

import scala.util.Failure

class DdmSchemaSpec extends SchemaValidationFixture {

  override val publicSchema: String = s"$httpsEasySchemaBase/md/ddm/ddm.xsd"
  override val localSchemaFile: String = lastLocalXsd("md", "ddm.xsd")
  override val examples: TableFor1[File] = Table("file",
    exampleDir / "dcx-dai/example1.xml",
    exampleDir / "dcx-gml/example1.xml",
    exampleDir / "ddm/example1.xml",
    exampleDir / "ddm/example2.xml",
    exampleDir / "abr-type/example1.xml",
  )

  "relation validation" should "report an invalid DOI" in pendingUntilFixed {
    validateWithLocal(modify(
      ".*</ddm:relation>.*",
      """<ddm:relation scheme="id-type:DOI" href="https://doi.org/42">42</ddm:relation>"""
    )) shouldBe a[Failure[_]]
  }

  it should "an XSS attack" in pendingUntilFixed {
    validateWithLocal(modify(
      ".*</ddm:relation>.*",
      """<ddm:relation xml:lang="nld" href="javascript:alert('XSS')">xxx</ddm:relation>"""
    )) shouldBe a[Failure[_]]
  }

  private def modify(lineMatches: String, replacement: String) = {
    File("src/main/resources/examples/ddm/example1.xml")
      .contentAsString.split("\n")
      .map(line => if (line.matches(lineMatches)) replacement
                   else line
      ).mkString("\n")
  }
}
