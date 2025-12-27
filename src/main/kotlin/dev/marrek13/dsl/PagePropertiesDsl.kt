package dev.marrek13.dsl

import dev.marrek13.config.PageProperties
import dev.marrek13.config.PdfFormat

/** DSL builder for PageProperties configuration. */
@KotenbergDsl
class PagePropertiesDsl {
    var paperWidth: Float = 8.5f
    var paperHeight: Float = 11f
    var marginTop: Float = 0.39f
    var marginBottom: Float = 0.39f
    var marginLeft: Float = 0.39f
    var marginRight: Float = 0.39f
    var preferCssPageSize: Boolean = false
    var printBackground: Boolean = false
    var landscape: Boolean = false
    var scale: Float = 1f
    var pdfFormat: PdfFormat = PdfFormat.A_2B
    var pdfUniversalAccess: Boolean = false

    private var pageRangeStart: Int? = null
    private var pageRangeEnd: Int? = null

    /**
     * Sets page range for the conversion.
     *
     * @param start The start page number.
     * @param end The end page number.
     */
    @Suppress("unused")
    fun pageRange(start: Int, end: Int) {
        pageRangeStart = start
        pageRangeEnd = end
    }

    /**
     * Sets all margins to the same value.
     *
     * @param value The margin value for all sides.
     */
    @Suppress("unused")
    fun margins(value: Float) {
        marginTop = value
        marginBottom = value
        marginLeft = value
        marginRight = value
    }

    /**
     * Sets paper size.
     *
     * @param width Paper width in inches.
     * @param height Paper height in inches.
     */
    @Suppress("unused")
    fun paperSize(width: Float, height: Float) {
        paperWidth = width
        paperHeight = height
    }

    internal fun build(): PageProperties {
        val builder =
            PageProperties.Builder()
                .addPaperWidth(paperWidth)
                .addPaperHeight(paperHeight)
                .addMarginTop(marginTop)
                .addMarginBottom(marginBottom)
                .addMarginLeft(marginLeft)
                .addMarginRight(marginRight)
                .addPreferCssPageSize(preferCssPageSize)
                .addPrintBackground(printBackground)
                .addLandscape(landscape)
                .addScale(scale)
                .addPdfFormat(pdfFormat)
                .addPdfUniversalAccess(pdfUniversalAccess)

        if (pageRangeStart != null && pageRangeEnd != null) {
            builder.addNativePageRanges(pageRangeStart!!, pageRangeEnd!!)
        }

        return builder.build()
    }
}

/**
 * Creates PageProperties using DSL syntax.
 *
 * Example:
 * ```kotlin
 * val props = pageProperties {
 *     paperWidth = 8.5f
 *     paperHeight = 11f
 *     margins(0.5f)
 *     landscape = true
 * }
 * ```
 */
@Suppress("unused")
fun pageProperties(block: PagePropertiesDsl.() -> Unit): PageProperties {
    return PagePropertiesDsl().apply(block).build()
}
