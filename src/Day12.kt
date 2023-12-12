fun main() {
	fun List<Char>.countConsecutiveHashes(): List<Int> {
		val counts = mutableListOf<Int>()
		var currentCount = 0

		this.forEach { char ->
			if (char == '#') {
				currentCount++
			} else {
				if (currentCount > 0) {
					counts.add(currentCount)
					currentCount = 0
				}
			}
		}

		if (currentCount > 0) counts.add(currentCount)
		return counts
	}

	fun part1(input: List<String>) = input.map { line ->
		val groups = line.substringAfter(' ').split(',').map { it.toInt() }
		val records = line.substringBefore(' ')

		groups to records.toList()
	}.sumOf { (groups, records) ->
		records.map { c ->
			if (c == '?') listOf('#', '.')
			else listOf(c)
		}.fold(listOf(listOf<Char>())) { acc, list ->
			acc.flatMap { combination ->
				list.map { char ->
					combination + char
				}
			}
		}.count {
			it.countConsecutiveHashes() == groups
		}
	}

	fun part2(input: List<String>) = input.size

	val testInput = readInput("Day12_test")
	check(part1(testInput).also { it.println() } == 21)
//	check(part2(testInput).also { it.println() } == 281)


	val input = readInput("Day12")
	part1(input).println()
//	part2(input).println()
}
