package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BuyOneDao {

    // Products
    @Query("SELECT * FROM products ORDER BY isFeatured DESC, isBestseller DESC, id ASC")
    fun observeAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: Int): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("UPDATE products SET isWishlisted = :wishlisted WHERE id = :productId")
    suspend fun setProductWishlisted(productId: Int, wishlisted: Boolean)

    @Query("UPDATE products SET stock = MAX(0, stock - :qty), reservedStock = reservedStock + :qty WHERE id = :productId")
    suspend fun reserveStockForOrder(productId: Int, qty: Int)

    @Query("UPDATE products SET stock = stock + :addedStock WHERE id = :productId")
    suspend fun addProductStock(productId: Int, addedStock: Int)

    // Categories
    @Query("SELECT * FROM categories ORDER BY id ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    // Cart
    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    fun observeCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId AND variantLabel = :variant AND orderMode = :orderMode AND savedForLater = 0 LIMIT 1")
    suspend fun findCartItem(productId: Int, variant: String, orderMode: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :cartItemId")
    suspend fun deleteCartItem(cartItemId: Int)

    @Query("DELETE FROM cart_items WHERE savedForLater = 0")
    suspend fun clearActiveCart()

    // Orders
    @Query("SELECT * FROM orders ORDER BY createdAtMillis DESC")
    fun observeOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber OR customerPhone LIKE '%' || :query || '%' ORDER BY createdAtMillis DESC")
    suspend fun searchOrders(orderNumber: String, query: String): List<OrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    // Warehouses
    @Query("SELECT * FROM warehouses ORDER BY isMain DESC, id ASC")
    fun observeWarehouses(): Flow<List<WarehouseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarehouses(warehouses: List<WarehouseEntity>)

    @Update
    suspend fun updateWarehouse(warehouse: WarehouseEntity)

    // Coupons
    @Query("SELECT * FROM coupons ORDER BY id ASC")
    fun observeCoupons(): Flow<List<CouponEntity>>

    @Query("SELECT * FROM coupons WHERE UPPER(code) = UPPER(:code) AND isActive = 1 LIMIT 1")
    suspend fun findActiveCoupon(code: String): CouponEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupons(coupons: List<CouponEntity>)

    // CRM Customers
    @Query("SELECT * FROM customers_crm ORDER BY totalSpentBdt DESC")
    fun observeCustomersCrm(): Flow<List<CustomerCrmEntity>>

    @Query("SELECT * FROM customers_crm WHERE phone = :phone LIMIT 1")
    suspend fun findCustomerByPhone(phone: String): CustomerCrmEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomersCrm(customers: List<CustomerCrmEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCustomerCrm(customer: CustomerCrmEntity)

    // Partner Accounts (Reseller / Wholesale / Affiliate)
    @Query("SELECT * FROM partner_accounts ORDER BY id ASC")
    fun observePartnerAccounts(): Flow<List<PartnerAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartnerAccounts(accounts: List<PartnerAccountEntity>)

    @Update
    suspend fun updatePartnerAccount(account: PartnerAccountEntity)

    // Return Requests
    @Query("SELECT * FROM return_requests ORDER BY id DESC")
    fun observeReturnRequests(): Flow<List<ReturnRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReturnRequests(requests: List<ReturnRequestEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReturnRequest(request: ReturnRequestEntity)

    @Update
    suspend fun updateReturnRequest(request: ReturnRequestEntity)

    // Blog Posts
    @Query("SELECT * FROM blog_posts ORDER BY id ASC")
    fun observeBlogPosts(): Flow<List<BlogPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlogPosts(posts: List<BlogPostEntity>)

    // Universal Tracking Events (Meta Pixel + CAPI / GA4 / TikTok / WhatsApp / SMS)
    @Query("SELECT * FROM tracking_events ORDER BY timestampMillis DESC LIMIT 60")
    fun observeTrackingEvents(): Flow<List<TrackingEventLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackingEvent(event: TrackingEventLogEntity)

    // Platform Settings
    @Query("SELECT * FROM platform_settings ORDER BY id ASC")
    fun observePlatformSettings(): Flow<List<PlatformSettingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlatformSettings(settings: List<PlatformSettingEntity>)

    @Query("UPDATE platform_settings SET settingValue = :newValue WHERE settingKey = :key")
    suspend fun updatePlatformSetting(key: String, newValue: String)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int
}
