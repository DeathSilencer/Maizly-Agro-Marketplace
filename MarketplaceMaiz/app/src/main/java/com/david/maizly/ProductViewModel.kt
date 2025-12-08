package com.david.maizly

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.util.UUID
import java.util.Locale

// --- ¡NUEVO! ---
// Función para normalizar texto (quitar acentos y minúsculas)
private fun String.normalizeForSearch(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "").lowercase(Locale.getDefault())
}
// --- FIN NUEVO ---


// --- ¡NUEVO! ---
// Definimos el Enum aquí para que el ViewModel lo conozca
enum class ViewMode { LIST, GRID }
// --- FIN NUEVO ---

// Estado para la pantalla de "Añadir Producto"
data class AddProductState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val successMessage: String? = null
)
// --- ¡NUEVO! Estado para Actualizar/Eliminar Productos ---
data class ProductUpdateState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val deleteSuccess: Boolean = false,
    val updateSuccess: Boolean = false // <-- AÑADIDO
)
class ProductViewModel : ViewModel() {

    // --- MANEJO DE ESTADO DE LA LISTA DE PRODUCTOS ---
    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    // --- ¡ESTA ES LA LÍNEA QUE FALTABA! ---
    val allProducts: StateFlow<List<Product>> = _allProducts.asStateFlow()

    // --- ¡NUEVO! MANEJO DE LA LISTA DE PRODUCTORES ---
    private val _allProducers = MutableStateFlow<List<Producer>>(emptyList())
    val allProducers: StateFlow<List<Producer>> = _allProducers.asStateFlow() // <-- ¡ARREGLO!

    // --- MANEJO DE LA BÚSQUEDA ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow() // <-- ¡ARREGLO!

