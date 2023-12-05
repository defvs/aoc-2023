data class MappingRange(
	val sourceRange: LongRange,
	val destinationRange: Long
) {
	private val offset = sourceRange.first - destinationRange
	fun map(value: Long) = value - offset

	companion object {
		fun fromString(str: String): MappingRange {
			val split = str.split(' ').map { it.toLong() }
			val size = split[2]
			return MappingRange(split[1]..(split[1] + size), split[0])
		}
	}
}

data class Mappings(val ranges: List<MappingRange>) {
	fun map(value: Long) = ranges.singleOrNull { value in it.sourceRange }
		.let { it?.map(value) ?: value }

	constructor(lines: List<String>, ignore: Unit = Unit) : this(lines.map { MappingRange.fromString(it) })
}

fun main() {
	fun getMappings(input: List<String>): List<Mappings> {
		val maps = input.joinToString("\n").split("map:").drop(1)
			.map {
				it.lines()
					.filter { line -> line.isNotEmpty() && line.all { char -> char.isDigit() || char == ' ' } }
					.let { mappingsStr -> Mappings(mappingsStr) }
			}
		return maps
	}

	fun part1(input: List<String>): Long {
		val maps = getMappings(input)
		val seeds = input[0].substringAfter("seeds: ").trim().split(' ').map { it.toLong() }

		return seeds.minOf { originalSeed ->
			maps.fold(originalSeed) { acc, mappings ->
				mappings.map(acc)
			}
		}
	}

	fun part2(input: List<String>): Long {
		val maps = getMappings(input)
		val seeds = input[0].substringAfter("seeds: ").trim().split(' ')
			.map { it.toLong() }.chunked(2)
			.map { it[0]..<(it[0] + it[1]) }
			.flatMap { it.toList() }
			.toSet()

		return seeds.minOf { originalSeed ->
			maps.fold(originalSeed) { acc, mappings ->
				mappings.map(acc)
			}
		}
	}

	val testInput = readInput("Day05_test")
	check(part1(testInput).also { it.println() } == 35L)
	check(part2(testInput).also { it.println() } == 46L)

	val input = readInput("Day05")
	part1(input).println()
	part2(input).println()
}
