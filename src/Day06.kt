fun main() {
	data class Race(
		val time: Long,
		val distance: Long,
	) {
		constructor(pair: Pair<Long, Long>) : this(pair.first, pair.second)
	}

	fun parseRaces(input: List<String>) = input.map { line ->
		line.split(' ')
			.filter { sepStr -> sepStr.isNotEmpty() && sepStr.all { it.isDigit() } }
			.map { it.toLong() }
	}
		.let { it[0].zip(it[1]) }
		.map { Race(it) }

	fun parseRace(input: List<String>) = input.map { line -> line.filter { it.isDigit() } }
		.let { Race(it[0].toLong(), it[1].toLong()) }

	fun isEnoughToWin(timeHolding: Long, race: Race) = timeHolding * (race.time - timeHolding) > race.distance

	fun part1(input: List<String>) = parseRaces(input).map { race ->
		// just bruteforce it bruh !!
		(1..<race.time).count { isEnoughToWin(it, race) }
	}.reduce(Int::times)


	fun part2(input: List<String>) = parseRace(input).let { race ->
		val range = 1..<race.time
		// don't bruteforce just get first and last :troll:
		val winningRange =
			range.first { isEnoughToWin(it, race) }..range.reversed().first { isEnoughToWin(it, race) }
		winningRange.count()
	}

	val testInput = readInput("Day06_test")
	check(part1(testInput).also { it.println() } == 288)
	check(part2(testInput).also { it.println() } == 71503)


	val input = readInput("Day06")
	part1(input).println()
	part2(input).println()
}
