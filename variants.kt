

        GlobalScope.launch {


            suspendCoroutine<Int> {
                it.resume(1)
            }

            async {

            }.await()

            val j = launch {

            }

            coroutineScope {

            }

            supervisorScope {

            }


        }
