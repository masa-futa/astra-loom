package com.astraloom.util

import kotlin.math.PI

/**
 * Math utility extensions for Kotlin Multiplatform
 *
 * These extension functions replace Java's Math.toRadians() and Math.toDegrees()
 * which are not available in Kotlin Multiplatform common code.
 */

/**
 * Convert degrees to radians
 */
fun Double.toRadians(): Double = this * PI / 180.0

/**
 * Convert radians to degrees
 */
fun Double.toDegrees(): Double = this * 180.0 / PI
