import kotlin.math.abs
import kotlin.math.max

fun main() {
	fun manhattanDistance(point1: Pair<Long, Long>, point2: Pair<Long, Long>) =
		abs(point1.first - point2.first) + abs(point1.second - point2.second)

	fun List<Pair<Long, Long>>.calculateAllDistancesFP() = this.flatMapIndexed { index, point1 ->
		this.subList(index + 1, this.size).map { point2 ->
			manhattanDistance(point1, point2)
		}
	}

	fun List<List<Char>>.getGalaxyPositions(multiplier: Long = 1L) = this.let { matrix ->
		val galaxyPositions = matrix.flatMapIndexed { rowIndex, row ->
			row.mapIndexedNotNull { colIndex, char ->
				if (char == '#') Pair(colIndex, rowIndex) else null
			}
		}

		val expansionRows = matrix.withIndex().filter { (_, row) -> row.all { it == '.' } }.map { it.index }
		val expansionCols =
			matrix.transpose().withIndex().filter { (_, row) -> row.all { it == '.' } }.map { it.index }

		galaxyPositions.map { (x, y) ->
			expansionCols.count { it < x }.let { expansionX -> expansionX * max(multiplier - 1, 1) + x } to
					expansionRows.count { it < y }.let { expansionY -> expansionY * max(multiplier - 1, 1) + y }
		}
	}

	fun part1(input: List<String>) = input
		.map(String::toList).getGalaxyPositions().calculateAllDistancesFP().sum()

	fun part2(input: List<String>, multiplier: Long) =
		input.map(String::toList).getGalaxyPositions(multiplier).calculateAllDistancesFP().sum()


	val testInput = readInput("Day11_test")
	check(part1(testInput).also { it.println() } == 374L)
	check(part2(testInput, 100).also { it.println() } == 8410L)


	val input = readInput("Day11")
	part1(input).println()
	part2(input, 1000000).println()
}
