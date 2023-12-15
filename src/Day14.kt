fun main() {
	fun Array<Array<Char>>.rotate90() = this.transpose().reversedArray()
	fun Array<Array<Char>>.applyGravity() = this.also { map2d ->
		var changes: Int
		do {
			changes = 0
			for ((y, row) in map2d.dropLast(1).withIndex()) {
				for ((x, item) in row.withIndex()) {
					if (item != 'O') continue
					if (map2d[y + 1][x] == '.') {
						map2d[y + 1][x] = 'O'
						map2d[y][x] = '.'
						changes++
					}
				}
			}
		} while (changes != 0)
	}

	fun Array<Array<Char>>.cycle() = (0..3).fold(this) { acc, _ ->
		acc.applyGravity().rotate90()
	}

	fun part1(input: List<String>) = input.map { line -> line.toCharArray().toTypedArray() }.toTypedArray()
		.reversedArray().applyGravity().foldIndexed(0) { index, acc, row ->
			acc + (row.count { it == 'O' } * (index + 1))
		}

	fun part2(input: List<String>) = input.map { it.toCharArray().toTypedArray().reversedArray() }.toTypedArray().reversedArray()
		.let { map2d ->
			var map2d2 = map2d
			var counter = 0
			do {
				map2d2 = map2d2.cycle()
				counter++
				if (counter.mod(100) == 0) counter.println()
			} while (!map2d.contentDeepEquals(map2d2))

			map2d2 = map2d
			for (i in 0..<(1000000000L.mod(counter.toLong())))
				map2d2 = map2d2.cycle()

			map2d2
		}.foldIndexed(0) { index, acc, row ->
			acc + (row.count { it == 'O' } * (index + 1))
		}

	val testInput = readInput("Day14_test")
	check(part1(testInput).also { it.println() } == 136)
	check(part2(testInput).also { it.println() } == 64)


	val input = readInput("Day14")
	part1(input).println()
	part2(input).println()
}
