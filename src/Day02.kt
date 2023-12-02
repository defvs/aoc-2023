enum class Colors(val str: String, val limit: Int) {
	Red("red", 12),
	Green("green", 13),
	Blue("blue", 14);

	companion object {
		fun fromString(str: String) = entries.single { it.str == str }
	}
}

fun main() {
	fun parseGame(line: String): List<Map<Colors, Int>> = line
		.substringAfter(':')
		.trim()
		.split(';')
		.map { set ->
			set.split(',')
				.associate {  setContent ->
					setContent.trim().split(' ').let {
						Colors.fromString(it[1]) to it[0].toInt()
					}
				}
		}

	fun part1(input: List<String>) = input.map(::parseGame).withIndex().sumOf { (index, game) ->
		val isInvalid = game.any { set -> set.any { (color, amount) -> amount > color.limit } }
		if (!isInvalid) index + 1
		else 0
	}

	fun part2(input: List<String>) = input.map(::parseGame).map { game: List<Map<Colors, Int>> ->
		game.map { it.toList() }
			.flatten()
			.groupBy { it.first }
			.mapValues { (_, value: List<Pair<Colors, Int>>) ->
				value.maxBy { it.second }.second
			}
			.values.reduce(Int::times)
	}.sum()

	val testInput = readInput("Day02_test")
	check(part1(testInput).also { it.println() } == 8)
	check(part2(testInput).also { it.println() } == 2286)


	val input = readInput("Day02")
	part1(input).println()
	part2(input).println()
}
