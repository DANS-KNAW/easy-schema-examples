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

import java.net.{ URL, UnknownHostException }

import better.files.{ File, StringOps }
import javax.xml.XMLConstants
import javax.xml.transform.Source
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.{ Schema, SchemaFactory }
import nl.knaw.dans.lib.error._
import org.scalatest.prop.{ TableDrivenPropertyChecks, TableFor1 }
import org.scalatest.{ FlatSpec, Matchers }

import scala.collection.immutable
import scala.util.{ Failure, Success, Try }
import scala.xml._

trait SchemaValidationFixture extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  val publicSchema: String
  val localSchemaFile: String
  val examples: TableFor1[String]

  private val publicEasySchemaBase = "//easy.dans.knaw.nl/schemas"
  protected val httpsEasySchemaBase = s"https:$publicEasySchemaBase"

  private lazy val triedPublicSchema: Try[Schema] = Try {
    SchemaFactory
      .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
      .newSchema(Array[Source](new StreamSource(publicSchema.toString)))
  }
  private lazy val triedLocalSchema: Try[Schema] = Try {
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
      val xml = XML.loadFile((exampleDir / example).toString())
      easyLocationsIn(xml).foreach(_.toJava should exist)
      assume(schemaIsOnline(triedLocalSchema)) // inside the loop to not block other errors
      validate(triedLocalSchema, toLocalSchemas(xml)) shouldBe a[Success[_]]
    }
  }

  it should "be schema valid with (unqualified) public schema" in {
    // sub-schemas may not have an unqualified public schema
    assume(schemaIsOnline(triedPublicSchema)) // outside the loop as we are only validating
    assume(lastLocalIsPublic)
    forEvery(examples) { example =>
      val xml = XML.loadFile((exampleDir / example).toString())
      validate(triedPublicSchema, xml.toString) shouldBe a[Success[_]]
    }
  }

  private def lastLocalIsPublic: Boolean = {
    // to ignore tests while easy-schema and easy-schema-examples are under parallel development
    // and the new schema's are not yet published
    // reported with: ...lastLocalIsPublic was false
    val newPublicSchema = localSchemaFile.replace(schemaDir.toString(), httpsEasySchemaBase)
    Try(new URL(newPublicSchema).openStream()).fold(_ => false, _ => true)
  }

  private def validate(schema: Try[Schema], xmlString: String): Try[Unit] = {
    schema.map(_.newValidator().validate(new StreamSource(xmlString.inputStream)))
      .doIfFailure {
        case e: SAXParseException => showErrorWithSourceContext(xmlString, e)
      }
  }

  private def easyLocationsIn(xml: Elem): immutable.Iterable[File] = {

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

  private def schemaIsOnline(schema: Try[Schema]): Boolean = {
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

  private def toLocalSchemas(xml: Elem) = {
    xml
      .toString() // schema location attribute becomes a standardized one liner
      .replaceAll( // a leading space is supposed to be the location
      s" $httpsEasySchemaBase", // replace public xsd location
      s" file://$schemaDir" // with local xsd location
    )
  }
}
