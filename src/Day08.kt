@file:Suppress("MemberVisibilityCanBePrivate", "EqualsOrHashCode", "unused")

import Step.Companion.followOrders

object Day08Math {
	fun lcm(a: Long, b: Long): Long = a * (b / gcd(a, b))
	fun lcm(numbers: LongArray): Long = numbers.reduce { acc, num -> lcm(acc, num) }
	tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
	fun gcd(numbers: LongArray): Long = numbers.reduce { acc, num -> gcd(acc, num) }
}

class Step(val name: String) {
	lateinit var left: Step
	lateinit var right: Step
	fun followOrder(order: Char) = if (order == 'L') left else right

	companion object {
		tailrec fun Step.followOrders(orders: String, destination: (Step) -> Boolean, acc: Int = 0): Int =
			if (destination.invoke(this)) acc
			else followOrder(orders[acc.mod(orders.length)]).followOrders(orders, destination, acc + 1)

		fun Step.followOrders(orders: String, destination: Step, acc: Int = 0) =
			followOrders(orders, { it == destination }, acc)

		fun createStepMap(input: List<String>): Map<String, Step> {
			val stepMap = input.asSequence()
				.mapNotNull { parseLine(it) }
				.associate { (name, left, right) ->
					name to Step(name).apply {
						this.left = Step(left)
						this.right = Step(right)
					}
				}

			// Linking steps
			stepMap.values.forEach { step ->
				step.left = stepMap[step.left.name] ?: step.left
				step.right = stepMap[step.right.name] ?: step.right
			}

			return stepMap
		}

		private fun parseLine(line: String): Triple<String, String, String>? {
			val regex = """([A-Z]{3}) = \(([A-Z]{3}), ([A-Z]{3})\)""".toRegex()
			return regex.matchEntire(line)?.destructured?.let { (a, b, c) ->
				Triple(a, b, c)
			}
		}
	}

	override fun equals(other: Any?): Boolean {
		return if (other is Step) this.name == other.name
		else false
	}
}

fun main() {
	fun part1(input: List<String>) = input.let { line ->
		Step.createStepMap(line.drop(2)).let {
			it["AAA"]!!.followOrders(line[0], it["ZZZ"]!!)
		}
	}

	fun part2(input: List<String>) = input.let { line ->
		Day08Math.lcm(
			Step.createStepMap(line.drop(2))
				.filterKeys { it.last() == 'A' }.toList().toMutableList()
				.map { (_, step) ->
					step.followOrders(line[0], { it.name.last() == 'Z' })
				}.map { it.toLong() }.toLongArray()
		)
	}

	val testInput = readInput("Day08_test")
	check(part1(testInput).also { it.println() } == 6)

	val input = readInput("Day08")
	part1(input).println()
	part2(input).println()
}
