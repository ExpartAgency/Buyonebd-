package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.OrderEntity
import com.example.data.local.PartnerAccountEntity
import com.example.data.local.ProductEntity
import com.example.data.local.ReturnRequestEntity

@Composable
fun B2bAndOrderTrackingScreen(
    orders: List<OrderEntity>,
    products: List<ProductEntity>,
    partnerAccounts: List<PartnerAccountEntity>,
    returnRequests: List<ReturnRequestEntity>,
    onSyncCourierWebhook: (OrderEntity) -> Unit,
    onSubmitReturnRequest: (String, String, String, String, String, Int) -> Unit,
    onRequestPartnerPayout: (PartnerAccountEntity) -> Unit,
    onAddResellerOrderToCart: (ProductEntity, Int, Int) -> Unit,
    onAddWholesaleTierToCart: (ProductEntity, Int) -> Unit
) {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val subTabs = listOf(
        "Order Tracking & Returns",
        "Reseller Dropship Hub",
        "Wholesale Tier Portal",
        "Affiliate & Loyalty"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("b2b_tracking_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner Header
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_b2b_banner),
                        contentDescription = "BuyOneBD B2B & Courier Hub",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(145.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(145.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xEE042E1A), Color(0x99063E24))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "ORDER TRACKING • RESELLER • WHOLESALE • AFFILIATE",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFDE68A),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "কুরিয়ার ট্র্যাকিং এবং পাইকারি/রিসেলার পোর্টাল",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = "Live Steadfast, Pathao & RedX Consignment Tracking + B2B Margin Engine",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD9F5E6)
                            )
                        }
                    }
                }
            }
        }

        // Sub-Tab Selector
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(subTabs.size) { idx ->
                    FilterChip(
                        selected = selectedSubTab == idx,
                        onClick = { selectedSubTab = idx },
                        label = { Text(subTabs[idx]) }
                    )
                }
            }
        }

        when (selectedSubTab) {
            0 -> {
                item {
                    OrderTrackingAndReturnSection(
                        orders = orders,
                        returnRequests = returnRequests,
                        onSyncCourierWebhook = onSyncCourierWebhook,
                        onSubmitReturnRequest = onSubmitReturnRequest
                    )
                }
            }
            1 -> {
                item {
                    ResellerDashboardSection(
                        resellerAccount = partnerAccounts.firstOrNull { it.partnerType == "RESELLER" },
                        products = products,
                        onRequestPayout = onRequestPartnerPayout,
                        onAddResellerOrder = onAddResellerOrderToCart
                    )
                }
            }
            2 -> {
                item {
                    WholesalePortalSection(
                        wholesaleAccount = partnerAccounts.firstOrNull { it.partnerType == "WHOLESALE" },
                        products = products,
                        onAddWholesaleOrder = onAddWholesaleTierToCart
                    )
                }
            }
            3 -> {
                item {
                    AffiliateAndLoyaltySection(
                        affiliateAccount = partnerAccounts.firstOrNull { it.partnerType == "AFFILIATE" },
                        onRequestPayout = onRequestPartnerPayout
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderTrackingAndReturnSection(
    orders: List<OrderEntity>,
    returnRequests: List<ReturnRequestEntity>,
    onSyncCourierWebhook: (OrderEntity) -> Unit,
    onSubmitReturnRequest: (String, String, String, String, String, Int) -> Unit
) {
    var trackingQuery by remember { mutableStateOf("") }
    var returnOrderNum by remember { mutableStateOf(orders.firstOrNull()?.orderNumber ?: "BOBD-2026-9841") }
    var returnReason by remember { mutableStateOf("Variant / Size exchange request") }

    val matchingOrders = remember(orders, trackingQuery) {
        if (trackingQuery.isBlank()) orders
        else orders.filter {
            it.orderNumber.contains(trackingQuery, ignoreCase = true) ||
                it.customerPhone.contains(trackingQuery, ignoreCase = true) ||
                it.consignmentId.contains(trackingQuery, ignoreCase = true)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("অর্ডার ও কুরিয়ার ট্র্যাকিং (Live Consignment Tracker)", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = trackingQuery,
                    onValueChange = { trackingQuery = it },
                    placeholder = { Text("Enter Order # (BOBD-2026-9841), Phone or Consignment ID...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        matchingOrders.forEach { order ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Order #${order.orderNumber} • ${order.invoiceNumber}",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${order.customerName} (${order.customerPhone}) • ${order.district}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = order.orderStatus,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text("Items: ${order.itemsSummary}", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "Courier: ${order.courierProvider} (${order.consignmentId}) • Warehouse: ${order.warehouseCode} • Total: ৳${order.grandTotalBdt}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    // Visual Status Steps
                    val stages = listOf("Pending", "Confirmed", "Packed", "Shipped", "Out for Delivery", "Delivered")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(stages) { stage ->
                            val reached = stages.indexOf(stage) <= stages.indexOf(order.orderStatus).coerceAtLeast(1)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (reached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = stage,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (reached) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Timeline: ${order.timelineSummary}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedButton(
                        onClick = { onSyncCourierWebhook(order) },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync Courier", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sync Live ${order.courierProvider} Webhook Status")
                    }
                }
            }
        }

        // Return & Refund Request Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AssignmentReturn,
                        contentDescription = "Return Request",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("রিটার্ন ও রিফান্ড রিকোয়েস্ট (7-Day Return / Exchange)", style = MaterialTheme.typography.titleMedium)
                }

                OutlinedTextField(
                    value = returnOrderNum,
                    onValueChange = { returnOrderNum = it },
                    label = { Text("Order Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = returnReason,
                    onValueChange = { returnReason = it },
                    label = { Text("Return / Exchange Reason & Media Note") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val matched = orders.firstOrNull { it.orderNumber == returnOrderNum } ?: orders.firstOrNull()
                        onSubmitReturnRequest(
                            returnOrderNum,
                            matched?.customerName ?: "Customer",
                            matched?.customerPhone ?: "01712345678",
                            matched?.itemsSummary ?: "Order Item",
                            returnReason,
                            matched?.grandTotalBdt ?: 1200
                        )
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Submit Return / Refund Request")
                }

                HorizontalDivider()
                Text("Recent Return Requests (${returnRequests.size}):", style = MaterialTheme.typography.titleSmall)
                returnRequests.forEach { req ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Order #${req.orderNumber} • Status: ${req.status} • ৳${req.refundAmountBdt}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(req.productName, style = MaterialTheme.typography.bodySmall)
                            Text("Reason: ${req.reason} • ${req.resolutionType}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResellerDashboardSection(
    resellerAccount: PartnerAccountEntity?,
    products: List<ProductEntity>,
    onRequestPayout: (PartnerAccountEntity) -> Unit,
    onAddResellerOrder: (ProductEntity, Int, Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (resellerAccount != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(resellerAccount.businessOrChannelName, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Partner ID: ${resellerAccount.referralOrPartnerCode} • ${resellerAccount.tierName}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = resellerAccount.status,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatMiniBox("Reseller Sales", "৳${resellerAccount.totalSalesBdt}", Modifier.weight(1f))
                        StatMiniBox("Total Earned", "৳${resellerAccount.totalEarnedCommissionBdt}", Modifier.weight(1f))
                        StatMiniBox("Wallet Balance", "৳${resellerAccount.walletBalanceBdt}", Modifier.weight(1f))
                    }

                    Button(
                        onClick = { onRequestPayout(resellerAccount) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Withdraw", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Withdraw Wallet Balance (৳${resellerAccount.walletBalanceBdt}) to bKash")
                    }
                }
            }
        }

        Text(
            text = "রিসেলার প্রোডাক্ট ক্যাটালগ (Set Your Custom Customer Price)",
            style = MaterialTheme.typography.titleMedium
        )

        products.forEach { product ->
            var customSellPrice by remember(product.id) { mutableIntStateOf(product.salePrice) }
            val profit = (customSellPrice - product.resellerPrice).coerceAtLeast(0)

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(product.name, style = MaterialTheme.typography.titleSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Reseller Base Cost: ৳${product.resellerPrice}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Your Profit: ৳$profit / unit",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFFD97706),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { customSellPrice = (customSellPrice - 20).coerceAtLeast(product.resellerPrice) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("-৳20")
                        }
                        Text(
                            text = "Customer Price: ৳$customSellPrice",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(
                            onClick = { customSellPrice += 20 },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+৳20")
                        }
                    }

                    Button(
                        onClick = { onAddResellerOrder(product, 1, customSellPrice) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Dropship Order (Collect ৳$customSellPrice • Earn ৳$profit)")
                    }
                }
            }
        }
    }
}

@Composable
private fun WholesalePortalSection(
    wholesaleAccount: PartnerAccountEntity?,
    products: List<ProductEntity>,
    onAddWholesaleOrder: (ProductEntity, Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Store, contentDescription = "Wholesale", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = wholesaleAccount?.businessOrChannelName ?: "BuyOneBD Wholesale B2B Tier System",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = "MOQ 5 Units • Tier 1 (5–19 pcs) • Tier 2 (20–49 pcs) • Tier 3 (50+ Factory Bulk Rate) with Automated B2B GST/VAT Invoice",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        products.forEach { product ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(product.name, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "Retail: ৳${product.salePrice} | Tier 1 (5+): ৳${product.wholesaleTier1Price} | Tier 2 (20+): ৳${product.wholesaleTier2Price} | Tier 3 (50+): ৳${product.wholesaleTier3Price}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onAddWholesaleOrder(product, 5) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Add 5 (Tier 1)", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = { onAddWholesaleOrder(product, 20) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Add 20 (Tier 2)", style = MaterialTheme.typography.labelSmall)
                        }
                        Button(
                            onClick = { onAddWholesaleOrder(product, 50) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Add 50 (Tier 3)", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AffiliateAndLoyaltySection(
    affiliateAccount: PartnerAccountEntity?,
    onRequestPayout: (PartnerAccountEntity) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (affiliateAccount != null) {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Share, contentDescription = "Affiliate", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("অ্যাফিলিয়েট ড্যাশবোর্ড (Affiliate Program)", style = MaterialTheme.typography.titleMedium)
                    }
                    Text("Channel: ${affiliateAccount.businessOrChannelName} (${affiliateAccount.tierName})")
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Referral Link: https://buyonebd.com/?ref=${affiliateAccount.referralOrPartnerCode}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatMiniBox("Clicks", "${affiliateAccount.clicksCount}", Modifier.weight(1f))
                        StatMiniBox("Conversions", "${affiliateAccount.conversionsCount}", Modifier.weight(1f))
                        StatMiniBox("Approved Wallet", "৳${affiliateAccount.walletBalanceBdt}", Modifier.weight(1f))
                    }

                    Text(
                        text = "Self-referral & duplicate cookie guard: ACTIVE (30-Day Attribution Window)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = { onRequestPayout(affiliateAccount) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Withdraw Affiliate Earnings (৳${affiliateAccount.walletBalanceBdt})")
                    }
                }
            }
        }

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
                    Icon(Icons.Default.Redeem, contentDescription = "Loyalty", tint = Color(0xFFD97706))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("BuyOneBD Loyalty & Cashback Rules", style = MaterialTheme.typography.titleMedium)
                }
                Text("• Purchase Reward: Earn 1 Point per ৳20 spent (100 Points = ৳50 Wallet Cashback)")
                Text("• Verified Photo/Video Review Reward: +50 Bonus Loyalty Points")
                Text("• Referral Reward: Give ৳100, Get ৳100 on friend's first delivered order")
            }
        }
    }
}

@Composable
private fun StatMiniBox(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}
