package dev.marrek13.dsl

import dev.marrek13.Kotenberg
import io.ktor.client.statement.HttpResponse

/**
 * Converts HTML files to PDF using DSL syntax.
 *
 * Example:
 * ```kotlin
 * val response = kotenberg.html {
 *     file("index.html")
 *     file("header.html")
 *     file("footer.html")
 *     pageProperties {
 *         landscape = true
 *         margins(0.5f)
 *     }
 * }
 * ```
 */
suspend fun Kotenberg.html(block: HtmlConversionBuilder.() -> Unit): HttpResponse {
    val builder = HtmlConversionBuilder().apply(block)
    return convertHtml(builder.fileList, builder.getPageProperties())
}

/**
 * Converts a URL to PDF using DSL syntax.
 *
 * Example:
 * ```kotlin
 * val response = kotenberg.url {
 *     url("https://example.com")
 *     pageProperties {
 *         printBackground = true
 *     }
 * }
 * ```
 */
suspend fun Kotenberg.url(block: UrlConversionBuilder.() -> Unit): HttpResponse {
    val builder = UrlConversionBuilder().apply(block)
    return convertUrl(builder.urlValue, builder.getPageProperties())
}

/**
 * Converts a URL to PDF using DSL syntax with direct URL parameter.
 *
 * Example:
 * ```kotlin
 * val response = kotenberg.url("https://example.com") {
 *     pageProperties {
 *         printBackground = true
 *     }
 * }
 * ```
 */
suspend fun Kotenberg.url(url: String, block: UrlConversionBuilder.() -> Unit = {}): HttpResponse {
    val builder =
        UrlConversionBuilder().apply {
            url(url)
            block()
        }
    return convertUrl(builder.urlValue, builder.getPageProperties())
}

/**
 * Converts Markdown files to PDF using DSL syntax.
 *
 * Example:
 * ```kotlin
 * val response = kotenberg.markdown {
 *     file("index.html")
 *     file("content.md")
 *     pageProperties {
 *         scale = 1.2f
 *     }
 * }
 * ```
 */
suspend fun Kotenberg.markdown(block: MarkdownConversionBuilder.() -> Unit): HttpResponse {
    val builder = MarkdownConversionBuilder().apply(block)
    return convertMarkdown(builder.fileList, builder.getPageProperties())
}

/**
 * Converts Office documents to PDF using LibreOffice with DSL syntax.
 *
 * Example:
 * ```kotlin
 * val response = kotenberg.libreOffice {
 *     file("document.docx")
 *     file("spreadsheet.xlsx")
 * }
 * ```
 */
suspend fun Kotenberg.libreOffice(block: LibreOfficeConversionBuilder.() -> Unit): HttpResponse {
    val builder = LibreOfficeConversionBuilder().apply(block)
    return convertWithLibreOffice(builder.fileList, builder.getPageProperties())
}

/**
 * Converts PDF files using PDF Engines with DSL syntax.
 *
 * Example:
 * ```kotlin
 * val response = kotenberg.pdfEngines {
 *     file("document.pdf")
 *     pageProperties {
 *         pdfUniversalAccess = true
 *     }
 * }
 * ```
 */
suspend fun Kotenberg.pdfEngines(block: PdfEnginesConversionBuilder.() -> Unit): HttpResponse {
    val builder = PdfEnginesConversionBuilder().apply(block)
    return convertWithPdfEngines(builder.fileList, builder.getPageProperties())
}

/**
 * Merges PDF files using PDF Engines with DSL syntax.
 *
 * Example:
 * ```kotlin
 * val response = kotenberg.mergePdfs {
 *     file("document1.pdf")
 *     file("document2.pdf")
 *     pageProperties {
 *         pdfUniversalAccess = true
 *     }
 * }
 * ```
 */
suspend fun Kotenberg.mergePdfs(block: PdfEnginesMergeBuilder.() -> Unit): HttpResponse {
    val builder = PdfEnginesMergeBuilder().apply(block)
    return mergeWithPdfEngines(builder.fileList, builder.getPageProperties())
}
