package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content?
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val service: GeminiApiService = retrofit.create(GeminiApiService::class.java)

    suspend fun generateResponse(prompt: String, systemInstruction: String? = null): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "API Key is missing or placeholder. Using local smart fallback.")
            return getLocalFallbackResponse(prompt)
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = systemInstruction?.let { Content(parts = listOf(Part(text = it))) }
        )

        return try {
            val response = service.generateContent(apiKey, request)
            val textResult = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            textResult ?: "Désolé, l'assistant n'a pas pu formuler de réponse."
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API Call failed: ${e.message}. Using fallback.", e)
            getLocalFallbackResponse(prompt)
        }
    }

    private fun getLocalFallbackResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("montre") || lower.contains("watch") -> {
                "Voici les montres recommandées à moins de 15 000 FCFA chez **Luxury Time** et **Kora Store** :\n\n" +
                        "1. **Montre Quartz Chrono** : Design ultra-chic avec bracelet en cuir, prix promo à 12 500 FCFA (au lieu de 18 000 FCFA).\n" +
                        "2. **Smart Watch Fit Pulse** : Suivi cardiaque et notifications WhatsApp, dispo chez Kora Store à 14 000 FCFA.\n\n" +
                        "Voulez-vous que je contacte un vendeur ?"
            }
            lower.contains("chaussure") || lower.contains("sneaker") || lower.contains("basket") -> {
                "Nous avons sélectionné des sneakers haut de gamme dispo immédiatement :\n\n" +
                        "• **Sneakers Pulse Sport** : Style moderne et confortable, dispo chez **StreetWear Hub** à 24 500 FCFA.\n" +
                        "• **Mocassins Premium** : En cuir véritable, parfaits pour le bureau, chez **Elite Shoes** à 30 000 FCFA.\n\n" +
                        "Aimeriez-vous filtrer par taille ou par couleur ?"
            }
            lower.contains("téléphone") || lower.contains("phone") || lower.contains("iphone") || lower.contains("samsung") -> {
                "Voici les meilleures offres smartphones du moment :\n\n" +
                        "• **iPhone 13 Pro 128Go** : État comme neuf avec garantie 6 mois, dispo chez **Tech Zone** à 380 000 FCFA.\n" +
                        "• **Samsung Galaxy A54 5G** : Autonomie de 2 jours, écran Super AMOLED, dispo chez **Digital Store** à 185 000 FCFA.\n\n" +
                        "Je peux comparer ces deux téléphones pour vous si vous le souhaitez !"
            }
            lower.contains("amelior") || lower.contains("optimis") || lower.contains("titre") || lower.contains("fiche") -> {
                "**[Pulse AI Assistant]** Voici une version optimisée pour votre produit :\n\n" +
                        "• **Titre optimisé** : Chaussures Sneakers Pulse Sport - Confort Ultra & Style Streetwear\n" +
                        "• **Description enrichie** : Découvrez l'alliance parfaite du style et du confort avec nos Sneakers Pulse Sport. Conçues avec des matériaux respirants et une semelle amortissante, elles conviennent aussi bien aux séances de sport qu'à vos sorties quotidiennes.\n" +
                        "• **Tags SEO** : #Sneakers #PulseMarket #SportStyle #ModeHomme #BasketsPremium\n" +
                        "• **Prix conseillé** : 24 500 FCFA"
            }
            else -> {
                "Bonjour ! Je suis l'assistant IA de Pulse Market. Je peux vous aider à chercher des produits en langage naturel, comparer des articles, ou optimiser vos fiches produits de vendeur.\n\n" +
                        "Que recherchez-vous aujourd'hui ?"
            }
        }
    }
}
