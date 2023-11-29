package dev.marrek13.exception

import java.io.FileNotFoundException

/**
 * EmptyFileListException is an exception class that is thrown when a list of files is found to be empty.
 */
class EmptyFileListException : FileNotFoundException("Files should not be empty.")