    // Lista "pública" FILTRADA de productos
    val products: StateFlow<List<Product>> = _allProducts
        .combine(_searchQuery) { allProducts, query ->
            if (query.isBlank()) {
                allProducts
            } else {
                val normalizedQuery = query.normalizeForSearch()
                allProducts.filter {
                    it.nombre.normalizeForSearch().contains(normalizedQuery) ||
                            it.producerName.normalizeForSearch().contains(normalizedQuery)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- MANEJO DE ESTADO DE "AÑADIR PRODUCTO" ---
    private val _addProductState = MutableStateFlow(AddProductState())
    val addProductState: StateFlow<AddProductState> = _addProductState.asStateFlow() // <-- ¡ARREGLO!

    // --- MANEJO DEL PRODUCTO SELECCIONADO ---
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow() // <-- ¡ARREGLO!

    // --- ¡NUEVO! MANEJO DEL PRODUCTOR SELECCIONADO ---
    private val _selectedProducer = MutableStateFlow<Producer?>(null)
    val selectedProducer: StateFlow<Producer?> = _selectedProducer.asStateFlow() // <-- ¡ARREGLO!

    // --- MANEJO DEL MODO DE VISTA ---
    private val _viewMode = MutableStateFlow(ViewMode.LIST)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow() // <-- ¡ARREGLO!
    // --- ¡NUEVO! Lista filtrada solo para el usuario actual ---
    val userProducts: StateFlow<List<Product>> = allProducts
        .map { allProductsList ->
            // Filtra por el ID "p_user" que asignamos en addProduct
            allProductsList.filter { it.producerId == "p_user" }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    // --- ¡NUEVO! Estado para Actualizar/Eliminar ---
    private val _productUpdateState = MutableStateFlow(ProductUpdateState())
    val productUpdateState: StateFlow<ProductUpdateState> = _productUpdateState.asStateFlow()

    fun toggleViewMode() {
        _viewMode.value = if (_viewMode.value == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
    // --- FIN NUEVO ---

    init {
        // Carga los productos de demo al iniciar
        loadDemoData()
    }

    private fun loadDemoData() {
        val demoProducers = listOf(
            Producer(
                id = "p1",
                name = "Familia López",
                location = "Toluca, Estado de México",
                imageUrl = "https://www.la7tv.es/asset/thumbnail,1280,720,center,center/media/la7tv/images/2025/11/05/2025110514293667372.jpg", // <-- AQUÍ PUEDES CAMBIAR LA URL
                description = "Somos una familia con más de 50 años de tradición en el cultivo de maíz cacahuazintle. Nuestro maíz es 100% orgánico y cosechado a mano."
            ),
            Producer(
                id = "p2",
                name = "Rancho San Juan",
                location = "Querétaro, Querétaro",
                imageUrl = "https://www.gob.mx/cms/uploads/article/main_image/154007/02_Manejo_integral_de_ranchos_ganaderos.jpg", // <-- AQUÍ PUEDES CAMBIAR LA URL
                description = "Especialistas en elote dulce y maíz forrajero. Nuestros procesos garantizan la frescura y dulzura de cada elote."
            ),
            Producer(
                id = "p3",
                name = "Tortillería Doña Chela",
                location = "Milpa Alta, CDMX",
                imageUrl = "https://s3-media0.fl.yelpcdn.com/bphoto/LIM_6Qa5B2tC_zg1Nqpn6g/o.jpg", // <-- AQUÍ PUEDES CAMBIAR LA URL
                description = "La auténtica tortilla de Milpa Alta. Usamos maíz criollo y nixtamalización tradicional sin cal."
            ),
            Producer(
                id = "p4",
                name = "Molino El Grano de Oro",
                location = "Iztapalapa, CDMX",
                imageUrl = "https://media-cdn.tripadvisor.com/media/photo-s/06/7b/40/28/moli-d-oli-cal-viudo.jpg", // <-- AQUÍ PUEDES CAMBIAR LA URL
                description = "Proveedores de la mejor masa harina para tamales, atoles y tortillas. Calidad industrial y artesanal."
            ),
            Producer(
                id = "p5",
                name = "Productores de Oaxaca",
                location = "Oaxaca, Oaxaca",
                imageUrl = "https://www.gob.mx/cms/uploads/press/main_image/253503/post_230400_AGRICULTURA_JALISCO__VERACRUZ__OAXACA_ENTIDADES_CON_MAYOR_PRODUCCI_N_AGROALIMENTARIA-10.JPG", // <-- AQUÍ PUEDES CAMBIAR LA URL
                description = "Cooperativa de productores de maíz criollo (azul, rojo y amarillo). Rescatamos los sabores ancestrales de México."
            ),
            Producer(
                id = "p6",
                name = "Agricultores de Sinaloa",
                location = "Culiacán, Sinaloa",
                imageUrl = "https://revistaespejo.com/wp-content/uploads/2025/07/agricultura-sinaloa-trabajadores.webp", // <-- AQUÍ PUEDES CAMBIAR LA URL
                description = "Líderes en producción de maíz blanco a gran escala. Calidad de exportación para la industria."
            )
        )
        _allProducers.value = demoProducers

        // Tus productos con tus imágenes
        _allProducts.value = listOf(
            Product(
                id = "1",
                nombre = "Maíz Pozolero (Cacahuazintle)",
                precio = 60.00,
                descripcion = "Maíz de grano grande y suave, ideal para pozole. Cosechado en las faldas del Nevado de Toluca.",
                imageUrl = "https://www.biodiversidad.gob.mx/media/1/usos/maices/grupos/conico/Rm_Cacahuacintle.jpg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                stock = 50,
                producerId = "p1",
                producerName = "Familia López"
            ),
            Product(
                id = "2",
                nombre = "Elote Amarillo Dulce",
                precio = 25.00,
                descripcion = "Elote tierno y dulce, perfecto para asar o hervir. Paquete de 3 piezas.",
                imageUrl = "https://ingredienta.com/wp-content/uploads/2023/07/000ELOTEDORADO.jpg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                stock = 100,
                producerId = "p2",
                producerName = "Rancho San Juan"
            ),
            Product(
                id = "3",
                nombre = "Tortillas Hechas a Mano",
                precio = 30.00,
                descripcion = "1kg de auténticas tortillas de maíz nixtamalizado, hechas a mano. Sabor inigualable.",
                imageUrl = "https://blob.luznoticias.mx/images/2018/01/27/df.jpg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                stock = 30,
                producerId = "p3",
                producerName = "Tortillería Doña Chela"
            ),
            Product(
                id = "4",
                nombre = "Harina de Maíz (Masa Harina)",
                precio = 45.00,
                descripcion = "1kg de harina de maíz de alta calidad para preparar tamales, atole y más.",
                imageUrl = "https://imgs.search.brave.com/qUK22TR4XgUZOtUxF2IxyIFGQACdXsu8WJmP0HBalKY/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/NzFMeGU5cmR5UUwu/anBn?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                stock = 75,
                producerId = "p4",
                producerName = "Molino El Grano de Oro"
            ),
            Product(
                id = "5",
                nombre = "Maíz Azul Criollo",
                precio = 85.00,
                descripcion = "Maíz criollo de color azul intenso, alto en antioxidantes. Ideal para tortillas y pinole. 1kg.",
                imageUrl = "https://www.cimmyt.org/content/uploads/sites/2/2022/09/551_maiz-azul.jpg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                stock = 40,
                producerId = "p5",
                producerName = "Productores de Oaxaca"
            ),
            Product(
                id = "6",
                nombre = "Maíz Blanco Sinaloa",
                precio = 1200.00,
                descripcion = "Costal de 20kg de maíz blanco de Sinaloa, el estándar de oro para la industria de la tortilla.",
                imageUrl = "https://www.gob.mx/cms/uploads/article/main_image/16321/maiz_blanco.jpg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                stock = 15,
                producerId = "p6",
                producerName = "Agricultores de Sinaloa"
            )
        )
    }

    // Lógica para "Añadir Producto" (Modo Demo)
    fun addProduct(
        nombre: String,
        precioStr: String,
        descripcion: String,
        stockStr: String, // <-- ¡NUEVO!
        imageUri: Uri?
    ) {
        if (nombre.isBlank() || precioStr.isBlank() || stockStr.isBlank() || imageUri == null) {
            _addProductState.value = AddProductState(error = "Por favor, rellena todos los campos y selecciona una imagen.")
            return
        }

        val precio = precioStr.toDoubleOrNull()
        val stock = stockStr.toIntOrNull() // <-- ¡NUEVO!

        if (precio == null || precio <= 0) {
            _addProductState.value = AddProductState(error = "El precio no es válido.")
            return
        }

        if (stock == null || stock < 0) { // <-- ¡NUEVO!
            _addProductState.value = AddProductState(error = "La cantidad de stock no es válida.")
            return
        }

        _addProductState.value = AddProductState(isLoading = true)

        viewModelScope.launch {
            try {
                // 1. Simular la subida (1.5 segundos)
                delay(1500)

                // 2. Crear el nuevo producto (en modo demo, usamos la URI local)
                val newProduct = Product(
                    id = UUID.randomUUID().toString(),
                    nombre = nombre,
                    precio = precio,
                    descripcion = descripcion,
                    imageUrl = imageUri.toString(), // Guardamos la URI local
                    stock = stock, // <-- ¡NUEVO!
                    producerId = "p_user", // ID Falso para el usuario
                    producerName = "Productor (Tú)" // Nombre del productor
                )

                // 3. Añadir el producto a nuestra lista local
                _allProducts.value = _allProducts.value + newProduct

                Log.d("ProductViewModel", "Producto demo añadido: $nombre")
                _addProductState.value = AddProductState(isSuccess = true, successMessage = "¡Producto publicado con éxito!")

            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error al añadir producto demo: ${e.message}")
                _addProductState.value = AddProductState(error = "Error al publicar: ${e.message}")
            }
        }
    }

    // Lógica para "Seleccionar Producto" para la pantalla de detalle
    fun selectProductById(id: String) {
        _selectedProduct.value = _allProducts.value.find { it.id == id }
    }

    // --- ¡NUEVA FUNCIÓN! ---
    fun selectProducerById(id: String) {
        _selectedProducer.value = _allProducers.value.find { it.id == id }
    }

    // Resetea el estado de la pantalla "Añadir Producto"
    fun resetAddProductState() {
        _addProductState.value = AddProductState()
    }

    // --- ¡NUEVA FUNCIÓN! ---
    fun deleteProduct(product: Product) {
        _productUpdateState.value = ProductUpdateState(isLoading = true) // Inicia la carga

        viewModelScope.launch {
            try {
                // Simular una llamada de red
                delay(1000)

                // (Modo Demo) Elimina el producto de la lista maestra
                _allProducts.value = _allProducts.value.filter { it.id != product.id }

                Log.i("ProductViewModel", "Producto demo eliminado: ${product.nombre}")
                _productUpdateState.value = ProductUpdateState(deleteSuccess = true) // ¡Éxito!
            } catch (e: Exception) {
                _productUpdateState.value = ProductUpdateState(error = "Error al eliminar el producto.")
            }
        }
    }

    // --- ¡NUEVA FUNCIÓN! ---
    fun resetProductUpdateState() {
        _productUpdateState.value = ProductUpdateState()
    }

    // --- ¡NUEVA FUNCIÓN! ---
    fun updateProduct(
        productToUpdate: Product,
        nombre: String,
        precioStr: String,
        descripcion: String,
        stockStr: String,
        imageUri: Uri? // La nueva imagen seleccionada (o null si no cambió)
    ) {
        if (nombre.isBlank() || precioStr.isBlank() || stockStr.isBlank()) {
            _productUpdateState.value = ProductUpdateState(error = "Los campos nombre, precio y stock no pueden estar vacíos.")
            return
        }

        val precio = precioStr.toDoubleOrNull()
        val stock = stockStr.toIntOrNull()

        if (precio == null || precio <= 0) {
            _productUpdateState.value = ProductUpdateState(error = "El precio no es válido.")
            return
        }
        if (stock == null || stock < 0) {
            _productUpdateState.value = ProductUpdateState(error = "La cantidad de stock no es válida.")
            return
        }

        _productUpdateState.value = ProductUpdateState(isLoading = true)

        viewModelScope.launch {
            try {
                // Simular subida
                delay(1500)

                // Si el usuario eligió una nueva URI, usa esa.
                // Si no, MANTIENE la URI original del producto.
                val finalImageUrl = imageUri?.toString() ?: productToUpdate.imageUrl

                // Crea el producto actualizado
                val updatedProduct = productToUpdate.copy(
                    nombre = nombre,
                    precio = precio,
                    descripcion = descripcion,
                    stock = stock,
                    imageUrl = finalImageUrl
                )

                // Reemplaza el producto antiguo en la lista maestra
                _allProducts.value = _allProducts.value.map {
                    if (it.id == updatedProduct.id) {
                        updatedProduct
                    } else {
                        it
                    }
                }

                // Actualiza el producto seleccionado para que la pantalla de detalle vea el cambio
                _selectedProduct.value = updatedProduct

                Log.i("ProductViewModel", "Producto demo actualizado: ${updatedProduct.nombre}")
                _productUpdateState.value = ProductUpdateState(updateSuccess = true)

            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error al actualizar: ${e.message}")
                _productUpdateState.value = ProductUpdateState(error = "Error al actualizar el producto.")
            }
        }
    }
    // --- ¡NUEVA FUNCIÓN DE LIMPIEZA! ---
// Limpia el producto seleccionado al salir de la pantalla de detalle
    fun resetSelectedProduct() {
        _selectedProduct.value = null
    }
}