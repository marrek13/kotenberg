package dev.marrek13

import dev.marrek13.config.PageProperties
import dev.marrek13.container.GotenbergTestContainer
import dev.marrek13.exception.EmptyFileListException
import dev.marrek13.exception.IndexFileNotFoundExceptions
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.toByteArray
import java.io.File
import java.io.FileNotFoundException
import java.net.MalformedURLException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class KotenbergTest {
    @Test
    fun `should validate endpoint`() {
        assertThrows<MalformedURLException> { Kotenberg("invalid") }
    }

    @Test
    fun `should accept valid endpoint`() {
        Kotenberg("http://localhost:3000/")
    }

    @Test
    fun `should convert url`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result = kotenberg.convertUrl("http://localhost:3000")

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should fail converting html without index file`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        assertThrows<IndexFileNotFoundExceptions> {
            kotenberg.convertHtml(
                listOf(
                    File("src/test/resources/html/header.html"),
                    File("src/test/resources/html/footer.html"),
                )
            )
        }
    }

    @Test
    fun `should convert html files with image`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result =
            kotenberg.convertHtml(
                listOf(
                    File("src/test/resources/html/index.html"),
                    File("src/test/resources/html/header.html"),
                    File("src/test/resources/html/footer.html"),
                    File("src/test/resources/html/image.jpg"),
                )
            )

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should convert markdown files`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result =
            kotenberg.convertMarkdown(
                listOf(
                    File("src/test/resources/markdown/index.html"),
                    File("src/test/resources/markdown/test.md"),
                )
            )

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should convert office docx file`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result =
            kotenberg.convertWithLibreOffice(
                listOf(File("src/test/resources/libreoffice/test.docx"))
            )

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should convert office xlsx file`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result =
            kotenberg.convertWithLibreOffice(
                listOf(File("src/test/resources/libreoffice/test.xlsx"))
            )

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should convert 2 office files into zip archive`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result =
            kotenberg.convertWithLibreOffice(
                listOf(
                    File("src/test/resources/libreoffice/test.docx"),
                    File("src/test/resources/libreoffice/test.xlsx"),
                )
            )

        assertEquals(200, result.status.value)
        assertEquals("application/zip", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should throw exception when trying to convert empty list`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        assertThrows<FileNotFoundException> {
            kotenberg.convertWithPdfEngines(
                listOf(
                    File("src/test/resources/libreoffice/test.docx"),
                    File("src/test/resources/libreoffice/test.xlsx"),
                ),
                PageProperties.Builder().addPdfUniversalAccess(true).build(),
            )
        }
    }

    @Test
    fun `should throw exception when trying to convert non-PDF files`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        assertThrows<EmptyFileListException> {
            kotenberg.convertWithPdfEngines(
                emptyList(),
                PageProperties.Builder().addPdfUniversalAccess(true).build(),
            )
        }
    }

    @Test
    fun `should convert with pdf engines`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result =
            kotenberg.convertWithPdfEngines(
                listOf(File("src/test/resources/test.pdf")),
                PageProperties.Builder().addPdfUniversalAccess(true).build(),
            )

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should convert multiple files with pdf engines to zip`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result =
            kotenberg.convertWithPdfEngines(
                listOf(File("src/test/resources/test.pdf"), File("src/test/resources/test2.pdf")),
                PageProperties.Builder().addPdfUniversalAccess(true).build(),
            )

        assertEquals(200, result.status.value)
        assertEquals("application/zip", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should throw exception when trying to merge empty list`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        assertThrows<FileNotFoundException> {
            kotenberg.mergeWithPdfEngines(
                listOf(
                    File("src/test/resources/libreoffice/test.docx"),
                    File("src/test/resources/libreoffice/test.xlsx"),
                ),
                PageProperties.Builder().addPdfUniversalAccess(true).build(),
            )
        }
    }

    @Test
    fun `should throw exception when trying to merge non-PDF files`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        assertThrows<EmptyFileListException> {
            kotenberg.mergeWithPdfEngines(
                emptyList(),
                PageProperties.Builder().addPdfUniversalAccess(true).build(),
            )
        }
    }

    @Test
    fun `should merge with pdf engines`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val files =
            listOf(File("src/test/resources/test.pdf"), File("src/test/resources/test2.pdf"))

        val result =
            kotenberg.mergeWithPdfEngines(
                files,
                PageProperties.Builder().addPdfUniversalAccess(true).build(),
            )

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
        assertTrue(result.bodyAsChannel().toByteArray().size > files.first().length() * 1.5)
    }

    companion object {
        private val gotenbergTestContainer = GotenbergTestContainer()

        @BeforeAll
        @JvmStatic
        fun setup() {
            gotenbergTestContainer.start()
        }
    }
}
