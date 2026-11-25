package com.akhilav.nutrilens.utils.parser


object NutritionLabelParser {

    private fun normalize(text: String): String {
        return text.uppercase()
            .replace(" ", "")
            .replace("*", "")
            .replace(":", "")
            .replace(".", "")
    }

    private fun isServingHeader(text: String): Boolean {
        val value = normalize(text)

        return value.contains("PERSER") ||
                value.contains("SERVING")
    }

    private fun isPer100gHeader(text: String): Boolean {
        val value = normalize(text)

        return value.startsWith("PER") &&
                !value.contains("SERV") &&
                !value.contains("RDA")
    }

    private fun isRdaHeader(text: String): Boolean {
        val value = normalize(text)

        return value.startsWith("%RDA") ||
                value == "RDA"
    }

    private data class ColumnPositions(
        val servingX: Int,
        val per100gX: Int,
        val rdaX: Int?
    )

    private fun findColumnPositions(
        lines: List<IngredientTextLine>
    ): ColumnPositions? {

        fun findX(predicate: (String) -> Boolean): Int? {
            return lines.firstOrNull {
                predicate(it.text)
            }?.let {
                (it.left + it.right) / 2
            }
        }

        val servingX = findX(::isServingHeader)
        val per100gX = findX(::isPer100gHeader)
        val rdaX = findX(::isRdaHeader)

        if (servingX == null || per100gX == null) {
            return null
        }

        return ColumnPositions(
            servingX = servingX,
            per100gX = per100gX,
            rdaX = rdaX
        )
    }

    private fun identifyNutrient(text: String): String? {
        val value = normalize(text).replace("-", "")

        return when {
            value.contains("ENERGY") -> "Energy"
            value.contains("PROTEIN") -> "Protein"
            value.contains("CARBOHYDRATE") -> "Carbohydrates"
            value.contains("TOTALSUGAR") -> "Total Sugar"
            value.contains("ADDEDSUGAR") -> "Added Sugar"
            value.contains("DIETARYFIBRE") ||
                    value.contains("DIETARYFIBER") -> "Dietary Fibre"
            value.contains("SATURATEDFAT") -> "Saturated Fat"
            value.contains("TRANSFAT") -> "Trans Fat"
            value.contains("TOTALFAT") -> "Total Fat"
            value.contains("CHOLESTEROL") -> "Cholesterol"
            value.contains("SODIUM") -> "Sodium"
            value.contains("VITAMIN") -> "Vitamin"
            value.contains("PHOSPHORUS") -> "Phosphorus"
            value.contains("MAGNESIUM") -> "Magnesiusm"
            value.contains("CALCIUM") -> "Calcium"
            value.contains("IRON") -> "Iron"
            value.contains("CALORIES") -> "Calories"
            value.contains("POTASSIUM") -> "Potassium"
            else -> null
        }
    }

    private fun extractNumericValue(text: String): String? {
        val value = text.trim()
            .replace(",", ".")

        return if (
            Regex("""^\d+(?:\.\d+)?%?$""").matches(value)
        ) {
            value
        } else {
            null
        }
    }

    fun parse(
        lines: List<IngredientTextLine>
    ): List<NutritionLabelData> {

        // First, check for a single-column nutrition label.
        val singleColumnResults = parsingSingleColumnFormat(lines)

        if(singleColumnResults.isNotEmpty()){
            return singleColumnResults
        }
        // Otherwise, use the existing multi-column parser.
        val columns = findColumnPositions(lines)
            ?: return emptyList()

        val nutrientLines = lines.mapNotNull { line ->
            val nutrient = identifyNutrient(line.text)
                ?: return@mapNotNull null

            nutrient to line
        }

        val numericLines = lines.mapNotNull { line ->
            val value = extractNumericValue(line.text)
                ?: return@mapNotNull null

            Triple(
                line,
                value,
                (line.top + line.bottom) / 2
            )
        }

        return nutrientLines.mapNotNull { (nutrient, nutrientLine) ->

            val rowY = (nutrientLine.top + nutrientLine.bottom) / 2

            fun findValue(columnX: Int?): String? {
                if (columnX == null) return null

                return numericLines
                    .filter { (_, _, y) ->
                        kotlin.math.abs(y - rowY) <= 90
                    }
                    .minByOrNull { (line, _, y) ->
                        kotlin.math.abs(
                            ((line.left + line.right) / 2) - columnX
                        ) + kotlin.math.abs(y - rowY) * 2
                    }
                    ?.second
            }

            val serving = findValue(columns.servingX)
            val per100g = findValue(columns.per100gX)
            val rda = findValue(columns.rdaX)

            if (serving == null && per100g == null && rda == null) {
                return@mapNotNull null
            }

            NutritionLabelData(
                nutrient = nutrient,
                perServing = serving,
                per100g = per100g,
                rda = rda
            )
        }
    }

    private fun parsingSingleColumnFormat(lines: List<IngredientTextLine>): List<NutritionLabelData>{
        val has100gHeader = lines.any {
            normalize(it.text).contains("NUTRITIONFACTS100G") ||
                    normalize(it.text).contains("PER100G")
        }

        if(!has100gHeader) return emptyList()

        return lines.mapNotNull { line ->
            val nutrient = identifyNutrient(line.text) ?: return@mapNotNull null

            val valueText = line.text.substringAfter(":",line.text)

            val match = Regex(
                """[-–—]?\s*([0-9][0-9,]*(?:\.[0-9]+)?)\s*(kcal|mg|g)?""",
                RegexOption.IGNORE_CASE
            ).find(valueText) ?: return@mapNotNull null

            val amount = match.groupValues[1]
            val unit = match.groupValues[2]

            NutritionLabelData(
                nutrient = nutrient, per100g = if(unit.isBlank()) amount else "$amount $unit"
            )

        }

    }

}
