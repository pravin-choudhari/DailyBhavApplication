package com.valuestocks.dailybhav.utils;

public class ValidationUtil {

  public static boolean notNullOrEmpty(final String input) {
    return input != null && input.length() > 0;
  }
}
