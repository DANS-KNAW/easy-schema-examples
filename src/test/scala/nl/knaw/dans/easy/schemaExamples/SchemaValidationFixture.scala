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

import better.files.{ File, StringOps }
import javax.xml.XMLConstants
import javax.xml.transform.Source
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.{ Schema, SchemaFactory }
import org.scalatest.{ FlatSpec, Matchers }

import scala.util.{ Failure, Try }
import scala.xml.{ Elem, SAXParseException, XML }

trait SchemaValidationFixture extends FlatSpec with Matchers {

  val schemaFile: String

  private val publicEasySchema = """//easy.dans.knaw.nl/schemas"""
  private val httpsEasySchema = s"https:$publicEasySchema"

  private lazy val triedSchema: Try[Schema] = Try {
    // lazy for two reasons:
    // - schemaFile is set by concrete test class
    // - postpone loading until actually validating
    val xsdInputStream = File(schemaFile).contentAsString.replaceAll(
      s"""schemaLocation="http:$publicEasySchema""",
      s"""schemaLocation="file://$schemaDir"""
    ).inputStream
    SchemaFactory
      .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
      .newSchema(Array(new StreamSource(xsdInputStream)).toArray[Source])
  }

  def validate(xml: Elem): Try[Unit] = {
    assume(schemasAreOnline)
    val xmlString = toLocalSchemas(xml)
    triedSchema.map(_.newValidator().validate(new StreamSource(xmlString.inputStream))) match {
      case Failure(e: SAXParseException) =>
        showErrorWithSourceContext(xmlString, e)
        Failure(e)
      case x => x
    }
  }

  def loadExampleXml(example: String): Elem = {
    XML.loadFile((exampleDir / example).toString())
  }

  def locationsIn(xml: Elem): Seq[String] = {
    Seq(
      "xsi:schemaLocation",
      "xsi:noNamespaceSchemaLocation"
    ).flatMap(xml.attributes.asAttrMap.getOrElse(_, "").split(" +"))
      .filter(_.endsWith(".xsd"))
      .map(_.replace(httpsEasySchema, ""))
  }

  private def schemasAreOnline = {
    triedSchema match {
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
      s" $httpsEasySchema", // replace public xsd location
      s" file://$schemaDir" // with local xsd location
    )
  }
}
