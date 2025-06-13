package com.mesofi.myth.collection.stats.exception;

/**
 * Exception thrown when an error occurs during web scraping operations. This runtime exception is
 * used to indicate failures in data extraction, parsing, or other scraper-related processes.
 */
public class ScraperException extends RuntimeException {
  /** Default constructor that creates a ScraperException with no detail message. */
  public ScraperException(String msg) {
    super(msg);
  }
}
