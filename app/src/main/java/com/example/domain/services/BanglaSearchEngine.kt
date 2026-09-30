package com.example.domain.services

import com.example.data.local.ProductEntity
import kotlin.math.min

/**
 * Advanced Bangla + Banglish + English + Typo-Tolerant Search Engine for BuyOneBD.
 * Supports queries like:
 * - "জিরা গুঁড়া" / "jira gura" / "zira powder" / "cumin powder"
 * - "প্রেসার মাপার মেশিন" / "omron" / "bp machine"
 * - "মধু" / "modhu" / "khalisha honey"
 * - "গ্রাইন্ডার" / "masala grinder" / "blender"
 */
object BanglaSearchEngine {

    private val synonymGroups: List<Set<String>> = listOf(
        setOf("জিরা", "গুঁড়া", "জিরা গুঁড়া", "jira", "gura", "zira", "jeera", "cumin", "powder", "mosla", "মসলা", "মশলা"),
        setOf("মধু", "modhu", "madhu", "honey", "khalisha", "sundarban", "খলিশা", "সুন্দরবন", "খাঁটি মধু"),
        setOf("প্রেসার", "ব্লাড প্রেসার", "মেশিন", "অমরন", "omron", "bp", "blood pressure", "hem-7120", "hem7120", "medical"),
        setOf("গ্রাইন্ডার", "ব্লেন্ডার", "মশলা করার মেশিন", "grinder", "blender", "mixer", "masala", "kitchen"),
        setOf("ঘড়ি", "স্মার্টওয়াচ", "smartwatch", "watch", "pulsepro", "amoled", "earbuds", "gadget"),
        setOf("কম্বো", "বান্ডেল", "অফার", "ঘি", "হলুদ", "মরিচ", "ধনিয়া", "combo", "bundle", "family", "pack")
    )

    fun searchProducts(
        products: List<ProductEntity>,
        rawQuery: String,
        selectedCategory: String?,
        onlyFlashSale: Boolean = false
    ): List<ProductEntity> {
        val baseFiltered = products.filter { product ->
            val matchesCat = selectedCategory.isNullOrBlank() ||
                selectedCategory == "All" ||
                product.category.equals(selectedCategory, ignoreCase = true)
            val matchesFlash = !onlyFlashSale || product.isFlashSale
            matchesCat && matchesFlash
        }

        val query = rawQuery.trim().lowercase()
        if (query.isBlank()) return baseFiltered

        val expandedTokens = expandQueryTokens(query)

        return baseFiltered
            .map { product ->
                val score = scoreProduct(product, query, expandedTokens)
                product to score
            }
            .filter { (_, score) -> score > 0 }
            .sortedByDescending { (_, score) -> score }
            .map { (product, _) -> product }
    }

    private fun expandQueryTokens(query: String): Set<String> {
        val rawTokens = query.split(Regex("\\s+")).filter { it.isNotBlank() }
        val result = mutableSetOf<String>()
        result.add(query)
        result.addAll(rawTokens)

        for (group in synonymGroups) {
            val matched = rawTokens.any { token ->
                group.any { syn ->
                    syn.contains(token) || token.contains(syn) || levenshtein(token, syn) <= 1
                }
            } || group.any { syn -> query.contains(syn) }

            if (matched) {
                result.addAll(group)
            }
        }
        return result
    }

    private fun scoreProduct(
        product: ProductEntity,
        rawQuery: String,
        expandedTokens: Set<String>
    ): Int {
        val searchableBlob = buildString {
            append(product.name.lowercase()).append(" ")
            append(product.banglaName.lowercase()).append(" ")
            append(product.searchKeywords.lowercase()).append(" ")
            append(product.category.lowercase()).append(" ")
            append(product.brand.lowercase()).append(" ")
            append(product.sku.lowercase()).append(" ")
            append(product.shortDescription.lowercase())
        }

        var score = 0
        if (searchableBlob.contains(rawQuery)) {
            score += 100
        }

        for (token in expandedTokens) {
            if (token.length < 2) continue
            if (searchableBlob.contains(token)) {
                score += 25
            } else {
                // Typo tolerance check against product words
                val blobWords = searchableBlob.split(Regex("[^\\p{L}\\p{N}]+"))
                if (blobWords.any { word -> word.length >= 3 && levenshtein(word, token) <= 1 }) {
                    score += 15
                }
            }
        }
        return score
    }

    private fun levenshtein(lhs: String, rhs: String): Int {
        if (lhs == rhs) return 0
        if (lhs.isEmpty()) return rhs.length
        if (rhs.isEmpty()) return lhs.length

        val lhsLen = lhs.length
        val rhsLen = rhs.length
        var cost = IntArray(lhsLen + 1) { it }
        var newCost = IntArray(lhsLen + 1)

        for (i in 1..rhsLen) {
            newCost[0] = i
            for (j in 1..lhsLen) {
                val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1
                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1
                newCost[j] = min(min(costInsert, costDelete), costReplace)
            }
            val swap = cost
            cost = newCost
            newCost = swap
        }
        return cost[lhsLen]
    }
}
