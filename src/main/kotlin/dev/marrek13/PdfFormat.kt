package dev.marrek13

/**
 * Constructs a PdfFormat enum with the specified format string.
 *
 * @param format The PDF format string.
 */
enum class PdfFormat(val format: String) {
    /**
     * PDF/A-1a format.
     */
    A_1A("PDF/A-1a"),

    /**
     * PDF/A-2b format.
     */
    @Suppress("unused")
    A_2B("PDF/A-2b"),

    /**
     * PDF/A-3b format.
     */
    @Suppress("unused")
    A_3B("PDF/A-3b");
}

