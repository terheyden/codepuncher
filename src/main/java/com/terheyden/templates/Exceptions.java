package com.terheyden.templates;

/**
 * Exceptions class.
 */
public final class Exceptions {

    private Exceptions() {
        // Private since this class shouldn't be instantiated.
    }

    /**
     * Throw any exception unchecked.
     */
    @SuppressWarnings("unchecked")
    public static <E extends Throwable, R> R throwUnchecked(Throwable throwable) throws E {
        throw (E) throwable;
    }
}
