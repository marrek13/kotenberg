# Kotenberg

[![build](https://github.com/marrek13/kotenberg/actions/workflows/build.yml/badge.svg)](https://github.com/marrek13/kotenberg/actions/workflows/build.yml)
[![License](https://img.shields.io/github/license/marrek13/kotenberg)](LICENSE.md)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9+-blue.svg?logo=kotlin)](http://kotlinlang.org)

A Kotlin library that provides a type-safe wrapper for [Gotenberg](https://gotenberg.dev/) API, enabling seamless conversion of various document formats to PDF files.

## Features

- 🚀 **Modern Kotlin DSL** - Idiomatic Kotlin API with DSL support
- 📄 **Multiple Format Support** - Convert HTML, Markdown, URLs, and Office documents
- 🔧 **Highly Configurable** - Extensive customization options via PageProperties
- 🔗 **PDF Operations** - Merge and convert PDFs to PDF/A formats
- 🏗️ **Type-Safe** - Leverages Kotlin's type system for compile-time safety
- 🔄 **Backward Compatible** - Traditional builder pattern also available

## Table of Contents

- [Quick Start](#quick-start)
- [Installation](#installation)
- [Prerequisites](#prerequisites)
- [Usage](#usage)
  - [DSL API (Recommended)](#dsl-api-recommended)
  - [Traditional API](#traditional-api)
  - [Handling Responses](#handling-responses)
- [Examples](#examples)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [License](#license)

## Quick Start

Start a Gotenberg container using Docker:
```bash
docker run --rm -p 8090:8090 gotenberg/gotenberg:8 gotenberg --api-port=8090
```

Create a Kotenberg client and convert a URL to PDF:
```kotlin
val client = Kotenberg("http://localhost:8090/")

// Convert URL to PDF using DSL
val response = client.url("https://example.com") {
    pageProperties {
        landscape = true
        margins(1f)
        printBackground = true
    }
}

// Save the PDF
response.entity.content.use { input ->
    File("output.pdf").outputStream().use { output ->
        input.copyTo(output)
    }
}
```

## Installation

Add `Kotenberg` to your project using Gradle or Maven:

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

### Maven

```xml
<repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/marrek13/kotenberg</url>
</repository>

<dependency>
    <groupId>dev.marrek13</groupId>
    <artifactId>kotenberg</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Note:** GitHub Package Registry requires authentication. Set `GITHUB_USERNAME` and `GITHUB_TOKEN` environment variables or configure credentials in your build file.

## Prerequisites

Before using `Kotenberg`, ensure you have [Docker](https://www.docker.com/) installed on your system.

### Starting Gotenberg

Once Docker is running, start a Gotenberg container:

```bash
docker run --rm -p 8090:8090 gotenberg/gotenberg:8 gotenberg --api-port=8090
```

The Gotenberg API will be available at `http://localhost:8090`.

**Optional:** For production environments, consider using Docker Compose. Create a `docker-compose.yml`:

```yaml
services:
  gotenberg:
    image: gotenberg/gotenberg:8
    command: gotenberg --api-port=8090
    ports:
      - "8090:8090"
    restart: unless-stopped
```

Start with: `docker-compose up -d`

## Usage

### Creating a Client

First, create a `Kotenberg` client instance:

```kotlin
val client = Kotenberg("http://localhost:8090/")
```

### DSL API (Recommended)

Kotenberg provides a modern Kotlin DSL for more concise and idiomatic code:

#### URL to PDF

```kotlin
val response = client.url("https://example.com") {
    pageProperties {
        landscape = true
        margins(1f) // 1 inch margins on all sides
        printBackground = true
        preferCssPageSize = false
    }
}
```

#### HTML to PDF

```kotlin
val response = client.html {
    // Add HTML files - one must be named "index.html"
    +"index.html"
    +"header.html"
    +"footer.html"
    
    pageProperties {
        printBackground = true
        scale(0.95)
        paperSize(8.5f, 11f) // Letter size in inches
    }
}
```

#### Markdown to PDF

```kotlin
val response = client.markdown {
    +"index.html"  // Required wrapper HTML
    +"document.md"
    +"chapter1.md"
    
    pageProperties {
        margins(top = 1f, bottom = 1f, left = 0.5f, right = 0.5f)
    }
}
```

#### Office Documents to PDF

```kotlin
val response = client.libreOffice {
    files("document.docx", "spreadsheet.xlsx", "presentation.pptx")
    
    pageProperties {
        landscape = true
        nativePageRanges("1-5") // Convert only pages 1-5
    }
}
```

#### PDF Operations

```kotlin
// Merge PDFs
val response = client.mergePdfs {
    files("doc1.pdf", "doc2.pdf", "doc3.pdf")
    pageProperties {
        pdfUniversalAccess = true
    }
}

// Convert to PDF/A format
val response = client.pdfEngines {
    files("input.pdf")
    pageProperties {
        pdfFormat = PdfFormat.A_3B
    }
}
```

### Traditional API

The traditional builder pattern API is also available for backward compatibility:

#### URL Conversion

```kotlin
val response = client.convertUrl("https://gotenberg.dev/")
```

With customization:

```kotlin
val pageProperties = PageProperties.Builder()
    .landscape(true)
    .margins(1f)
    .printBackground(true)
    .build()
    
val response = client.convertUrl("https://gotenberg.dev/", pageProperties)
```

#### HTML Conversion

The only requirement is that one of the files must be named `index.html`.

```kotlin
val index = File("path/to/index.html")
val header = File("path/to/header.html")
val footer = File("path/to/footer.html")

val pageProperties = PageProperties.Builder()
    .printBackground(true)
    .build()

val response = client.convertHtml(listOf(index, header, footer), pageProperties)
```

#### Markdown Conversion

This route accepts an `index.html` file plus markdown files. Check [Gotenberg docs](https://gotenberg.dev/docs/routes#markdown-files-into-pdf-route) for details.

```kotlin
val index = File("path/to/index.html")
val markdown = File("path/to/markdown.md")

val pageProperties = PageProperties.Builder()
    .margins(1f, 1f, 0.5f, 0.5f)
    .build()

val response = client.convertMarkdown(listOf(index, markdown), pageProperties)
```

#### LibreOffice Conversion

Convert various Office document formats (`.docx`, `.xlsx`, `.pptx`, `.epub`, `.odt`, and more).

```kotlin
val docx = File("path/to/file.docx")
val xlsx = File("path/to/file.xlsx")

val response = client.convertWithLibreOffice(listOf(docx, xlsx))
```

#### PDF Engine Operations

Convert PDF files to specific PDF/A formats (`PDF/A-1a`, `PDF/A-2b`, `PDF/A-3b`):

```kotlin
val pdf1 = File("path/to/first.pdf")
val pdf2 = File("path/to/second.pdf")

val pageProperties = PageProperties.Builder()
    .addPdfFormat(PdfFormat.A_3B.format())
    .build()

val response = client.convertWithPdfEngines(listOf(pdf1, pdf2), pageProperties)
```

Merge multiple PDFs alphabetically:

```kotlin
val pdf1 = File("path/to/first.pdf")
val pdf2 = File("path/to/second.pdf")

val response = client.mergeWithPdfEngines(listOf(pdf1, pdf2))
```

### Handling Responses

All conversion methods return an Apache `CloseableHttpResponse` containing the PDF data. Here's how to handle it:

#### Save to File

```kotlin
val response = client.url("https://example.com")

response.entity.content.use { input ->
    File("output.pdf").outputStream().use { output ->
        input.copyTo(output)
    }
}
```

#### Get as Byte Array

```kotlin
val response = client.url("https://example.com")
val pdfBytes = response.entity.content.readBytes()
```

#### Check Response Status

```kotlin
val response = client.url("https://example.com")

when (response.statusLine.statusCode) {
    200 -> {
        // Success - process PDF
        response.entity.content.use { input ->
            File("output.pdf").outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
    else -> {
        // Handle error
        println("Error: ${response.statusLine.reasonPhrase}")
    }
}

response.close() // Always close the response
```

## Examples

### Complete Workflow Example

```kotlin
fun main() {
    // Create client
    val client = Kotenberg("http://localhost:8090/")
    
    // Convert URL with custom page properties
    val response = client.url("https://kotlinlang.org") {
        pageProperties {
            landscape = true
            margins(top = 0.5f, bottom = 0.5f, left = 1f, right = 1f)
            printBackground = true
            preferCssPageSize = false
            scale(0.9)
        }
    }
    
    // Save to file
    response.use {
        it.entity.content.use { input ->
            File("kotlin-docs.pdf").outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
    
    println("PDF generated successfully!")
}
```

### Batch Processing Example

```kotlin
val urls = listOf(
    "https://example.com/page1",
    "https://example.com/page2",
    "https://example.com/page3"
)

urls.forEachIndexed { index, url ->
    val response = client.url(url) {
        pageProperties {
            printBackground = true
        }
    }
    
    response.use {
        it.entity.content.use { input ->
            File("output-$index.pdf").outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}
```

### Merge Multiple Documents

```kotlin
// Convert different formats and merge
val htmlPdf = client.html {
    +"index.html"
    pageProperties { printBackground = true }
}

val urlPdf = client.url("https://example.com") {
    pageProperties { printBackground = true }
}

// Save individual PDFs first, then merge
// ... save logic ...

// Merge all PDFs
val merged = client.mergePdfs {
    files("html-output.pdf", "url-output.pdf", "document.pdf")
    pageProperties {
        pdfUniversalAccess = true
    }
}
```

## Configuration

### PageProperties Options

The `PageProperties` builder provides extensive customization options:

#### Paper & Layout

```kotlin
pageProperties {
    // Paper size (in inches)
    paperWidth = 8.5f
    paperHeight = 11f
    
    // Or use paperSize helper
    paperSize(8.5f, 11f)
    
    // Orientation
    landscape = true
    
    // Margins (in inches)
    marginTop = 1f
    marginBottom = 1f
    marginLeft = 0.5f
    marginRight = 0.5f
    
    // Or use margins helper
    margins(1f) // All sides
    margins(top = 1f, bottom = 1f, left = 0.5f, right = 0.5f) // Individual
}
```

#### Rendering Options

```kotlin
pageProperties {
    // Scale content (0.1 to 2.0)
    scale = 0.95
    
    // Print background graphics
    printBackground = true
    
    // Use CSS-defined page size
    preferCssPageSize = false
    
    // Wait before rendering (for JS-heavy pages)
    waitDelay = 2000 // milliseconds
    
    // Custom headers/footers
    headerHtml = "<html><body><h1>Header</h1></body></html>"
    footerHtml = "<html><body><p>Page {{pageNumber}} of {{totalPages}}</p></body></html>"
}
```

#### PDF Format Options

```kotlin
pageProperties {
    // PDF/A format
    pdfFormat = PdfFormat.A_3B
    
    // Universal access (PDF/UA)
    pdfUniversalAccess = true
    
    // Page ranges
    nativePageRanges = "1-5,8,11-13"
}
```

### Supported PDF Formats

```kotlin
PdfFormat.A_1A  // PDF/A-1a
PdfFormat.A_2B  // PDF/A-2b
PdfFormat.A_3B  // PDF/A-3b
```

### Supported Office Formats

LibreOffice module supports:
- **Documents:** `.doc`, `.docx`, `.odt`, `.rtf`, `.txt`
- **Spreadsheets:** `.xls`, `.xlsx`, `.ods`, `.csv`
- **Presentations:** `.ppt`, `.pptx`, `.odp`
- **Other:** `.epub`, `.xml`, `.html`

For a complete list, see [Gotenberg documentation](https://gotenberg.dev/docs/routes#office-documents-into-pdfs-route).

## Troubleshooting

### Common Issues

**Connection Refused**
```
Make sure Gotenberg is running:
docker ps | grep gotenberg
```

**File Not Found**
```kotlin
// Use absolute paths or ensure files exist
val file = File("/absolute/path/to/file.html")
require(file.exists()) { "File not found: ${file.absolutePath}" }
```

**PDF Generation Timeout**
```kotlin
// Increase wait delay for heavy pages
pageProperties {
    waitDelay = 5000 // 5 seconds
}
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## Acknowledgments

- [Gotenberg](https://gotenberg.dev/) - The powerful document conversion API
- Built with ❤️ using Kotlin

## Support

- 📫 Issues: [GitHub Issues](https://github.com/marrek13/kotenberg/issues)
- 📖 Documentation: [Gotenberg Docs](https://gotenberg.dev/docs/)
- ⭐ Star this repo if you find it useful!

