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

class DcxDaiSchemaSpec extends SchemaValidationFixture {

  override val localSchemaFile: String = lastLocalXsd("dcx", "dcx-dai.xsd")
  override val publicSchema: String = localSchemaFile.toString.replace(schemaDir.toString(), httpsEasySchemaBase)
  override val examples: TableFor1[File] = {
    if (testDir.exists) testDir.delete()
    testDir.createDirectories()
    Table("file",
      exampleDir / "dcx-dai/example2.xml",
      (testDir / "isni0.xml").write(modify(".*<dcx-dai:DAI>.*", "<dcx-dai:ISNI>0000 0001 2103 2683</dcx-dai:ISNI>")),
      (testDir / "isni1.xml").write(modify(".*<dcx-dai:role>.*", "<dcx-dai:ISNI>0000000121032268</dcx-dai:ISNI>")),
      (testDir / "isni2.xml").write(modify(".*<dcx-dai:role>.*", "<dcx-dai:ISNI>http://isni.org/isni/0000000121032683</dcx-dai:ISNI>")),
      (testDir / "isni3.xml").write(modify(".*<dcx-dai:role>.*", "<dcx-dai:ISNI>ISNI: 0000 0001 2103 2268</dcx-dai:ISNI>")),
      (testDir / "isni4.xml").write(modify(".*<dcx-dai:role>.*", "<dcx-dai:ISNI>ISNI:0000 0001 2103 2268</dcx-dai:ISNI>")),
      (testDir / "isni5.xml").write(modify(".*<dcx-dai:role>.*", "<dcx-dai:ISNI>ISNI 0000 0001 2103 2268</dcx-dai:ISNI>")),
      (testDir / "isni6.xml").write(modify(".*<dcx-dai:role>.*", "<dcx-dai:ISNI>ISNI0000000121032268</dcx-dai:ISNI>")),
      (testDir / "orcid1.xml").write(modify(".*<dcx-dai:ORCID>.*", "<dcx-dai:ORCID>https://orcid.org/0000-0002-1825-009x</dcx-dai:ORCID>")),
      (testDir / "orcid2.xml").write(modify(".*<dcx-dai:ORCID>.*", "<dcx-dai:ORCID>0000-0002-1825-009X</dcx-dai:ORCID>")),
      (testDir / "dai1.xml").write(modify(".*<dcx-dai:DAI>.*", "<dcx-dai:DAI>123456789</dcx-dai:DAI>")),
      (testDir / "dai2.xml").write(modify(".*<dcx-dai:DAI>.*", "<dcx-dai:DAI>123456789x</dcx-dai:DAI>")),
      (testDir / "dai3.xml").write(modify(".*<dcx-dai:DAI>.*", "<dcx-dai:DAI>123456789X</dcx-dai:DAI>")),
      (testDir / "dai4.xml").write(modify(".*<dcx-dai:DAI>.*", "<dcx-dai:DAI>info:eu-repo/dai/nl/123456789</dcx-dai:DAI>")),
      // TODO NOTE: the DAI regexp is defined both in ddm.xsd as in dcx-dai.xsd
    )
  }

  "ISNI validation" should "report X as 17th digit" in {
    validateWithLocal(modify(
      """.*<dcx-dai:DAI>.*""",
      "<dcx-dai:ISNI>00000001210322683X</dcx-dai:ISNI>"
    )) should notMatchRegexpInXsd
  }

  it should "report X as 17th digit in URL" in {
    validateWithLocal(modify(
      """.*<dcx-dai:DAI>.*""",
      "<dcx-dai:ISNI>http://isni.org/isni/0000000121032683X</dcx-dai:ISNI>"
    )) should notMatchRegexpInXsd
  }

  "DAI validation" should "report missing check digit with only 8 digits" in {
    validateWithLocal(modify(
      """.*<dcx-dai:DAI>.*""",
      "<dcx-dai:DAI>12345678</dcx-dai:DAI>"
    )) should notMatchRegexpInXsd
  }

  private def modify(lineMatches: String, replacement: String) = {
    File("src/main/resources/examples/dcx-dai/example2.xml")
      .contentAsString.split("\n")
      .map(line => if (line.matches(lineMatches)) replacement
                   else line
      ).mkString("\n")
  }
}
