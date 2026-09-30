package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BlogPostEntity
import com.example.data.local.BuyOneDatabase
import com.example.data.local.CartItemEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.CouponEntity
import com.example.data.local.CustomerCrmEntity
import com.example.data.local.OrderEntity
import com.example.data.local.PartnerAccountEntity
import com.example.data.local.PlatformSettingEntity
import com.example.data.local.ProductEntity
import com.example.data.local.ReturnRequestEntity
import com.example.data.local.TrackingEventLogEntity
import com.example.data.local.WarehouseEntity
import com.example.data.repository.BuyOneRepository
import com.example.domain.services.AiGeneratedSeoPack
import com.example.domain.services.BanglaSearchEngine
import com.example.domain.services.BangladeshAddressDirectory
import com.example.domain.services.BuyOneAiService
import com.example.domain.services.CourierManager
import com.example.domain.services.PaymentManager
import com.example.domain.services.UniversalTrackingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String, val banglaLabel: String) {
    HOME("Shop", "হোম"),
    SEARCH("Catalog", "খুঁজুন"),
    CART("Cart", "কার্ট"),
    B2B_HUB("B2B & Track", "পাইকারি/ট্র্যাক"),
    ADMIN("Admin & AI", "অ্যাডমিন/AI")
}

data class AiChatMessage(
    val sender: String, // "USER" or "AI"
    val text: String,
    val timestampLabel: String = "Just now"
)

class BuyOneViewModel(application: Application) : AndroidViewModel(application) {

    private val database = BuyOneDatabase.getInstance(application)
    private val repository = BuyOneRepository(database.buyOneDao())

    private val courierManager = CourierManager()
    private val paymentManager = PaymentManager()
    private val trackingService = UniversalTrackingService()
    private val aiService = BuyOneAiService()

    // Core Database Flows
    val products: StateFlow<List<ProductEntity>> = repository.products
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.orders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val warehouses: StateFlow<List<WarehouseEntity>> = repository.warehouses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coupons: StateFlow<List<CouponEntity>> = repository.coupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customersCrm: StateFlow<List<CustomerCrmEntity>> = repository.customersCrm
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val partnerAccounts: StateFlow<List<PartnerAccountEntity>> = repository.partnerAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val returnRequests: StateFlow<List<ReturnRequestEntity>> = repository.returnRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blogPosts: StateFlow<List<BlogPostEntity>> = repository.blogPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trackingEvents: StateFlow<List<TrackingEventLogEntity>> = repository.trackingEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val platformSettings: StateFlow<List<PlatformSettingEntity>> = repository.platformSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation & Active UI State
    private val _currentTab = MutableStateFlow(AppNavTab.HOME)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    // Pricing Mode: RETAIL, WHOLESALE, RESELLER
    private val _activeOrderMode = MutableStateFlow("RETAIL")
    val activeOrderMode: StateFlow<String> = _activeOrderMode.asStateFlow()

