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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.CustomerCrmEntity
import com.example.data.local.OrderEntity
import com.example.data.local.PlatformSettingEntity
import com.example.data.local.ProductEntity
import com.example.data.local.ReturnRequestEntity
import com.example.data.local.TrackingEventLogEntity
import com.example.data.local.WarehouseEntity
import com.example.domain.services.AiGeneratedSeoPack
import com.example.ui.AiChatMessage

@Composable
fun AdminErpAndAiScreen(
    products: List<ProductEntity>,
    orders: List<OrderEntity>,
    warehouses: List<WarehouseEntity>,
    customersCrm: List<CustomerCrmEntity>,
    returnRequests: List<ReturnRequestEntity>,
    trackingEvents: List<TrackingEventLogEntity>,
    settings: List<PlatformSettingEntity>,
    aiMessages: List<AiChatMessage>,
    isAiLoading: Boolean,
    generatedSeoPack: AiGeneratedSeoPack?,
    initialSubTab: Int = 0,
    onAdvanceOrderStatus: (OrderEntity) -> Unit,
    onSyncCourierWebhook: (OrderEntity) -> Unit,
    onBulkBookCourier: () -> Unit,
    onRestockProduct: (ProductEntity) -> Unit,
    onAdvanceReturnStatus: (ReturnRequestEntity) -> Unit,
    onUpdateSetting: (String, String) -> Unit,
    onRotateHomepageSections: () -> Unit,
    onSendAiMessage: (String) -> Unit,
    onGenerateAiSeo: (ProductEntity) -> Unit,
    onApplyAiSeo: (ProductEntity, AiGeneratedSeoPack) -> Unit
) {
    var activeSubTab by remember(initialSubTab) { mutableIntStateOf(initialSubTab) }
    val tabs = listOf(
        "AI Assistant & SEO",
        "Executive ERP & Orders",
        "Warehouses & Stock",
        "Courier & Settings",
        "Pixel/CAPI & CRM"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("admin_erp_ai_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "BuyOneBD Admin ERP, Courier & AI Control Center",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tabs.size) { index ->
                        FilterChip(
                            selected = activeSubTab == index,
                            onClick = { activeSubTab = index },
                            label = { Text(tabs[index]) },
                            modifier = Modifier.testTag("admin_subtab_$index")
                        )
                    }
                }
            }
        }

        when (activeSubTab) {
            0 -> item {
                AiCustomerAndSeoStudioSection(
                    products = products,
                    aiMessages = aiMessages,
                    isAiLoading = isAiLoading,
                    generatedSeoPack = generatedSeoPack,
                    onSendAiMessage = onSendAiMessage,
                    onGenerateAiSeo = onGenerateAiSeo,
                    onApplyAiSeo = onApplyAiSeo
                )
            }
            1 -> item {
                ExecutiveErpDashboardSection(
                    orders = orders,
                    products = products,
                    warehouses = warehouses,
                    returnRequests = returnRequests,
                    onAdvanceOrderStatus = onAdvanceOrderStatus,
                    onSyncCourierWebhook = onSyncCourierWebhook,
                    onBulkBookCourier = onBulkBookCourier,
                    onRotateHomepageSections = onRotateHomepageSections,
                    onAdvanceReturnStatus = onAdvanceReturnStatus
                )
            }
            2 -> item {
                MultiWarehouseAndInventorySection(
                    warehouses = warehouses,
                    products = products,
                    onRestockProduct = onRestockProduct
                )
            }
            3 -> item {
                CourierPaymentAndSettingsSection(
                    settings = settings,
                    onUpdateSetting = onUpdateSetting
                )
            }
            4 -> item {
                PixelCapiAndCrmSection(
                    trackingEvents = trackingEvents,
                    customersCrm = customersCrm
                )
            }
        }
    }
}

