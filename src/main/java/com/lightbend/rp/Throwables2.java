package com.lightbend.rp;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.PrivilegedActionException;
import java.util.concurrent.ExecutionException;

final class Throwables2 {
  private Throwables2() {}

  @SuppressWarnings("unchecked")
  static <T extends Throwable> void throwUndeclared(final Throwable t) throws T { throw (T) t; }

  /** Unwraps the outermost layers of known checked-exception wrappers. */
  static Throwable unwrap(final Throwable t) {
    if (t == null)
      throw new NullPointerException();

    if (t instanceof InvocationTargetException
     || t instanceof ExceptionInInitializerError
     || t instanceof UndeclaredThrowableException
     || t instanceof ExecutionException
     || t instanceof UncheckedIOException
     || t instanceof PrivilegedActionException
    ) {
      final Throwable cause = t.getCause();
      if (cause != null)
        return unwrap(cause);
    }

    return t;
  }

  static void throwIfUnchecked(final Throwable t) {
    if (t == null)
      throw new NullPointerException();
    if (t instanceof RuntimeException)
      throw (RuntimeException) t;
    if (t instanceof Error)
      throw (Error) t;
  }

  /**
   * @param context Added context to the exception. Use <code>null</code> or an empty string are
   *                valid, and avoid boxing.
   */
  static void throwRuntimeException(final Throwable t, final String context) {
    if (t == null)
      throw new NullPointerException();
    if (context == null || context.isEmpty()) {
      if (t instanceof RuntimeException)
        throw (RuntimeException) t;
      if (t instanceof IOException)
        throw new UncheckedIOException((IOException) t);
      throw new RuntimeException(t);
    } else {
      if (t instanceof IOException)
        throw new UncheckedIOException(context, (IOException) t);
      throw new RuntimeException(context, t);
    }
  }

}
