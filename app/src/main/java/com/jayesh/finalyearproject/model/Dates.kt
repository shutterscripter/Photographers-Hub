package com.jayesh.finalyearproject.model

class Dates {
    var date: String? = null
    var availability: Boolean? = null
    var name: String? = null
    var senderName: String? = null


    constructor() {}

    constructor(date: String, availability: Boolean, name: String, senderName: String) {
        this.date = date
        this.availability = availability
        this.name = name
        this.senderName = senderName
    }
}