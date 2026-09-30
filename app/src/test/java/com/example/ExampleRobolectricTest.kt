package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SeedData
import com.example.domain.services.BanglaSearchEngine
import com.example.domain.services.BangladeshAddressDirectory
import com.example.domain.services.CourierManager
import com.example.domain.services.PaymentManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("BuyOneBD", appName)
    }

    @Test
    fun `bangla banglish and english typo tolerant search finds cumin powder`() {
        val catalog = SeedData.initialProducts
        val byBangla = BanglaSearchEngine.searchProducts(catalog, "জিরা গুঁড়া", null)
        val byBanglish = BanglaSearchEngine.searchProducts(catalog, "jira gura", null)
        val byZira = BanglaSearchEngine.searchProducts(catalog, "zira powder", null)
        val byEnglish = BanglaSearchEngine.searchProducts(catalog, "cumin powder", null)

        assertTrue(byBangla.any { it.sku == "BOBD-GRC-101" })
        assertTrue(byBanglish.any { it.sku == "BOBD-GRC-101" })
        assertTrue(byZira.any { it.sku == "BOBD-GRC-101" })
        assertTrue(byEnglish.any { it.sku == "BOBD-GRC-101" })
    }

    @Test
    fun `courier manager resolves steadfast pathao and redx adapters`() {
        val courierManager = CourierManager()
        val sampleOrder = SeedData.initialOrders.first()

        val sfShipment = courierManager.createShipmentWithProvider(sampleOrder, "Steadfast")
        val pathaoShipment = courierManager.createShipmentWithProvider(sampleOrder, "Pathao")
        val redxShipment = courierManager.createShipmentWithProvider(sampleOrder, "RedX")

        assertTrue(sfShipment.consignmentId.startsWith("SF-"))
        assertTrue(pathaoShipment.consignmentId.startsWith("PTH-"))
        assertTrue(redxShipment.consignmentId.startsWith("RDX-"))
    }

    @Test
    fun `payment manager and bangladesh delivery zone calculation work`() {
        val paymentManager = PaymentManager()
        val insideZone = BangladeshAddressDirectory.resolveDeliveryZone("Dhaka", "Dhanmondi")
        val outsideZone = BangladeshAddressDirectory.resolveDeliveryZone("Chattogram", "Panchlaish")

        assertEquals("Inside Dhaka", insideZone)
        assertEquals("Outside Dhaka", outsideZone)

        val bkashResult = paymentManager.processPayment("bKash", "BOBD-2026-9900", 1500, "01712345678", insideZone)
        assertTrue(bkashResult.isVerified)
        assertTrue(bkashResult.transactionId.startsWith("BKX"))
    }
}
