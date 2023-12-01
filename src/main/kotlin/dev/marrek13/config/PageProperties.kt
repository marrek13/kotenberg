package dev.marrek13.config

/**
 * PageProperties is a class that represents various properties for configuring document conversion,
 * such as paper size, margins, and other formatting options.
 */
class PageProperties(builder: Builder) {
    /**
     * Returns the width of the paper.
     *
     * @return The paper width.
     */
    private val paperWidth: Float = builder.paperWidth

    /**
     * Returns the height of the paper.
     *
     * @return The paper height.
     */
    private val paperHeight: Float = builder.paperHeight

    /**
     * Returns the top margin.
     *
     * @return The top margin.
     */
    private val marginTop: Float = builder.marginTop

    /**
     * Returns the bottom margin.
     *
     * @return The bottom margin.
     */
    private val marginBottom: Float = builder.marginBottom

    /**
     * Returns the left margin.
     *
     * @return The left margin.
     */
    private val marginLeft: Float = builder.marginLeft

    /**
     * Returns the right margin.
     *
     * @return The right margin.
     */
    private val marginRight: Float = builder.marginRight

    /**
     * Checks if CSS page size is preferred.
     *
     * @return `true` if CSS page size is preferred, `false` otherwise.
     */
    private val isPreferCssPageSize: Boolean = builder.preferCssPageSize

    /**
     * Checks if background should be printed.
     *
     * @return `true` if background should be printed, `false` otherwise.
     */
    private val isPrintBackground: Boolean = builder.printBackground

    /**
     * Checks if the document is in landscape orientation.
     *
     * @return `true` if the document is in landscape orientation, `false` otherwise.
     */
    private val isLandscape: Boolean = builder.landscape

    /**
     * Returns the scaling factor.
     *
     * @return The scaling factor.
     */
    private val scale: Float = builder.scale

    /**
     * Returns the native page ranges.
     *
     * @return The native page ranges.
     */
    private val nativePageRanges: String = builder.nativePageRanges

    /**
     * Returns the PDF format.
     *
     * @return The PDF format.
     */
    private val pdfFormat: String = builder.pdfFormat

    /**
     * Returns the native PDF format.
     *
     * @return The native PDF format.
     */
    private val nativePdfFormat: String = builder.nativePdfFormat

    fun all(): Map<String, String> =
        mapOf(
            "paperWidth" to paperWidth.toString(),
            "paperHeight" to paperHeight.toString(),
            "marginTop" to marginTop.toString(),
            "marginBottom" to marginBottom.toString(),
            "marginLeft" to marginLeft.toString(),
            "marginRight" to marginRight.toString(),
            "preferCssPageSize" to isPreferCssPageSize.toString(),
            "printBackground" to isPrintBackground.toString(),
            "landscape" to isLandscape.toString(),
            "scale" to scale.toString(),
            "nativePageRanges" to nativePageRanges,
            "pdfFormat" to pdfFormat,
            "nativePdfFormat" to nativePdfFormat,
        )

    /**
     * The Builder class is used to construct instances of PageProperties with specific configuration options.
     */
    @Suppress("unused")
    class Builder {
        var paperWidth = 8.5f
            private set
        var paperHeight = 11f
            private set
        var marginTop = 0.39f
            private set
        var marginBottom = 0.39f
            private set
        var marginLeft = 0.39f
            private set
        var marginRight = 0.39f
            private set
        var preferCssPageSize = false
            private set
        var printBackground = false
            private set
        var landscape = false
            private set
        var scale = 1f
            private set
        var nativePageRanges = ""
            private set
        var pdfFormat = ""
            private set
        val nativePdfFormat = PdfFormat.A_1A.format

        /**
         * Sets the paper width for the PageProperties being constructed.
         *
         * @param paperWidth The width of the paper.
         * @return The Builder instance for method chaining.
         * @throws IllegalArgumentException If the specified paper width is below the minimum printing requirement.
         */
        fun addPaperWidth(paperWidth: Float): Builder {
            require(paperWidth > MINIMAL_PAPER_WIDTH) { PAPER_WIDTH_ERROR }
            this.paperWidth = paperWidth
            return this
        }

        /**
         * Sets the paper height for the PageProperties being constructed.
         *
         * @param paperHeight The height of the paper.
         * @return The Builder instance for method chaining.
         * @throws IllegalArgumentException If the specified paper height is below the minimum printing requirement.
         */
        fun addPaperHeight(paperHeight: Float): Builder {
            require(paperHeight > MINIMAL_PAPER_HEIGHT) { PAPER_HEIGHT_ERROR }
            this.paperHeight = paperHeight
            return this
        }

