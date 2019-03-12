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

import org.scalatest.prop.TableFor1

class CollectionsDmoSpec extends SchemaValidationFixture {

  override val publicSchema: String = s"$httpsEasySchemaBase/collections/dmo-collection.xsd"
  override val localSchemaFile: String = (schemaDir / "collections/dmo-collection.xsd").toString()
  override val examples: TableFor1[String] = Table(
    "example",
    "collections/dmo/example1.xml",
  )
}
