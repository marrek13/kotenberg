package dev.marrek13.exception

import java.io.FileNotFoundException

/**
 * IndexFileNotFoundExceptions is an exception class that is thrown when an index.html file is not found.
 */
class IndexFileNotFoundExceptions : FileNotFoundException("No index.html file found.")
