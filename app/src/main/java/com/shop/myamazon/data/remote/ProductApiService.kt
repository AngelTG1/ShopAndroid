package com.shop.myamazon.data.remote

import com.shop.myamazon.data.remote.models.CreateProductRequest
import com.shop.myamazon.data.remote.models.ProductsResponse
import com.shop.myamazon.data.remote.models.SingleProductResponse
import com.shop.myamazon.data.remote.models.UpdateProductRequest
import retrofit2.Response
import retrofit2.http.*

interface ProductApiService {

    // Obtener todos los productos (público)
    @GET("products")
    suspend fun getAllProducts(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("category") category: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null
    ): Response<ProductsResponse>

    // Buscar productos (público)
    @GET("products/search")
    suspend fun searchProducts(
        @Query("search") searchQuery: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<ProductsResponse>

    // Obtener producto por UUID (público)
    @GET("products/{uuid}")
    suspend fun getProductByUuid(
        @Path("uuid") uuid: String
    ): Response<SingleProductResponse>

    // Obtener mis productos (requiere autenticación)
    @GET("products/my-products")
    suspend fun getMyProducts(): Response<ProductsResponse>

    // ✅ Crear nuevo producto (requiere autenticación Premium)
    @POST("products")
    suspend fun createProduct(
        @Body createProductRequest: CreateProductRequest
    ): Response<SingleProductResponse>

    // ✅ Actualizar producto (requiere autenticación Premium)
    @PUT("products/{uuid}")
    suspend fun updateProduct(
        @Path("uuid") uuid: String,
        @Body updateProductRequest: UpdateProductRequest
    ): Response<SingleProductResponse>

    // ✅ Eliminar producto (requiere autenticación Premium)
    @DELETE("products/{uuid}")
    suspend fun deleteProduct(
        @Path("uuid") uuid: String
    ): Response<SingleProductResponse>
}