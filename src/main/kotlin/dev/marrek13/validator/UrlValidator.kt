package dev.marrek13.validator

import java.net.URI

object UrlValidator {
    /**
     * Checks if a given string is a valid URL.
     *
     * @param url The URL to validate.
     * @return `true` if the URL is valid, `false` otherwise.
     */
    fun isValidURL(url: String): Boolean =
        try {
            URI(url).toURL()
            true
        } catch (e: Exception) {
            false
        }
}
