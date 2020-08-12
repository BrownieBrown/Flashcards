open class Coffee (private val cost: Int, private val volume: Int) {
    override fun toString(): String {
        return "cost=${this.cost} volume=${this.volume}"
    }
}

class Latte(private val milk: String, cost: Int, volume: Int): Coffee (cost, volume) {
    override fun toString(): String {
        return "${super.toString()} milk=$milk"
    }
}

fun main() {
    val coffee = Coffee(4, 4)
    val latte = Latte("Yes", 4, 4)
    println(coffee)
    println(latte)
}