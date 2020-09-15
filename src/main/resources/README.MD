Example Resources
-----------------

These example resources consist of a number of xml files that can be used
to become more familiar with the formats used in the EASY system.
The examples are organised with the same directory structure
as de XSD files at https://easy.dans.knaw.nl/schemas/

```
https://easy.dans.knaw.nl/schemas/
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

The [DANS bag] documentation provides best practices for [namespaces].
Note that actual practice also uses `dct` for `dcterms`.
The following table maps formats to files.

| XSD          | file in DANS V0 bag                      |
|--------------|------------------------------------------|
| `ddm`        | `metadata/dataset.xml`                   |
| `files`      | `metadata/files.xml`                     |
| `agreements` | `metadata/depositor-info/agreements.xml` |

[DANS bag]: https://github.com/DANS-KNAW/dans-bagit-profile/blob/master/docs/versions/0.0.0.md#dans-bagit-profile-v0
[namespaces]: https://github.com/DANS-KNAW/dans-bagit-profile/blob/master/docs/versions/0.0.0.md#xml-namespaces

Examples for other XSDs may be standalone,
or embedded within one of the files from the table above. 
 
