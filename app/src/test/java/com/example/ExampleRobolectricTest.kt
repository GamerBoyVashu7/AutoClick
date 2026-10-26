package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.FeasibilityData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("GamerBoyVashu Auto Clicker", appName)
    }

    @Test
    fun `feasibility audit items are populated`() {
        val items = FeasibilityData.items
        assertTrue("Feasibility audit items should not be empty", items.isNotEmpty())
        val mouseInterceptionTopic = items.find { it.title.contains("Physical USB Mouse Button 1") }
        assertNotNull("Mouse button interception feasibility topic must exist", mouseInterceptionTopic)
    }

    @Test
    fun `cps calculation for intervals`() {
        val intervals = listOf(50L, 100L, 200L, 500L)
        val expectedCps = listOf(20L, 10L, 5L, 2L)
        intervals.forEachIndexed { index, ms ->
            val cps = 1000L / ms
            assertEquals(expectedCps[index], cps)
        }
    }
}
