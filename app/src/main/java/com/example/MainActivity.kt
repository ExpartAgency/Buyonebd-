package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppNavTab
import com.example.ui.BuyOneViewModel
import com.example.ui.screens.AdminErpAndAiScreen
import com.example.ui.screens.B2bAndOrderTrackingScreen
import com.example.ui.screens.BlogPostDetailModal
import com.example.ui.screens.CartAndCheckoutScreen
import com.example.ui.screens.CatalogSearchScreen
import com.example.ui.screens.ProductDetailFullScreen
import com.example.ui.screens.StorefrontHomeScreen
import com.example.ui.theme.BuyOneBDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuyOneBDTheme {
                BuyOneBdMainApp()
            }
        }
    }
}

@Composable
fun BuyOneBdMainApp(
    viewModel: BuyOneViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val activeOrderMode by viewModel.activeOrderMode.collectAsStateWithLifecycle()
    val allProducts by viewModel.products.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val warehouses by viewModel.warehouses.collectAsStateWithLifecycle()
    val coupons by viewModel.coupons.collectAsStateWithLifecycle()
    val appliedCoupon by viewModel.appliedCoupon.collectAsStateWithLifecycle()
    val customersCrm by viewModel.customersCrm.collectAsStateWithLifecycle()
    val partnerAccounts by viewModel.partnerAccounts.collectAsStateWithLifecycle()
    val returnRequests by viewModel.returnRequests.collectAsStateWithLifecycle()
    val blogPosts by viewModel.blogPosts.collectAsStateWithLifecycle()
    val trackingEvents by viewModel.trackingEvents.collectAsStateWithLifecycle()
    val settings by viewModel.platformSettings.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val onlyFlashSale by viewModel.onlyFlashSale.collectAsStateWithLifecycle()

    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val selectedBlogPost by viewModel.selectedBlogPost.collectAsStateWithLifecycle()
    val bannerMessage by viewModel.bannerMessage.collectAsStateWithLifecycle()
    val lastCreatedOrder by viewModel.lastCreatedOrder.collectAsStateWithLifecycle()

    val aiMessages by viewModel.aiMessages.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val generatedSeoPack by viewModel.generatedSeoPack.collectAsStateWithLifecycle()

    val activeCartCount = cartItems.filter { !it.savedForLater }.sumOf { it.quantity }

    val sectionOrderCsv = settings.firstOrNull { it.settingKey == "homepage_section_order" }?.settingValue
        ?: "FLASH_SALE,BESTSELLERS,WHOLESALE_TIERS,GROCERY,HEALTHCARE,KITCHEN,ELECTRONICS,BUNDLES,REVIEWS,BLOG"
    val freeShippingThreshold = settings.firstOrNull { it.settingKey == "free_shipping_threshold" }?.settingValue?.toIntOrNull() ?: 2000
    val insideDhakaRate = settings.firstOrNull { it.settingKey == "inside_dhaka_charge" }?.settingValue?.toIntOrNull() ?: 60
    val outsideDhakaRate = settings.firstOrNull { it.settingKey == "outside_dhaka_charge" }?.settingValue?.toIntOrNull() ?: 120

    // BackHandler for secondary tabs
    if (currentTab != AppNavTab.HOME && selectedProduct == null && selectedBlogPost == null) {
        BackHandler {
            viewModel.selectTab(AppNavTab.HOME)
        }
    }

    // Full-screen Modals
    if (selectedProduct != null) {
        ProductDetailFullScreen(
            product = selectedProduct!!,
            activeOrderMode = activeOrderMode,
            onClose = { viewModel.closeProductDetail() },
            onToggleWishlist = { viewModel.toggleWishlist(it) },
            onAddToCart = { prod, variant, qty, customResellerPrice ->
                viewModel.addToCart(prod, variant, qty, customResellerPrice, navigateToCart = false)
            },
            onBuyNow = { prod, variant, qty, customResellerPrice ->
                viewModel.addToCart(prod, variant, qty, customResellerPrice, navigateToCart = true)
            }
        )
        return
    }

    if (selectedBlogPost != null) {
        BlogPostDetailModal(
            post = selectedBlogPost!!,
            onClose = { viewModel.openBlogPost(null) }
        )
        return
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == AppNavTab.HOME,
                    onClick = { viewModel.selectTab(AppNavTab.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text(AppNavTab.HOME.title) },
                    modifier = Modifier.testTag("nav_tab_home")
                )
                NavigationBarItem(
                    selected = currentTab == AppNavTab.SEARCH,
                    onClick = { viewModel.selectTab(AppNavTab.SEARCH) },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Catalog & Search") },
                    label = { Text(AppNavTab.SEARCH.title) },
                    modifier = Modifier.testTag("nav_tab_search")
                )
                NavigationBarItem(
                    selected = currentTab == AppNavTab.CART,
                    onClick = { viewModel.selectTab(AppNavTab.CART) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (activeCartCount > 0) {
                                    Badge { Text(activeCartCount.toString()) }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart & Checkout")
                        }
                    },
                    label = { Text(AppNavTab.CART.title) },
                    modifier = Modifier.testTag("nav_tab_cart")
                )
                NavigationBarItem(
                    selected = currentTab == AppNavTab.B2B_HUB,
                    onClick = { viewModel.selectTab(AppNavTab.B2B_HUB) },
                    icon = { Icon(Icons.Default.LocalShipping, contentDescription = "B2B & Order Tracking") },
                    label = { Text(AppNavTab.B2B_HUB.title) },
                    modifier = Modifier.testTag("nav_tab_b2b")
                )
                NavigationBarItem(
                    selected = currentTab == AppNavTab.ADMIN,
                    onClick = { viewModel.selectTab(AppNavTab.ADMIN) },
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin & AI") },
                    label = { Text(AppNavTab.ADMIN.title) },
                    modifier = Modifier.testTag("nav_tab_admin")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppNavTab.HOME -> {
                    StorefrontHomeScreen(
                        products = allProducts,
                        categories = categories,
                        blogPosts = blogPosts,
                        activeOrderMode = activeOrderMode,
                        sectionOrderCsv = sectionOrderCsv,
                        onSwitchOrderMode = { viewModel.setOrderMode(it) },
                        onSelectCategoryAndNavigate = { cat ->
                            viewModel.selectCategory(cat)
                            viewModel.selectTab(AppNavTab.SEARCH)
                        },
                        onQuickSearchQuery = { query ->
                            viewModel.updateSearchQuery(query)
                            viewModel.selectTab(AppNavTab.SEARCH)
                        },
                        onProductClick = { viewModel.openProductDetail(it) },
                        onAddToCart = { viewModel.addToCart(it, navigateToCart = false) },
                        onBuyNow = { viewModel.addToCart(it, navigateToCart = true) },
                        onToggleWishlist = { viewModel.toggleWishlist(it) },
                        onBlogClick = { viewModel.openBlogPost(it) },
                        onOpenB2bHub = { viewModel.selectTab(AppNavTab.B2B_HUB) },
                        onOpenAiAssistant = { viewModel.selectTab(AppNavTab.ADMIN) },
                        onRotateSections = { viewModel.rotateHomepageSectionOrder() }
                    )
                }

                AppNavTab.SEARCH -> {
                    CatalogSearchScreen(
                        products = filteredProducts,
                        categories = categories,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        onlyFlashSale = onlyFlashSale,
                        activeOrderMode = activeOrderMode,
                        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                        onSelectCategory = { viewModel.selectCategory(it) },
                        onToggleFlashSale = { viewModel.toggleFlashSaleFilter() },
                        onProductClick = { viewModel.openProductDetail(it) },
                        onAddToCart = { viewModel.addToCart(it, navigateToCart = false) },
                        onBuyNow = { viewModel.addToCart(it, navigateToCart = true) },
                        onToggleWishlist = { viewModel.toggleWishlist(it) }
                    )
                }

                AppNavTab.CART -> {
                    CartAndCheckoutScreen(
                        cartItems = cartItems,
                        allProducts = allProducts,
                        coupons = coupons,
                        appliedCoupon = appliedCoupon,
                        activeOrderMode = activeOrderMode,
                        freeShippingThreshold = freeShippingThreshold,
                        insideDhakaRate = insideDhakaRate,
                        outsideDhakaRate = outsideDhakaRate,
                        lastCreatedOrder = lastCreatedOrder,
                        calculateUnitPrice = { viewModel.calculateCartItemUnitPrice(it) },
                        onUpdateQuantity = { item, qty -> viewModel.updateCartItemQuantity(item, qty) },
                        onToggleSaveForLater = { viewModel.toggleSaveForLater(it) },
                        onRemoveCartItem = { viewModel.removeCartItem(it) },
                        onApplyCoupon = { code, sub -> viewModel.applyCouponCode(code, sub) },
                        onRemoveCoupon = { viewModel.removeCoupon() },
                        onQuickAddUpsell = { viewModel.addToCart(it, navigateToCart = false) },
                        onPlaceOrder = { name, phone, email, div, dist, upz, area, addr, note, pay, aff ->
                            viewModel.placeBangladeshOrder(
                                customerName = name,
                                customerPhone = phone,
                                customerEmail = email,
                                division = div,
                                district = dist,
                                upazila = upz,
                                area = area,
                                fullAddress = addr,
                                orderNote = note,
                                paymentMethod = pay,
                                affiliateCode = aff
                            )
                        },
                        onDismissOrderModal = { viewModel.dismissLastOrderModal() },
                        onNavigateToTrackOrder = { viewModel.selectTab(AppNavTab.B2B_HUB) }
                    )
                }

                AppNavTab.B2B_HUB -> {
                    B2bAndOrderTrackingScreen(
                        orders = orders,
                        products = allProducts,
                        partnerAccounts = partnerAccounts,
                        returnRequests = returnRequests,
                        onSyncCourierWebhook = { viewModel.triggerCourierWebhookSync(it) },
                        onSubmitReturnRequest = { ordNum, cName, cPhone, pName, reason, amt ->
                            viewModel.createReturnRequest(ordNum, cName, cPhone, pName, reason, amt)
                        },
                        onRequestPartnerPayout = { viewModel.requestPartnerPayout(it) },
                        onAddResellerOrderToCart = { prod, qty, customPrice ->
                            viewModel.setOrderMode("RESELLER")
                            viewModel.addToCart(prod, quantity = qty, customResellerSellPrice = customPrice, navigateToCart = true)
                        },
                        onAddWholesaleTierToCart = { prod, qty ->
                            viewModel.setOrderMode("WHOLESALE")
                            viewModel.addToCart(prod, quantity = qty, navigateToCart = true)
                        }
                    )
                }

                AppNavTab.ADMIN -> {
                    AdminErpAndAiScreen(
                        products = allProducts,
                        orders = orders,
                        warehouses = warehouses,
                        customersCrm = customersCrm,
                        returnRequests = returnRequests,
                        trackingEvents = trackingEvents,
                        settings = settings,
                        aiMessages = aiMessages,
                        isAiLoading = isAiLoading,
                        generatedSeoPack = generatedSeoPack,
                        onAdvanceOrderStatus = { viewModel.advanceOrderStatus(it) },
                        onSyncCourierWebhook = { viewModel.triggerCourierWebhookSync(it) },
                        onBulkBookCourier = { viewModel.bulkBookCourierForPendingOrders() },
                        onRestockProduct = { viewModel.restockProduct(it) },
                        onAdvanceReturnStatus = { viewModel.advanceReturnStatus(it) },
                        onUpdateSetting = { k, v -> viewModel.updateSetting(k, v) },
                        onRotateHomepageSections = { viewModel.rotateHomepageSectionOrder() },
                        onSendAiMessage = { viewModel.sendAiSupportMessage(it) },
                        onGenerateAiSeo = { viewModel.generateAiSeoForProduct(it) },
                        onApplyAiSeo = { prod, pack -> viewModel.applyGeneratedSeoToProduct(prod, pack) }
                    )
                }
            }

            // Floating Notification Banner
            AnimatedVisibility(
                visible = bannerMessage != null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF042E1A),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.clearBanner() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Status",
                            tint = Color(0xFF34D399)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = bannerMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Dismiss",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFDE68A)
                        )
                    }
                }
            }
        }
    }
}
