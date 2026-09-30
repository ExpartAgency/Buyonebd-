package com.example.domain.services

import com.example.BuildConfig
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiGeneratedSeoPack(
    val seoTitle: String,
    val metaDescription: String,
    val banglaSalesHook: String,
    val faqSnippet: String,
    val socialAdCopy: String,
    val jsonLdSchema: String
)

class BuyOneAiService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * AI Customer Support & Sales Assistant using Gemini 3.5 Flash (`gemini-3.5-flash`)
     * with live BuyOneBD product stock, pricing, delivery rules, and authenticated order tracking.
     */
    suspend fun answerCustomerQuery(
        userQuery: String,
        products: List<ProductEntity>,
        orders: List<OrderEntity>,
        insideDhakaCharge: Int,
        outsideDhakaCharge: Int
    ): String = withContext(Dispatchers.IO) {
        val catalogSnapshot = products.joinToString("\n") { p ->
            "- ${p.name} (${p.banglaName}) | SKU: ${p.sku} | Retail: ৳${p.salePrice} (Reg: ৳${p.retailPrice}) | Wholesale Tier1(5+): ৳${p.wholesaleTier1Price}, Tier2(20+): ৳${p.wholesaleTier2Price}, Tier3(50+): ৳${p.wholesaleTier3Price} | Reseller: ৳${p.resellerPrice} | Stock: ${p.stock} units | Warranty: ${p.warranty}"
        }
        val orderSnapshot = orders.take(5).joinToString("\n") { o ->
            "- Order #${o.orderNumber} | Customer: ${o.customerName} (${o.customerPhone}) | Status: ${o.orderStatus} | Courier: ${o.courierProvider} (${o.consignmentId}) | Total: ৳${o.grandTotalBdt}"
        }

        val systemInstruction = """
            You are BuyOneBD's official AI Customer Support & Sales Assistant ("Better Products • Better Life").
            Respond helpfully in a warm, concise mix of Bangla and English matching the customer's language.
            Never fabricate prices or stock—always use the exact live catalog and order data below:
            
            Delivery Rules:
            - Inside Dhaka Metro: ৳$insideDhakaCharge (24 Hours Next-Day Delivery via Steadfast/Pathao)
            - Sub-Dhaka (Savar, Keraniganj, Gazipur, Narayanganj): ৳100
            - Outside Dhaka (All 64 Districts): ৳$outsideDhakaCharge (48-72 Hours)
            - Free Shipping on orders above ৳2,000 or with coupon FREEDEL.
            - Payment: Cash on Delivery (COD), bKash, Nagad, Rocket, SSLCommerz.
            
            Live Catalog:
            $catalogSnapshot
            
            Recent Authenticated Customer Orders:
            $orderSnapshot
        """.trimIndent()

        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            val geminiReply = callGeminiFlash(systemInstruction, userQuery, apiKey)
            if (geminiReply != null) return@withContext geminiReply
        }

        // Intelligent catalog-driven response when offline or before user sets GEMINI_API_KEY in Secrets panel
        buildSmartCatalogResponse(userQuery, products, orders, insideDhakaCharge, outsideDhakaCharge)
    }

    /**
     * AI Product Description, SEO Title, Meta Description, Social Ad Copy & JSON-LD Schema Generator.
     * Never modifies database directly without explicit admin approval.
     */
    suspend fun generateProductSeoAndCopy(product: ProductEntity): AiGeneratedSeoPack = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            val prompt = """
                Generate a concise eCommerce SEO & Marketing package for this Bangladesh product:
                Product: ${product.name} (${product.banglaName})
                Category: ${product.category}
                Sale Price: ৳${product.salePrice} (Wholesale from ৳${product.wholesaleTier3Price})
                Features: ${product.features}
                Warranty: ${product.warranty}
                
                Format response with 4 short lines prefixed by:
                SEO_TITLE:
                META_DESC:
                BANGLA_HOOK:
                FAQ:
            """.trimIndent()
            val raw = callGeminiFlash(
                "You are an expert Bangladesh eCommerce SEO & Facebook CAPI Ad Copywriter for BuyOneBD.com.",
                prompt,
                apiKey
            )
            if (raw != null) {
                val lines = raw.lines()
                val title = lines.firstOrNull { it.startsWith("SEO_TITLE:") }?.removePrefix("SEO_TITLE:")?.trim()
                val meta = lines.firstOrNull { it.startsWith("META_DESC:") }?.removePrefix("META_DESC:")?.trim()
                val hook = lines.firstOrNull { it.startsWith("BANGLA_HOOK:") }?.removePrefix("BANGLA_HOOK:")?.trim()
                val faq = lines.firstOrNull { it.startsWith("FAQ:") }?.removePrefix("FAQ:")?.trim()
                if (!title.isNullOrBlank() && !meta.isNullOrBlank()) {
                    return@withContext AiGeneratedSeoPack(
                        seoTitle = title,
                        metaDescription = meta,
                        banglaSalesHook = hook ?: "সেরা মানের ${product.banglaName} এখন পাচ্ছেন BuyOneBD-তে মাত্র ৳${product.salePrice} টাকায়!",
                        faqSnippet = faq ?: "প্রশ্ন: ডেলিভারির সময় চেক করে নেওয়া যাবে? উত্তর: হ্যাঁ, ডেলিভারি ম্যানের সামনে চেক করে পেমেন্ট করার সুবিধা রয়েছে।",
                        socialAdCopy = "🔥 ${product.name} এখন বিশেষ ছাড়ে মাত্র ৳${product.salePrice}! ✅ ${product.warranty} • সারা বাংলাদেশে ক্যাশ অন ডেলিভারি।",
                        jsonLdSchema = buildProductJsonLd(product)
                    )
                }
            }
        }

        AiGeneratedSeoPack(
            seoTitle = "${product.name} Price in Bangladesh (2026) | BuyOneBD",
            metaDescription = "Buy original ${product.name} (${product.banglaName}) online at ৳${product.salePrice} in Bangladesh. ${product.warranty}, Wholesale & Reseller pricing, fast Steadfast/Pathao COD delivery.",
            banglaSalesHook = "✨ ১০০% অরিজিনাল ${product.banglaName} — খুচরা মাত্র ৳${product.salePrice} এবং পাইকারি ৳${product.wholesaleTier3Price} থেকে শুরু! আজই অর্ডার করুন BuyOneBD-তে।",
            faqSnippet = "Q1: Is Cash on Delivery available outside Dhaka? A: Yes, nationwide COD via Steadfast & Pathao.\nQ2: What warranty is included? A: ${product.warranty} with ${product.guarantee}.",
            socialAdCopy = "🌿 Better Products • Better Life! ${product.name} এখন ৳${product.retailPrice - product.salePrice} টাকা ছাড়ে মাত্র ৳${product.salePrice}! অর্ডার করতে বাটনে ক্লিক করুন।",
            jsonLdSchema = buildProductJsonLd(product)
        )
    }

    private fun callGeminiFlash(systemPrompt: String, userPrompt: String, apiKey: String): String? {
        return try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val bodyJson = JSONObject().apply {
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                })
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", userPrompt)))
                }))
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.4)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(bodyJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val rawBody = response.body?.string() ?: return null
                val root = JSONObject(rawBody)
                root.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun buildSmartCatalogResponse(
        userQuery: String,
        products: List<ProductEntity>,
        orders: List<OrderEntity>,
        insideDhakaCharge: Int,
        outsideDhakaCharge: Int
    ): String {
        val q = userQuery.lowercase()
        return when {
            q.contains("omron") || q.contains("bp") || q.contains("প্রেসার") -> {
                val omron = products.firstOrNull { it.slug.contains("omron") }
                if (omron != null) {
                    "✅ হ্যাঁ, **${omron.name}** স্টকে আছে (${omron.stock}টি রেডি স্টক)।\n" +
                        "• **খুচরা মূল্য (Sale):** ৳${omron.salePrice} (রেগুলার ৳${omron.retailPrice})\n" +
                        "• **পাইকারি (Wholesale 5+):** ৳${omron.wholesaleTier1Price} | **Reseller:** ৳${omron.resellerPrice}\n" +
                        "• **ওয়ারেন্টি:** ${omron.warranty} (${omron.guarantee})\n" +
                        "আপনি চাইলে এক ক্লিকেই কার্টে যোগ করতে বা সরাসরি Buy Now করতে পারেন!"
                } else {
                    "আমাদের Healthcare সেকশনে অরিজিনাল Omron BP মেশিন স্টকে আছে।"
                }
            }
            q.contains("delivery") || q.contains("ডেলিভারি") || q.contains("koto") || q.contains("কত") -> {
                "🚚 **BuyOneBD ডেলিভারি চার্জ ও সময়সূচী:**\n" +
                    "• **ঢাকা সিটির ভেতরে (Inside Dhaka):** মাত্র ৳$insideDhakaCharge (২৪ ঘণ্টায় ডেলিভারি, COD চার্জ ফ্রি)\n" +
                    "• **সাব-ঢাকা (সাভার, কেরানীগঞ্জ, গাজীপুর, নারায়ণগঞ্জ):** ৳১০০\n" +
                    "• **ঢাকার বাইরে সারা বাংলাদেশ (৬৪ জেলা):** ৳$outsideDhakaCharge (৪৮–৭২ ঘণ্টায় Steadfast / Pathao কুরিয়ারের মাধ্যমে হোম ডেলিভারি)\n" +
                    "🎁 **৳২,০০০+ অর্ডারে বা `FREEDEL` কুপনে ডেলিভারি সম্পূর্ণ ফ্রি!**"
            }
            q.contains("order") || q.contains("অর্ডার") || q.contains("কোথায়") || q.contains("track") -> {
                val latest = orders.firstOrNull()
                if (latest != null) {
                    "📦 **আপনার সর্বশেষ অর্ডার আপডেট:**\n" +
                        "• **অর্ডার নম্বর:** #${latest.orderNumber} (ইনভয়েস: ${latest.invoiceNumber})\n" +
                        "• **বর্তমান স্ট্যাটাস:** ${latest.orderStatus}\n" +
                        "• **কুরিয়ার ও কনসাইনমেন্ট:** ${latest.courierProvider} — `${latest.consignmentId}`\n" +
                        "• **টাইমলাইন:** ${latest.timelineSummary}"
                } else {
                    "আপনার অর্ডার নম্বর (যেমন `BOBD-2026-9841`) দিলে আমি এখনই কুরিয়ার স্ট্যাটাস জানিয়ে দিচ্ছি।"
                }
            }
            q.contains("জিরা") || q.contains("jira") || q.contains("zira") || q.contains("cumin") || q.contains("মধু") || q.contains("honey") -> {
                val matched = BanglaSearchEngine.searchProducts(products, userQuery, null)
                if (matched.isNotEmpty()) {
                    val top = matched.first()
                    "🌿 **${top.name}** (${top.banglaName}) এখন স্টকে আছে!\n" +
                        "• **অফার মূল্য:** ৳${top.salePrice} (সাশ্রয় ৳${top.retailPrice - top.salePrice})\n" +
                        "• **পাইকারি মূল্য (৫+ পিস):** ৳${top.wholesaleTier1Price} | **৫০+ পিস:** ৳${top.wholesaleTier3Price}\n" +
                        "• **গ্যারান্টি:** ${top.guarantee}\n" +
                        "• **স্টক:** ${top.stock} ইউনিট রেডি।"
                } else {
                    "আমাদের কাছে ১০০% খাঁটি ভাজা জিরা গুঁড়া এবং সুন্দরবনের খলিশা ফুলের মধু রেডি স্টকে আছে।"
                }
            }
            q.contains("reseller") || q.contains("wholesale") || q.contains("পাইকারি") || q.contains("রিসেলার") -> {
                "🤝 **BuyOneBD B2B (Wholesale & Reseller) সুবিধা:**\n" +
                    "• **Wholesale Tier Pricing:** ৫–১৯ পিসে Tier-1, ২০–৪৯ পিসে Tier-2 এবং ৫০+ পিসে সর্বোচ্চ Tier-3 পাইকারি ছাড়।\n" +
                    "• **Reseller Dropshipping:** জিরো ইনভেস্টমেন্টে নিজের কাস্টম দামে অর্ডার বুক করুন, কুরিয়ার ডেলিভারি শেষে আপনার প্রফিট সরাসরি Reseller Wallet-এ যুক্ত হবে!"
            }
            else -> {
                val matched = BanglaSearchEngine.searchProducts(products, userQuery, null).take(2)
                if (matched.isNotEmpty()) {
                    "আমি আপনার প্রশ্নের সাথে মিল থাকা নিচের প্রোডাক্টগুলো খুঁজে পেয়েছি:\n" +
                        matched.joinToString("\n") { "• **${it.name}** — মাত্র ৳${it.salePrice} (স্টক: ${it.stock}টি, ওয়ারেন্টি: ${it.warranty})" } +
                        "\n\nডেলিভারি চার্জ ঢাকার ভেতরে ৳$insideDhakaCharge এবং ঢাকার বাইরে ৳$outsideDhakaCharge। কোনো নির্দিষ্ট প্রোডাক্ট বা অর্ডার ট্র্যাকিং জানতে চাইলে জিজ্ঞেস করুন!"
                } else {
                    "স্বাগতম **BuyOneBD**-তে (Better Products • Better Life)! 🌿\n" +
                        "আমি আপনাকে নিচের বিষয়গুলোতে তাৎক্ষণিক সাহায্য করতে পারি:\n" +
                        "১. প্রোডাক্টের দাম, স্টক ও ওয়ারেন্টি (যেমন: *\"Omron machine available?\"* বা *\"জিরা গুঁড়ার দাম কত?\"*)\n" +
                        "২. ডেলিভারি চার্জ ও সময় (*\"Delivery koto?\"*)\n" +
                        "৩. লাইভ অর্ডার ট্র্যাকিং (*\"আমার order কোথায়?\"*)\n" +
                        "৪. পাইকারি (Wholesale) ও রিসেলার (Reseller) মার্জিন তথ্য।"
                }
            }
        }
    }

    private fun buildProductJsonLd(product: ProductEntity): String {
        return """
            {
              "@context": "https://schema.org/",
              "@type": "Product",
              "name": "${product.name}",
              "sku": "${product.sku}",
              "gtin13": "${product.barcode}",
              "brand": { "@type": "Brand", "name": "${product.brand}" },
              "offers": {
                "@type": "Offer",
                "url": "${product.canonicalUrl}",
                "priceCurrency": "BDT",
                "price": "${product.salePrice}",
                "availability": "https://schema.org/InStock"
              },
              "aggregateRating": {
                "@type": "AggregateRating",
                "ratingValue": "${product.rating}",
                "reviewCount": "${product.reviewCount}"
              }
            }
        """.trimIndent()
    }
}
