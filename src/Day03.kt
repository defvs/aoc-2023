fun main() {
	data class Position(val x: Int, val y: Int)

	data class Symbol(
		val character: Char,
		val position: Position,
	) {
		val isGearCharacter = character == '*'
	}

	fun findSymbolLocations(input: List<String>): List<Symbol> {
		return input.flatMapIndexed { y, line ->
			line.mapIndexedNotNull { x, char ->
				when {
					char.isDigit() || char == '.' -> null
					else -> Symbol(char, Position(x, y))
				}
			}
		}
	}

	data class PartNumber(
		val startPosition: Position,
		val numberValue: Int,
	) {
		val allPositions: List<Position> by lazy {
			(0..<(numberValue.toString().length))
				.map { it + startPosition.x } // add the offset of the startPosition.x
				.map { Position(it, startPosition.y) }
		}

		fun isSymbolAdjacent(symbol: Symbol): Boolean {
			val symbolPosition = symbol.position
			val xRange = symbolPosition.x.let { it - 1..it + 1 }
			val yRange = symbolPosition.y.let { it - 1..it + 1 }

			return xRange.any { x ->
				yRange.any { y ->
					allPositions.contains(Position(x, y))
				}
			}
		}
	}

	fun findPartNumbers(input: List<String>) = input.flatMapIndexed { y, line ->
		"""\d+""".toRegex().findAll(line).map { matchResult ->
			PartNumber(Position(matchResult.range.first, y), matchResult.value.toInt())
		}
	}


	fun part1(input: List<String>): Int {
		val symbolLocations = findSymbolLocations(input)
		return findPartNumbers(input).filter {
			symbolLocations.any(it::isSymbolAdjacent)
		}.distinct().sumOf { it.numberValue }
	}

	fun part2(input: List<String>): Int {
		val partNumbers = findPartNumbers(input)

		return findSymbolLocations(input)
			.filter(Symbol::isGearCharacter)
			.map { symbol -> partNumbers.filter { partNumber -> partNumber.isSymbolAdjacent(symbol) } }
			.filter { it.size == 2 }
			.sumOf { it.map { symbol -> symbol.numberValue }.reduce(Int::times) }
	}

	val testInput = readInput("Day03_test")
	check(part1(testInput).also { it.println() } == 4361)
	check(part2(testInput).also { it.println() } == 467835)

	val input = readInput("Day03")
	part1(input).println()
	part2(input).println()
}
