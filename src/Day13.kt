fun main() {
	fun List<String>.parseMap() = map { row -> row.map { it == '#' } }
	fun List<String>.splitMaps(): List<List<String>> =
		fold(mutableListOf(mutableListOf<String>())) { acc, element ->
			if (element.isEmpty()) acc.add(mutableListOf())
			else acc.last().add(element)

			acc
		}

	fun List<List<Boolean>>.matchingSection() = indices.toList().dropLast(1).firstOrNull { index ->
		val sublistLeft = this.subList(0, index + 1)
		val sublistRight = this.subList(index + 1, this.size)

		sublistLeft.reversed().zip(sublistRight).all { it.first == it.second }
	}

	fun List<List<Boolean>>.matchingSectionDiffersByOne() = indices.toList().dropLast(1).firstOrNull { index ->
		val sublistLeft = this.subList(0, index + 1)
		val sublistRight = this.subList(index + 1, this.size)

		sublistLeft.reversed().zip(sublistRight).fold(0) { acc, (first, second) ->
			acc + first.zip(second).count { it.first != it.second }
		} == 1
	}


	fun part1(input: List<String>) = input.splitMaps().map { it.parseMap() }.sumOf { map2d ->
		map2d.matchingSection()?.let { return@sumOf (it + 1) * 100 }
		map2d.transpose().matchingSection()?.let { return@sumOf it + 1 }
		throw Exception("No mirroring found.")
	}

	fun part2(input: List<String>) = input.splitMaps().map { it.parseMap() }.sumOf { map2d ->
		map2d.matchingSectionDiffersByOne()?.let { return@sumOf (it + 1) * 100 }
		map2d.transpose().matchingSectionDiffersByOne()?.let { return@sumOf it + 1 }
		throw Exception("No mirroring found.")
	}

	val testInput = readInput("Day13_test")
	check(part1(testInput).also { it.println() } == 405)
	check(part2(testInput).also { it.println() } == 400)


	val input = readInput("Day13")
	part1(input).println()
	part2(input).println()
}
