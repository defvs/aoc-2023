fun main() {
	fun findFirstAndLastDigits(string: String) = String(charArrayOf(
		string.first { it.isDigit() },
		string.last { it.isDigit() }
	)).toInt()

	fun part1(input: List<String>) = input.sumOf { line ->
		findFirstAndLastDigits(line)
	}

	fun String.replaceFirstAndLast(matchings: List<Pair<String, String>>): String {
		val str1  = findAnyOf(matchings.map { it.first }, ignoreCase = true)?.let { (_, firstNum) ->
			this.replace(firstNum, matchings.toMap()[firstNum]!! + firstNum, ignoreCase = true)
		} ?: this

		return str1.findLastAnyOf(matchings.map { it.first }, ignoreCase = true)?.let { (lastNumIndex, lastNum) ->
			str1.replaceRange(lastNumIndex, lastNumIndex+(lastNum.length), matchings.toMap()[lastNum]!!)
		} ?: str1
	}

	val matchingPairs = listOf(
		"one" to "1",
		"two" to "2",
		"three" to "3",
		"four" to "4",
		"five" to "5",
		"six" to "6",
		"seven" to "7",
		"eight" to "8",
		"nine" to "9",
	)

	fun part2(input: List<String>) = input.sumOf { line ->
		findFirstAndLastDigits(line.replaceFirstAndLast(matchingPairs))
	}

	val testInput = readInput("Day01_test")
	check(part1(testInput).also { it.println() } == 142)
	check(part2(testInput).also { it.println() } == 281)


	val input = readInput("Day01")
	part1(input).println()
	part2(input).println()
}