@Composable
private fun AiCustomerAndSeoStudioSection(
    products: List<ProductEntity>,
    aiMessages: List<AiChatMessage>,
    isAiLoading: Boolean,
    generatedSeoPack: AiGeneratedSeoPack?,
    onSendAiMessage: (String) -> Unit,
    onGenerateAiSeo: (ProductEntity) -> Unit,
    onApplyAiSeo: (ProductEntity, AiGeneratedSeoPack) -> Unit
) {
    var chatInput by remember { mutableStateOf("") }
    var selectedProductForSeo by remember(products) { mutableStateOf(products.firstOrNull()) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // AI Customer Support & Sales Assistant Card
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("BuyOneBD AI Customer Support & Sales Assistant", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Powered by Gemini 3.5 Flash • Answers Bangla, Banglish & English with live DB stock & orders",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Quick Customer Prompts from Prompt Specification
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val samplePrompts = listOf(
                        "Omron machine available?",
                        "Delivery koto?",
                        "আমার order কোথায়?",
                        "জিরা গুঁড়া ও মধুর দাম কত?",
                        "Wholesale & Reseller নিয়ম কি?"
                    )
                    items(samplePrompts) { sample ->
                        FilterChip(
                            selected = false,
                            onClick = { onSendAiMessage(sample) },
                            label = { Text(sample, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // Chat Conversation Log
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    aiMessages.takeLast(6).forEach { msg ->
                        val isUser = msg.sender == "USER"
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (isUser) "Customer / Admin" else "BuyOneBD AI Assistant",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isUser) Color(0xFFD9F5E6) else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    if (isAiLoading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Text("Gemini AI is checking live stock & order status...", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        placeholder = { Text("Ask in Bangla, Banglish or English...") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_chat_input")
                    )
                    Button(
                        onClick = {
                            if (chatInput.isNotBlank()) {
                                onSendAiMessage(chatInput)
                                chatInput = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("ai_chat_send_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }

        // AI Product SEO, Copy & JSON-LD Schema Generator (With Explicit Admin Approval Gate)
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
                Text(
                    text = "AI Product Content, SEO & Schema.org Generator",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Safety Policy: AI never modifies catalog data without explicit Admin review & approval.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(products) { prod ->
                        FilterChip(
                            selected = selectedProductForSeo?.id == prod.id,
                            onClick = { selectedProductForSeo = prod },
                            label = { Text(prod.sku) }
                        )
                    }
                }

                selectedProductForSeo?.let { target ->
                    Text("Selected: ${target.name} (${target.banglaName})", style = MaterialTheme.typography.labelLarge)
                    Button(
                        onClick = { onGenerateAiSeo(target) },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Generate AI SEO Title, Meta, FAQ & JSON-LD Schema")
                    }

                    if (generatedSeoPack != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("SEO Title: ${generatedSeoPack.seoTitle}", style = MaterialTheme.typography.labelLarge)
                                Text("Meta Description: ${generatedSeoPack.metaDescription}", style = MaterialTheme.typography.bodySmall)
                                Text("Bangla Ad Hook: ${generatedSeoPack.banglaSalesHook}", style = MaterialTheme.typography.bodySmall)
                                Text("FAQ: ${generatedSeoPack.faqSnippet}", style = MaterialTheme.typography.bodySmall)
                                Text("JSON-LD Schema:\n${generatedSeoPack.jsonLdSchema}", style = MaterialTheme.typography.labelSmall)

                                Button(
                                    onClick = { onApplyAiSeo(target, generatedSeoPack) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Approve & Save SEO Metadata to Database")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExecutiveErpDashboardSection(
    orders: List<OrderEntity>,
    products: List<ProductEntity>,
    warehouses: List<WarehouseEntity>,
    returnRequests: List<ReturnRequestEntity>,
    onAdvanceOrderStatus: (OrderEntity) -> Unit,
    onSyncCourierWebhook: (OrderEntity) -> Unit,
    onBulkBookCourier: () -> Unit,
    onRotateHomepageSections: () -> Unit,
    onAdvanceReturnStatus: (ReturnRequestEntity) -> Unit
) {
    val totalRevenue = orders.sumOf { it.grandTotalBdt }
    val estimatedProfit = (totalRevenue * 0.27).toInt()
    val totalStockValue = warehouses.sumOf { it.stockValueBdt }
    val avgOrderValue = if (orders.isNotEmpty()) totalRevenue / orders.size else 0

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // KPI Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KpiMetricCard("Total Revenue", "৳$totalRevenue", "+18.4% MoM", Modifier.weight(1f))
            KpiMetricCard("Net Profit (Est.)", "৳$estimatedProfit", "27% Margin", Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KpiMetricCard("Orders (${orders.size})", "AOV ৳$avgOrderValue", "Conv. Rate 4.2%", Modifier.weight(1f))
            KpiMetricCard("Warehouse Stock", "৳${totalStockValue / 100000}L", "${products.size} Core SKUs", Modifier.weight(1f))
        }

        // Bulk Admin Operations
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
                Text("Bulk Admin Operations & Storefront Controls", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onBulkBookCourier,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Bulk Courier Booking", style = MaterialTheme.typography.labelSmall)
                    }
                    OutlinedButton(
                        onClick = onRotateHomepageSections,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reorder Home Sections", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Order Management Table
        Text("All Orders & Courier Dispatch (${orders.size})", style = MaterialTheme.typography.titleMedium)
        orders.forEach { order ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                            text = "#${order.orderNumber} (${order.orderMode})",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${order.orderStatus} • ৳${order.grandTotalBdt}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${order.customerName} (${order.customerPhone}) • ${order.upazila}, ${order.district}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Courier: ${order.courierProvider} (${order.consignmentId}) • Payment: ${order.paymentMethod} (${order.paymentStatus}) • COD Risk: ${order.codRiskLevel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onAdvanceOrderStatus(order) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Next Status", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = { onSyncCourierWebhook(order) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Webhook Sync", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Return Moderation
        if (returnRequests.isNotEmpty()) {
            Text("Return & Refund Moderation", style = MaterialTheme.typography.titleMedium)
            returnRequests.forEach { req ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("#${req.orderNumber} • ${req.status}", style = MaterialTheme.typography.titleSmall)
                            Text("${req.productName} • Refund ৳${req.refundAmountBdt}", style = MaterialTheme.typography.bodySmall)
                        }
                        OutlinedButton(onClick = { onAdvanceReturnStatus(req) }) {
                            Text("Approve Next")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MultiWarehouseAndInventorySection(
    warehouses: List<WarehouseEntity>,
    products: List<ProductEntity>,
    onRestockProduct: (ProductEntity) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Multi-Warehouse Fulfillment Network", style = MaterialTheme.typography.titleMedium)
        warehouses.forEach { wh ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                        Text("${wh.name} (${wh.code})", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        Text(if (wh.isMain) "MAIN HUB" else "REGIONAL HUB", style = MaterialTheme.typography.labelSmall)
                    }
                    Text(wh.address, style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "Manager: ${wh.managerName} (${wh.phone}) • Available: ${wh.availableUnits} units • Reserved: ${wh.reservedUnits} • Damaged: ${wh.damagedUnits}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Text("Product Stock & Low-Stock Alerts", style = MaterialTheme.typography.titleMedium)
        products.forEach { product ->
            val isLowStock = product.stock <= product.lowStockThreshold
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isLowStock) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(product.name, style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = "SKU: ${product.sku} • Stock: ${product.stock} • Reserved: ${product.reservedStock} (Min: ${product.lowStockThreshold})",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Button(
                        onClick = { onRestockProduct(product) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+50 Stock", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun CourierPaymentAndSettingsSection(
    settings: List<PlatformSettingEntity>,
    onUpdateSetting: (String, String) -> Unit
) {
    val defaultCourier = settings.firstOrNull { it.settingKey == "default_courier" }?.settingValue ?: "Steadfast"

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                    Icon(Icons.Default.LocalShipping, contentDescription = "Courier", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CourierManager Default Adapter (Not Hardcoded)", style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    "CourierInterface → CourierManager → Active Adapter ($defaultCourier)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Steadfast", "Pathao", "RedX").forEach { provider ->
                        FilterChip(
                            selected = defaultCourier.equals(provider, ignoreCase = true),
                            onClick = { onUpdateSetting("default_courier", provider) },
                            label = { Text(provider) }
                        )
                    }
                }
            }
        }

        settings.forEach { setting ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                        Text("${setting.groupName} • ${setting.label}", style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = setting.settingValue,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(setting.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    if (setting.settingValue in listOf("ENABLED", "DISABLED")) {
                        val toggled = if (setting.settingValue == "ENABLED") "DISABLED" else "ENABLED"
                        OutlinedButton(
                            onClick = { onUpdateSetting(setting.settingKey, toggled) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Toggle to $toggled", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PixelCapiAndCrmSection(
    trackingEvents: List<TrackingEventLogEntity>,
    customersCrm: List<CustomerCrmEntity>
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Customer CRM Segments & COD Risk Engine", style = MaterialTheme.typography.titleMedium)
        customersCrm.forEach { cust ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${cust.name} (${cust.phone})", style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = "${cust.segment} • Lead: ${cust.leadScore}",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (cust.segment == "COD Risk") Color(0xFFDC2626) else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Orders: ${cust.totalOrders} • Spent: ৳${cust.totalSpentBdt} • Failed COD: ${cust.codFailedDeliveries} • Points: ${cust.loyaltyPoints} • Source: ${cust.sourceChannel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Text("Universal Event Stream (Meta Pixel + CAPI / GA4 / TikTok / WhatsApp)", style = MaterialTheme.typography.titleMedium)
        trackingEvents.forEach { ev ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${ev.eventName} • ${ev.channel}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "৳${ev.valueBdt}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(ev.entityReference, style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "event_id: ${ev.eventId} • ${ev.status}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun KpiMetricCard(
    label: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}
