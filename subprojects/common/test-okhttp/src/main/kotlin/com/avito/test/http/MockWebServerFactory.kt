package com.avito.test.http

import okhttp3.mockwebserver.MockWebServer
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils

class MockWebServerFactory {

    fun create(): MockWebServer {
        if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_11)) {
            // https://github.com/robolectric/robolectric/issues/5115#issuecomment-593107140
            System.setProperty("javax.net.ssl.trustStoreType", "JKS")
        }
        return MockWebServer()
    }

    companion object {
        fun create(): MockWebServer = MockWebServerFactory().create()
    }

}