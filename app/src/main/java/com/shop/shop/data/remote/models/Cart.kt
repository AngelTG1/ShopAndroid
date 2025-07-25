package com.shop.shop.data.remote.models

import com.google.gson.annotations.SerializedName

// ===== MODELOS PRINCIPALES =====

data class Cart(
    @SerializedName("id") val id: Int,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("userId") val userId: Int,
    @SerializedName("status") val status: String,
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("items") val items: List<CartItem>,
    @SerializedName("expiresAt") val expiresAt: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
) {
    fun isEmpty(): Boolean = totalItems == 0 || items.isEmpty()
    fun getFormattedTotal(): String = "$${String.format("%.2f", totalAmount)}"
}

data class CartItem(
    @SerializedName("id") val id: Int,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("cartId") val cartId: Int,
    @SerializedName("productId") val productId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unitPrice") val unitPrice: Double,
    @SerializedName("totalPrice") val totalPrice: Double,
    @SerializedName("product") val product: Product?,
    @SerializedName("addedAt") val addedAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
) {
    fun getFormattedUnitPrice(): String = "$${String.format("%.2f", unitPrice)}"
    fun getFormattedTotalPrice(): String = "$${String.format("%.2f", totalPrice)}"
}

// ===== RESPONSES =====

data class CartResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: Cart?
)

data class CartCountResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: CartSummary?
)

data class CartSummary(
    @SerializedName("count") val count: Int,
    @SerializedName("totalAmount") val totalAmount: Double
)

data class SavedItemsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<CartItem>?
)

// ===== REQUESTS =====

data class AddToCartRequest(
    @SerializedName("productId") val productId: Int,
    @SerializedName("quantity") val quantity: Int
)

data class UpdateCartItemRequest(
    @SerializedName("quantity") val quantity: Int
)