package com.shop.myamazon.data.remote

import com.shop.myamazon.data.remote.models.*
import retrofit2.Response
import retrofit2.http.*

interface CartApiService {

    // GET /cart - Obtener carrito completo
    @GET("cart")
    suspend fun getCart(): Response<CartResponse>

    // GET /cart/count - Obtener solo el conteo (para badge)
    @GET("cart/count")
    suspend fun getCartCount(): Response<CartCountResponse>

    // POST /cart/items - Agregar producto al carrito
    @POST("cart/items")
    suspend fun addToCart(
        @Body request: AddToCartRequest
    ): Response<CartResponse>

    // PUT /cart/items/{itemId} - Actualizar cantidad de un item
    @PUT("cart/items/{itemId}")
    suspend fun updateCartItem(
        @Path("itemId") itemId: String,
        @Body request: UpdateCartItemRequest
    ): Response<CartResponse>

    // DELETE /cart/items/{itemId} - Eliminar item del carrito
    @DELETE("cart/items/{itemId}")
    suspend fun removeFromCart(
        @Path("itemId") itemId: String
    ): Response<CartResponse>

    // DELETE /cart - Vaciar carrito completamente
    @DELETE("cart")
    suspend fun clearCart(): Response<CartResponse>

    // POST /cart/save-for-later/{itemId} - Guardar item para después
    @POST("cart/save-for-later/{itemId}")
    suspend fun saveForLater(
        @Path("itemId") itemId: String
    ): Response<CartResponse>

    // GET /cart/saved-items - Obtener items guardados
    @GET("cart/saved-items")
    suspend fun getSavedItems(): Response<SavedItemsResponse>
}