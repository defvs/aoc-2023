fun main() {
	fun String.parseHistory() = split(' ').map { it.toInt() }
	fun List<Int>.reduceHistory(): List<List<Int>> {
		val reducedHistory = mutableListOf(toMutableList())
		while (!reducedHistory.last().all { it == 0 })
			reducedHistory += reducedHistory.last().windowed(2).map { it[1] - it[0] }.toMutableList()
		return reducedHistory
	}
	fun List<List<Int>>.runPrediction(operation: (ints: List<Int>, acc: Int) -> Int) =
		this.dropLast(1).foldRight(0, operation)

	fun part1(input: List<String>) = input.map(String::parseHistory).sumOf { history ->
		history.reduceHistory().runPrediction { ints, acc -> acc + ints.last() }
	}

	fun part2(input: List<String>) = input.map(String::parseHistory).sumOf { history ->
		history.reduceHistory().map { it.reversed() }.runPrediction { ints, acc -> ints.last() - acc }
	}

	val testInput = readInput("Day09_test")
	check(part1(testInput).also { it.println() } == 114)
	check(part2(testInput).also { it.println() } == 2)


	val input = readInput("Day09")
	part1(input).println()
	part2(input).println()
}
