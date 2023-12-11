data class PipeVector(var x: Int, var y: Int) {
	operator fun plusAssign(movement: PipeVector) {
		x += movement.x
		y += movement.y
	}

	override fun toString(): String {
		return "v($x, $y)"
	}
}

data class PipeSegment(
	val type: Type,
) {
	enum class Type(val representation: Char) {
		NS('|') { // 0,1 becomes 0,1
			override fun redirect(v: PipeVector) = v
			override val startVector1 = PipeVector(0, 1)
			override val startVector2 = PipeVector(0, -1)
		},
		EW('-') { // 1,0 becomes 1,0
			override fun redirect(v: PipeVector) = v
			override val startVector1 = PipeVector(1, 0)
			override val startVector2 = PipeVector(-1, 0)
		},
		NE('L') { // 0,1 becomes 1,0
			override fun redirect(v: PipeVector) = PipeVector(v.y, v.x)
			override val startVector1 = PipeVector(1, 0)
			override val startVector2 = PipeVector(0, -1)
		},
		NW('J') { // 1,0 becomes 0,-1; 0,1 becomes -1,0
			override fun redirect(v: PipeVector) = PipeVector(-v.y, -v.x)
			override val startVector1 = PipeVector(-1, 0)
			override val startVector2 = PipeVector(0, -1)
		},
		SW('7') { // 1,0 becomes 0,1;
			override fun redirect(v: PipeVector) = PipeVector(v.y, v.x)
			override val startVector1 = PipeVector(-1, 0)
			override val startVector2 = PipeVector(0, 1)
		},
		SE('F') { // -1,0 becomes 0,1
			override fun redirect(v: PipeVector) = PipeVector(-v.y, -v.x)
			override val startVector1 = PipeVector(1, 0)
			override val startVector2 = PipeVector(0, -1)
		},
		Ground('.') {
			override fun redirect(v: PipeVector) = throw Exception("Not supposed to redirect from this tile.")
			override val startVector1
				get() = throw Exception("No start vector from this tile.")
			override val startVector2
				get() = throw Exception("No start vector from this tile.")
		},
		Start('S') {
			override fun redirect(v: PipeVector) = throw Exception("Not supposed to redirect from this tile.")
			override val startVector1
				get() = throw Exception("No start vector from this tile.")
			override val startVector2
				get() = throw Exception("No start vector from this tile.")
		};

		abstract fun redirect(v: PipeVector): PipeVector
		abstract val startVector1: PipeVector
		abstract val startVector2: PipeVector

		companion object {
			fun fromChar(char: Char) = Type.entries.first { it.representation == char }
			fun fromName(name: String) = Type.valueOf(name)
		}
	}

	constructor(char: Char) : this(Type.fromChar(char))

	companion object {
		fun guessPipe(surroundings: List<List<PipeSegment>>): PipeSegment {
			val cardinals = listOf(
				surroundings[0][1].type in listOf(Type.NS, Type.SW, Type.SE),
				surroundings[1][0].type in listOf(Type.EW, Type.NE, Type.SE),
				surroundings[1][2].type in listOf(Type.EW, Type.NW, Type.SW),
				surroundings[2][1].type in listOf(Type.NS, Type.NW, Type.NE),
			)

			return listOf('N', 'W', 'E', 'S').zip(cardinals).filter { it.second }.map { it.first }.joinToString("")
				.let { PipeSegment(Type.fromName(it)) }
		}
	}
}

fun main() {
	fun day(input: List<String>) = input.map { line ->
		line.map { char ->
			PipeSegment(char)
		}.toMutableList()
	}.let { map2d ->
		var startPosition: PipeVector? = null
		yLoop@ for ((y, row) in map2d.withIndex()) for ((x, segment) in row.withIndex()) if (segment.type == PipeSegment.Type.Start) {
			startPosition = PipeVector(x, y)
			break@yLoop
		}


		val guessedTile = PipeSegment.guessPipe(
			map2d.subList(startPosition!!.y - 1, startPosition.y + 2)
				.map {
					it.subList(startPosition.x - 1, startPosition.x + 2)
				}
		).also { map2d[startPosition.y][startPosition.x] = it }

		data class State(
			var position: PipeVector,
			var movement: PipeVector,
		)

		val stateMachine = mutableListOf(
			State(startPosition.copy(), guessedTile.type.startVector1.copy()),
			State(startPosition.copy(), guessedTile.type.startVector2.copy())
		)

		val visitedPositions: HashSet<PipeVector> = hashSetOf(startPosition)

		var dist = 0
		do {
			dist++
			for (it in stateMachine) {
				it.position += it.movement
				val (x, y) = it.position
				it.movement = map2d[y][x].type.redirect(it.movement)
				visitedPositions += it.position.copy()
			}
		} while (stateMachine[0].position != stateMachine[1].position)

		val visitedPositionsMap = map2d.mapIndexed { y, row ->
			row.mapIndexed { x, segment ->
				if (PipeVector(x, y) in visitedPositions)
					if (segment.type in listOf(PipeSegment.Type.NE, PipeSegment.Type.NS, PipeSegment.Type.NW)) 1
					else 2
				else 0
			}
		}

		val contained: Int = visitedPositionsMap.sumOf {
			it.fold(Pair(0, false)) { (acc, counting), tristate ->
				var acc2 = acc
				var counting2 = counting
				if (tristate == 1) counting2 = !counting
				else if (counting && tristate != 2) acc2++

				Pair(acc2, counting2)
			}.first
		}

		Pair(dist, contained)
	}

	val input = readInput("Day10")
	val (part1, part2) = day(input)
	part1.println()
	part2.println()
}
