package dev.marrek13.validator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class FileValidatorTest {
    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/markdown/test.md,true",
            "src/test/resources/missing.md,false",
            "src/test/resources/markdown/index.html,false",
        ],
        delimiter = ',',
    )
    fun `test is markdown file`(
        file: String,
        expected: Boolean,
    ) {
        assertEquals(expected, FileValidator.isMarkdown(File(file)))
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/html/index.html,true",
            "src/test/resources/html/footer.html,false",
            "src/test/resources/missing.html,false",
            "src/test/resources/markdown/index.html,true",
        ],
        delimiter = ',',
    )
    fun `test is index file`(
        file: String,
        expected: Boolean,
    ) {
        assertEquals(expected, FileValidator.isIndexHtml(File(file)))
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/test.pdf,true",
            "src/test/resources/missing.pdf,false",
            "src/test/resources/markdown/index.html,false",
        ],
        delimiter = ',',
    )
    fun `test is pdf file`() {
        val file = File("src/test/resources/test.pdf")
        assertTrue(FileValidator.isPdf(file))
    }

    @Test
    fun `test validate files contains existing index`() {
        val fileMarkdown = File("src/test/resources/markdown/test.md")
        val fileIndex = File("src/test/resources/markdown/index.html")

        assertTrue(FileValidator.containsIndex(listOf(fileMarkdown, fileIndex)))
    }

    @Test
    fun `test validate files contains missing index`() {
        val fileMarkdown = File("src/test/resources/markdown/test.md")
        val fileMissing = File("src/test/resources/missing.html")
        val fileMissingIndex = File("src/test/resources/missing/index.html")

        assertFalse(FileValidator.containsIndex(listOf(fileMarkdown, fileMissing, fileMissingIndex)))
    }

    @ParameterizedTest
    @MethodSource("supportedFiles")
    fun `test validate files supported by libre office`(
        file: String,
        expected: Boolean,
    ) {
        assertEquals(expected, FileValidator.isSupportedByLibreOffice(File(file)))
    }

    companion object {
        @JvmStatic
        fun supportedFiles() =
            arrayOf(
                arrayOf("src/test/resources/test.pdf", true),
                arrayOf("src/test/resources/html/index.html", true),
                arrayOf("src/test/resources/markdown/test.md", false),
                arrayOf("src/test/resources/missing.html", false),
                arrayOf("src/test/kotlin/dev/marrek13/validator/FileValidatorTest.kt", false),
            )
    }
}
