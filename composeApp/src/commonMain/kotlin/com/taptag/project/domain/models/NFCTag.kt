package com.taptag.project.domain.models

data class NFCTag(
    val id: String,
    val content: String,
    val type: NFCType
)

enum class NFCType{
    NDEF,
    MIFARE_CLASSIC,
    MIFARE_ULTRALIGHT,
    UNKNOWN
}


