package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProductEntity::class,
        CategoryEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        WarehouseEntity::class,
        CouponEntity::class,
        CustomerCrmEntity::class,
        PartnerAccountEntity::class,
        ReturnRequestEntity::class,
        BlogPostEntity::class,
        TrackingEventLogEntity::class,
        PlatformSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BuyOneDatabase : RoomDatabase() {
    abstract fun buyOneDao(): BuyOneDao

    companion object {
        @Volatile
        private var INSTANCE: BuyOneDatabase? = null

        fun getInstance(context: Context): BuyOneDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BuyOneDatabase::class.java,
                    "buyonebd_production.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
