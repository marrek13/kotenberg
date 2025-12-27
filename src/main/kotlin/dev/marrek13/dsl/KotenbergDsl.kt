package dev.marrek13.dsl

/** Marker annotation for Kotenberg DSL to prevent improper nesting. */
@Suppress("unused")
@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class KotenbergDsl
