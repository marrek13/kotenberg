# Kotenberg

[![build](https://github.com/marrek13/kotenberg/actions/workflows/build.yml/badge.svg)](https://github.com/marrek13/kotenberg/actions/workflows/build.yml)

A Kotlin library that interacts with [Gotenberg](https://gotenberg.dev/)'s different modules to convert a variety of document formats to PDF files.

## Snippets
To incorporate `kotenberg` into your project, follow the snippets below for Gradle dependencies.

### Gradle Kotlin DSL
```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/marrek13/kotenberg")
        credentials {
            username = project.findProperty("gpr.user")?.toString() ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key")?.toString() ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("dev.marrek13:kotenberg:1.0.0")
}
```

## Prerequisites

Before attempting to use `Kotenberg`, be sure you install [Docker](https://www.docker.com/) if you have not already done so.

Once the docker Daemon is up and running, you can start a default Docker container of [Gotenberg](https://gotenberg.dev/) as follows:

```bash
docker run --rm -p 8090:8090 gotenberg/gotenberg:7 gotenberg --api-port=8090
```

After that, you need to download the latest `Kotenberg` JAR library from the GitHub [Releases](https://github.com/marrek13/kotenberg/releases) page and add it to your Java project `classpath`.

## Get Started

Create an instance of `Kotenberg` class and pass your `Gotenberg` `endpoint` url as a constructor parameter.

```kotlin
val client = Kotenberg("http://localhost:8090/")
```

### Chromium

`Kotenberg` client comes with a `convertUrl`, `convertHtml` and `convertMarkdown` methods that call one of Chromium's [routes](https://gotenberg.dev/docs/modules/chromium#routes) to convert `html` and `markdown` files, or a `url` to a `CloseableHttpResponse` that contains the `HttpEntity` which holds the content of the converted PDF file.

`convert` expects two parameters; the first parameter represents what will be converted (i.e. `url`, `html`, or `markdown` files), and the second one is a `PageProperties` parameter.

#### URL

```kotlin
val response = client.convertUrl("https://gotenberg.dev/")
```

#### HTML

The only requirement is that one of the files name should be `index.html`.

```kotlin
val index = File("path/to/index.html")
val header = File("path/to/header.html")
val response = client.convertHtml(listOf(index, header))
```

#### Markdown

This route accepts an `index.html` file plus markdown files. Check [Gotenberg docs](https://gotenberg.dev/docs/routes#markdown-files-into-pdf-route) for details.

```kotlin
val index = File("path/to/index.html")
val markdown = File("path/to/markdown.md")

val response = client.convertMarkdown(listOf(index, markdown))
```

### Customization

`Kotenberg` client comes with `PageProperties` which is a builder class that allows you to customize the style of the generated PDF. The default page properties can be found [here](https://gotenberg.dev/docs/routes#page-properties-chromium).

```kotlin
val pageProperties = PageProperties.Builder().build()
val response = client.convertMarkdown(listOf(index), pageProperties)
```
### LibreOffice
`Kotenberg` client provides a `convertWithLibreOffice` method which interacts with [LibreOffice](https://gotenberg.dev/docs/routes#convert-with-libreoffice) to convert different types of documents such as `.docx`, `.epub`, `.eps`, and so on. You can find the list of all file extensions [here](https://gotenberg.dev/docs/routes#office-documents-into-pdfs-route).

```kotlin
val docx = File("path/to/file.docx")
val xlsx = File("path/to/file.xlsx")

val response = client.convertWithLibreOffice(listOf(docx, xlsx))
```

### PDF Engines
Similarly, `Kotenberg` client provides a `convertWithPdfEngines` method which interacts with [PDF Engines](https://gotenberg.dev/docs/routes#office-documents-into-pdfs-route) to convert PDF files to a specific format (i.e. `PDF/A-1a`, `PDF/A-2b`, `PDF/A-3b`)).

The supported formats can be found in `PdfFormat`.

```kotlin
val pdf1 = File("path/to/first.pdf")
val pdf2 = File("path/to/second.pdf")

val pageProperties = PageProperties.Builder()
    .addPdfFormat(PdfFormat.A_3B.format())
    .build()

val response = client.convertWithPdfEngines(listOf(pdf1, pdf2), pageProperties)
```

Additionally, you can also use `mergeWithPdfEngines` method to [alphabetically](https://gotenberg.dev/docs/routes#merge-pdfs-route) merge the PDF files.

```kotlin
val pdf1 = File("path/to/first.pdf")
val pdf2 = File("path/to/second.pdf")

val response = client.mergeWithPdfEngines(listOf(pdf1, pdf2))
```

## DSL API

Kotenberg provides a Kotlin DSL for more concise code:

```kotlin
// URL conversion
val response = client.url("https://example.com") {
    pageProperties {
        landscape = true
        margins(1f)
    }
}

// HTML conversion
val response = client.html {
    +"index.html"
    +"header.html"
    pageProperties {
        printBackground = true
    }
}

// PDF merge
val response = client.mergePdfs {
    files("doc1.pdf", "doc2.pdf")
    pageProperties {
        pdfUniversalAccess = true
    }
}
```

Available methods: `html {}`, `url {}`, `markdown {}`, `libreOffice {}`, `pdfEngines {}`, `mergePdfs {}`

The DSL is fully backward compatible - all existing code continues to work.

