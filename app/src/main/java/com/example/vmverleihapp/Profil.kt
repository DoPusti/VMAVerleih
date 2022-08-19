package com.example.vmverleihapp

data class Profil(
    var vorname: String? = null,
    var nachname: String? = null,
    val email: String? = null,
    var contact: String? = null,
    var imgUri : String? = null,
    var id: String?
)
{
    constructor() : this("","", "", "", "", "")
}
