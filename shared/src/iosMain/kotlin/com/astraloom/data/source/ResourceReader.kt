package com.astraloom.data.source

import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile

/**
 * iOS implementation of ResourceReader
 */
actual class ResourceReader {
    actual fun readResource(path: String): String {
        val bundle = NSBundle.mainBundle
        val parts = path.split(".")
        val name = parts.dropLast(1).joinToString(".")
        val ext = parts.lastOrNull() ?: ""

        val resourcePath = bundle.pathForResource(name, ext)
            ?: throw IllegalArgumentException("Resource not found: $path")

        return NSString.stringWithContentsOfFile(
            resourcePath,
            encoding = NSUTF8StringEncoding,
            error = null
        ) ?: throw IllegalArgumentException("Failed to read resource: $path")
    }
}
