package com.shop.shop.data.remote.repository

import com.shop.shop.data.remote.RetrofitClient
import com.shop.shop.data.remote.models.*
import retrofit2.Response

class CartRepository {
    private val cartApi = RetrofitClient.cartApi

    // Obtener carrito completo
    suspend fun getCart(): Response<CartResponse> {
        return cartApi.getCart()
    }

    // Obtener solo el conteo (para badge)
    suspend fun getCartCount(): Response<CartCountResponse> {
        return cartApi.getCartCount()
    }

    // Agregar producto al carrito
    suspend fun addToCart(productId: Int, quantity: Int): Response<CartResponse> {
        val request = AddToCartRequest(productId, quantity)
        return cartApi.addToCart(request)
    }

    // Actualizar cantidad de un item
    suspend fun updateCartItem(itemId: String, quantity: Int): Response<CartResponse> {
        val request = UpdateCartItemRequest(quantity)
        return cartApi.updateCartItem(itemId, request)
    }

    // Eliminar item del carrito
    suspend fun removeFromCart(itemId: String): Response<CartResponse> {
        return cartApi.removeFromCart(itemId)
    }

    // Vaciar carrito completamente
    suspend fun clearCart(): Response<CartResponse> {
        return cartApi.clearCart()
    }

    // Guardar item para después
    suspend fun saveForLater(itemId: String): Response<CartResponse> {
        return cartApi.saveForLater(itemId)
    }

    // Obtener items guardados
    suspend fun getSavedItems(): Response<SavedItemsResponse> {
        return cartApi.getSavedItems()
    }
}