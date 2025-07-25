package com.shop.shop.data.remote.repository

import com.shop.shop.data.remote.RetrofitClient
import com.shop.shop.data.remote.models.CreateProductRequest
import com.shop.shop.data.remote.models.ProductsResponse
import com.shop.shop.data.remote.models.SingleProductResponse
import com.shop.shop.data.remote.models.UpdateProductRequest
import retrofit2.Response

class ProductRepository {
    private val productApi = RetrofitClient.productApi

    suspend fun getAllProducts(
        page: Int? = null,
        limit: Int? = null,
        category: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ): Response<ProductsResponse> {
        return productApi.getAllProducts(page, limit, category, minPrice, maxPrice)
    }

    suspend fun searchProducts(
        searchQuery: String,
        page: Int? = null,
        limit: Int? = null
    ): Response<ProductsResponse> {
        return productApi.searchProducts(searchQuery, page, limit)
    }

    suspend fun getProductByUuid(uuid: String): Response<SingleProductResponse> {
        return productApi.getProductByUuid(uuid)
    }

    suspend fun getMyProducts(): Response<ProductsResponse> {
        return productApi.getMyProducts()
    }

    // ✅ Nueva función para crear producto
    suspend fun createProduct(
        name: String,
        description: String,
        price: Double,
        stockQuantity: Int,
        category: String,
        images: List<String> = emptyList()
    ): Response<SingleProductResponse> {
        val createProductRequest = CreateProductRequest(
            name = name,
            description = description,
            price = price,
            stockQuantity = stockQuantity,
            category = category,
            images = images
        )
        return productApi.createProduct(createProductRequest)
    }

    // ✅ Nueva función para actualizar producto
    suspend fun updateProduct(
        uuid: String,
        name: String? = null,
        description: String? = null,
        price: Double? = null,
        stockQuantity: Int? = null,
        category: String? = null,
        images: List<String>? = null
    ): Response<SingleProductResponse> {
        val updateProductRequest = UpdateProductRequest(
            name = name,
            description = description,
            price = price,
            stockQuantity = stockQuantity,
            category = category,
            images = images
        )
        return productApi.updateProduct(uuid, updateProductRequest)
    }

    // ✅ Nueva función para eliminar producto
    suspend fun deleteProduct(uuid: String): Response<SingleProductResponse> {
        return productApi.deleteProduct(uuid)
    }
}