        /**
         * Sets the top margin for the PageProperties being constructed.
         *
         * @param marginTop The top margin.
         * @return The Builder instance for method chaining.
         * @throws IllegalArgumentException If the specified margin is negative.
         */
        fun addMarginTop(marginTop: Float): Builder {
            require(marginTop > MINIMAL_MARGIN) { NON_POSITIVE_MARGIN_ERROR }
            this.marginTop = marginTop
            return this
        }

        /**
         * Sets the bottom margin for the PageProperties being constructed.
         *
         * @param marginBottom The bottom margin.
         * @return The Builder instance for method chaining.
         * @throws IllegalArgumentException If the specified margin is negative.
         */
        fun addMarginBottom(marginBottom: Float): Builder {
            require(marginBottom > MINIMAL_MARGIN) { NON_POSITIVE_MARGIN_ERROR }
            this.marginBottom = marginBottom
            return this
        }

        /**
         * Sets the left margin for the PageProperties being constructed.
         *
         * @param marginLeft The left margin.
         * @return The Builder instance for method chaining.
         * @throws IllegalArgumentException If the specified margin is negative.
         */
        fun addMarginLeft(marginLeft: Float): Builder {
            require(marginLeft > MINIMAL_MARGIN) { NON_POSITIVE_MARGIN_ERROR }
            this.marginLeft = marginLeft
            return this
        }

        /**
         * Sets the right margin for the PageProperties being constructed.
         *
         * @param marginRight The right margin.
         * @return The Builder instance for method chaining.
         * @throws IllegalArgumentException If the specified margin is negative.
         */
        fun addMarginRight(marginRight: Float): Builder {
            require(marginRight > MINIMAL_MARGIN) { NON_POSITIVE_MARGIN_ERROR }
            this.marginRight = marginRight
            return this
        }

        /**
         * Sets whether CSS page size is preferred for the PageProperties being constructed.
         *
         * @param preferCssPageSize `true` if CSS page size is preferred, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        fun addPreferCssPageSize(preferCssPageSize: Boolean): Builder {
            this.preferCssPageSize = preferCssPageSize
            return this
        }

        /**
         * Sets whether background should be printed for the PageProperties being constructed.
         *
         * @param printBackground `true` if background should be printed, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        fun addPrintBackground(printBackground: Boolean): Builder {
            this.printBackground = printBackground
            return this
        }

        /**
         * Sets whether the document is in landscape orientation for the PageProperties being constructed.
         *
         * @param landscape `true` if the document is in landscape orientation, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        fun addLandscape(landscape: Boolean): Builder {
            this.landscape = landscape
            return this
        }

        /**
         * Sets the scaling factor for the PageProperties being constructed.
         *
         * @param scale The scaling factor.
         * @return The Builder instance for method chaining.
         */
        fun addScale(scale: Float): Builder {
            this.scale = scale
            return this
        }

        /**
         * Sets the native page ranges for the PageProperties being constructed.
         *
         * @param start The start page number.
         * @param end   The end page number.
         * @return The Builder instance for method chaining.
         * @throws IllegalArgumentException If the page range is malformed.
         */
        fun addNativePageRanges(
            start: Int,
            end: Int,
        ): Builder {
            require(start in 1..<end) {
                PAGE_RANGE_ERROR
            }
            nativePageRanges = "$start-$end"
            return this
        }

        /**
         * Sets the PDF format for the PageProperties being constructed.
         *
         * @param pdfFormat The PDF format.
         * @return The Builder instance for method chaining.
         */
        fun addPdfFormat(pdfFormat: String): Builder {
            this.pdfFormat = pdfFormat
            return this
        }

        /**
         * Builds and returns an instance of PageProperties with the configured options.
         *
         * @return An instance of PageProperties.
         */
        fun build() = PageProperties(this)

        companion object {
            const val MINIMAL_PAPER_WIDTH = 1.0f
            const val MINIMAL_PAPER_HEIGHT = 1.5f
            const val MINIMAL_MARGIN = 0f

            private const val PAPER_WIDTH_ERROR = "Paper width must be at least 1.0 inches."
            private const val NON_POSITIVE_MARGIN_ERROR = "Margin must be at least 0 inches."
            private const val PAPER_HEIGHT_ERROR = "Paper height must be at least 1.5 inches."
            private const val PAGE_RANGE_ERROR =
                "Page range must be in the format of 'start-end' where start and end are positive integers and end is greater than start."
        }
    }
}
