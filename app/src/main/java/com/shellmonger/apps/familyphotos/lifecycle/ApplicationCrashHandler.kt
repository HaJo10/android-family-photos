package com.shellmonger.apps.familyphotos.lifecycle

import android.content.Context

/**
 * Handler for dealing with uncaught exceptions.  When developing, you should always
 * place a breakpoint on the uncaughtException method so you can get the appropriate
 * error messages.
 */
class ApplicationCrashHandler : Thread.UncaughtExceptionHandler {
    /**
     * Private logger
     */
    private val logger = Logger("ApplicationCrashHandler")

    /**
     * Original handler
     */
    private var defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    /**
     * Static context - used for initialization / singleton
     */
    companion object {
        private var isInitialized = false

        /**
         * Installs the handler
         * SUPPRESS("UNUSED_PARAMETER") - we may alter this in the future to store the exception
         *   within the file, then submit any exceptions to a service.  For that, we will need
         *   the context.  Since we don't want to adjust the code outside of this class, we pass
         *   the context in, but ignore it for now.
         */
        @Synchronized
        fun initialize(@Suppress("UNUSED_PARAMETER") context: Context) {
            if (!isInitialized) {
                Thread.setDefaultUncaughtExceptionHandler(ApplicationCrashHandler())
                isInitialized = true
            }
        }
    }

    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     *
     * Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     * @param t the thread
     * @param e the exception
     */
    override fun uncaughtException(t: Thread?, e: Throwable?) {
        // Place a breakpoint here
        logger.wtf("UNCAUGHT EXCEPTION ${e?.javaClass?.simpleName}", e)

        // Call the original uncaught exception here
        defaultHandler.uncaughtException(t, e)
    }
}