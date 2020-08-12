fun main() {
    val number = readLine()!!.toInt()
    println(if (number < 1000)round(number) else 0)
}