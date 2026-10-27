package com.example.saferun.data.repository

import android.os.Looper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class BaseRepositoryTest {
    @BeforeEach
    protected fun init() {
        mockkStatic(Looper::class)

        every { Looper.getMainLooper() } returns mockk(relaxed = true)
    }

    @AfterEach
    protected fun tearDownMocks() {
        unmockkStatic(Looper::class)
    }
}
