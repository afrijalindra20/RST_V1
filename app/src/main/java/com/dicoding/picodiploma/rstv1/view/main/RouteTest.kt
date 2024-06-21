import com.dicoding.picodiploma.rstv1.view.main.APIService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.runBlocking

class RouteTest {
    private val apiService = APIService()
    private val jakarta = LatLng(-6.2088, 106.8456)

    fun testRouteAndEmergency() = runBlocking {
        println("Mulai pengujian rute dan fitur emergency")

        // Test rute normal
        testRoute("Jakarta", "Bandung", "car", 50f, 10f)
        testRoute("Jakarta", "Surabaya", "car", 60f, 12f)

        // Test fitur emergency
        testEmergency(jakarta, "gas_station")
        testEmergency(jakarta, "rest_area")
        testEmergency(jakarta, "lodging")

        println("Pengujian selesai")
    }

    private suspend fun testRoute(start: String, destination: String, vehicleType: String, tankCapacity: Float, fuelEfficiency: Float) {
        println("\nMerencanakan rute dari $start ke $destination:")
        val startLatLng = apiService.getCity(start) ?: jakarta
        val response = apiService.getRoute(startLatLng, destination, vehicleType)

        println("Rute berhasil direncanakan:")
        println("- Jumlah titik dalam rute: ${response.route.size}")
        println("- Jumlah tempat di sepanjang rute: ${response.places.size}")

        // Hitung jarak total
        val totalDistance = apiService.calculateRouteDistance(response.route)
        println("- Perkiraan jarak total: ${"%.2f".format(totalDistance)} km")

        // Analisis tempat-tempat di sepanjang rute
        val gasStations = response.places.filter { it.type == "gas_station" }
        val restAreas = response.places.filter { it.type == "rest_area" }
        val hotels = response.places.filter { it.type == "lodging" }

        println("- SPBU: ${gasStations.size}")
        println("- Tempat Istirahat: ${restAreas.size}")
        println("- Hotel: ${hotels.size}")

        // Hitung rekomendasi SPBU
        val safeDistance = tankCapacity * fuelEfficiency * 0.8f
        val recommendedRefuelDistance = totalDistance - safeDistance
        if (recommendedRefuelDistance > 0) {
            println("- Rekomendasi isi bahan bakar pada jarak: ${"%.2f".format(recommendedRefuelDistance)} km")
        } else {
            println("- Tidak perlu isi bahan bakar selama perjalanan")
        }
    }

    private suspend fun testEmergency(position: LatLng, type: String) {
        println("\nMencari $type terdekat dalam keadaan darurat:")
        val response = apiService.findNearest(position, type)

        println("Lokasi terdekat ditemukan:")
        println("- Nama: ${response.place.name}")
        println("- Tipe: ${response.place.type}")
        println("- Jarak: ${"%.2f".format(response.place.distance)} km")
        println("- Lokasi: (${response.place.location.latitude}, ${response.place.location.longitude})")
    }
}

fun main() {
    val test = RouteTest()
    test.testRouteAndEmergency()
}