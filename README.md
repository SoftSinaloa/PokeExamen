# PokeExamen

App Android que consume la [PokeAPI](https://pokeapi.co/) para mostrar un listado de Pokemon y ver el detalle de cada uno.

## Evidencia de funcionamiento

https://github.com/SoftSinaloa/PokeExamen/raw/main/media/demo.mp4

## Como correr el proyecto

1. Clonar el repo
2. Abrir con Android Studio Hedgehog 2023.1 o superior
3. Sincronizar Gradle y correr en emulador o dispositivo con Android 7.0+

La primera vez descarga los datos de la API, despues funciona sin conexion gracias al cache en Room.

## Como correr las pruebas unitarias

Desde Android Studio: clic derecho en la carpeta `test` → Run Tests.

O desde terminal:

```bash
./gradlew testDebugUnitTest
```

## Arquitectura

Use Clean Architecture con tres capas:

- **data** — Retrofit, entidades Room, DAOs y `PokemonRepositoryImpl`
- **domain** — modelos, interfaz `PokemonRepository` y casos de uso
- **presentation** — ViewModels con StateFlow, pantallas Compose y NavGraph

Separe el codigo en esas tres capas para que cada parte solo haga lo suyo. Los ViewModels exponen un `StateFlow` con el estado y las pantallas solo se encargan de renderizar.

## Consideraciones tecnicas

- **Retrofit** — consumo de la PokeAPI con `@GET`, interceptor de logs y `GsonConverterFactory`
- **Koin** — inyeccion de dependencias con `single`, `factory` y `viewModel` en `AppModule`
- **Jetpack Compose** — todas las pantallas son composables, sin XML ni Views
- **Coil** — libreria open source para carga y cache de imagenes con `AsyncImage`
- **MVVM** — `ViewModel` expone un `StateFlow` con el estado, la pantalla solo observa y renderiza
- **Navigation Compose** — navegacion entre pantallas con `NavHost` y `composable`, equivalente al Navigation Component con fragments pero para Compose

## Flujo de datos

Cache-first: primero se busca en Room y solo se va a red si no hay datos. Al paginar, los nuevos resultados se guardan en la BD local para que esten disponibles offline despues.

## Funcionalidades

- Splash screen con animacion Lottie al abrir la app
- Grid de 3 columnas con los primeros 20 Pokemon y paginacion automatica al llegar al final
- Busqueda por nombre en tiempo real sobre el listado cargado
- Filtro por tipo (Fuego, Agua, Planta, etc.) con chips horizontales scrollables
- Cards blancas con numero, imagen, nombre y chip de tipo coloreado
- Barra de navegacion inferior con cuatro secciones: Inicio, Coleccion, Descubrir y Mas
- Favoritos persistentes: el corazon en cada card guarda el Pokemon en Room y sobrevive al cerrar la app
- Seccion Coleccion muestra solo los Pokemon marcados como favoritos
- Seccion Descubrir con las 9 regiones del mundo Pokemon (Kanto a Paldea)
- Seccion Mas con estadisticas del entrenador: Pokemon vistos, favoritos, tipo mas frecuente, progreso de Pokedex y distribucion por tipo
- Dialogo de confirmacion al presionar atras para salir de la app
- Detalle con hero coloreado segun el tipo, imagen grande del Pokemon, boton de volver y corazon para marcar favorito desde ahi
- Estadisticas en dos columnas: Base Stats con barras de progreso y seccion About con peso, altura, habilidades y exp base
- Debilidades y resistencias del tipo con chips coloreados y multiplicador
- Loader animado con pokeball en carga inicial, paginacion y pantalla de detalle
- Pantallas de error con reintento en caso de fallo
- contentDescription en imagenes y elementos interactivos para accesibilidad

## Librerias

| Libreria | Para que la use |
|---|---|
| Retrofit + OkHttp | Consumo de la PokeAPI |
| Gson | Serializar respuestas y TypeConverters de Room |
| Koin | Inyeccion de dependencias |
| Coil | Carga de imagenes |
| Room 2.6.1 | Persistencia local |
| Navigation Compose | Navegacion entre pantallas |
| Lottie | Animacion pokeball en splash y loaders |
| Material Icons Extended | Iconos del bottom nav y cards |

## Tests

Pruebas unitarias con `kotlinx-coroutines-test` usando fakes en vez de mocks:

- `GetPokemonListUseCaseTest` — exito, fallo y que el offset llegue bien al repo
- `PokemonListViewModelTest` — carga, error, busqueda y limpiar filtro

## Decisiones y trade-offs

**Gson en vez de Moshi** — ya venia de Retrofit como dependencia, no tenia caso agregar Moshi solo para los converters de Room.

**Paginacion manual en vez de Paging 3** — Paging 3 tiene bastante setup (PagingSource, RemoteMediator, LazyPagingItems) para lo que necesitaba. Preferi un scroll infinito simple que hace lo mismo sin tanta complejidad.

**Fakes en vez de MockK** — para este proyecto los fakes son suficientes y no necesitan dependencias extra.

**Room para persistencia** — queries tipadas, coroutines nativo, y funciona bien con KSP. La estrategia cache-first fue suficiente para cubrir el caso offline sin complicar mas la arquitectura. Los favoritos se guardan en una tabla separada (`favorites`) para que los `INSERT` de nuevas paginas de Pokemon no los pisen.

**Lottie en vez de animaciones custom** — para el loader y el splash queria algo visualmente llamativo sin tener que dibujar frame a frame. Lottie carga un JSON liviano y se integra bien con Compose con un composable reutilizable (`PokeLoader`).
