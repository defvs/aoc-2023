fun main() {
	data class Card(
		val numbers: List<Int>,
		val winningNumbers: List<Int>,
	) {
		val part2Value = numbers.intersect(winningNumbers.toSet()).count()
		val part1Value = part2Value.let { if (it > 1) 1 shl (it - 1) else it }

		constructor(str: String) : this(
			str.substringBetween(':', '|').split(' ').filter { it.isNotEmpty() }.map { it.toInt() },
			str.substringAfter('|').split(' ').filter { it.isNotEmpty() }.map { it.toInt() },
		)
	}

	fun part1(input: List<String>) = input.sumOf { Card(it).part1Value }

	fun part2(input: List<String>) = input.map { Card(it) }.let { cards ->
		val cardCounts = cards.map { 1 }.toMutableList()
		cards.forEachIndexed { index, value ->
			(index..<(index+value.part2Value)).forEach { cardCounts[it + 1] += cardCounts[index] }
		}
		cardCounts.sum()
	}

	val testInput = readInput("Day04_test")
	check(part1(testInput).also { it.println() } == 13)
	check(part2(testInput).also { it.println() } == 30)

	val input = readInput("Day04")
	part1(input).println()
	part2(input).println()
}
