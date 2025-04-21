package dev.marrek13.config

/**
 * Constructs a PdfFormat enum with the specified format string.
 *
 * @param format The PDF format string.
 */
enum class PdfFormat(
    val format: String,
) {
    /**
     * PDF/A-1a format.
     */
    @Deprecated("Deprecated in Gotenberg 8.x, see https://gotenberg.dev/docs/troubleshooting#pdfa-1a")
    @Suppress("unused")
    A_1A("PDF/A-1a"),

    /**
     * PDF/A-2b format.
     */
    A_2B("PDF/A-2b"),

    /**
     * PDF/A-3b format.
     */
    @Suppress("unused")
    A_3B("PDF/A-3b"),
}
