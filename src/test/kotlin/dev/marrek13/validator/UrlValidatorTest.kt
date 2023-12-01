package dev.marrek13.validator

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UrlValidatorTest {
    @Test
    fun `test validate url`() {
        val url = "https://www.google.com"
        assertTrue(UrlValidator.isValidURL(url))
    }

    @Test
    fun `test validate url with invalid protocol`() {
        val url = "htps://www.google.com"
        assertFalse(UrlValidator.isValidURL(url))
    }
}
