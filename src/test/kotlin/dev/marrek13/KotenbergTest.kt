package dev.marrek13

import dev.marrek13.config.PageProperties
import dev.marrek13.container.GotenbergTestContainer
import dev.marrek13.exception.IndexFileNotFoundExceptions
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.net.MalformedURLException

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
    fun `should convert url`() =
        runTest {
            val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

            val result = kotenberg.convertUrl(gotenbergTestContainer.endpoint + "/health", PageProperties.Builder().build())

            assertEquals(200, result.status.value)
            assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
        }

    @Test
    fun `should fail converting html without index file`() =
        runTest {
            val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

            assertThrows<IndexFileNotFoundExceptions> {
                kotenberg.convertHtml(
                    listOf(
                        File("src/test/resources/html/header.html"),
                        File("src/test/resources/html/footer.html"),
                    ),
                    PageProperties.Builder().build(),
                )
            }
        }

    @Test
    fun `should convert html files with image`() =
        runTest {
            val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

            val result =
                kotenberg.convertHtml(
                    listOf(
                        File("src/test/resources/html/index.html"),
                        File("src/test/resources/html/header.html"),
                        File("src/test/resources/html/footer.html"),
                        File("src/test/resources/html/image.jpg"),
                    ),
                    PageProperties.Builder().build(),
                )

            assertEquals(200, result.status.value)
            assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
        }

    @Test
    fun `should convert markdown files`() =
        runTest {
            val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

            val result =
                kotenberg.convertMarkdown(
                    listOf(
                        File("src/test/resources/markdown/index.html"),
                        File("src/test/resources/markdown/test.md"),
                    ),
                    PageProperties.Builder().build(),
                )

            assertEquals(200, result.status.value)
            assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
        }

    @Test
    fun `should convert office docx file`() =
        runTest {
            val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

            val result =
                kotenberg.convertWithLibreOffice(
                    listOf(
                        File("src/test/resources/libreoffice/test.docx"),
                    ),
                    PageProperties.Builder().build(),
                )

            assertEquals(200, result.status.value)
            assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
        }

    @Test
    fun `should convert office xlsx file`() =
        runTest {
            val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

            val result =
                kotenberg.convertWithLibreOffice(
                    listOf(
                        File("src/test/resources/libreoffice/test.xlsx"),
                    ),
                    PageProperties.Builder().build(),
                )

            assertEquals(200, result.status.value)
            assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
        }

    @Test
    fun `should convert 2 office files into zip archive`() =
        runTest {
            val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

            val result =
                kotenberg.convertWithLibreOffice(
                    listOf(
                        File("src/test/resources/libreoffice/test.docx"),
                        File("src/test/resources/libreoffice/test.xlsx"),
                    ),
                    PageProperties.Builder().build(),
                )

            assertEquals(200, result.status.value)
            assertEquals("application/zip", result.headers[HttpHeaders.ContentType])
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
