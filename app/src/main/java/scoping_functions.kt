fun scoping_functions() {

    // LET
    // Serve per eseguire un blocco di codice su un oggetto non-null e ritornare il risultato del blocco.
    var name: String? = "Kotlin"

    val length = name?.let {
        println("Il nome è $it")
        it.length  // il blocco ritorna questo valore
    }

    println(length)  // 6


    // ALSO
    // Serve per eseguire azioni secondarie sull'oggetto (es. logging) senza modificarlo,
    // ritorna l'oggetto stesso.
    val numbers = mutableListOf(1, 2, 3)

    numbers.also { println("Lista originale: $it") }
        .add(4)

    println(numbers)  // [1, 2, 3, 4]


    // APPLY (this)
    // Serve per inizializzare o configurare un oggetto, ritorna l'oggetto stesso.
    class Person(var name: String = "", var age: Int = 0)

    var person = Person().apply {
        name = "Alice"
        age = 25
    }

    println(person.name) // Alice


    // RUN (this)
    // Serve per eseguire un blocco di codice su un oggetto e ritornare il risultato del blocco.
    val text = "Kotlin"

    val result = text.run {
        println("Lunghezza: $length")
        length?.times(2) ?: 0  // ritorno del blocco
    }

    println(result)  // 12


    // WITH SIMILE A RUN (this)
    // Serve a eseguire un blocco di codice su un oggetto esistente e ritornare il risultato del blocco.
    person = Person().apply { name = "Bob"; age = 30 }

    val description = with(person) {
        println("Nome: $name, Età: $age")
        "$name ha $age anni"
    }

    println(description)  // Bob ha 30 anni

}