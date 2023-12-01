package dev.marrek13.config

import dev.marrek13.config.PageProperties.Builder.Companion.MINIMAL_MARGIN
import dev.marrek13.config.PageProperties.Builder.Companion.MINIMAL_PAPER_HEIGHT
import dev.marrek13.config.PageProperties.Builder.Companion.MINIMAL_PAPER_WIDTH
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PagePropertiesTest {
    @ParameterizedTest
    @ValueSource(floats = [0f, Float.MIN_VALUE, MINIMAL_PAPER_WIDTH])
    fun `should validate minimal paper width`(width: Float) {
        assertThrows<IllegalArgumentException> { PageProperties.Builder().addPaperWidth(width) }
    }

    @Test
    fun `should accept valid paper width`() {
        PageProperties.Builder().addPaperWidth(MINIMAL_PAPER_WIDTH + 0.01f)
    }

    @ParameterizedTest
    @ValueSource(floats = [0f, Float.MIN_VALUE, MINIMAL_PAPER_HEIGHT])
    fun `should validate minimal paper height`(height: Float) {
        assertThrows<IllegalArgumentException> { PageProperties.Builder().addPaperHeight(height) }
    }

    @Test
    fun `should accept valid paper height`() {
        PageProperties.Builder().addPaperWidth(MINIMAL_PAPER_HEIGHT + 0.01f)
    }

    @Test
    fun `should validate minimal margin`() {
        assertThrows<IllegalArgumentException> { PageProperties.Builder().addMarginBottom(MINIMAL_MARGIN) }
        assertThrows<IllegalArgumentException> { PageProperties.Builder().addMarginTop(MINIMAL_MARGIN) }
        assertThrows<IllegalArgumentException> { PageProperties.Builder().addMarginRight(MINIMAL_MARGIN) }
        assertThrows<IllegalArgumentException> { PageProperties.Builder().addMarginLeft(MINIMAL_MARGIN) }
    }

    @Test
    fun `should accept valid margin`() {
        PageProperties.Builder().addMarginBottom(MINIMAL_MARGIN + 0.01f)
        PageProperties.Builder().addMarginTop(MINIMAL_MARGIN + 0.01f)
        PageProperties.Builder().addMarginRight(MINIMAL_MARGIN + 0.01f)
        PageProperties.Builder().addMarginLeft(MINIMAL_MARGIN + 0.01f)
    }
}
