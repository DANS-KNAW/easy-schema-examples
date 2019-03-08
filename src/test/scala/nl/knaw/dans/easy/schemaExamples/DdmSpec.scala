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

import scala.util.Success

class DdmSpec extends SchemaValidationFixture {

  override val schemaFile: String = masterXsd("md/ddm", "ddm.xsd")

  "dcx-dai/example1" should "be schema valid" in pendingUntilFixed {
    val xml = loadExampleXml("dcx-dai/example1.xml")
    locationsIn(xml).foreach { s => (schemaDir / s.tail).toJava should exist }
    validate(xml) shouldBe a[Success[_]]
  }

  "example1" should "be schema valid" in {
    val xml = loadExampleXml("ddm/example1.xml")
    locationsIn(xml).foreach { s => (schemaDir / s.tail).toJava should exist }
    validate(xml) shouldBe a[Success[_]]
  }

  "example2" should "be schema valid" in {
    val xml = loadExampleXml("ddm/example2.xml")
    // TODO locationsIn(xml).foreach{ s => (schemaDir / s.tail).toJava should exist }
    validate(xml) shouldBe a[Success[_]]
  }
}
