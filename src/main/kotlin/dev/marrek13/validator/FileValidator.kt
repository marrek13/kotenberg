package dev.marrek13.validator

import org.apache.commons.io.FilenameUtils
import java.io.File

object FileValidator {
    private const val INDEX_HTML = "index.html"
    private const val EXTENSION_MARKDOWN = "md"
    private const val EXTENSION_PDF = "pdf"
    private val extensions =
        arrayOf(
            "bib",
            "doc",
            "xml",
            "docx",
            "fodt",
            "html",
            "ltx",
            "txt",
            "odt",
            "ott",
            "pdb",
            "pdf",
            "psw",
            "rtf",
            "sdw",
            "stw",
            "sxw",
            "uot",
            "vor",
            "wps",
            "epub",
            "png",
            "bmp",
            "emf",
            "eps",
            "fodg",
            "gif",
            "jpg",
            "met",
            "odd",
            "otg",
            "pbm",
            "pct",
            "pgm",
            "ppm",
            "ras",
            "std",
            "svg",
            "svm",
            "swf",
            "sxd",
            "sxw",
            "tiff",
            "xhtml",
            "xpm",
            "fodp",
            "potm",
            "pot",
            "pptx",
            "pps",
            "ppt",
            "pwp",
            "sda",
            "sdd",
            "sti",
            "sxi",
            "uop",
            "wmf",
            "csv",
            "dbf",
            "dif",
            "fods",
            "ods",
            "ots",
            "pxl",
            "sdc",
            "slk",
            "stc",
            "sxc",
            "uos",
            "xls",
            "xlt",
            "xlsx",
            "tif",
            "jpeg",
            "odp",
        )

    /**
     * Checks if a file has a supported file extension.
     *
     * @param file The file to check.
     * @return `true` if the file has a supported extension, `false` otherwise.
     */
    fun isSupportedByLibreOffice(file: File) = file.isFile() && extensions.contains(FilenameUtils.getExtension(file.getName()))

    /**
     * Checks if a file is in Markdown format (extension: .md).
     *
     * @param file The file to check.
     * @return `true` if the file is a Markdown file, `false` otherwise.
     */
    fun isMarkdown(file: File) = hasExtension(file, EXTENSION_MARKDOWN)

    /**
     * Checks if a file is a PDF file (extension: .pdf).
     *
     * @param file The file to check.
     * @return `true` if the file is a PDF file, `false` otherwise.
     */
    fun isPdf(file: File) = hasExtension(file, EXTENSION_PDF)

    private fun hasExtension(
        file: File,
        extension: String,
    ) = file.isFile() && FilenameUtils.getExtension(file.getName()) == extension

    /**
     * Checks if a list of files contains an index HTML file.
     *
     * @param files The list of files to check.
     * @return `true` if the list contains an index HTML file, `false` otherwise.
     */
    fun containsIndex(files: List<File>) = files.any { it.isFile() && FilenameUtils.getName(it.getName()) == INDEX_HTML }
}
