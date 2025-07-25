package com.shop.shop.data.remote.models

import com.google.gson.annotations.SerializedName

// Modelo principal del producto
data class Product(
    @SerializedName("id") val id: Int,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("sellerId") val sellerId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("stockQuantity") val stockQuantity: Int,
    @SerializedName("category") val category: String,
    @SerializedName("images") val images: List<String>,
    @SerializedName("status") val status: String,
    @SerializedName("viewsCount") val viewsCount: Int = 0,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
) {
    // Helper para obtener la primera imagen
    fun getFirstImage(): String? = images.firstOrNull()

    // Helper para verificar si está disponible
    fun isAvailable(): Boolean = status == "active" && stockQuantity > 0

    // Helper para formatear el precio
    fun getFormattedPrice(): String = "$${String.format("%.2f", price)}"
}

// Response del API para productos
data class ProductsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ProductsData?
)

data class ProductsData(
    @SerializedName("products") val products: List<Product>,
    @SerializedName("pagination") val pagination: Pagination? = null
)

data class Pagination(
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("limit") val limit: Int
)

// Response para un solo producto
data class SingleProductResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Product?
)

// ✅ Request para crear producto
data class CreateProductRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("stockQuantity") val stockQuantity: Int,
    @SerializedName("category") val category: String,
    @SerializedName("images") val images: List<String> = emptyList()
)

// ✅ Request para actualizar producto
data class UpdateProductRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("price") val price: Double? = null,
    @SerializedName("stockQuantity") val stockQuantity: Int? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("images") val images: List<String>? = null
)