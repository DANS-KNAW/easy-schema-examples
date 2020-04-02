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

import java.net.UnknownHostException

import better.files.File.currentWorkingDirectory
import better.files.{ File, StringExtensions }
import javax.xml.XMLConstants
import javax.xml.transform.Source
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.{ Schema, SchemaFactory }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.{ TableDrivenPropertyChecks, TableFor1 }

import scala.util.{ Failure, Success, Try }
import scala.xml._

trait SchemaValidationFixture extends AnyFlatSpec with Matchers with TableDrivenPropertyChecks {
  lazy val testDir: File = currentWorkingDirectory / "target" / "test" / getClass.getSimpleName

  val publicSchema: String
  val localSchemaFile: String
  val examples: TableFor1[File]
  private lazy val primaryExamples: TableFor1[File] = examples.filterNot(_.isChildOf(testDir))

  private val publicEasySchemaBase = "//easy.dans.knaw.nl/schemas"
  protected val httpsEasySchemaBase = s"https:$publicEasySchemaBase"

  lazy val triedLocalSchema: Try[Schema] = Try {
    // lazy for two reasons:
    // - schemaFile is set by concrete test class
    // - postpone loading until actually validating
    val xsdInputStream = File(localSchemaFile).contentAsString.replaceAll(
      s"""schemaLocation="http:$publicEasySchemaBase""",
      s"""schemaLocation="file://$schemaDir"""
    ).inputStream
    SchemaFactory
      .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
      .newSchema(Array[Source](new StreamSource(xsdInputStream)))
  }

  "examples" should "be schema valid with local copy of easy-schema" in {
    forEvery(examples) { example =>
      val xml = XML.loadFile(example.toString())
      validate(toLocalSchemas(xml)) shouldBe a[Success[_]]
    }
  }

  "primary examples" should "reference the last schema version" in {
    // not "should include(publicSchema)" to avoid the full XML in the stack trace on failure
    forEvery(primaryExamples)(_.contentAsString.contains(publicSchema) shouldBe true)
  }

  it should "reference existing locations" in {
    forEvery(primaryExamples) { example =>
      every(easyLocationsIn(XML.loadFile(example.toString))) should exist
    }
  }

  def validate(xmlString: String): Try[Unit] = {
    assume(referencedSchemasAreOnline(triedLocalSchema))
    triedLocalSchema.map(_.newValidator()
      .validate(new StreamSource(xmlString.inputStream)))
      /*
      .doIfFailure {
        case e: SAXParseException => showErrorWithSourceContext(xmlString, e)
      }*/
  }

  private def easyLocationsIn(xml: Elem): Iterable[File] = {

    val attributeValues = xml.attributes.asAttrMap.collect {
      case ("xsi:noNamespaceSchemaLocation", value: String) => s"dummyUri $value"
      case ("xsi:schemaLocation", value: String) => value
    }
    // now we should have strings formatted like: "nsUri1 xsdUrl1 nsUri2 xsdUrl2"
    for {
      value <- attributeValues
      tuples <- value.trim.split(" +").grouped(2) // extract (uri,url) tuples
      url = tuples.applyOrElse(1, "").toString // get second element of each tuple
      if url contains "easy.dans"
    } yield File(url.replace(httpsEasySchemaBase, schemaDir.toString()))
  }

  def referencedSchemasAreOnline(schema: Try[Schema]): Boolean = {
    // to ignore tests when executed without web-access or when third party schema's are not available
    // reported with: ...triedPublicSchema was false
    schema match {
      case Failure(e: SAXParseException) if e.getCause != null && e.getCause.isInstanceOf[UnknownHostException] => false
      case Failure(e: SAXParseException) if e.getMessage.contains("Cannot resolve") =>
        println("Probably an offline third party schema: " + e.getMessage)
        false
      case _ => true
    }
  }

  private def showErrorWithSourceContext(xmlString: String, e: SAXParseException): Unit = {
    val lines = xmlString.split("\n")
    val lineNumber = e.getLineNumber
    printNonEmpty(lines.slice(0, lineNumber))
    println("-" * e.getColumnNumber + "^  " + e.getMessage)
    printNonEmpty(lines.slice(lineNumber + 1, Int.MaxValue))
  }

  private def printNonEmpty(lines: Seq[String]): Unit = {
    lines.withFilter(_.trim.nonEmpty).foreach(println)
  }

  def toLocalSchemas(xml: Elem): String = {
    xml
      .toString() // schema location attribute becomes a standardized one liner
      .replaceAll( // a leading space is supposed to be the location
        s" $httpsEasySchemaBase", // replace public xsd location
        s" file://$schemaDir" // with local xsd location
      )
  }
}
