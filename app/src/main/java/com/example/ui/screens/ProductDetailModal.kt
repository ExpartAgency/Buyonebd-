package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.data.local.BlogPostEntity
import com.example.data.local.ProductEntity

@Composable
fun ProductDetailFullScreen(
    product: ProductEntity,
    activeOrderMode: String,
    onClose: () -> Unit,
    onToggleWishlist: (ProductEntity) -> Unit,
    onAddToCart: (ProductEntity, String, Int, Int) -> Unit,
    onBuyNow: (ProductEntity, String, Int, Int) -> Unit
) {
    BackHandler { onClose() }

    val variants = remember(product.variantsCsv) {
        product.variantsCsv.split("|").filter { it.isNotBlank() }.ifEmpty { listOf("Standard Pack") }
    }
    var selectedVariant by remember(product.id) { mutableStateOf(variants.first()) }
    var quantity by remember(product.id, activeOrderMode) {
        mutableIntStateOf(if (activeOrderMode == "WHOLESALE") 5 else 1)
    }
    var customResellerPriceText by remember(product.id) {
        mutableStateOf(product.salePrice.toString())
    }

    val effectiveUnitPrice = when (activeOrderMode) {
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

    val customResellerSellPrice = customResellerPriceText.toIntOrNull() ?: product.salePrice
    val resellerMarginPerUnit = (customResellerSellPrice - product.resellerPrice).coerceAtLeast(0)

    Scaffold(
        bottomBar = {
            // Sticky Mobile Bottom Bar: BUY NOW | ADD TO CART
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 10.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(0.85f)) {
                        Text(
                            text = "মোট (x$quantity)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "৳${effectiveUnitPrice * quantity}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onAddToCart(product, selectedVariant, quantity, customResellerSellPrice)
                        },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 12.dp),
                        modifier = Modifier
                            .weight(1.1f)
                            .testTag("sticky_add_to_cart_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ADD TO CART", style = MaterialTheme.typography.labelLarge)
                    }

                    Button(
                        onClick = {
                            onBuyNow(product, selectedVariant, quantity, customResellerSellPrice)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD97706),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 12.dp),
                        modifier = Modifier
                            .weight(1.1f)
                            .testTag("sticky_buy_now_btn")
                    ) {
                        Text("BUY NOW", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onClose,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Shop"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Back to Shop")
                    }

                    IconButton(onClick = { onToggleWishlist(product) }) {
                        Icon(
                            imageVector = if (product.isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (product.isWishlisted) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Product Header Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
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
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${product.category} • ${product.subcategory}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Stock: ${product.stock} Ready (${product.reservedStock} Reserved)",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (product.stock <= product.lowStockThreshold) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = product.banglaName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "৳$effectiveUnitPrice",
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (product.retailPrice > effectiveUnitPrice) {
                                Text(
                                    text = "৳${product.retailPrice}",
                                    style = MaterialTheme.typography.titleMedium,
                                    textDecoration = TextDecoration.LineThrough,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Save ৳${product.retailPrice - effectiveUnitPrice}/unit",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "SKU: ${product.sku}  |  Barcode: ${product.barcode}  |  Brand: ${product.brand}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Variant & Quantity Selector
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("প্যাক সাইজ / ভ্যারিয়েন্ট (Select Variant):", style = MaterialTheme.typography.titleSmall)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            variants.forEach { variant ->
                                FilterChip(
                                    selected = selectedVariant == variant,
                                    onClick = { selectedVariant = variant },
                                    label = { Text(variant) }
                                )
                            }
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("পরিমাণ (Quantity):", style = MaterialTheme.typography.titleSmall)
                                Text(
                                    text = "5+ pcs unlocks Wholesale Tier-1 (৳${product.wholesaleTier1Price})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { if (quantity > 1) quantity-- },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                }
                                Text(
                                    text = quantity.toString(),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                OutlinedButton(
                                    onClick = { quantity++ },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase")
                                }
                            }
                        }

                        // Wholesale Tier Table
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                Triple("1–4 Retail", "৳${product.salePrice}", quantity in 1..4),
                                Triple("5–19 Tier 1", "৳${product.wholesaleTier1Price}", quantity in 5..19),
                                Triple("20–49 Tier 2", "৳${product.wholesaleTier2Price}", quantity in 20..49),
                                Triple("50+ Tier 3", "৳${product.wholesaleTier3Price}", quantity >= 50)
                            ).forEach { (tierLabel, priceStr, isActive) ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = tierLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = priceStr,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Reseller Custom Selling Price Box when in RESELLER mode
                        if (activeOrderMode == "RESELLER") {
                            HorizontalDivider()
                            Text(
                                text = "Reseller Dropship Margin Calculator",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Your Reseller Base Cost: ৳${product.resellerPrice}/unit • Suggested Retail: ৳${product.salePrice}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            OutlinedTextField(
                                value = customResellerPriceText,
                                onValueChange = { customResellerPriceText = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Customer Invoice Price per Unit (BDT)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "Your Instant Reseller Commission on this Order: ৳${resellerMarginPerUnit * quantity}",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                }
            }

            // Warranty, Guarantee & Delivery Estimate
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Warranty",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${product.warranty} • ${product.guarantee}",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = "Delivery",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Inside Dhaka: ৳60 (24h) | Sub-Dhaka: ৳100 | Outside Dhaka: ৳120 (Steadfast / Pathao / RedX)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Description, Specs, Features & SEO Schema
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("প্রোডাক্টের বিস্তারিত বিবরণ (Description)", style = MaterialTheme.typography.titleMedium)
                        Text(product.description, style = MaterialTheme.typography.bodyMedium)

                        HorizontalDivider()
                        Text("স্পেসিফিকেশন (Specifications)", style = MaterialTheme.typography.titleSmall)
                        Text(product.specifications, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "Weight: ${product.weightKg} kg • Dimensions: ${product.dimensions}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider()
                        Text("বৈশিষ্ট্য ও উপকারিতা (Features & Benefits)", style = MaterialTheme.typography.titleSmall)
                        Text("• ${product.features}", style = MaterialTheme.typography.bodyMedium)
                        Text("• ${product.benefits}", style = MaterialTheme.typography.bodyMedium)

                        HorizontalDivider()
                        Text("SEO & Schema Metadata", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text("SEO Title: ${product.seoTitle}", style = MaterialTheme.typography.bodySmall)
                        Text("Meta Description: ${product.metaDescription}", style = MaterialTheme.typography.bodySmall)
                        Text("Canonical URL: ${product.canonicalUrl}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun BlogPostDetailModal(
    post: BlogPostEntity,
    onClose: () -> Unit
) {
    BackHandler { onClose() }

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedButton(onClick = onClose, shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Back to Storefront")
                }
            }
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "${post.category} • ${post.publishedDate} • ${post.readTimeMin} min read",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(post.title, style = MaterialTheme.typography.headlineMedium)
                        Text(
                            post.banglaSubtitle,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Author: ${post.author}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        HorizontalDivider()
                        Text(post.content, style = MaterialTheme.typography.bodyLarge)
                        HorizontalDivider()
                        Text("FAQ Schema:", style = MaterialTheme.typography.titleSmall)
                        Text(post.faqSummary, style = MaterialTheme.typography.bodyMedium)
                        HorizontalDivider()
                        Text("SEO Slug: /blog/${post.slug}", style = MaterialTheme.typography.labelSmall)
                        Text("SEO Title: ${post.seoTitle}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
