package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.CategoryEntity
import com.example.data.local.ProductEntity

@Composable
fun CatalogSearchScreen(
    products: List<ProductEntity>,
    categories: List<CategoryEntity>,
    searchQuery: String,
    selectedCategory: String,
    onlyFlashSale: Boolean,
    activeOrderMode: String,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onToggleFlashSale: () -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onBuyNow: (ProductEntity) -> Unit,
    onToggleWishlist: (ProductEntity) -> Unit
) {
    var onlyWishlist by remember { mutableStateOf(false) }

    val displayedProducts = remember(products, onlyWishlist) {
        if (onlyWishlist) products.filter { it.isWishlisted } else products
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "সার্চ ও শপ ক্যাটালগ (Bangla • Banglish • English Search)",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    "জিরা গুঁড়া / jira gura / zira powder / omron / মধু...",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("catalog_search_input")
        )

        // Quick Typo-Tolerant Search Demo Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val synonymExamples = listOf(
                "জিরা গুঁড়া",
                "jira gura",
                "zira powder",
                "cumin powder",
                "omron machine",
                "খাঁটি মধু",
                "masala grinder"
            )
            items(synonymExamples) { syn ->
                FilterChip(
                    selected = searchQuery.equals(syn, ignoreCase = true),
                    onClick = { onSearchQueryChange(syn) },
                    label = { Text(syn, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        // Category + Flash Sale + Wishlist Filters
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == "All",
                    onClick = { onSelectCategory("All") },
                    label = { Text("All Categories") }
                )
            }
            item {
                FilterChip(
                    selected = onlyFlashSale,
                    onClick = onToggleFlashSale,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = "Flash Sale",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("Flash Sale") }
                )
            }
            item {
                FilterChip(
                    selected = onlyWishlist,
                    onClick = { onlyWishlist = !onlyWishlist },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Wishlist",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("Wishlist") }
                )
            }
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory.equals(category.name, ignoreCase = true),
                    onClick = { onSelectCategory(category.name) },
                    label = { Text(category.name) }
                )
            }
        }

        Text(
            text = "Showing ${displayedProducts.size} products • Mode: $activeOrderMode",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("catalog_results_list"),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(displayedProducts, key = { it.id }) { product ->
                CatalogProductRowCard(
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
private fun CatalogProductRowCard(
    product: ProductEntity,
    activeOrderMode: String,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit,
    onToggleWishlist: () -> Unit
) {
    val activePrice = when (activeOrderMode) {
        "WHOLESALE" -> product.wholesaleTier1Price
        "RESELLER" -> product.resellerPrice
        else -> product.salePrice
    }

    ElevatedCard(
        onClick = onProductClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = product.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "SKU: ${product.sku}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = product.banglaName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onToggleWishlist) {
                    Icon(
                        imageVector = if (product.isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (product.isWishlisted) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = product.shortDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "৳$activePrice",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (product.retailPrice > activePrice) {
                            Text(
                                text = "৳${product.retailPrice}",
                                style = MaterialTheme.typography.bodySmall,
                                textDecoration = TextDecoration.LineThrough,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = "Wholesale: ৳${product.wholesaleTier1Price} (5+) • ৳${product.wholesaleTier3Price} (50+) • Stock: ${product.stock}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${product.rating} (${product.reviewCount})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onAddToCart,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Add to Cart",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to Cart")
                }

                Button(
                    onClick = onBuyNow,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Buy Now")
                }
            }
        }
    }
}
