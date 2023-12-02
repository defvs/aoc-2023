fun main() {
	fun findFirstAndLastDigits(string: String) = String(charArrayOf(
		string.first { it.isDigit() },
		string.last { it.isDigit() }
	)).toInt()

	fun part1(input: List<String>) = input.sumOf { line ->
		findFirstAndLastDigits(line)
	}

	fun String.replacePairs(pairs: List<Pair<String, String>>) =
		pairs.fold(this) { acc, (it1, it2) -> acc.replace(it1, it2, true) }

	val matchingPairs = listOf(
		"one" to "o1e",
		"two" to "t2o",
		"three" to "t3e",
		"four" to "f4r",
		"five" to "f5e",
		"six" to "s6x",
		"seven" to "s7n",
		"eight" to "e8t",
		"nine" to "n9e",
	)

	fun part2(input: List<String>) = input.sumOf { line ->
		findFirstAndLastDigits(line.replacePairs(matchingPairs))
	}

	val testInput = readInput("Day01_test")
	check(part1(testInput).also { it.println() } == 142)
	check(part2(testInput).also { it.println() } == 281)


	val input = readInput("Day01")
	part1(input).println()
	part2(input).println()
}
