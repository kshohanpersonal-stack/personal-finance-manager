package com.shohankhan.ledgerly.domain.money

/**
 * A self contained currency definition.
 *
 * Symbols are declared here rather than read from [java.util.Currency] so that
 * formatting is identical on every device, OS version and JVM used by tests.
 */
data class CurrencyDef(
    val code: String,
    val displayName: String,
    val symbol: String,
    val fractionDigits: Int
) {
    val minorUnitScale: Int get() = fractionDigits
}

object Currencies {

    val BDT = CurrencyDef("BDT", "Bangladeshi Taka", "৳", 2)
    val USD = CurrencyDef("USD", "US Dollar", "$", 2)
    val EUR = CurrencyDef("EUR", "Euro", "€", 2)
    val GBP = CurrencyDef("GBP", "British Pound", "£", 2)
    val INR = CurrencyDef("INR", "Indian Rupee", "₹", 2)
    val AED = CurrencyDef("AED", "UAE Dirham", "د.إ", 2)
    val SAR = CurrencyDef("SAR", "Saudi Riyal", "﷼", 2)

    val ALL: List<CurrencyDef> = listOf(
        BDT, USD, EUR, GBP, INR, AED, SAR,
        CurrencyDef("AUD", "Australian Dollar", "A$", 2),
        CurrencyDef("CAD", "Canadian Dollar", "C$", 2),
        CurrencyDef("SGD", "Singapore Dollar", "S$", 2),
        CurrencyDef("MYR", "Malaysian Ringgit", "RM", 2),
        CurrencyDef("PKR", "Pakistani Rupee", "Rs", 2),
        CurrencyDef("LKR", "Sri Lankan Rupee", "Rs", 2),
        CurrencyDef("NPR", "Nepalese Rupee", "Rs", 2),
        CurrencyDef("QAR", "Qatari Riyal", "QR", 2),
        CurrencyDef("KWD", "Kuwaiti Dinar", "KD", 3),
        CurrencyDef("JPY", "Japanese Yen", "¥", 0),
        CurrencyDef("CNY", "Chinese Yuan", "¥", 2),
        CurrencyDef("TRY", "Turkish Lira", "₺", 2),
        CurrencyDef("IDR", "Indonesian Rupiah", "Rp", 2),
        CurrencyDef("THB", "Thai Baht", "฿", 2),
        CurrencyDef("VND", "Vietnamese Dong", "₫", 0),
        CurrencyDef("PHP", "Philippine Peso", "₱", 2),
        CurrencyDef("NGN", "Nigerian Naira", "₦", 2),
        CurrencyDef("KES", "Kenyan Shilling", "KSh", 2),
        CurrencyDef("ZAR", "South African Rand", "R", 2),
        CurrencyDef("BRL", "Brazilian Real", "R$", 2),
        CurrencyDef("CHF", "Swiss Franc", "CHF", 2),
        CurrencyDef("SEK", "Swedish Krona", "kr", 2),
        CurrencyDef("AED_ALT", "UAE Dirham (Latin)", "AED", 2)
    )

    val DEFAULT: CurrencyDef = BDT

    /** Never throws: unknown codes fall back to the default currency. */
    fun byCode(code: String?): CurrencyDef {
        if (code.isNullOrBlank()) return DEFAULT
        val wanted = code.trim().uppercase()
        return ALL.firstOrNull { it.code == wanted } ?: DEFAULT
    }

    fun isValid(code: String?): Boolean =
        !code.isNullOrBlank() && ALL.any { it.code.equals(code.trim(), ignoreCase = true) }
}
