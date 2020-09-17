Example Resources
-----------------

These example resources consist of a number of XML files that can be used
to become more familiar with the XML formats used by the EASY services.

The examples are organised with the same directory structure
as de XSD files at https://easy.dans.knaw.nl/schemas/

```
├── abr-type
├── bag
│   ├── agreements
│   └── files
├── collections
│   └── dmo
├── dcx-dai
├── dcx-gml
└── ddm
```

The [DANS bag] documentation provides best practices for [namespace prefixes].
Note that actual practice also uses `dct` for `dcterms`.

The table maps formats to files.
Examples for other XSDs may be standalone,
or embedded within one of the files from the table.
The root element of the XML examples have an attribute `xsi:schemaLocation`.
The attribute values defines which XSD applies.

| XSD          | file in DANS V0 bag                      |
|--------------|------------------------------------------|
| `ddm`        | `metadata/dataset.xml`                   |
| `files`      | `metadata/files.xml`                     |
| `agreements` | `metadata/depositor-info/agreements.xml` |

[DANS bag]: https://github.com/DANS-KNAW/dans-bagit-profile/blob/master/docs/versions/0.0.0.md#dans-bagit-profile-v0
[namespace prefixes]: https://github.com/DANS-KNAW/dans-bagit-profile/blob/master/docs/versions/0.0.0.md#xml-namespaces

Validation
----------

Various online and offline tools can validate an XML file against an XSD schema.
They may silently ignore problems with loading 3rd party schemas referenced by the main schema.
This can cause error messages like:

    Cannot resolve the name 'dc:description' to a(n) 'element declaration' component

Some loading problems might be caused by services refusing the default
`User-Agent` header that is sent with the request to fetch the XSD.
A system property for Java applications can override the default value,
for example with a command line argument:
 
    -Dhttp.agent=something/1.0
    
Below an example to write your own validator in Java.
It silently ignores loading problems of 3rd party schemas.

```java
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import static javax.xml.validation.SchemaFactory.newInstance;

public class Validate {

  public static void main(String[] args) throws IOException, SAXException {
    System.setProperty("http.agent", "Test");
    String xsd = " https://easy.dans.knaw.nl/schemas/dcx/2020/03/dcx-dai.xsd";
    String xml = "src/main/resources/examples/dcx-dai/example2.xml";
    newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        .newSchema(new URL(xsd))
        .newValidator()
        .validate(new StreamSource(new FileInputStream(xml)));
  }
}
```