    // Search & Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _onlyFlashSale = MutableStateFlow(false)
    val onlyFlashSale: StateFlow<Boolean> = _onlyFlashSale.asStateFlow()

    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        products,
        _searchQuery,
        _selectedCategory,
        _onlyFlashSale
    ) { allProducts, query, category, flashOnly ->
        BanglaSearchEngine.searchProducts(allProducts, query, category, flashOnly)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Product Detail Modal
    private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

    // Selected Blog Post Modal
    private val _selectedBlogPost = MutableStateFlow<BlogPostEntity?>(null)
    val selectedBlogPost: StateFlow<BlogPostEntity?> = _selectedBlogPost.asStateFlow()

    // Coupon & Checkout State
    private val _appliedCoupon = MutableStateFlow<CouponEntity?>(null)
    val appliedCoupon: StateFlow<CouponEntity?> = _appliedCoupon.asStateFlow()

    private val _bannerMessage = MutableStateFlow<String?>(null)
    val bannerMessage: StateFlow<String?> = _bannerMessage.asStateFlow()

    private val _lastCreatedOrder = MutableStateFlow<OrderEntity?>(null)
    val lastCreatedOrder: StateFlow<OrderEntity?> = _lastCreatedOrder.asStateFlow()

    // AI Chat & Content Studio State
    private val _aiMessages = MutableStateFlow(
        listOf(
            AiChatMessage(
                sender = "AI",
                text = "আসসালামু আলাইকুম! BuyOneBD AI Assistant-এ স্বাগতম 🌿\nযেকোনো প্রোডাক্টের দাম, স্টক, ওয়ারেন্টি (যেমন: \"Omron machine available?\", \"জিরা গুঁড়া কত?\"), ডেলিভারি চার্জ (\"Delivery koto?\") কিংবা আপনার অর্ডার ট্র্যাকিং (\"আমার order কোথায়?\") জানতে প্রশ্ন করুন!"
            )
        )
    )
    val aiMessages: StateFlow<List<AiChatMessage>> = _aiMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _generatedSeoPack = MutableStateFlow<AiGeneratedSeoPack?>(null)
    val generatedSeoPack: StateFlow<AiGeneratedSeoPack?> = _generatedSeoPack.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun setOrderMode(mode: String) {
        _activeOrderMode.value = mode
        showBanner("Switched pricing & checkout mode to $mode")
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleFlashSaleFilter() {
        _onlyFlashSale.value = !_onlyFlashSale.value
    }

    fun openProductDetail(product: ProductEntity) {
        _selectedProduct.value = product
        viewModelScope.launch {
            trackingService.buildViewContentEvents(product).forEach {
                repository.logTrackingEvent(it)
            }
        }
    }

    fun closeProductDetail() {
        _selectedProduct.value = null
    }

    fun openBlogPost(post: BlogPostEntity?) {
        _selectedBlogPost.value = post
    }

    fun toggleWishlist(product: ProductEntity) {
        viewModelScope.launch {
            repository.toggleWishlist(product)
            // Keep selected product state in sync if open
            if (_selectedProduct.value?.id == product.id) {
                _selectedProduct.value = product.copy(isWishlisted = !product.isWishlisted)
            }
        }
    }

    fun calculateEffectiveUnitPrice(
        product: ProductEntity,
        quantity: Int,
        mode: String
    ): Int {
        return when (mode) {
            "WHOLESALE" -> when {
                quantity >= 50 -> product.wholesaleTier3Price
                quantity >= 20 -> product.wholesaleTier2Price
                quantity >= 5 -> product.wholesaleTier1Price
                else -> product.salePrice
            }
            "RESELLER" -> product.resellerPrice
            else -> when {
                quantity >= 50 -> product.wholesaleTier3Price
                quantity >= 20 -> product.wholesaleTier2Price
                quantity >= 5 -> product.wholesaleTier1Price
                else -> product.salePrice
            }
        }
    }

    fun calculateCartItemUnitPrice(item: CartItemEntity): Int {
        return when (item.orderMode) {
            "WHOLESALE" -> when {
                item.quantity >= 50 -> item.wholesaleTier3Price
                item.quantity >= 20 -> item.wholesaleTier2Price
                item.quantity >= 5 -> item.wholesaleTier1Price
                else -> item.saleUnitPrice
            }
            "RESELLER" -> item.customResellerSellPrice.coerceAtLeast(item.resellerBasePrice)
            else -> when {
                item.quantity >= 50 -> item.wholesaleTier3Price
                item.quantity >= 20 -> item.wholesaleTier2Price
                item.quantity >= 5 -> item.wholesaleTier1Price
                else -> item.saleUnitPrice
            }
        }
    }

    fun addToCart(
        product: ProductEntity,
        variant: String = product.variantsCsv.split("|").firstOrNull() ?: "Standard",
        quantity: Int = 1,
        customResellerSellPrice: Int = product.salePrice,
        navigateToCart: Boolean = false
    ) {
        viewModelScope.launch {
            val mode = _activeOrderMode.value
            val effectiveQty = if (mode == "WHOLESALE" && quantity < 5) 5 else quantity
            repository.addToCart(product, variant, effectiveQty, mode, customResellerSellPrice)
            val unitPrice = calculateEffectiveUnitPrice(product, effectiveQty, mode)
            repository.logTrackingEvent(
                trackingService.buildAddToCartEvent(product, effectiveQty, unitPrice)
            )
            showBanner("Added ${product.name} (x$effectiveQty) to Cart")
            if (navigateToCart) {
                _selectedProduct.value = null
                _currentTab.value = AppNavTab.CART
            }
        }
    }

    fun updateCartItemQuantity(item: CartItemEntity, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(item, newQuantity)
        }
    }

    fun toggleSaveForLater(item: CartItemEntity) {
        viewModelScope.launch {
            repository.toggleSaveForLater(item)
        }
    }

    fun removeCartItem(itemId: Int) {
        viewModelScope.launch {
            repository.removeCartItem(itemId)
        }
    }

    fun applyCouponCode(code: String, currentSubtotal: Int) {
        viewModelScope.launch {
            val found = repository.findActiveCoupon(code)
            if (found == null) {
                showBanner("Invalid or expired coupon code: $code")
                return@launch
            }
            if (currentSubtotal < found.minOrderBdt) {
                showBanner("Coupon ${found.code} requires minimum order of ৳${found.minOrderBdt}")
                return@launch
            }
            _appliedCoupon.value = found
            showBanner("Coupon ${found.code} applied: ${found.title}")
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        showBanner("Coupon removed")
    }

    fun placeBangladeshOrder(
        customerName: String,
        customerPhone: String,
        customerEmail: String,
        division: String,
        district: String,
        upazila: String,
        area: String,
        fullAddress: String,
        orderNote: String,
        paymentMethod: String,
        affiliateCode: String
    ) {
        viewModelScope.launch {
            val activeCart = cartItems.value.filter { !it.savedForLater }
            if (activeCart.isEmpty()) {
                showBanner("Your cart is empty!")
                return@launch
            }
            if (customerName.isBlank() || customerPhone.length < 10 || fullAddress.isBlank()) {
                showBanner("Please enter valid Name, BD Phone Number (11 digits) & Full Address")
                return@launch
            }

            val deliveryZone = BangladeshAddressDirectory.resolveDeliveryZone(district, upazila)
            val subtotal = activeCart.sumOf { calculateCartItemUnitPrice(it) * it.quantity }
            val coupon = _appliedCoupon.value
            val discount = when (coupon?.discountType) {
                "PERCENTAGE" -> ((subtotal * coupon.discountValue) / 100).coerceAtMost(coupon.maxDiscountBdt)
                "FIXED" -> coupon.discountValue.coerceAtMost(subtotal)
                else -> 0
            }

            val freeShippingThreshold = getSettingInt("free_shipping_threshold", 2000)
            val insideRate = getSettingInt("inside_dhaka_charge", 60)
            val outsideRate = getSettingInt("outside_dhaka_charge", 120)
            val baseDelivery = BangladeshAddressDirectory.resolveDeliveryCharge(deliveryZone, insideRate, outsideRate)
            val deliveryCharge = if (subtotal >= freeShippingThreshold || coupon?.isFreeDelivery == true) 0 else baseDelivery

            val orderSeq = (9842 + orders.value.size)
            val orderNumber = "BOBD-2026-$orderSeq"
            val invoiceNumber = "INV-2026-$orderSeq"

            val paymentVerification = paymentManager.processPayment(
                method = paymentMethod,
                orderNumber = orderNumber,
                amountBdt = (subtotal - discount + deliveryCharge).coerceAtLeast(0),
                customerPhone = customerPhone,
                deliveryZone = deliveryZone
            )

            val codCharge = paymentVerification.codChargeBdt
            val grandTotal = (subtotal - discount + deliveryCharge + codCharge).coerceAtLeast(0)

            val existingCustomer = repository.findCustomerByPhone(customerPhone)
            val codRiskEnabled = getSettingValue("cod_risk_threshold", "ENABLED") == "ENABLED"
            val codRisk = paymentManager.evaluateCodRisk(existingCustomer, grandTotal, codRiskEnabled)

            val resellerCommission = activeCart.sumOf { item ->
                if (item.orderMode == "RESELLER") {
                    (item.customResellerSellPrice - item.resellerBasePrice).coerceAtLeast(0) * item.quantity
                } else 0
            }

            val defaultCourier = getSettingValue("default_courier", "Steadfast")
            val draftOrder = OrderEntity(
                orderNumber = orderNumber,
                invoiceNumber = invoiceNumber,
                customerName = customerName.trim(),
                customerPhone = customerPhone.trim(),
                customerEmail = customerEmail.trim(),
                division = division,
                district = district,
                upazila = upazila,
                area = area.ifBlank { upazila },
                fullAddress = fullAddress.trim(),
                deliveryZone = deliveryZone,
                orderNote = orderNote.trim(),
                paymentMethod = paymentMethod,
                paymentStatus = paymentVerification.paymentStatus,
                transactionId = paymentVerification.transactionId,
                itemsSummary = activeCart.joinToString(", ") { "${it.productName} (${it.variantLabel} x${it.quantity})" },
                itemCount = activeCart.sumOf { it.quantity },
                subtotalBdt = subtotal,
                discountBdt = discount,
                couponCode = coupon?.code ?: "",
                deliveryChargeBdt = deliveryCharge,
                codChargeBdt = codCharge,
                grandTotalBdt = grandTotal,
                orderMode = _activeOrderMode.value,
                resellerCommissionBdt = resellerCommission,
                affiliateCode = affiliateCode.trim(),
                courierProvider = defaultCourier,
                consignmentId = "PENDING",
                orderStatus = "Confirmed",
                timelineSummary = "Order Placed • ${paymentVerification.gatewayNotice} • Assigned to $defaultCourier",
                codRiskLevel = codRisk.riskLevel,
                warehouseCode = if (division == "Chattogram") "WH-CTG-02" else if (division in listOf("Rajshahi", "Rangpur")) "WH-BOG-03" else "WH-DHK-01"
            )

            val shipment = courierManager.createShipmentWithProvider(draftOrder, defaultCourier)
            val finalizedOrder = draftOrder.copy(
                consignmentId = shipment.consignmentId,
                timelineSummary = "Order Placed • ${paymentVerification.gatewayNotice} • Booked on ${shipment.providerName} (${shipment.consignmentId}) • Est. ${shipment.estimatedDeliveryLabel}"
            )

            val trackingChain = trackingService.buildPurchaseEventChain(finalizedOrder)
            repository.saveOrderAndSyncInventory(finalizedOrder, activeCart, trackingChain)
            repository.clearActiveCart()
            _appliedCoupon.value = null
            _lastCreatedOrder.value = finalizedOrder
            showBanner("Order #$orderNumber Confirmed! Consignment: ${shipment.consignmentId}")
        }
    }

    fun dismissLastOrderModal() {
        _lastCreatedOrder.value = null
    }

    // Admin & Courier Actions
    fun advanceOrderStatus(order: OrderEntity) {
        val statusSequence = listOf(
            "Pending",
            "Confirmed",
            "Processing",
            "Packed",
            "Shipped",
            "Out for Delivery",
            "Delivered"
        )
        val currentIndex = statusSequence.indexOf(order.orderStatus)
        val nextStatus = if (currentIndex in 0 until statusSequence.lastIndex) {
            statusSequence[currentIndex + 1]
        } else {
            "Delivered"
        }
        viewModelScope.launch {
            val updated = order.copy(
                orderStatus = nextStatus,
                timelineSummary = "${order.timelineSummary} • Status updated to $nextStatus"
            )
            repository.updateOrder(updated)
            showBanner("Order #${order.orderNumber} moved to $nextStatus")
        }
    }

    fun triggerCourierWebhookSync(order: OrderEntity) {
        viewModelScope.launch {
            val sync = courierManager.syncWebhookStatus(order)
            val nextStatus = if (order.orderStatus == "Confirmed" || order.orderStatus == "Packed") "Shipped" else "Out for Delivery"
            repository.updateOrder(
                order.copy(
                    orderStatus = nextStatus,
                    timelineSummary = "${order.timelineSummary} • ${sync.updatedTimeline} (Rider: ${sync.riderName} ${sync.riderPhone})"
                )
            )
            showBanner("Webhook synced ${order.consignmentId} via ${order.courierProvider}")
        }
    }

    fun bulkBookCourierForPendingOrders() {
        viewModelScope.launch {
            val provider = getSettingValue("default_courier", "Steadfast")
            val targetOrders = orders.value.filter { it.orderStatus in listOf("Pending", "Confirmed", "Processing") }
            targetOrders.forEach { order ->
                val shipment = courierManager.createShipmentWithProvider(order, provider)
                repository.updateOrder(
                    order.copy(
                        courierProvider = provider,
                        consignmentId = shipment.consignmentId,
                        orderStatus = "Shipped",
                        timelineSummary = "${order.timelineSummary} • Bulk Dispatched via $provider (${shipment.consignmentId})"
                    )
                )
            }
            showBanner("Bulk booked ${targetOrders.size} orders with $provider Courier!")
        }
    }

    fun restockProduct(product: ProductEntity, addQty: Int = 50) {
        viewModelScope.launch {
            repository.addProductStock(product.id, addQty)
            showBanner("Restocked +$addQty units for ${product.sku}")
        }
    }

    fun createReturnRequest(
        orderNumber: String,
        customerName: String,
        customerPhone: String,
        productName: String,
        reason: String,
        refundAmount: Int
    ) {
        viewModelScope.launch {
            repository.submitReturnRequest(
                ReturnRequestEntity(
                    orderNumber = orderNumber,
                    customerName = customerName,
                    customerPhone = customerPhone,
                    productName = productName,
                    reason = reason,
                    status = "Requested",
                    refundAmountBdt = refundAmount,
                    resolutionType = "Pending Admin Inspection & Courier Pickup",
                    createdAtLabel = "Just now"
                )
            )
            showBanner("Return request submitted for Order #$orderNumber")
        }
    }

    fun advanceReturnStatus(request: ReturnRequestEntity) {
        val sequence = listOf("Requested", "Approved", "Pickup", "Received", "Inspection", "Refund Approved", "Refunded")
        val idx = sequence.indexOf(request.status)
        val next = if (idx in 0 until sequence.lastIndex) sequence[idx + 1] else "Refunded"
        viewModelScope.launch {
            repository.updateReturnRequest(request.copy(status = next))
            showBanner("Return #${request.orderNumber} updated to $next")
        }
    }

    fun requestPartnerPayout(account: PartnerAccountEntity) {
        if (account.walletBalanceBdt <= 0) {
            showBanner("No available wallet balance for withdrawal.")
            return
        }
        viewModelScope.launch {
            val withdrawn = account.walletBalanceBdt
            repository.updatePartnerAccount(
                account.copy(
                    walletBalanceBdt = 0,
                    pendingCommissionBdt = account.pendingCommissionBdt + withdrawn
                )
            )
            showBanner("Withdrawal of ৳$withdrawn requested to bKash (${account.phone})")
        }
    }

    fun updateSetting(key: String, newValue: String) {
        viewModelScope.launch {
            repository.updateSetting(key, newValue)
            showBanner("Updated setting '$key' -> $newValue")
        }
    }

    fun rotateHomepageSectionOrder() {
        val current = getSettingValue(
            "homepage_section_order",
            "FLASH_SALE,BESTSELLERS,WHOLESALE_TIERS,GROCERY,HEALTHCARE,KITCHEN,ELECTRONICS,BUNDLES,REVIEWS,BLOG"
        )
        val parts = current.split(",").filter { it.isNotBlank() }.toMutableList()
        if (parts.size > 1) {
            val first = parts.removeAt(0)
            parts.add(first)
            updateSetting("homepage_section_order", parts.joinToString(","))
        }
    }

    // AI Assistant & Content Generator
    fun sendAiSupportMessage(userText: String) {
        if (userText.isBlank()) return
        val trimmed = userText.trim()
        _aiMessages.value = _aiMessages.value + AiChatMessage("USER", trimmed)
        _isAiLoading.value = true

        viewModelScope.launch {
            val insideRate = getSettingInt("inside_dhaka_charge", 60)
            val outsideRate = getSettingInt("outside_dhaka_charge", 120)
            val reply = aiService.answerCustomerQuery(
                userQuery = trimmed,
                products = products.value,
                orders = orders.value,
                insideDhakaCharge = insideRate,
                outsideDhakaCharge = outsideRate
            )
            _aiMessages.value = _aiMessages.value + AiChatMessage("AI", reply)
            _isAiLoading.value = false
        }
    }

    fun generateAiSeoForProduct(product: ProductEntity) {
        _isAiLoading.value = true
        viewModelScope.launch {
            val pack = aiService.generateProductSeoAndCopy(product)
            _generatedSeoPack.value = pack
            _isAiLoading.value = false
            showBanner("Generated AI SEO & Ad Copy for ${product.sku}")
        }
    }

    fun applyGeneratedSeoToProduct(product: ProductEntity, pack: AiGeneratedSeoPack) {
        viewModelScope.launch {
            repository.updateProduct(
                product.copy(
                    seoTitle = pack.seoTitle,
                    metaDescription = pack.metaDescription
                )
            )
            showBanner("Applied validated AI SEO metadata to ${product.name}")
        }
    }

    fun clearBanner() {
        _bannerMessage.value = null
    }

    private fun showBanner(msg: String) {
        _bannerMessage.value = msg
    }

    fun getSettingValue(key: String, default: String): String {
        return platformSettings.value.firstOrNull { it.settingKey == key }?.settingValue ?: default
    }

    private fun getSettingInt(key: String, default: Int): Int {
        return getSettingValue(key, default.toString()).toIntOrNull() ?: default
    }
}
