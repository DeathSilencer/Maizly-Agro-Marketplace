package com.david.maizly

/**
 * Define la estructura de un producto en el marketplace.
 *
 * @param id ID único del producto (generado por el ViewModel).
 * @param nombre Nombre del producto (Ej: "Maíz Pozolero").
 * @param precio El precio del producto.
 * @param descripcion Descripción larga.
 * @param imageUrl La URI de la imagen (local o de Firebase).
 * @param stock La cantidad de unidades disponibles.
 * @param producerId ID único del productor.
 * @param producerName Nombre del vendedor.
 */
data class Product(
    val id: String,
    val nombre: String,
    val precio: Double,
    val descripcion: String,
    val imageUrl: String,
    val stock: Int,
    val producerId: String, // <-- ¡NUEVO CAMPO!
    val producerName: String
)