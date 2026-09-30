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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import com.example.data.local.CartItemEntity
import com.example.data.local.CouponEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.domain.services.BangladeshAddressDirectory

@Composable
fun CartAndCheckoutScreen(
    cartItems: List<CartItemEntity>,
    allProducts: List<ProductEntity>,
    coupons: List<CouponEntity>,
    appliedCoupon: CouponEntity?,
    activeOrderMode: String,
    freeShippingThreshold: Int,
    insideDhakaRate: Int,
    outsideDhakaRate: Int,
    lastCreatedOrder: OrderEntity?,
    calculateUnitPrice: (CartItemEntity) -> Int,
    onUpdateQuantity: (CartItemEntity, Int) -> Unit,
    onToggleSaveForLater: (CartItemEntity) -> Unit,
    onRemoveCartItem: (Int) -> Unit,
    onApplyCoupon: (String, Int) -> Unit,
    onRemoveCoupon: () -> Unit,
    onQuickAddUpsell: (ProductEntity) -> Unit,
    onPlaceOrder: (
        name: String,
        phone: String,
        email: String,
        division: String,
        district: String,
        upazila: String,
        area: String,
        fullAddress: String,
        note: String,
        paymentMethod: String,
        affiliateCode: String
    ) -> Unit,
    onDismissOrderModal: () -> Unit,
    onNavigateToTrackOrder: () -> Unit
) {
    val activeItems = remember(cartItems) { cartItems.filter { !it.savedForLater } }
    val savedLaterItems = remember(cartItems) { cartItems.filter { it.savedForLater } }

    var couponInput by remember { mutableStateOf("") }

    // Bangladesh Checkout Form State
    var customerName by remember { mutableStateOf("Farhana Rahman") }
    var customerPhone by remember { mutableStateOf("01712345678") }
    var customerEmail by remember { mutableStateOf("farhana.dhk@gmail.com") }
    var selectedDivision by remember { mutableStateOf("Dhaka") }

    val availableDistricts = remember(selectedDivision) {
        BangladeshAddressDirectory.districtsByDivision[selectedDivision] ?: listOf("Dhaka")
    }
    var selectedDistrict by remember(selectedDivision) { mutableStateOf(availableDistricts.first()) }

    val availableUpazilas = remember(selectedDistrict) {
        BangladeshAddressDirectory.getUpazilas(selectedDistrict)
    }
    var selectedUpazila by remember(selectedDistrict) { mutableStateOf(availableUpazilas.first()) }

    var areaName by remember { mutableStateOf("Dhanmondi Road 9A") }
    var fullAddress by remember { mutableStateOf("House 34, Flat 4B, Road 9A, Dhanmondi") }
    var orderNote by remember { mutableStateOf("Please call before delivery") }
    var selectedPaymentMethod by remember { mutableStateOf("Cash on Delivery") }
    var affiliateCode by remember { mutableStateOf("") }

    val deliveryZone = BangladeshAddressDirectory.resolveDeliveryZone(selectedDistrict, selectedUpazila)
    val subtotal = activeItems.sumOf { calculateUnitPrice(it) * it.quantity }
    val discount = when (appliedCoupon?.discountType) {
        "PERCENTAGE" -> ((subtotal * appliedCoupon.discountValue) / 100).coerceAtMost(appliedCoupon.maxDiscountBdt)
        "FIXED" -> appliedCoupon.discountValue.coerceAtMost(subtotal)
        else -> 0
    }
    val baseDelivery = BangladeshAddressDirectory.resolveDeliveryCharge(deliveryZone, insideDhakaRate, outsideDhakaRate)
    val deliveryCharge = if (subtotal >= freeShippingThreshold || appliedCoupon?.isFreeDelivery == true || activeItems.isEmpty()) 0 else baseDelivery
    val codCharge = if (selectedPaymentMethod == "Cash on Delivery" && deliveryZone != "Inside Dhaka" && activeItems.isNotEmpty()) 20 else 0
    val grandTotal = (subtotal - discount + deliveryCharge + codCharge).coerceAtLeast(0)

    // Order Confirmation & Invoice Dialog
    if (lastCreatedOrder != null) {
        AlertDialog(
            onDismissRequest = onDismissOrderModal,
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Order Success",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
            },
            title = {
                Text("অর্ডার সফলভাবে সম্পন্ন হয়েছে! (#${lastCreatedOrder.orderNumber})")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Invoice: ${lastCreatedOrder.invoiceNumber} • Mode: ${lastCreatedOrder.orderMode}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Customer: ${lastCreatedOrder.customerName} (${lastCreatedOrder.customerPhone})")
                    Text("Courier: ${lastCreatedOrder.courierProvider} • Consignment: ${lastCreatedOrder.consignmentId}")
                    Text("Payment: ${lastCreatedOrder.paymentMethod} (${lastCreatedOrder.paymentStatus})")
                    Text("Grand Total: ৳${lastCreatedOrder.grandTotalBdt}", fontWeight = FontWeight.Bold)
                    HorizontalDivider()
                    Text(
                        text = "✅ Meta Pixel + CAPI deduplicated Purchase event dispatched.\n✅ WhatsApp & SMS confirmation sent.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismissOrderModal()
                        onNavigateToTrackOrder()
                    }
                ) {
                    Text("Track Order & Invoice")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissOrderModal) {
                    Text("Close")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("cart_checkout_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Free Shipping Threshold Progress Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val remaining = (freeShippingThreshold - subtotal).coerceAtLeast(0)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = "Shipping Progress",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (remaining == 0 && activeItems.isNotEmpty()) {
                                "🎉 অভিনন্দন! আপনি সারা বাংলাদেশে FREE Delivery পাচ্ছেন!"
                            } else {
                                "আর মাত্র ৳$remaining টাকার পণ্য যোগ করলেই ডেলিভারি চার্জ সম্পূর্ণ ফ্রি!"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    LinearProgressIndicator(
                        progress = { (subtotal.toFloat() / freeShippingThreshold.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Active Cart Items Section
        if (activeItems.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Empty Cart",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(42.dp)
                        )
                        Text("আপনার কার্ট বর্তমানে খালি (Your Cart is Empty)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "নিচের জনপ্রিয় পণ্যগুলো থেকে এক ক্লিকে কার্টে যোগ করে অর্ডার সম্পন্ন করুন।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(activeItems, key = { "cart_${it.id}" }) { item ->
                val unitPrice = calculateUnitPrice(item)
                val tierLabel = when {
                    item.orderMode == "RESELLER" -> "Reseller Dropship (Base ৳${item.resellerBasePrice})"
                    item.quantity >= 50 -> "Wholesale Tier 3 (50+ pcs)"
                    item.quantity >= 20 -> "Wholesale Tier 2 (20–49 pcs)"
                    item.quantity >= 5 -> "Wholesale Tier 1 (5–19 pcs)"
                    else -> "Retail Price"
                }

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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.productName, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    text = "${item.variantLabel} • $tierLabel",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = { onRemoveCartItem(item.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Remove Item",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "৳$unitPrice x ${item.quantity} = ৳${unitPrice * item.quantity}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                TextButton(
                                    onClick = { onToggleSaveForLater(item) },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Save for Later", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onUpdateQuantity(item, item.quantity - 1) },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                }
                                Text(
                                    text = item.quantity.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                                OutlinedButton(
                                    onClick = { onUpdateQuantity(item, item.quantity + 1) },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Saved for Later Section
        if (savedLaterItems.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Saved for Later (${savedLaterItems.size})", style = MaterialTheme.typography.titleSmall)
                        savedLaterItems.forEach { saved ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(saved.productName, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                TextButton(onClick = { onToggleSaveForLater(saved) }) {
                                    Text("Move to Cart")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Cross-Sell & Upsell Recommendations
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Frequently Bought Together (একসাথে কিনলে ডেলিভারি সাশ্রয়)",
                    style = MaterialTheme.typography.titleSmall
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(allProducts.take(4), key = { "upsell_${it.id}" }) { prod ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp,
                            modifier = Modifier.width(200.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(prod.name, style = MaterialTheme.typography.labelLarge, maxLines = 1)
                                Text(
                                    "৳${prod.salePrice} (Wholesale 5+: ৳${prod.wholesaleTier1Price})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                OutlinedButton(
                                    onClick = { onQuickAddUpsell(prod) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("+ Add to Cart", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Coupon Engine Section
        item {
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
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = "Coupon",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("কুপন কোড (Apply Coupon)", style = MaterialTheme.typography.titleSmall)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = couponInput,
                            onValueChange = { couponInput = it.uppercase() },
                            placeholder = { Text("Enter BUYONE10 / FIRST100 / FREEDEL") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("coupon_code_input")
                        )
                        Button(
                            onClick = { onApplyCoupon(couponInput, subtotal) },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Apply")
                        }
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(coupons) { c ->
                            FilterChip(
                                selected = appliedCoupon?.code == c.code,
                                onClick = {
                                    couponInput = c.code
                                    onApplyCoupon(c.code, subtotal)
                                },
                                label = { Text("${c.code}: ${c.title}", style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    if (appliedCoupon != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✅ Applied ${appliedCoupon.code} (${appliedCoupon.title})",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(onClick = onRemoveCoupon) {
                                Text("Remove")
                            }
                        }
                    }
                }
            }
        }

        // Bangladesh-Friendly Simple Checkout Form
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "চেকআউট ও ডেলিভারি তথ্য (Bangladesh Easy Checkout)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("আপনার নাম (Full Name) *") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_name_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("মোবাইল নম্বর (Phone) *") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("checkout_phone_input")
                        )
                        OutlinedTextField(
                            value = customerEmail,
                            onValueChange = { customerEmail = it },
                            label = { Text("Email (Optional)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Division Selector
                    Text("বিভাগ নির্বাচন করুন (Division):", style = MaterialTheme.typography.labelLarge)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(BangladeshAddressDirectory.divisions) { div ->
                            FilterChip(
                                selected = selectedDivision == div,
                                onClick = { selectedDivision = div },
                                label = { Text(div) }
                            )
                        }
                    }

                    // District Selector
                    Text("জেলা নির্বাচন করুন (District):", style = MaterialTheme.typography.labelLarge)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(availableDistricts) { dist ->
                            FilterChip(
                                selected = selectedDistrict == dist,
                                onClick = { selectedDistrict = dist },
                                label = { Text(dist) }
                            )
                        }
                    }

                    // Upazila Selector
                    Text("থানা / উপজেলা (Upazila / Thana):", style = MaterialTheme.typography.labelLarge)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(availableUpazilas) { upz ->
                            FilterChip(
                                selected = selectedUpazila == upz,
                                onClick = { selectedUpazila = upz },
                                label = { Text(upz) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = areaName,
                            onValueChange = { areaName = it },
                            label = { Text("এলাকা / ইউনিয়ন (Area)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = affiliateCode,
                            onValueChange = { affiliateCode = it },
                            label = { Text("Referral / Affiliate Code") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = fullAddress,
                        onValueChange = { fullAddress = it },
                        label = { Text("সম্পূর্ণ ঠিকানা (House, Road, Landmark) *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_address_input")
                    )

                    OutlinedTextField(
                        value = orderNote,
                        onValueChange = { orderNote = it },
                        label = { Text("অর্ডার নোট (Delivery Note)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider()

                    // Payment Gateway Selection
                    Text("পেমেন্ট পদ্ধতি (Payment Method):", style = MaterialTheme.typography.titleSmall)
                    val paymentOptions = listOf(
                        "Cash on Delivery",
                        "bKash",
                        "Nagad",
                        "Rocket",
                        "SSLCommerz"
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(paymentOptions) { method ->
                            FilterChip(
                                selected = selectedPaymentMethod == method,
                                onClick = { selectedPaymentMethod = method },
                                label = { Text(method) }
                            )
                        }
                    }

                    HorizontalDivider()

                    // Order Cost Summary
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SummaryLine("Subtotal (${activeItems.sumOf { it.quantity }} items • $activeOrderMode)", "৳$subtotal")
                        SummaryLine("Coupon Discount", "-৳$discount", valueColor = MaterialTheme.colorScheme.primary)
                        SummaryLine("Delivery Charge ($deliveryZone)", if (deliveryCharge == 0) "FREE (৳0)" else "৳$deliveryCharge")
                        if (codCharge > 0) {
                            SummaryLine("COD Handling Charge (Outside Dhaka)", "৳$codCharge")
                        }
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("সর্বমোট (Grand Total)", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "৳$grandTotal",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onPlaceOrder(
                                customerName,
                                customerPhone,
                                customerEmail,
                                selectedDivision,
                                selectedDistrict,
                                selectedUpazila,
                                areaName,
                                fullAddress,
                                orderNote,
                                selectedPaymentMethod,
                                affiliateCode
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(vertical = 15.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("confirm_order_btn")
                    ) {
                        Text(
                            text = "অর্ডার কনফার্ম করুন • ৳$grandTotal ($selectedPaymentMethod)",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryLine(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}
