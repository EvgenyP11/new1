import java.io.File


const val MODE = "-mode"
const val KEY = "-key"
const val DATA = "-data"
const val INPUT = "-in"
const val OUTPUT = "-out"
const val ALG = "-alg"
const val ERROR = "Error. No such file"

class ParseArgs (var modeValue: String = "enc",
                 var dataValue: String = "",
                 var keyValue: Int = 0,
                 var inputNameFile: String? = null,
                 var outputNameFile: String? = null,
                 var algorithmValue: String = "shift")


fun main(args: Array<String>) {
    val arguments = parseArgs(args)
    //кодирование без аргументов имен файлов
    if (arguments.modeValue == "enc"
        && arguments.dataValue != ""
        &&arguments.outputNameFile == null
        &&arguments.inputNameFile == null) {
        val encMessage = if (arguments.algorithmValue == "unicode") {
            encodeUnicode(arguments.dataValue, arguments.keyValue)
        } else encodeShift(arguments.dataValue, arguments.keyValue)
        println(encMessage)
    }
    //декодирование без аргументов имен файлов
    if (arguments.modeValue == "dec"
        && arguments.dataValue != ""
        &&arguments.outputNameFile == null
        &&arguments.inputNameFile == null) {
        val encMessage = if (arguments.algorithmValue == "unicode") {
            decodeUnicode(arguments.dataValue, arguments.keyValue)
        } else decodeShift(arguments.dataValue, arguments.keyValue)
        println(encMessage)
    }
    //кодирование со читыванием и записью
    if (arguments.modeValue == "enc"
        && (arguments.inputNameFile != null
                || arguments.dataValue != "")
        && arguments.outputNameFile != null) {
        //проверка откуда брать строку для шифрования
        val readMessage = if (arguments.dataValue != "") arguments.dataValue
        else readFile(arguments.inputNameFile!!)
        //провеерка на калечность файла
        if (readMessage == ERROR || readMessage == null) {
            println(ERROR)
            return
        } else {
            val encMessage = if (arguments.algorithmValue == "unicode") {
                encodeUnicode(readMessage, arguments.keyValue)
            } else encodeShift(readMessage, arguments.keyValue)
            saveFile(arguments.outputNameFile!!, encMessage)
        }
    }

    if (arguments.modeValue == "dec"
        && (arguments.inputNameFile != null
                || arguments.dataValue != "")
        && arguments.outputNameFile != null) {
        //проверка откуда брать строку для шифрования
        val readMessage = if (arguments.dataValue != "") arguments.dataValue
        else readFile(arguments.inputNameFile!!)
        //провеерка на калечность файла
        if (readMessage == ERROR || readMessage == null) {
            println(ERROR)
            return
        } else {
            val encMessage = if (arguments.algorithmValue == "unicode") {
                decodeUnicode(readMessage, arguments.keyValue)
            } else decodeShift(readMessage, arguments.keyValue)
            saveFile(arguments.outputNameFile!!, encMessage)
        }
    }
}


fun parseArgs(args: Array<String>): ParseArgs {
    val parse = ParseArgs()
    val regex = "-(\\w)+".toRegex()
    if (args.contains(MODE)//проверка на наличие команды
        && args.elementAtOrNull(args.indexOf(MODE) + 1) != null // проверка на то что вледующий элемент не NULL
        && !regex.matches(args[args.indexOf(MODE) + 1])//проверка на наличие аргумента для команды
        && args[args.indexOf(MODE) + 1] == "dec") parse.modeValue = "dec"
    if (args.contains(KEY)//проверка на наличие команды
        && args.elementAtOrNull(args.indexOf(KEY) + 1) != null// проверка на то что вледующий элемент не NULL
        && !regex.matches(args[args.indexOf(KEY) + 1])) {//проверка на наличие аргумента для команды
        parse.keyValue = args[args.indexOf(KEY) + 1].toIntOrNull()?: 0
    }
    if (args.contains(DATA)//проверка на наличие команды
        && args.elementAtOrNull(args.indexOf(DATA) + 1) != null// проверка на то что вледующий элемент не NULL
        && !regex.matches(args[args.indexOf(DATA) + 1])) {//проверка на наличие аргумента для команды
        parse.dataValue = args[args.indexOf(DATA) + 1]
    }
    if (args.contains(INPUT) //проверка на наличие команды
        && args.elementAtOrNull(args.indexOf(INPUT) + 1) != null // проверка на то что вледующий элемент не NULL
        && !regex.matches(args[args.indexOf(INPUT) + 1])) {//проверка на наличие аргумента для команды
        parse.inputNameFile = args[args.indexOf(INPUT) + 1]
    }
    if (args.contains(OUTPUT) //проверка на наличие команды
        && args.elementAtOrNull(args.indexOf(OUTPUT) + 1) != null // проверка на то что вледующий элемент не NULL
        && !regex.matches(args[args.indexOf(OUTPUT) + 1])) {//проверка на наличие аргумента для команды
        parse.outputNameFile = args[args.indexOf(OUTPUT) + 1]
    }
    if (args.contains(ALG)//проверка на наличие команды
        && args.elementAtOrNull(args.indexOf(ALG) + 1) != null // проверка на то что вледующий элемент не NULL
        && !regex.matches(args[args.indexOf(ALG) + 1])//проверка на наличие аргумента для команды
        && args[args.indexOf(ALG) + 1] == "unicode") parse.algorithmValue = "unicode"
    return parse
}

fun encodeUnicode(str: String, key: Int): String {
    val input = str.toCharArray()
    val output = mutableListOf<Char>()
    input.forEach { output.add(it + key) }
    return output.joinToString("")
}

fun decodeUnicode(str: String, key: Int): String {
    val input = str.toCharArray()
    val output = mutableListOf<Char>()
    input.forEach { output.add((it - key)) }
    return output.joinToString("")
}

fun saveFile(fileName: String, message: String) {
    val fileName = File(fileName)
    fileName.writeText(message)
}

fun readFile(fileName: String): String {
    return if (File(fileName).exists()) {
        File(fileName).readText()
    } else ERROR
}

fun encodeShift(s: String, key: Int): String {
    val offset = key % 26
    if (offset == 0) return s
    var d: Char
    val chars = CharArray(s.length)
    for ((index, c) in s.withIndex()) {
        if (c in 'A'..'Z') {
            d = c + offset
            if (d > 'Z') d -= 26
        }
        else if (c in 'a'..'z') {
            d = c + offset
            if (d > 'z') d -= 26
        }
        else
            d = c
        chars[index] = d
    }
    return chars.joinToString("")
}

fun decodeShift(s: String, key: Int): String {
    return encodeShift(s, 26 - key)
}
