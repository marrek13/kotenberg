package dev.marrek13

import dev.marrek13.config.PageProperties
import dev.marrek13.exception.EmptyFileListException
import dev.marrek13.exception.IndexFileNotFoundExceptions
import dev.marrek13.validator.FileValidator
import dev.marrek13.validator.UrlValidator
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClients
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.MalformedURLException

/**
 * Kotenberg is a class that provides functionality for interacting with the Gotenberg API
 * to convert and process various types of documents.
 *
 * @param endpoint The URL of the Gotenberg API endpoint.
 * @throws MalformedURLException If the provided endpoint URL is not a valid URL.
 */
@Suppress("unused")
class Kotenberg(private val endpoint: String) : AutoCloseable {
    private val multipartEntityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
    private val httpClient = HttpClients.createDefault()

    init {
        if (!UrlValidator.isValidURL(endpoint)) {
            throw MalformedURLException()
        }
    }

    /**
     * Converts a document from a URL using the Chromium HTML conversion route.
     *
     * @param url            The URL of the document to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A CloseableHttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    fun convertUrl(
        url: String,
        pageProperties: PageProperties,
    ): CloseableHttpResponse {
        if (!UrlValidator.isValidURL(url)) {
            throw MalformedURLException()
        }
        multipartEntityBuilder.addTextBody("url", url)
        return executeHttpPostRequest(endpoint + CHROMIUM_URL_ROUTE, pageProperties)
    }

    /**
     * Converts a document from a local file using the Chromium HTML conversion route.
     *
     * @param files          The list of files to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A CloseableHttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    fun convertHtml(
        files: List<File>,
        pageProperties: PageProperties,
    ): CloseableHttpResponse =
        files
            .ifEmpty { throw EmptyFileListException() }
            .also { if (!FileValidator.containsIndex(it)) throw IndexFileNotFoundExceptions() }
            .forEach { multipartEntityBuilder.addBinaryBody(it.getName(), it) }
            .let {
                executeHttpPostRequest(endpoint + CHROMIUM_HTML_ROUTE, pageProperties)
            }

    /**
     * Converts a list of Markdown files using the Chromium Markdown conversion route.
     *
     * @param files          The list of files to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A CloseableHttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    fun convertMarkdown(
        files: List<File>,
        pageProperties: PageProperties,
    ): CloseableHttpResponse =
        files
            .ifEmpty { throw EmptyFileListException() }
            .also { if (!FileValidator.containsIndex(it)) throw IndexFileNotFoundExceptions() }
            .filter(FileValidator::isMarkdown)
            .ifEmpty { throw FileNotFoundException("Chromium's markdown route accepts a single index.html and markdown files.") }
            .forEach { multipartEntityBuilder.addBinaryBody(it.getName(), it) }
            .let {
                executeHttpPostRequest(endpoint + CHROMIUM_MARKDOWN_ROUTE, pageProperties)
            }

    /**
     * Converts a list of files using LibreOffice.
     * Please refer to https://gotenberg.dev/docs/modules/libreoffice for more details.
     *
     * @param files          The list of files to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A CloseableHttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    fun convertWithLibreOffice(
        files: List<File>,
        pageProperties: PageProperties,
    ): CloseableHttpResponse =
        files
            .ifEmpty { throw EmptyFileListException() }
            .filter(FileValidator::isSupportedByLibreOffice)
            .ifEmpty { throw FileNotFoundException(LIBRE_OFFICE_UNSUPPORTED_FILE_ERROR.trimIndent()) }
            .forEach { multipartEntityBuilder.addBinaryBody(it.getName(), it) }
            .let {
                executeHttpPostRequest(endpoint + LIBRE_OFFICE_ROUTE, pageProperties)
            }

    /**
     * Converts a list of documents using PDF Engines.
     *
     * @param files          The list of files to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A CloseableHttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    fun convertWithPdfEngines(
        files: List<File>,
        pageProperties: PageProperties,
    ): CloseableHttpResponse = getPdfEnginesHttpResponse(files, pageProperties, PDF_ENGINES_CONVERT_ROUTE)

    /**
     * Merges a list of PDF documents using PDF Engines.
     *
     * @param files          The list of PDF files to merge.
     * @param pageProperties Page properties for the merge operation.
     * @return A CloseableHttpResponse containing the result of the merge.
     * @throws IOException If an I/O error occurs during the merge process.
     */
    fun mergeWithPdfEngines(
        files: List<File>,
        pageProperties: PageProperties,
    ): CloseableHttpResponse = getPdfEnginesHttpResponse(files, pageProperties, PDF_ENGINES_MERGE_ROUTE)

    /**
     * Executes an HTTP POST request for PDF Engines operations with the provided list of files, page properties,
     * and the specified PDF Engines route.
     *
     * @param files          The list of files to process with PDF Engines.
     * @param pageProperties Page properties for the PDF Engines operation.
     * @param pdfEnginesRoute The route for the PDF Engines operation (e.g., convert or merge).
     * @return A CloseableHttpResponse containing the result of the PDF Engines operation.
     * @throws IOException If an I/O error occurs during the PDF Engines operation.
     */
    private fun getPdfEnginesHttpResponse(
        files: List<File>,
        pageProperties: PageProperties,
        pdfEnginesRoute: String,
    ) = files
        .ifEmpty { throw EmptyFileListException() }
        .filter(FileValidator::isPdf)
        .ifEmpty { throw FileNotFoundException("PDF Engines route accepts only PDF files.") }
        .forEach { multipartEntityBuilder.addBinaryBody(it.getName(), it) }
        .let { executeHttpPostRequest(endpoint + pdfEnginesRoute, pageProperties) }

    /**
     * Executes an HTTP POST request with the provided route and page properties.
     *
     * @param route          The route for the POST request.
     * @param pageProperties Page properties for the request.
     * @return A CloseableHttpResponse containing the response of the request.
     * @throws IOException If an I/O error occurs during the request.
     */
    private fun executeHttpPostRequest(
        route: String,
        pageProperties: PageProperties,
    ) = buildPageProperties(pageProperties)
        .let {
            httpClient.execute(
                HttpPost(route).apply { entity = multipartEntityBuilder.build() },
            )
        }

    /**
     * Builds page properties using reflection and adds them to the request entity.
     *
     * @param pageProperties Page properties to add to the request entity.
     */
    private fun buildPageProperties(pageProperties: PageProperties) =
        pageProperties.all().forEach { (name, value) ->
            multipartEntityBuilder.addTextBody(name, value)
        }

    override fun close() = httpClient.close()

    companion object {
        private const val CHROMIUM_HTML_ROUTE = "forms/chromium/convert/html"
        private const val CHROMIUM_MARKDOWN_ROUTE = "forms/chromium/convert/markdown"
        private const val CHROMIUM_URL_ROUTE = "forms/chromium/convert/url"
        private const val LIBRE_OFFICE_ROUTE = "forms/libreoffice/convert"
        private const val PDF_ENGINES_CONVERT_ROUTE = "forms/pdfengines/convert"
        private const val PDF_ENGINES_MERGE_ROUTE = "forms/pdfengines/merge"

        private const val LIBRE_OFFICE_UNSUPPORTED_FILE_ERROR = """
            File extensions are not supported by Libre Office. 
            Please refer to https://gotenberg.dev/docs/modules/libreoffice for more details.
        """
    }
}
