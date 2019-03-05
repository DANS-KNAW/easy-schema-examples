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

import scala.util.{ Failure, Try }
import scala.xml.{ Elem, SAXParseException, XML }

package object schemaExamples {

  val distDir = File("src/main/assembly/dist")

  def loadExampleXml(example: String): Elem = {
    XML.loadFile((distDir / "docs/examples/" / example).toString())
  }

  implicit class StringExtensions[T](val s: String) extends AnyVal {
    def relativeToDistDir: String = s.replace(distDir.path.toAbsolutePath.toString, "")
  }

  implicit class TryExtensions[T](val triedT: Try[T]) extends AnyVal {

    /**
     * Print the XML line that breaks the test.
     *
     * The line numbers don't match the source because
     * xml.toString joins attributes on a single line and
     * reduces the number of comment lines.
     */
    def printBreakingLine(xml: Elem): Try[T] = {
      triedT match {
        case Failure(e: SAXParseException) =>
          Try(xml.toString.split("\n")(e.getLineNumber - 1))
            .foreach(println)
          Failure(e)
        case x => x
      }
    }
  }
}
