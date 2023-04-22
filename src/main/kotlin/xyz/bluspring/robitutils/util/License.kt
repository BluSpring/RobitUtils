package xyz.bluspring.robitutils.util

import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.util.*

data class License(
    val type: LicenseType,
    val expiryDate: Long,
    val forged: Boolean,
    val id: String
) : ConfigurationSerializable {
    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "type" to type.name,
            "expiryDate" to expiryDate,
            "forged" to forged,
            "id" to id
        )
    }

    companion object {
        fun deserialize(data: MutableMap<String, Any>): License {
            return License(
                LicenseType.valueOf(data["type"] as String),
                data["expiryDate"] as Long,
                data["forged"] as Boolean,
                data["id"] as String
            )
        }
    }
}