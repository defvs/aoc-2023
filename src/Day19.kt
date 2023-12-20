import Day19.instructionLineRegex
import Day19.instructionRegex

object Day19 {
	val instructionLineRegex = """([a-zA-Z]+)\{(.+)\}""".toRegex()
	val instructionRegex = """([a-z])([<>])(\d+):([a-zA-Z]+)""".toRegex()
}

data class Part(
	val characteristics: Map<Char, Int>
) {
	fun sumOfAll() = characteristics.values.sum()

	companion object {
		fun fromString(str: String) = str.removeSurrounding("{", "}").split(",")
			.associate { it.split("=").let { (char, int) -> char.single() to int.toInt() } }.let { Part(it) }
	}
}

data class InstructionLine(
	val name: String,
	val instructions: List<Instruction>,
) {
	operator fun invoke(part: Part): String = instructions.first { it.operation.invoke(part) }.destination

	companion object {
		fun fromString(str: String) =
			instructionLineRegex.matchEntire(str)!!.groupValues.let { (_, name, instructionsStr) ->
				InstructionLine(name, instructionsStr.split(",").map { Instruction.fromString(it) })
			}
	}
}

data class Instruction(
	val destination: String,
	val operation: (Part) -> Boolean,
) {
	companion object {
		private fun operationFromChar(char: Char): (Int, Int) -> Boolean = when (char) {
			'>' -> { a, b -> a > b }
			'<' -> { a, b -> a < b }
			else -> throw Exception("Invalid operation character '$char'.")
		}

		fun fromString(str: String) = instructionRegex.matchEntire(str)?.groupValues?.let {
			Instruction(it[4]) { part ->
				operationFromChar(it[2].single()).invoke(
					part.characteristics[it[1].single()]!!, it[3].toInt()
				)
			}
		} ?: Instruction(str) { true }
	}
}

data class Operation(
	val char: Char,
	val op: Char,
	val value: Int,
	val dest: String,
) {
	fun split(charaMap: Map<Char, IntRange>): Pair<Map<Char, IntRange>?, Map<Char, IntRange>?> {
		return when (op) {
			'>' -> {
				var mapTrue: Map<Char, IntRange>? = charaMap.mapValues {
					if (it.key == char) it.value.let { range ->
						(value + 1)..range.last
					} else it.value
				}
				var mapFalse: Map<Char, IntRange>? = charaMap.mapValues {
					if (it.key == char) it.value.let { range ->
						range.first..value
					} else it.value
				}

				if (mapTrue!!.any { it.value.first > it.value.last }) mapTrue = null
				if (mapFalse!!.any { it.value.first > it.value.last }) mapFalse = null

				mapTrue to mapFalse
			}

			'<' -> {
				var mapTrue: Map<Char, IntRange>? = charaMap.mapValues {
					if (it.key == char) it.value.let { range ->
						range.first..<value
					} else it.value
				}
				var mapFalse: Map<Char, IntRange>? = charaMap.mapValues {
					if (it.key == char) it.value.let { range ->
						value..range.last
					} else it.value
				}

				if (mapTrue!!.any { it.value.first > it.value.last }) mapTrue = null
				if (mapFalse!!.any { it.value.first > it.value.last }) mapFalse = null

				mapTrue to mapFalse
			}

			'a' -> Pair(charaMap, null)

			else -> throw Exception()
		}
	}

	companion object {
		fun fromString(str: String) =
			instructionRegex.matchEntire(str)?.groupValues?.let { (_, char, op, value, dest) ->
					Operation(char.single(), op.single(), value.toInt(), dest)
				} ?: (Operation('a', 'a', 0, str))
	}
}

fun main() {

	fun part1(input: List<String>) = input.indexOf("").let { splitIndex ->
		Pair(input.subList(0, splitIndex), input.subList(splitIndex + 1, input.size))
	}.let { (instructionsStr, partsStr) ->
		Pair(instructionsStr.map { InstructionLine.fromString(it) }, partsStr.map { Part.fromString(it) })
	}.let { (instructionLines, parts) ->
		val partPositions = instructionLines.associate { it.name to (it to arrayListOf<Part>()) }
			.let { it: Map<String, Pair<InstructionLine?, ArrayList<Part>>> ->
				it + mapOf(
					"A" to (null to arrayListOf()),
					"R" to (null to arrayListOf()),
				)
			}
		partPositions["in"]!!.second += parts

		while (partPositions.filter { it.key !in listOf("A", "R") }.any { it.value.second.isNotEmpty() }) {
			partPositions.filter { it.value.second.isNotEmpty() }.forEach { (lineName, value) ->
					if (value.first == null) return@forEach // do not run for A and R
					value.second.toList().forEach { part ->
						val destination = value.first!!(part)
						partPositions[lineName]!!.second.remove(part)
						partPositions[destination]!!.second.add(part)
					}
				}
		}

		partPositions["A"]!!.second.sumOf { it.sumOfAll() }
	}

	fun part2(input: List<String>) = input.indexOf("").let { input.subList(0, it) }.associate { opLineStr ->
		instructionLineRegex.matchEntire(opLineStr)!!.groupValues.let { (_, name, instructionsStr) ->
			name to instructionsStr.split(",").map(Operation.Companion::fromString)
		}
	}.let { opLineMap: Map<String, List<Operation>> ->
		val rangesPositions = opLineMap.keys.associateWith { arrayListOf<Map<Char, IntRange>>() }.let {
			it + mapOf(
				// add A and R
				"A" to arrayListOf(),
				"R" to arrayListOf(),
			)
		}

		rangesPositions["in"]!! += mapOf(
			'x' to 1..4000,
			'm' to 1..4000,
			'a' to 1..4000,
			's' to 1..4000,
		) // merry christmas yall

		while (rangesPositions.filterKeys { it !in listOf("A", "R") }.any { it.value.isNotEmpty() }) {
			for ((lineName, items) in rangesPositions.filter { it.value.isNotEmpty() && it.key !in listOf("A", "R") }) {
				for (item in items.toList()) {
					rangesPositions[lineName]!!.remove(item)
					var remainder = item
					for (operation in opLineMap[lineName]!!) {
						val (ifTrue, ifFalse) = operation.split(remainder)
						ifTrue?.let { rangesPositions[operation.dest]!!.add(ifTrue) }
						remainder = ifFalse ?: break
					}
				}
			}
		}

		rangesPositions["A"]!!.sumOf { combinations -> // count the total number of possibilities
			combinations.values.map { it.count() }.fold(1.toLong()) { acc, size -> acc * size.toLong() }.toLong()
		}
	}

	val testInput = readInput("Day19_test")
	check(part1(testInput).also { it.println() } == 19114)
	check(part2(testInput).also { it.println() } == 167409079868000L)


	val input = readInput("Day19")
	part1(input).println()
	part2(input).println()
}
