package dev.marrek13

import dev.marrek13.config.PageProperties
import dev.marrek13.exception.EmptyFileListException
import dev.marrek13.exception.IndexFileNotFoundExceptions
import dev.marrek13.validator.FileValidator
import dev.marrek13.validator.UrlValidator
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
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
class Kotenberg(endpoint: String, private val httpClient: HttpClient = HttpClient(CIO)) : AutoCloseable {
    init {
        if (!UrlValidator.isValidURL(endpoint)) {
            throw MalformedURLException()
        }
    }

    private val endpoint = if (endpoint.endsWith("/")) endpoint else "$endpoint/"

    /**
     * Converts a document from a URL using the Chromium HTML conversion route.
     *
     * @param url            The URL of the document to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A HttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    suspend fun convertUrl(
        url: String,
        pageProperties: PageProperties,
    ): HttpResponse {
        if (!UrlValidator.isValidURL(url)) {
            throw MalformedURLException()
        }
        return executeHttpPostRequest(
            route = CHROMIUM_URL_ROUTE,
            pageProperties = pageProperties,
            parameters = mapOf("url" to url),
        )
    }

    /**
     * Converts a document from a local file using the Chromium HTML conversion route.
     *
     * @param files          The list of files to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A HttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    suspend fun convertHtml(
        files: List<File>,
        pageProperties: PageProperties,
    ): HttpResponse =
        files
            .ifEmpty { throw EmptyFileListException() }
            .also { if (!FileValidator.containsIndex(it)) throw IndexFileNotFoundExceptions() }
            .let {
                executeHttpPostRequest(
                    route = CHROMIUM_HTML_ROUTE,
                    pageProperties = pageProperties,
                    files = it,
                )
            }

    /**
     * Converts a list of Markdown files using the Chromium Markdown conversion route.
     *
     * @param files          The list of files to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A HttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    suspend fun convertMarkdown(
        files: List<File>,
        pageProperties: PageProperties,
    ): HttpResponse =
        files
            .ifEmpty { throw EmptyFileListException() }
            .also { if (!FileValidator.containsIndex(it)) throw IndexFileNotFoundExceptions() }
            .filter { FileValidator.isMarkdown(it) || FileValidator.isIndexHtml(it) }
            .ifEmpty { throw FileNotFoundException("Chromium's markdown route accepts a single index.html and markdown files.") }
            .let {
                executeHttpPostRequest(
                    route = CHROMIUM_MARKDOWN_ROUTE,
                    pageProperties = pageProperties,
                    files = it,
                )
            }

    /**
     * Converts a list of files using LibreOffice.
     * Please refer to https://gotenberg.dev/docs/modules/libreoffice for more details.
     *
     * @param files          The list of files to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A HttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    suspend fun convertWithLibreOffice(
        files: List<File>,
        pageProperties: PageProperties,
    ): HttpResponse =
        files
            .ifEmpty { throw EmptyFileListException() }
            .filter(FileValidator::isSupportedByLibreOffice)
            .ifEmpty { throw FileNotFoundException(LIBRE_OFFICE_UNSUPPORTED_FILE_ERROR.trimIndent()) }
            .let {
                executeHttpPostRequest(
                    route = LIBRE_OFFICE_ROUTE,
                    pageProperties = pageProperties,
                    files = it,
                )
            }

    /**
     * Converts a list of documents using PDF Engines.
     *
     * @param files          The list of files to convert.
     * @param pageProperties Page properties for the conversion.
     * @return A HttpResponse containing the result of the conversion.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    suspend fun convertWithPdfEngines(
        files: List<File>,
        pageProperties: PageProperties,
    ): HttpResponse = getPdfEnginesHttpResponse(files, pageProperties, PDF_ENGINES_CONVERT_ROUTE)

    /**
     * Merges a list of PDF documents using PDF Engines.
     *
     * @param files          The list of PDF files to merge.
     * @param pageProperties Page properties for the merge operation.
     * @return A HttpResponse containing the result of the merge.
     * @throws IOException If an I/O error occurs during the merge process.
     */
    suspend fun mergeWithPdfEngines(
        files: List<File>,
        pageProperties: PageProperties,
    ): HttpResponse =
        getPdfEnginesHttpResponse(
            files = files,
            pageProperties = pageProperties,
            pdfEnginesRoute = PDF_ENGINES_MERGE_ROUTE,
        )

    /**
     * Executes an HTTP POST request for PDF Engines operations with the provided list of files, page properties,
     * and the specified PDF Engines route.
     *
     * @param files          The list of files to process with PDF Engines.
     * @param pageProperties Page properties for the PDF Engines operation.
     * @param pdfEnginesRoute The route for the PDF Engines operation (e.g., convert or merge).
     * @return A HttpResponse containing the result of the PDF Engines operation.
     * @throws IOException If an I/O error occurs during the PDF Engines operation.
     */
    private suspend fun getPdfEnginesHttpResponse(
        files: List<File>,
        pageProperties: PageProperties,
        pdfEnginesRoute: String,
    ) = files
        .ifEmpty { throw EmptyFileListException() }
        .filter(FileValidator::isPdf)
        .ifEmpty { throw FileNotFoundException("PDF Engines route accepts only PDF files.") }
        .let {
            executeHttpPostRequest(
                route = endpoint + pdfEnginesRoute,
                pageProperties = pageProperties,
                files = it,
            )
        }

    /**
     * Executes an HTTP POST request with the provided route and page properties.
     *
     * @param route          The route for the POST request.
     * @param pageProperties Page properties for the request.
     * @return A HttpResponse containing the response of the request.
     * @throws IOException If an I/O error occurs during the request.
     */
    private suspend fun executeHttpPostRequest(
        route: String,
        pageProperties: PageProperties,
        parameters: Map<String, String> = emptyMap(),
        files: List<File> = emptyList(),
    ) = httpClient.submitFormWithBinaryData(
        url = endpoint + route,
        formData =
            formData {
                pageProperties.all().forEach { (key, value) -> append(key, value) }
                parameters.forEach { (key, value) -> append(key, value) }
                files.forEach { file ->
                    append(
                        file.name,
                        file.readBytes(),
                        headers {
                            append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                        },
                    )
                }
            },
    )

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
