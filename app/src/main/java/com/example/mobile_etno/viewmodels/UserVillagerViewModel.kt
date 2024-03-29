package com.example.mobile_etno.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_etno.isInternetAvailable
import com.example.mobile_etno.models.*
import com.example.mobile_etno.models.ad.Ad
import com.example.mobile_etno.models.bando.Bando
import com.example.mobile_etno.models.link.Link
import com.example.mobile_etno.models.mail.Mail
import com.example.mobile_etno.models.mail.MailSuccess
import com.example.mobile_etno.models.news.New
import com.example.mobile_etno.models.phone.Phone
import com.example.mobile_etno.models.sponsor.Sponsor
import com.example.mobile_etno.service.client.ImageClient
import com.example.mobile_etno.service.client.UserVillagerClient
import com.example.mobile_etno.service.database.SqlDataBase
import com.example.mobile_etno.utils.Parse
import com.example.mobile_etno.viewmodels.locality.LocalityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.net.SocketTimeoutException
import kotlin.system.measureTimeMillis


class UserVillagerViewModel(
    private val context: Context,
    private val sqlDataBase: SqlDataBase,
    private val localityViewModel: LocalityViewModel,
    val eventSubscriptionViewModel: EventSubscriptionViewModel,
    val fcmViewModel: FCMViewModel
): ViewModel() {

    //State to Connection ->
    private val _connection = MutableStateFlow(false)
    val connection: StateFlow<Boolean> = _connection

    val locality = localityViewModel.saveStateLocality

    //State to Events ->
    private val _userVillagerEvents = MutableStateFlow<MutableList<Event>>(mutableListOf())
    val userVillagerEvents: StateFlow<MutableList<Event>> = _userVillagerEvents

    private val _userVillagerEvent = MutableStateFlow(Event())
    val userVillagerEvent: StateFlow<Event> = _userVillagerEvent

    //State to Pharmacies ->
    private val _userVillagerPharmacies = MutableStateFlow<MutableList<Pharmacy>>(mutableListOf())
    val userVillagerPharmacies: StateFlow<MutableList<Pharmacy>> = _userVillagerPharmacies

    private val _userVillagerPharmacy = MutableStateFlow(Pharmacy())
    val userVillagerPharmacy: StateFlow<Pharmacy> = _userVillagerPharmacy

    //State to Tourism ->
    private val _userVillagerTourism = MutableStateFlow<MutableList<Tourism>>(mutableListOf())
    val userVillagerTourism: StateFlow<MutableList<Tourism>> = _userVillagerTourism

    private val _userVillagerTour = MutableStateFlow(Tourism())
    val userVillagerTour: StateFlow<Tourism> = _userVillagerTour

    //State to Deaths ->
    private val _userVillagerDeaths = MutableStateFlow<MutableList<Death>>(mutableListOf())
    val userVillagerDeaths: StateFlow<MutableList<Death>> = _userVillagerDeaths

    //State to Phones ->
    private val _userVillagerPhones = MutableStateFlow<MutableList<Phone>>(mutableListOf())
    val userVillagerPhone: StateFlow<MutableList<Phone>> = _userVillagerPhones

    //State to News ->
    private val _userVillagerNews = MutableStateFlow<MutableList<New>>(mutableListOf())
    val userVillagerNews: StateFlow<MutableList<New>> = _userVillagerNews

    //State to Images by locality ->
    private val _userVillagerImages = MutableStateFlow<MutableList<Image>>(mutableListOf())
    val userVillagerImages: StateFlow<MutableList<Image>> = _userVillagerImages

    //State to Save event list ->
    private val _saveUserVillagerEvents = MutableStateFlow<MutableList<Event>>(mutableListOf())
    private val saveUserVillagerEvents: StateFlow<MutableList<Event>> = _saveUserVillagerEvents

    //State to Save pharmacy list ->
    private val _saveUserVillagerPharmacies = MutableStateFlow<MutableList<Pharmacy>>(mutableListOf())
    private val saveUserVillagerPharmacies: StateFlow<MutableList<Pharmacy>> = _saveUserVillagerPharmacies

    //State to Save tourism list ->
    private val _saveUserVillagerTourism = MutableStateFlow<MutableList<Tourism>>(mutableListOf())
    private val saveUserVillagerTourism: StateFlow<MutableList<Tourism>> = _saveUserVillagerTourism

    //State to Save Phone list ->
    //--------------------------------------------------------------------------------------------
    private val _saveUserVillagerPhones = MutableStateFlow<MutableList<Phone>>(mutableListOf())
    private val saveUserVillagerPhones: StateFlow<MutableList<Phone>> = _saveUserVillagerPhones
    private val _saveStatePhoneCategory = MutableStateFlow("")
    val saveStatePhoneCategory: StateFlow<String> = _saveStatePhoneCategory
    //--------------------------------------------------------------------------------------------

    //State to Save New List ->
    private val _saveUserVillagerNews = MutableStateFlow<MutableList<New>>(mutableListOf())
    private val saveUserVillagerNews: StateFlow<MutableList<New>> = _saveUserVillagerNews
    private val _saveStateNewCategory = MutableStateFlow("")

    //save image list ->
    private val _saveImages = MutableStateFlow<MutableList<Image>>(mutableListOf())
    private val saveImages: StateFlow<MutableList<Image>> = _saveImages

    //State to save incident ->
    private val _saveIncidentsToVillager = MutableStateFlow<List<IncidentModel>>(listOf())
    val saveIncidentsToVillager: StateFlow<List<IncidentModel>> = _saveIncidentsToVillager

    private val _saveIncidentToVillager = MutableStateFlow(IncidentModel())
    val saveIncidentToVillager: StateFlow<IncidentModel> = _saveIncidentToVillager

    //State to pick the mail message ->
    private val _saveUserMessageMail = MutableStateFlow(MailSuccess())
    val saveUserMessage: StateFlow<MailSuccess> = _saveUserMessageMail

    private val _isFinishedMessage = MutableStateFlow(false)
    val isFinishedMessage: StateFlow<Boolean> = _isFinishedMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    //State Bandos ->
    private val _userBandos = MutableStateFlow<List<Bando>>(listOf())
    val userBandos: StateFlow<List<Bando>> = _userBandos

    private val _userBando = MutableStateFlow(Bando())
    val userBando: StateFlow<Bando> = _userBando

    //State Links ->
    private val _userLinks = MutableStateFlow<List<Link>>(listOf())
    val userLinks: StateFlow<List<Link>> = _userLinks

    //State sponsors ->
    private val _userSponsors = MutableStateFlow<List<Sponsor>>(listOf())
    val userSponsors: StateFlow<List<Sponsor>> = _userSponsors

    //State Ads ->
    private val _userAds = MutableStateFlow<List<Ad>>(listOf())
    val userAds: StateFlow<List<Ad>> = _userAds

    var isRefreshing by mutableStateOf(false)

    init {
        checkConnectionInRealTime()
    }

    // Events -> ------------------------------------------------------------------------------------------------------------------------------------------
    fun getUserToVillagerEvents(){
        viewModelScope.launch {
            try {
                val requestUserToVillager = UserVillagerClient.userVillager.getUserByUsernameToVillager(localityViewModel.saveStateLocality.value)
                val body = withContext(Dispatchers.IO){ requestUserToVillager.execute().body() }
                _userVillagerEvents.value = body?.events!!
                _saveUserVillagerEvents.value = userVillagerEvents.value
                Log.d("ev::", userVillagerEvents.value.toString())
                /*
                withContext(Dispatchers.IO){
                    userVillagerEvents.value.forEach { item ->
                        sqlDataBase.insertEventDb(
                            idEvent = item.idEvent,
                            title = item.title,
                            address = item.address,
                            description = item.description,
                            organization = item.organization,
                            link = item.link,
                            startDate = item.startDate,
                            endDate = item.endDate,
                            publicationDate = item.publicationDate,
                            time = item.time,
                            lat = item.lat,
                            long = item.long,
                        )
                        item.images?.forEach { image ->  sqlDataBase.insertImageDb(idImage = image.idImage, linkImage = image.link, idEvent = item.idEvent)}

                        val date = "${Parse.getYear(item.publicationDate!!)}-${Parse.getMouth(item.publicationDate!!)}-${Parse.getDay(item.publicationDate!!)}"

                        Log.d("data:value", date)

                        calendarEvents.value.add(KalendarEvent(
                            date = LocalDate.parse(date),
                            eventName = item.title!!,
                            eventDescription = item.description
                        ))
                    }
                }
                */
                isRefreshing = false
            }catch (_: Exception){  }
        }
    }

    private fun checkConnectionInRealTime(){
        Thread {
            while (true) {
                _connection.value = isInternetAvailable(context = context)
            }
        }.start()
    }

    fun eventsFilterByPublicationDate(date: String){
        val dateParsedToISO = "${Parse.getMouth(date)}-${Parse.getYear(date)}-${Parse.getDay(date)}"
        val filteredEvents = saveUserVillagerEvents.value.filter { event -> event.publicationDate == dateParsedToISO }
            _userVillagerEvents.value = filteredEvents.toMutableList()
    }
    fun resetListEventsConnection(){
        _userVillagerEvents.value.removeAll(userVillagerEvents.value)
    }

    /*
    fun eventFilterByTitle(title: String){
        val filteredEvent = saveUserVillagerEvents.value.find { event -> event.title == title }
        _userVillagerEvent.value = filteredEvent!!
    }
     */
    fun findEventByUsernameAndTitle(title: String){
        viewModelScope.launch {
            try {
                val getEvent = UserVillagerClient.userVillager.getEventByUsernameAndTitle(localityViewModel.saveStateLocality.value, title)
                val body = withContext(Dispatchers.IO){ getEvent.execute().body() }
                _userVillagerEvent.value = body!!
                eventSubscriptionViewModel.updateEventSeats(body.seats!!)
            }catch (_: Exception){  }
        }
    }

    //Pharmacies -> --------------------------------------------------------------------------------------------------------------------------------------------
    fun getUserToVillagerPharmacies(){
        viewModelScope.launch {
            try {
                val requestUserToVillager = UserVillagerClient.userVillager.getUserByUsernameToVillager(localityViewModel.saveStateLocality.value)
                val body = withContext(Dispatchers.IO){ requestUserToVillager.execute().body() }
                _userVillagerPharmacies.value = body?.pharmacies!!
                _saveUserVillagerPharmacies.value = userVillagerPharmacies.value
            }catch (_: Exception){  }
        }
    }
    fun pharmacyFilterByName(name: String){
        val filteredPharmacy = saveUserVillagerPharmacies.value.find { it.name == name }
        _userVillagerPharmacy.value = filteredPharmacy!!
    }
    fun filterAllPharmacies(){
        _userVillagerPharmacies.value = saveUserVillagerPharmacies.value
    }
    fun pharmaciesFilter(type: String){
        val filteredPharmacies = saveUserVillagerPharmacies.value.filter { it.type == type }
        _userVillagerPharmacies.value = filteredPharmacies.toMutableList()
    }

    //Tourism -> --------------------------------------------------------------------------------------------------------------------------------------------
    fun getUserToVillagerTourism(){
        viewModelScope.launch {
            try {
                val requestUserToVillager = UserVillagerClient.userVillager.getUserByUsernameToVillager(localityViewModel.saveStateLocality.value)
                val body = withContext(Dispatchers.IO){ requestUserToVillager.execute().body() }
                _userVillagerTourism.value = body?.tourism!!
                _saveUserVillagerTourism.value = userVillagerTourism.value
            }catch (_: Exception){  }
        }
    }
    fun filterAllTourism(){
        _userVillagerTourism.value = saveUserVillagerTourism.value
    }
    fun tourismFilter(type: String){
        val filteredTourism = saveUserVillagerTourism.value.filter { it.type == type }
        _userVillagerTourism.value = filteredTourism.toMutableList()
    }
    fun tourismFilterByTitle(title: String){
        val filteredTourism = saveUserVillagerTourism.value.find { it.title == title }
        _userVillagerTour.value = filteredTourism!!
    }

    //Deaths -> ------------------------------------------------------------------------------------------------------------------------------------------------
    fun getUserToVillagerDeaths(){
        viewModelScope.launch {
            try {
                val requestUserToVillager = UserVillagerClient.userVillager.getUserByUsernameToVillager(localityViewModel.saveStateLocality.value)
                val body = withContext(Dispatchers.IO){ requestUserToVillager.execute().body() }
                _userVillagerDeaths.value = body?.deaths!!
            }catch (_: Exception){  }
        }
    }

    //Phones -> -------------------------------------------------------------------------------------------------------------------------------------------------
    fun getUserToVillagerPhones(){
        viewModelScope.launch {
            try {
                val requestUserToVillager = UserVillagerClient.userVillager.getUserByUsernameToVillager(localityViewModel.saveStateLocality.value)
                val body = withContext(Dispatchers.IO){ requestUserToVillager.execute().body() }
                _userVillagerPhones.value = body?.phones!!
                _saveUserVillagerPhones.value = body.phones!!
            }catch (_: Exception){  }
        }
    }

    fun phoneFilter(phoneCategory: String){
        Log.d("filter_phone", phoneCategory)
        _saveStatePhoneCategory.value = phoneCategory
        val filteredPhones = saveUserVillagerPhones.value.filter { it.category?.lowercase() == phoneCategory.lowercase()}
        _userVillagerPhones.value = filteredPhones.toMutableList()
    }

    // News -> ------------------------------------------------------------------------------------------------------------------------------------------------
    fun getUserToVillagerNews(){
        viewModelScope.launch {
            try {
                val requestUserToVillager = UserVillagerClient.userVillager.getUserByUsernameToVillager(localityViewModel.saveStateLocality.value)
                val body = withContext(Dispatchers.IO){ requestUserToVillager.execute().body() }
                _userVillagerNews.value = body?.news!!
                _saveUserVillagerNews.value = body.news!!
            }catch (_: Exception){  }
        }
    }

    fun newsFilter(newCategory: String){
        _saveStateNewCategory.value = newCategory
        val filteredNews = saveUserVillagerNews.value.filter { it.category?.lowercase() == newCategory.lowercase() }
        _userVillagerNews.value = filteredNews.toMutableList()
    }
    fun newsFilterAll(){
        _userVillagerNews.value = saveUserVillagerNews.value
    }

    // Images -> ------------------------------------------------------------------------------------------------------------------------------------------------
    fun getImagesByLocality(){
        viewModelScope.launch {
            try {
                val requestImages = ImageClient.imageService.getImagesByLocality(localityViewModel.saveStateLocality.value)
                val body = withContext(Dispatchers.IO){ requestImages.execute().body() }
                _userVillagerImages.value = body!!
                _saveImages.value = body
            }catch (_: Exception){  }
        }
    }
    fun filterImages(category: String){
        val filteredImages = saveImages.value.filter { it.category?.lowercase() == category.lowercase() }
        _userVillagerImages.value = filteredImages.toMutableList()
    }
    fun filterAllImages(){
        _userVillagerImages.value = saveImages.value
    }

    // Mail -> ---------------------------------------------------------------------------------------------------------------------------------------------------
    fun getVillagerIncidents(fcmToken: String){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val requestIncidents = UserVillagerClient.userVillager.getIncidentsByUsernameAndFcmTokenAndTitle(localityViewModel.saveStateLocality.value, fcmToken)
                val response = requestIncidents.execute()

                _saveIncidentsToVillager.value = response.body()!!
            }catch (_: Exception){  }
        }
    }

    fun sendIncidence(mail: Mail, fcmToken: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mail.address = "ecomputerapps@gmail.com"
                    val requestMail = async {
                        val requestMail = UserVillagerClient.userVillager.sendMailWithAttachment(mail)
                        _isLoading.value = true
                        requestMail.execute()
                    }

                val requestIncident = async {
                    val requestIncident = UserVillagerClient.userVillager.addIncidentInUser(localityViewModel.saveStateLocality.value, IncidentModel(fcmToken = fcmToken, title = mail.subject, description = mail.message))
                    requestIncident.execute()
                }

                val time = measureTimeMillis {
                    val responseMail = requestMail.await()
                    val responseIncident = requestIncident.await()

                    _isFinishedMessage.value = true
                    _saveUserMessageMail.value = responseMail.body()!!
                    _saveIncidentToVillager.value = responseIncident.body()!!
                }
            }catch (_: SocketTimeoutException){  }
        }
    }

    fun updateIsFinished(isFinished: Boolean){
        _isFinishedMessage.value = isFinished
    }
    fun updateIsLoading(isLoading: Boolean){
        _isLoading.value = isLoading
    }

    // Bandos -> -----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    val getBandosByUsername: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val requestBandos = UserVillagerClient.userVillager.getBandosByUsername(localityViewModel.saveStateLocality.value)
                val response = requestBandos.execute()

                _userBandos.value = response.body()!!
            }catch (_: Exception){  }
        }
    }
    val getBandoByUsernameAndTitle: (title: String) -> Unit = {
        viewModelScope.launch(Dispatchers.IO){
            try {
                val requestBando = UserVillagerClient.userVillager.getBandoByUsernameAndTitle(username = localityViewModel.saveStateLocality.value, title = it)
                val response = requestBando.execute()

                _userBando.value = response.body()!!
            }catch (_: Exception){  }
        }
    }

    // Links -> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    val getLinksByUsername: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO){
            try {
                val requestLinks = UserVillagerClient.userVillager.getLinksByUsername(localityViewModel.saveStateLocality.value)
                val response = requestLinks.execute()

                _userLinks.value = response.body()!!
            }catch (_: Exception){  }
        }
    }

    // Sponsors -> ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    val getSponsorsByUsername: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val requestSponsors = UserVillagerClient.userVillager.getSponsors(localityViewModel.saveStateLocality.value)
                val response = requestSponsors.execute()

                _userSponsors.value = response.body()!!
            }catch (_: Exception){  }
        }
    }

    // Ads -> ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    val getAdsByUsername: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val requestAds = UserVillagerClient.userVillager.getAdsByUsername(localityViewModel.saveStateLocality.value)
                val response = requestAds.execute()

                _userAds.value = response.body()!!
            }catch (_: Exception){  }
        }
    }
}