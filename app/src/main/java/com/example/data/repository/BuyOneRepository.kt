package com.example.data.repository

import com.example.data.local.BlogPostEntity
import com.example.data.local.BuyOneDao
import com.example.data.local.CartItemEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.CouponEntity
import com.example.data.local.CustomerCrmEntity
import com.example.data.local.OrderEntity
import com.example.data.local.PartnerAccountEntity
import com.example.data.local.PlatformSettingEntity
import com.example.data.local.ProductEntity
import com.example.data.local.ReturnRequestEntity
import com.example.data.local.SeedData
import com.example.data.local.TrackingEventLogEntity
import com.example.data.local.WarehouseEntity
import kotlinx.coroutines.flow.Flow

class BuyOneRepository(private val dao: BuyOneDao) {

    val products: Flow<List<ProductEntity>> = dao.observeAllProducts()
    val categories: Flow<List<CategoryEntity>> = dao.observeCategories()
    val cartItems: Flow<List<CartItemEntity>> = dao.observeCartItems()
    val orders: Flow<List<OrderEntity>> = dao.observeOrders()
    val warehouses: Flow<List<WarehouseEntity>> = dao.observeWarehouses()
    val coupons: Flow<List<CouponEntity>> = dao.observeCoupons()
    val customersCrm: Flow<List<CustomerCrmEntity>> = dao.observeCustomersCrm()
    val partnerAccounts: Flow<List<PartnerAccountEntity>> = dao.observePartnerAccounts()
    val returnRequests: Flow<List<ReturnRequestEntity>> = dao.observeReturnRequests()
    val blogPosts: Flow<List<BlogPostEntity>> = dao.observeBlogPosts()
    val trackingEvents: Flow<List<TrackingEventLogEntity>> = dao.observeTrackingEvents()
    val platformSettings: Flow<List<PlatformSettingEntity>> = dao.observePlatformSettings()

    suspend fun ensureSeeded() {
        if (dao.getProductCount() == 0) {
            dao.insertCategories(SeedData.initialCategories)
            dao.insertProducts(SeedData.initialProducts)
            dao.insertWarehouses(SeedData.initialWarehouses)
            dao.insertCoupons(SeedData.initialCoupons)
            dao.insertOrders(SeedData.initialOrders)
            dao.insertCustomersCrm(SeedData.initialCustomersCrm)
            dao.insertPartnerAccounts(SeedData.initialPartnerAccounts)
            dao.insertReturnRequests(SeedData.initialReturnRequests)
            dao.insertBlogPosts(SeedData.initialBlogPosts)
            SeedData.initialTrackingLogs.forEach { dao.insertTrackingEvent(it) }
            dao.insertPlatformSettings(SeedData.initialSettings)
        }
    }

    suspend fun toggleWishlist(product: ProductEntity) {
        dao.setProductWishlisted(product.id, !product.isWishlisted)
    }

    suspend fun addToCart(
        product: ProductEntity,
        variant: String,
        quantity: Int,
        orderMode: String,
        customResellerPrice: Int = product.salePrice
    ) {
        val existing = dao.findCartItem(product.id, variant, orderMode)
        if (existing != null) {
            dao.updateCartItem(
                existing.copy(
                    quantity = existing.quantity + quantity,
                    customResellerSellPrice = customResellerPrice
                )
            )
        } else {
            dao.insertCartItem(
                CartItemEntity(
                    productId = product.id,
                    productName = product.name,
                    productBanglaName = product.banglaName,
                    sku = product.sku,
                    variantLabel = variant,
                    quantity = quantity,
                    retailUnitPrice = product.retailPrice,
                    saleUnitPrice = product.salePrice,
                    wholesaleTier1Price = product.wholesaleTier1Price,
                    wholesaleTier2Price = product.wholesaleTier2Price,
                    wholesaleTier3Price = product.wholesaleTier3Price,
                    resellerBasePrice = product.resellerPrice,
                    customResellerSellPrice = customResellerPrice.coerceAtLeast(product.resellerPrice),
                    orderMode = orderMode
                )
            )
        }
    }

    suspend fun updateCartQuantity(item: CartItemEntity, newQty: Int) {
        if (newQty <= 0) {
            dao.deleteCartItem(item.id)
        } else {
            dao.updateCartItem(item.copy(quantity = newQty))
        }
    }

    suspend fun toggleSaveForLater(item: CartItemEntity) {
        dao.updateCartItem(item.copy(savedForLater = !item.savedForLater))
    }

    suspend fun removeCartItem(cartItemId: Int) {
        dao.deleteCartItem(cartItemId)
    }

    suspend fun clearActiveCart() {
        dao.clearActiveCart()
    }

    suspend fun findActiveCoupon(code: String): CouponEntity? {
        return dao.findActiveCoupon(code.trim())
    }

    suspend fun findCustomerByPhone(phone: String): CustomerCrmEntity? {
        return dao.findCustomerByPhone(phone.trim())
    }

    suspend fun saveOrderAndSyncInventory(
        order: OrderEntity,
        purchasedItems: List<CartItemEntity>,
        events: List<TrackingEventLogEntity>
    ) {
        dao.insertOrder(order)
        purchasedItems.forEach { item ->
            dao.reserveStockForOrder(item.productId, item.quantity)
        }
        events.forEach { event ->
            dao.insertTrackingEvent(event)
        }
        val existingCustomer = dao.findCustomerByPhone(order.customerPhone)
        if (existingCustomer != null) {
            dao.upsertCustomerCrm(
                existingCustomer.copy(
                    totalOrders = existingCustomer.totalOrders + 1,
                    totalSpentBdt = existingCustomer.totalSpentBdt + order.grandTotalBdt,
                    loyaltyPoints = existingCustomer.loyaltyPoints + (order.grandTotalBdt / 20),
                    lastPurchaseLabel = "Just now"
                )
            )
        } else {
            dao.upsertCustomerCrm(
                CustomerCrmEntity(
                    name = order.customerName,
                    phone = order.customerPhone,
                    email = order.customerEmail,
                    district = order.district,
                    segment = if (order.orderMode == "WHOLESALE") "Wholesale" else if (order.orderMode == "RESELLER") "Reseller" else "New Customer",
                    totalOrders = 1,
                    totalSpentBdt = order.grandTotalBdt,
                    cancelledOrders = 0,
                    returnedOrders = 0,
                    codFailedDeliveries = 0,
                    loyaltyPoints = order.grandTotalBdt / 20,
                    walletBalanceBdt = 0,
                    leadScore = "HOT",
                    sourceChannel = "BuyOneBD App",
                    lastPurchaseLabel = "Just now"
                )
            )
        }
    }

    suspend fun updateOrder(order: OrderEntity) {
        dao.updateOrder(order)
    }

    suspend fun updateProduct(product: ProductEntity) {
        dao.updateProduct(product)
    }

    suspend fun addProductStock(productId: Int, addedStock: Int) {
        dao.addProductStock(productId, addedStock)
    }

    suspend fun submitReturnRequest(request: ReturnRequestEntity) {
        dao.insertReturnRequest(request)
    }

    suspend fun updateReturnRequest(request: ReturnRequestEntity) {
        dao.updateReturnRequest(request)
    }

    suspend fun updatePartnerAccount(account: PartnerAccountEntity) {
        dao.updatePartnerAccount(account)
    }

    suspend fun logTrackingEvent(event: TrackingEventLogEntity) {
        dao.insertTrackingEvent(event)
    }

    suspend fun updateSetting(key: String, newValue: String) {
        dao.updatePlatformSetting(key, newValue)
    }
}
