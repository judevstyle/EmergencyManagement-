package com.idon.emergencmanagement.model

data class CompanyData(
    var comp_id: String?,
    var comp_name: String?,
    var location_company_lat: Double?,
    var location_company_lng: Double?,
    var location_evacuate_lat: Double?,
    var location_evacuate_lng: Double?,
    var distance: Int?

)