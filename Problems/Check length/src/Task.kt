fun main() {
    val argument = readLine()!!
    println(if(argument.length <= 5) check(argument)?.length else 0)
}