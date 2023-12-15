fun main() {
	fun String.hash() = this.fold(0) { acc, char ->
		((acc + char.code) * 17) % 256
	}.toInt()

	fun part1(input: List<String>) = input[0].split(",").sumOf(String::hash)

	fun part2(input: List<String>, printDebug: Boolean = false): Int {
		val boxes = Array<LinkedHashMap<String, Int>>(256) { linkedMapOf() }
		val regex = """([a-z]+)([=\-])([0-9]+)?""".toRegex()

		data class Step(
			val label: String,
			val operation: Char,
			val focalLength: Int? = null,
		) {
			val boxHash by lazy { label.hash() }

			override fun toString() = "$label$operation${focalLength ?: ""}"
		}

		val steps = input[0].split(",").map {
			regex.matchEntire(it)!!.groupValues.let { matchResult ->
				Step(matchResult[1], matchResult[2][0], matchResult.getOrNull(3)?.toIntOrNull())
			}
		}

		steps.forEach { step ->
			when (step.operation) {
				'-' -> {
					boxes[step.boxHash].remove(step.label)
				}
				'=' -> {
					step.focalLength!! // never null

					if (!boxes[step.boxHash].containsKey(step.label))
						boxes[step.boxHash][step.label] = step.focalLength
					else
						boxes[step.boxHash].replace(step.label, step.focalLength)
				}
			}

			if (!printDebug) return@forEach
			boxes.filter { it.size != 0 }.mapIndexed { i, map ->
				"Box $i: " + map.toList().joinToString(separator = " ") {"[${it.first} ${it.second}]"}
			}.joinToString(separator = "\n").also {
				"After \"$step\":\n$it\n".println()
			}
		}

		return boxes.withIndex().sumOf { (boxIndex, map) ->
			map.values.withIndex().sumOf { (lensIndex, focal) ->
				(boxIndex + 1) * (lensIndex + 1) * focal
			}
		}
	}

	val testInput = readInput("Day15_test")
	check(part1(testInput).also { it.println() } == 1320)
	check(part2(testInput, true).also { it.println() } == 145)


	val input = readInput("Day15")
	part1(input).println()
	part2(input).println()
}
