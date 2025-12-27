package dev.marrek13.dsl

import dev.marrek13.config.PageProperties
import java.io.File

/** Base DSL builder for conversion operations. */
@KotenbergDsl
sealed class ConversionBuilder {
    internal val fileList = mutableListOf<File>()
    internal var pagePropertiesDsl: PagePropertiesDsl? = null
    internal var pagePropertiesInstance: PageProperties? = null

    /** Adds a single file to the conversion. */
    fun file(path: String) {
        fileList.add(File(path))
    }

    /** Adds a single file to the conversion. */
    fun file(file: File) {
        fileList.add(file)
    }

    /** Adds multiple files to the conversion. */
    @Suppress("unused")
    fun files(vararg paths: String) {
        fileList.addAll(paths.map { File(it) })
    }

    /** Adds multiple files to the conversion. */
    @Suppress("unused")
    fun files(files: List<File>) {
        fileList.addAll(files)
    }

    /** Adds multiple files to the conversion. */
    @Suppress("unused")
    fun files(vararg files: File) {
        fileList.addAll(files)
    }

    /** Operator to add a file using unary plus. Example: +File("index.html") */
    operator fun File.unaryPlus() {
        fileList.add(this)
    }

    /** Operator to add a file path using unary plus. Example: +"index.html" */
    operator fun String.unaryPlus() {
        fileList.add(File(this))
    }

    /** Configures page properties using DSL. */
    @Suppress("unused")
    fun pageProperties(block: PagePropertiesDsl.() -> Unit) {
        pagePropertiesDsl = PagePropertiesDsl().apply(block)
    }

    /** Sets page properties using an existing instance. */
    @Suppress("unused")
    fun pageProperties(properties: PageProperties) {
        pagePropertiesInstance = properties
    }

    internal fun getPageProperties(): PageProperties {
        return pagePropertiesInstance
            ?: pagePropertiesDsl?.build()
            ?: PageProperties.Builder().build()
    }
}

/** DSL builder for HTML conversion. */
@Suppress("unused")
@KotenbergDsl
class HtmlConversionBuilder internal constructor() : ConversionBuilder()

/** DSL builder for URL conversion. */
@Suppress("unused")
@KotenbergDsl
class UrlConversionBuilder internal constructor() : ConversionBuilder() {
    internal var urlValue: String = ""

    /** Sets the URL to convert. */
    @Suppress("unused")
    fun url(url: String) {
        urlValue = url
    }
}

/** DSL builder for Markdown conversion. */
@Suppress("unused")
@KotenbergDsl
class MarkdownConversionBuilder internal constructor() : ConversionBuilder()

/** DSL builder for LibreOffice conversion. */
@Suppress("unused")
@KotenbergDsl
class LibreOfficeConversionBuilder internal constructor() : ConversionBuilder()

/** DSL builder for PDF Engines conversion. */
@Suppress("unused")
@KotenbergDsl
class PdfEnginesConversionBuilder internal constructor() : ConversionBuilder()

/** DSL builder for PDF Engines merge. */
@Suppress("unused")
@KotenbergDsl
class PdfEnginesMergeBuilder internal constructor() : ConversionBuilder()
