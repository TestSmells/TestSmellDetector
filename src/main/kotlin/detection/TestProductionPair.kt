package detection

data class TestProductionPair(
        val testClass: String = "",
        val productionClass: String = "",
        val testClassPath: String,
        val productionClassPath: String
)
