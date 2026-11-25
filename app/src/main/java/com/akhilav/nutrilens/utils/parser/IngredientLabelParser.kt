package com.akhilav.nutrilens.utils.parser


object IngredientLabelParser {

    private val packagingMarkers = listOf(
        "MANUFACTURER AND MARKETED BY",
        "MARKETED BY",
        "MANUFACTURER BY",
        "AFTER OPENING",
        "STORE IN A COOL",
        "STORE IN COOL",
        "STORE IN A DRY",
        "STORE IN COOL AND DRY",
        "KEEP IN A COOL",
        "KEEP IN A DRY",
        "AIRTIGHT CONTAINER",
        "TRANSFER CONTENTS",
        "REFRIGERATE AFTER OPENING",
        "CONSUME WITHIN",
        "MANUFACTURED BY",
        "MANUFACTURED FOR",
        "MANUFACTURING DATE",
        "MFG DATE",
        "MFG BY",
        "PACKED BY",
        "MARKETED BY",
        "BEST BEFORE",
        "USE BY",
        "EXPIRY DATE",
        "EXPIRATION DATE",
        "FSSAI",
        "LIC NO",
        "LICENSE NO",
        "CUSTOMER CARE",
        "TOLL FREE",
        "STORE IN",
        "STORAGE",
        "GUARANTEE",
        "QUALITY ASSURANCE",
        "NET WEIGHT",
        "BATCH NO",
        "BATCH NUMBER",
        "MRP",
        "PRICE"
    )

    private fun findPackagingStart(text: String): Int {
        val upperText = text.uppercase()

        return packagingMarkers
            .map { upperText.indexOf(it) }
            .filter { it >= 0 }
            .minOrNull() ?: text.length
    }

    fun parse(
        lines: List<IngredientTextLine>
    ): IngredientLabelData? {

        val fullText = lines
            .sortedBy { it.top }
            .filterNot { line ->
                val value = line.text.trim().lowercase()

                value.matches(
                    Regex("""^[a-z0-9_-]{5,}\.? (jpg|jpeg|png|webp)$""")
                ) ||
                        value.matches(
                            Regex("""^[a-z0-9_-]{5,}\.?(jpg|jpeg|png|webp)$""")
                        ) ||
                        value in listOf(
                    "open with",
                    "com"
                )
            }
            .joinToString("\n") { it.text }
            .replace(Regex("\\s+"), " ")
            .trim()

        val upperText = fullText.uppercase()

        val ingredientsHeader = listOf(
            "INGREDIENTS",
            "INGREDNENTS",
            "NGREDNENTS"
        ).firstOrNull {
            upperText.contains(it)
        }

        val ingredientsStart = ingredientsHeader?.let {
            upperText.indexOf(it)
        } ?: -1

        if (ingredientsStart == -1) return null

        val contentStart = ingredientsStart +
                (ingredientsHeader?.length ?: 0)

        val containsStart = upperText.indexOf(
            "CONTAINS",
            contentStart
        )

        val mayContainStart = upperText.indexOf(
            "MAY CONTAIN",
            contentStart
        )

        val packagingStart = findPackagingStart(
            fullText.substring(contentStart)
        ) + contentStart

        val boundaries = listOf(
            containsStart,
            mayContainStart,
            packagingStart
        ).filter {
            it >= contentStart
        }

        val ingredientsEnd = boundaries.minOrNull()
            ?: fullText.length

        val ingredients = fullText
            .substring(contentStart, ingredientsEnd)
            .trim()
            .trimStart(':', '-', ' ')
            .trimEnd('.', ':', ';')
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString("\n")

        fun extractStatement(
            start: Int,
            nextStart: Int
        ): String? {
            if (start < 0 || start >= fullText.length) {
                return null
            }

            val end = listOf(
                nextStart,
                packagingStart,
                fullText.length
            ).filter {
                it > start
            }.minOrNull() ?: fullText.length

            return fullText
                .substring(start, end)
                .trim()
                .takeIf { it.isNotBlank() }
        }

        val containsStatement = extractStatement(
            containsStart,
            mayContainStart
        )

        val mayContainStatement = extractStatement(
            mayContainStart,
            -1
        )

        if (
            ingredients.isBlank() &&
            containsStatement == null &&
            mayContainStatement == null
        ) {
            return null
        }

        return IngredientLabelData(
            ingredientsText = ingredients,
            containsStatement = containsStatement,
            mayContainStatement = mayContainStatement
        )
    }
}
