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
val response = client.convertUrl("https://gotenberg.dev/", pageProperties)
```

#### HTML

The only requirement is that one of the files name should be `index.html`.

```kotlin
val index = File("path/to/index.html")
val header = File("path/to/header.html")
val response = client.convertHtml(listOf(index, header), pageProperties)
```

#### Markdown

This route accepts an `index.html` file plus markdown files. Check [Gotenberg docs](https://gotenberg.dev/docs/routes#markdown-files-into-pdf-route) for details.

```kotlin
val index = File("path/to/index.html")
val markdown = File("path/to/markdown.md")

val response = client.convertMarkdown(listOf(index, markdown), pageProperties)
```

### Customization

`Kotenberg` client comes with `PageProperties` which is a builder class that allows you to customize the style of the generated PDF. The default page properties can be found [here](https://gotenberg.dev/docs/routes#page-properties-chromium).

```kotlin
val pageProperties = PageProperties.Builder().build()
```
### LibreOffice
`Kotenberg` client provides a `convertWithLibreOffice` method which interacts with [LibreOffice](https://gotenberg.dev/docs/routes#convert-with-libreoffice) to convert different types of documents such as `.docx`, `.epub`, `.eps`, and so on. You can find the list of all file extensions [here](https://gotenberg.dev/docs/routes#office-documents-into-pdfs-route).

```kotlin
val docx = File("path/to/file.docx")
val xlsx = File("path/to/file.xlsx")

val response = client.convertWithLibreOffice(listOf(docx, xlsx), pageProperties)
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

val response = client.mergeWithPdfEngines(listOf(pdf1, pdf2), pageProperties)
```
## Example

The following is a short snippet of how to use the library.

```kotlin
import dev.marrek13.kotenberg.Kotenberg
import dev.marrek13.kotenberg.PageProperties
import org.apache.commons.io.FileUtils

import java.nio.file.Files
import java.nio.file.Paths

class Main {
    fun main() {
        val client = Kotenberg("http://localhost:80/")
        try {
            val url = "https://gotenberg.dev/"
            val properties = PageProperties.Builder()
                    .addMarginTop(1.0f)
                    .addMarginLeft(0.5f)
                    .addMarginBottom(1.0f)
                    .addMarginTop(0.5f)
                    .addPrintBackground(true)
                    .build()
            val response = client.convertUrl(url, properties)
            val projectDir = Paths.get("").toAbsolutePath().normalize()
            val tempDir = Files.createTempDirectory(projectDir, "temp_")
            val tempFile = Files.createTempFile(tempDir, "PDF_", ".pdf").toFile()
            val pdfContent = response.entity.content
            FileUtils.copyInputStreamToFile(pdfContent, tempFile)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
```

