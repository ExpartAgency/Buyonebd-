package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [Index(value = ["slug"], unique = true), Index(value = ["category"]), Index(value = ["sku"])]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val banglaName: String,
    val searchKeywords: String, // Bangla, Banglish, English synonyms for typo-tolerant search
    val slug: String,
    val sku: String,
    val barcode: String,
    val brand: String,
    val category: String,
    val subcategory: String,
    val shortDescription: String,
    val description: String,
    val specifications: String,
    val features: String,
    val benefits: String,
    val retailPrice: Int,
    val salePrice: Int,
    val costPrice: Int,
    val wholesaleTier1Price: Int, // 5–19 units
    val wholesaleTier2Price: Int, // 20–49 units
    val wholesaleTier3Price: Int, // 50+ units
    val resellerPrice: Int,
    val stock: Int,
    val reservedStock: Int,
    val lowStockThreshold: Int,
    val warranty: String,
    val guarantee: String,
    val weightKg: Double,
    val dimensions: String,
    val variantsCsv: String, // e.g., "200g Pack|500g Family Pack|1kg BulkJar"
    val isFeatured: Boolean,
    val isBestseller: Boolean,
    val isNewArrival: Boolean,
    val isFlashSale: Boolean,
    val isWishlisted: Boolean = false,
    val rating: Double,
    val reviewCount: Int,
    val seoTitle: String,
    val metaDescription: String,
    val canonicalUrl: String,
    val schemaType: String = "Product",
    val badgeColorHex: String = "#0A5C36"
)

@Entity(tableName = "categories", indices = [Index(value = ["slug"], unique = true)])
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val banglaName: String,
    val slug: String,
    val iconKey: String,
    val productCount: Int,
    val isFeatured: Boolean = true
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val productName: String,
    val productBanglaName: String,
    val sku: String,
    val variantLabel: String,
    val quantity: Int,
    val retailUnitPrice: Int,
    val saleUnitPrice: Int,
    val wholesaleTier1Price: Int,
    val wholesaleTier2Price: Int,
    val wholesaleTier3Price: Int,
    val resellerBasePrice: Int,
    val customResellerSellPrice: Int, // Used when ordering in Reseller mode
    val orderMode: String = "RETAIL", // RETAIL, WHOLESALE, RESELLER
    val savedForLater: Boolean = false
)

@Entity(tableName = "orders", indices = [Index(value = ["orderNumber"], unique = true)])
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderNumber: String,
    val invoiceNumber: String,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val division: String,
    val district: String,
    val upazila: String,
    val area: String,
    val fullAddress: String,
    val deliveryZone: String, // Inside Dhaka, Sub-Dhaka, Outside Dhaka
    val orderNote: String,
    val paymentMethod: String, // COD, bKash, Nagad, Rocket, SSLCommerz
    val paymentStatus: String, // Pending, Verified, Paid, Refunded
    val transactionId: String,
    val itemsSummary: String,
    val itemCount: Int,
    val subtotalBdt: Int,
    val discountBdt: Int,
    val couponCode: String,
    val deliveryChargeBdt: Int,
    val codChargeBdt: Int,
    val grandTotalBdt: Int,
    val orderMode: String, // RETAIL, WHOLESALE, RESELLER
    val resellerCommissionBdt: Int,
    val affiliateCode: String,
    val courierProvider: String, // Steadfast, Pathao, RedX
    val consignmentId: String,
    val orderStatus: String, // Pending, Confirmed, Processing, Packed, Shipped, Out for Delivery, Delivered, Cancelled, Returned
    val timelineSummary: String,
    val codRiskLevel: String, // LOW, MEDIUM, HIGH
    val warehouseCode: String = "WH-DHK-01",
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "warehouses")
data class WarehouseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val code: String,
    val division: String,
    val address: String,
    val managerName: String,
    val phone: String,
    val isMain: Boolean,
    val totalSkus: Int,
    val availableUnits: Int,
    val reservedUnits: Int,
    val damagedUnits: Int,
    val stockValueBdt: Long,
    val lowStockAlertCount: Int
)

@Entity(tableName = "coupons", indices = [Index(value = ["code"], unique = true)])
data class CouponEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String,
    val title: String,
    val discountType: String, // PERCENTAGE, FIXED, FREE_DELIVERY
    val discountValue: Int,
    val minOrderBdt: Int,
    val maxDiscountBdt: Int,
    val isFreeDelivery: Boolean,
    val expiryLabel: String,
    val usageCount: Int,
    val usageLimit: Int,
    val isActive: Boolean = true
)

@Entity(tableName = "customers_crm")
data class CustomerCrmEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val email: String,
    val district: String,
    val segment: String, // VIP, High Value, Returning Customer, New Customer, COD Risk, Wholesale, Reseller
    val totalOrders: Int,
    val totalSpentBdt: Int,
    val cancelledOrders: Int,
    val returnedOrders: Int,
    val codFailedDeliveries: Int,
    val loyaltyPoints: Int,
    val walletBalanceBdt: Int,
    val leadScore: String, // HOT, WARM, COLD
    val sourceChannel: String, // Meta Ads, Organic SEO, Direct, Affiliate, WhatsApp
    val lastPurchaseLabel: String
)

@Entity(tableName = "partner_accounts")
data class PartnerAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val partnerType: String, // RESELLER, WHOLESALE, AFFILIATE
    val businessOrChannelName: String,
    val ownerName: String,
    val phone: String,
    val status: String, // APPROVED, PENDING
    val tierName: String,
    val referralOrPartnerCode: String,
    val totalSalesBdt: Int,
    val totalEarnedCommissionBdt: Int,
    val pendingCommissionBdt: Int,
    val walletBalanceBdt: Int,
    val clicksCount: Int,
    val conversionsCount: Int
)

@Entity(tableName = "return_requests")
data class ReturnRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val productName: String,
    val reason: String,
    val status: String, // Requested, Approved, Pickup, Received, Inspection, Refund Approved, Refunded, Rejected
    val refundAmountBdt: Int,
    val resolutionType: String, // bKash Refund, Wallet Credit, Replacement
    val createdAtLabel: String
)

@Entity(tableName = "blog_posts", indices = [Index(value = ["slug"], unique = true)])
data class BlogPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val banglaSubtitle: String,
    val slug: String,
    val category: String,
    val author: String,
    val readTimeMin: Int,
    val summary: String,
    val content: String,
    val seoTitle: String,
    val metaDescription: String,
    val faqSummary: String,
    val relatedProductCategory: String,
    val publishedDate: String
)

@Entity(tableName = "tracking_events")
data class TrackingEventLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: String, // Deduplication key across Browser Pixel & Server CAPI
    val eventName: String, // PageView, ViewContent, Search, AddToCart, InitiateCheckout, Purchase, WhatsAppConfirm
    val channel: String, // Meta Pixel + CAPI, GA4, TikTok Events API, WhatsApp Cloud API, SMS Gateway
    val entityReference: String,
    val valueBdt: Int,
    val currency: String = "BDT",
    val status: String = "DELIVERED (200 OK)",
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "platform_settings", indices = [Index(value = ["settingKey"], unique = true)])
data class PlatformSettingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val settingKey: String,
    val groupName: String, // COURIER, PAYMENT, TRACKING, AUTOMATION, HOMEPAGE, COD_RISK, SEO
    val label: String,
    val settingValue: String,
    val description: String
)
