package com.example.mobile_etno

sealed class NavItem(var route: String, var icon: Int? = null, var title: String? = null) {
    object Splash : NavItem("Splash")
    object ChooseLocality : NavItem("ChooseLocality")
    object Localities : NavItem("Localities")
    object Home : NavItem("home")
    object HomeModern : NavItem("HomeModern")
    object DiscoverySections : NavItem("DiscoverySections")
    object Events : NavItem("Eventos", R.drawable.home_test, "Eventos")
    object EventNameScreen: NavItem("NombreEventoScreen")
    object Reservations : NavItem("Reservaciones", R.drawable.home_test, "Reservaciones")
    object Deaths : NavItem("Muertes", R.drawable.home_test, "Muertes")
    object DetailDeath : NavItem("DeathDetails")
    object Phone : NavItem("Telefonos", R.drawable.home_test, "Telefono")
    object PhoneDetailsList : NavItem("PhoneDetailsList")
    object News : NavItem("Noticias", R.drawable.home_test, "Noticias")
    object NewDetails : NavItem("NewDetails")
    object Gallery : NavItem("Galeria", R.drawable.home_test, "Galería")
    object ImageDetail : NavItem("ImageDetail")
    object Pharmacies: NavItem("Farmacias", R.drawable.home_test, "Farmacias")
    object DetailPharmacy: NavItem("PharmacyDetails")
    object Sponsors: NavItem("Patrocinadores", R.drawable.home_test, "Patrocinadores")
    object Festivities: NavItem("Fiestas", R.drawable.home_test, "Fiestas")
    object Advertisements: NavItem("Anuncios", R.drawable.home_test, "Anuncios")
    object Services: NavItem("Servicios", R.drawable.home_test, "Servicios")
    object Tourism: NavItem("Turismo", R.drawable.home_test, "Turismo")
    object DetailTourism : NavItem("TourismDetails")
    object Incidents: NavItem("Incidentes", R.drawable.home_test, "Incidentes")
    object CreateIncident : NavItem("CrearIncidencia")
    object Links: NavItem("Enlaces", R.drawable.home_test, "enlaces")
    object Bandos: NavItem("Bandos")
}