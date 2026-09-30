package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.BlogPostEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.ProductEntity

@Composable
fun StorefrontHomeScreen(
    products: List<ProductEntity>,
    categories: List<CategoryEntity>,
    blogPosts: List<BlogPostEntity>,
    activeOrderMode: String,
    sectionOrderCsv: String,
    onSwitchOrderMode: (String) -> Unit,
    onSelectCategoryAndNavigate: (String) -> Unit,
    onQuickSearchQuery: (String) -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onBuyNow: (ProductEntity) -> Unit,
    onToggleWishlist: (ProductEntity) -> Unit,
    onBlogClick: (BlogPostEntity) -> Unit,
    onOpenB2bHub: () -> Unit,
    onOpenAiAssistant: () -> Unit,
    onRotateSections: () -> Unit
) {
    val sectionKeys = sectionOrderCsv.split(",").map { it.trim() }.filter { it.isNotBlank() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("storefront_home_list"),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Announcement Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF063E24),
                                Color(0xFF0A5C36),
                                Color(0xFF0F766E)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = "Express Shipping",
                            tint = Color(0xFFFDE68A),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ঢাকায় ২৪ ঘণ্টায় ডেলিভারি • Free Shipping ৳2,000+ • Code: BUYONE10",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // 2. Brand Header & Pricing Mode Switcher (Retail / Wholesale / Reseller)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "BuyOneBD",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "BD",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Better Products • Better Life",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenAiAssistant,
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("home_ai_support_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "AI Support",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI সাপোর্ট",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                // Business Model Mode Switcher
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(
                            "RETAIL" to "খুচরা (Retail)",
                            "WHOLESALE" to "পাইকারি (Wholesale)",
                            "RESELLER" to "রিসেলার (Reseller)"
                        ).forEach { (modeKey, label) ->
                            val selected = activeOrderMode == modeKey
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSwitchOrderMode(modeKey) }
                                    .testTag("mode_switch_$modeKey")
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Search Bar & Bangla/Banglish Quick Chips
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .clickable { onQuickSearchQuery("") }
                        .testTag("home_search_bar_trigger")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Products",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "খুঁজুন: জিরা গুঁড়া, jira gura, Omron BP, মধু...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickQueries = listOf(
                        "জিরা গুঁড়া",
                        "jira gura",
                        "Omron BP",
                        "খাঁটি মধু",
                        "Masala Grinder",
                        "Combo Offer"
                    )
                    items(quickQueries) { q ->
                        FilterChip(
                            selected = false,
                            onClick = { onQuickSearchQuery(q) },
                            label = { Text(q, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }
        }

        // 4. Hero Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = "BuyOneBD Hero Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xEE042E1A),
                                        Color(0xBB063E24),
                                        Color(0x33000000)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                color = Color(0xFFFDE68A),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "100% LAB TESTED • DIRECT SOURCING",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF451A03),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "খাঁটি পণ্য, সুস্থ জীবন\nBetter Products • Better Life",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Retail • Wholesale Tier Pricing • Reseller Dropshipping",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFD9F5E6)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { onSelectCategoryAndNavigate("All") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFD97706),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("এখনই কিনুন (Shop Now)", style = MaterialTheme.typography.labelLarge)
                                }
                                OutlinedButton(
                                    onClick = onOpenB2bHub,
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Wholesale / Reseller", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Featured Categories Row
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "জনপ্রিয় ক্যাটাগরি (Categories)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(
                        onClick = onRotateSections,
                        modifier = Modifier.testTag("reorder_sections_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Reorder Dynamic Homepage Sections",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { cat ->
                        ElevatedCard(
                            onClick = { onSelectCategoryAndNavigate(cat.name) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(136.dp)
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Storefront,
                                            contentDescription = cat.name,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = cat.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${cat.banglaName} (${cat.productCount})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dynamic Database-Driven Sections (Admin Reorderable)
        sectionKeys.forEach { sectionKey ->
            when (sectionKey) {
                "FLASH_SALE" -> item(key = "sec_flash") {
                    FlashSaleSection(
                        products = products.filter { it.isFlashSale },
                        activeOrderMode = activeOrderMode,
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart,
                        onBuyNow = onBuyNow,
                        onToggleWishlist = onToggleWishlist
                    )
                }
                "BESTSELLERS" -> item(key = "sec_bestsellers") {
                    ProductCarouselSection(
                        title = "বেস্ট সেলিং প্রোডাক্ট (Best Sellers)",
                        subtitle = "Most ordered across all 64 districts this week",
                        products = products.filter { it.isBestseller },
                        activeOrderMode = activeOrderMode,
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart,
                        onBuyNow = onBuyNow,
                        onToggleWishlist = onToggleWishlist
                    )
                }
                "WHOLESALE_TIERS" -> item(key = "sec_wholesale") {
                    BuyMoreSaveMoreBanner(
                        onSwitchToWholesale = { onSwitchOrderMode("WHOLESALE") },
                        onOpenB2bHub = onOpenB2bHub
                    )
                }
                "GROCERY" -> item(key = "sec_grocery") {
                    ProductCarouselSection(
                        title = "গ্রোসারি ও খাঁটি মশলা (Grocery & Spices)",
                        subtitle = "Stone-ground spices & raw Sundarban honey",
                        products = products.filter { it.category == "Grocery & Spices" },
                        activeOrderMode = activeOrderMode,
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart,
                        onBuyNow = onBuyNow,
                        onToggleWishlist = onToggleWishlist
                    )
                }
                "HEALTHCARE" -> item(key = "sec_healthcare") {
                    ProductCarouselSection(
                        title = "হেলথকেয়ার ও মেডিকেল (Healthcare)",
                        subtitle = "Clinically validated home diagnostics with official warranty",
                        products = products.filter { it.category == "Healthcare" },
                        activeOrderMode = activeOrderMode,
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart,
                        onBuyNow = onBuyNow,
                        onToggleWishlist = onToggleWishlist
                    )
                }
                "KITCHEN" -> item(key = "sec_kitchen") {
                    ProductCarouselSection(
                        title = "হোম ও কিচেন (Home & Kitchen)",
                        subtitle = "Heavy-duty kitchen appliances for Bangladeshi homes",
                        products = products.filter { it.category == "Home & Kitchen" },
                        activeOrderMode = activeOrderMode,
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart,
                        onBuyNow = onBuyNow,
                        onToggleWishlist = onToggleWishlist
                    )
                }
                "ELECTRONICS" -> item(key = "sec_electronics") {
                    ProductCarouselSection(
                        title = "ইলেকট্রনিক্স ও গ্যাজেট (Electronics)",
                        subtitle = "Smart wearables with Bangla font notification support",
                        products = products.filter { it.category == "Electronics & Gadgets" },
                        activeOrderMode = activeOrderMode,
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart,
                        onBuyNow = onBuyNow,
                        onToggleWishlist = onToggleWishlist
                    )
                }
                "BUNDLES" -> item(key = "sec_bundles") {
                    ProductCarouselSection(
                        title = "বান্ডেল ও কম্বো অফার (Bundle Offers)",
                        subtitle = "Monthly family packs with automatic free delivery",
                        products = products.filter { it.category == "Bundle & Combo" },
                        activeOrderMode = activeOrderMode,
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart,
                        onBuyNow = onBuyNow,
                        onToggleWishlist = onToggleWishlist
                    )
                }
                "REVIEWS" -> item(key = "sec_reviews") {
                    TrustBadgesAndReviewsSection()
                }
                "BLOG" -> item(key = "sec_blog") {
                    BlogAndSeoSection(blogPosts = blogPosts, onBlogClick = onBlogClick)
                }
            }
        }
    }
}

@Composable
private fun FlashSaleSection(
    products: List<ProductEntity>,
    activeOrderMode: String,
    onProductClick: (ProductEntity) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onBuyNow: (ProductEntity) -> Unit,
    onToggleWishlist: (ProductEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f))
            .padding(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Flash Sale",
                    tint = Color(0xFFD97706)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "ফ্ল্যাশ সেল (Flash Sale)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "সীমিত সময়ের ধামাকা ডিসকাউন্ট",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Surface(
                color = Color(0xFFDC2626),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "04h : 38m : 19s",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products, key = { "flash_${it.id}" }) { product ->
                BuyOneProductCard(
                    product = product,
                    activeOrderMode = activeOrderMode,
                    onProductClick = { onProductClick(product) },
                    onAddToCart = { onAddToCart(product) },
                    onBuyNow = { onBuyNow(product) },
                    onToggleWishlist = { onToggleWishlist(product) }
                )
            }
        }
    }
}

@Composable
fun ProductCarouselSection(
    title: String,
    subtitle: String,
    products: List<ProductEntity>,
    activeOrderMode: String,
    onProductClick: (ProductEntity) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onBuyNow: (ProductEntity) -> Unit,
    onToggleWishlist: (ProductEntity) -> Unit
) {
    if (products.isEmpty()) return
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products, key = { "${title}_${it.id}" }) { product ->
                BuyOneProductCard(
                    product = product,
                    activeOrderMode = activeOrderMode,
                    onProductClick = { onProductClick(product) },
                    onAddToCart = { onAddToCart(product) },
                    onBuyNow = { onBuyNow(product) },
                    onToggleWishlist = { onToggleWishlist(product) }
                )
            }
        }
    }
}

