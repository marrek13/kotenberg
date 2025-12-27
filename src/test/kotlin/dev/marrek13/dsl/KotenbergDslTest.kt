package dev.marrek13.dsl

import dev.marrek13.Kotenberg
import dev.marrek13.config.PdfFormat
import dev.marrek13.container.GotenbergTestContainer
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.toByteArray
import java.io.File
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class KotenbergDslTest {

    @Test
    fun `should convert URL using DSL`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result = kotenberg.url { url("http://localhost:3000") }

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should convert URL using DSL with direct parameter`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result = kotenberg.url("http://localhost:3000")

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should convert URL with page properties using DSL`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result =
            kotenberg.url("http://localhost:3000") {
                pageProperties {
                    landscape = true
                    printBackground = true
                }
            }

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should convert HTML files using DSL`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result =
            kotenberg.html {
                file("src/test/resources/html/index.html")
                file("src/test/resources/html/header.html")
                file("src/test/resources/html/footer.html")
                file("src/test/resources/html/image.jpg")
            }

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should convert HTML files using DSL with unary plus operator`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result =
            kotenberg.html {
                +"src/test/resources/html/index.html"
                +"src/test/resources/html/header.html"
                +"src/test/resources/html/footer.html"
                +File("src/test/resources/html/image.jpg")
            }

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should convert HTML with page properties using DSL`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result =
            kotenberg.html {
                files(
                    "src/test/resources/html/index.html",
                    "src/test/resources/html/header.html",
                    "src/test/resources/html/footer.html",
                )
                pageProperties {
                    landscape = true
                    margins(0.5f)
                    scale = 1.2f
                    printBackground = true
                }
            }

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should convert Markdown files using DSL`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result =
            kotenberg.markdown {
                file("src/test/resources/markdown/index.html")
                file("src/test/resources/markdown/test.md")
            }

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should convert LibreOffice document using DSL`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result = kotenberg.libreOffice { file("src/test/resources/libreoffice/test.docx") }

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should convert multiple LibreOffice documents to zip using DSL`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result =
            kotenberg.libreOffice {
                file("src/test/resources/libreoffice/test.docx")
                file("src/test/resources/libreoffice/test.xlsx")
            }

        assertEquals(200, result.status.value)
        assertEquals("application/zip", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should convert with PDF engines using DSL`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result =
            kotenberg.pdfEngines {
                file("src/test/resources/test.pdf")
                pageProperties {
                    pdfUniversalAccess = true
                    pdfFormat = PdfFormat.A_3B
                }
            }

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should convert multiple PDFs to zip with PDF engines using DSL`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val result =
            kotenberg.pdfEngines {
                file("src/test/resources/test.pdf")
                file("src/test/resources/test2.pdf")
                pageProperties { pdfUniversalAccess = true }
            }

        assertEquals(200, result.status.value)
        assertEquals("application/zip", result.headers[HttpHeaders.ContentType])
    }

    @Test
    fun `should merge PDFs using DSL`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val files =
            listOf(File("src/test/resources/test.pdf"), File("src/test/resources/test2.pdf"))

        val result =
            kotenberg.mergePdfs {
                files(files)
                pageProperties { pdfUniversalAccess = true }
            }

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
        assertTrue(result.bodyAsChannel().toByteArray().size > files.first().length() * 1.5)
    }

    @Test
    fun `should use pageProperties helper function`() = runTest {
        val kotenberg = Kotenberg(gotenbergTestContainer.endpoint)

        val props = pageProperties {
            paperSize(8.5f, 11f)
            margins(1f)
            landscape = true
            scale = 0.9f
            printBackground = true
            preferCssPageSize = false
            pdfFormat = PdfFormat.A_2B
            pageRange(1, 5)
        }

        val result =
            kotenberg.html {
                file("src/test/resources/html/index.html")
                pageProperties(props)
            }

        assertEquals(200, result.status.value)
        assertEquals("application/pdf", result.headers[HttpHeaders.ContentType])
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