@Composable
fun BuyOneProductCard(
    product: ProductEntity,
    activeOrderMode: String,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit,
    onToggleWishlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val discountPercent = if (product.retailPrice > product.salePrice) {
        ((product.retailPrice - product.salePrice) * 100) / product.retailPrice
    } else 0

    val displayedPrice = when (activeOrderMode) {
        "WHOLESALE" -> product.wholesaleTier1Price
        "RESELLER" -> product.resellerPrice
        else -> product.salePrice
    }

    ElevatedCard(
        onClick = onProductClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .width(228.dp)
            .testTag("product_card_${product.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Badges + Wishlist Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (discountPercent > 0) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (discountPercent > 0) "-$discountPercent% OFF" else product.brand,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }

                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (product.isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle Wishlist",
                        tint = if (product.isWishlisted) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Product Visual Header Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                            )
                        )
                    )
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = product.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = product.banglaName,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SKU: ${product.sku}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${product.rating} (${product.reviewCount})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Product Title
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(40.dp)
            )

            // Pricing Row (adapts to Retail / Wholesale / Reseller)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "৳$displayedPrice",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (product.retailPrice > displayedPrice) {
                        Text(
                            text = "৳${product.retailPrice}",
                            style = MaterialTheme.typography.bodySmall,
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = when (activeOrderMode) {
                        "WHOLESALE" -> "Tier-1 (5+): ৳${product.wholesaleTier1Price} • 50+: ৳${product.wholesaleTier3Price}"
                        "RESELLER" -> "Reseller Base: ৳${product.resellerPrice} (Sug. Retail ৳${product.salePrice})"
                        else -> "Wholesale 5+ pcs: ৳${product.wholesaleTier1Price}/pc"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Action Buttons: Add to Cart + Buy Now
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onAddToCart,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_cart_${product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Add to Cart",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("কার্ট", style = MaterialTheme.typography.labelMedium)
                }

                Button(
                    onClick = onBuyNow,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("buy_now_${product.id}")
                ) {
                    Text("Buy Now", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun BuyMoreSaveMoreBanner(
    onSwitchToWholesale: () -> Unit,
    onOpenB2bHub: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Buy More • Save More (পাইকারি টিয়ার প্রাইসিং)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Automatic quantity tier discounts in Cart & Checkout",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("1–4 Units", "Retail Offer", "Up to 22% Off"),
                    Triple("5–19 Units", "Wholesale Tier 1", "Extra 8% Off"),
                    Triple("20–49 Units", "Wholesale Tier 2", "Extra 14% Off"),
                    Triple("50+ Units", "Wholesale Tier 3", "Max Factory Margin")
                ).forEach { (qty, tier, benefit) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = qty,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = tier,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = benefit,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onSwitchToWholesale,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Activate Wholesale Pricing", style = MaterialTheme.typography.labelMedium)
                }
                OutlinedButton(
                    onClick = onOpenB2bHub,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reseller & Affiliate Portal", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun TrustBadgesAndReviewsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "কেন BuyOneBD সেরা? (Trust & Customer Reviews)",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Triple(Icons.Default.Verified, "BSTI & Lab Tested", "100% Authentic"),
                Triple(Icons.Default.LocalShipping, "Steadfast / Pathao", "64 Districts COD"),
                Triple(Icons.Default.Security, "7-Day Return", "Easy bKash Refund")
            ).forEach { (icon, title, sub) ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = sub,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Buyer",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Farhana Rahman • Dhanmondi, Dhaka",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Text(
                        text = "★★★★★ 5.0",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFD97706),
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "“Omron HEM-7120 মেশিনটি এবং জিরা গুঁড়া অর্ডার করেছিলাম। ২৪ ঘণ্টার মধ্যে Steadfast কুরিয়ারে বাসায় পৌঁছে গেছে। প্রোডাক্ট ১০০% অরিজিনাল!”",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun BlogAndSeoSection(
    blogPosts: List<BlogPostEntity>,
    onBlogClick: (BlogPostEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "ব্লগ ও হেলথ গাইড (BuyOneBD Blog & SEO Guides)",
            style = MaterialTheme.typography.titleMedium
        )
        blogPosts.forEach { post ->
            ElevatedCard(
                onClick = { onBlogClick(post) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${post.category} • ${post.readTimeMin} min read",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = post.publishedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = post.title,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = post.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Read Full Guide & Schema",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Read Blog",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
