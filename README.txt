

How to compile the program:

cd batching/example
( cd ../batchlib && mvn install ) && ( cd ../example && mvn install )

How to run the example program:

cd batching/example
java -cp ../batchlib/target/classes:target/classes org.bxo.example.ExampleBatch a b c d e f g h i j k l m n o p

The parameters are just string data being sent.
The example program randomly gives the strings to one of batch1 .. batch5
so that we can check that both batches are processed in a fair manner.
The example program also shows how to use the library.
Each type will need a class similar to StringBatch.java.
For testing I just instantiated StringBatch.java twice, and logged the names.
The template should be adequate for developers to apply for other examples.


How to use the library:

A. For each api endpoint that takes the same type of data,
the developer needs to create a class similar to StringBatch.
The type of data is specified in the definition of StringBatch to be String, but it could be complex objects as well.
The process function needs to handle the batch processing of a list of items of the type specified.
In the example StringBatch, the process is just printing the values, but it could do anything else.

B. The class ExampleBatch covers how to create multiple batching instances.
They could be of similar types or different types.  They are given a name for
logging / debugging purposes, and specify the max number of items to be
handled in one batch, and also the max time before the batch is to be processed.
E.g. max batch size=7, and max time = 30 seconds
  If we get 100 items in 10 seconds, they will be processed
  in batches of 7 right away before the timeout is reached.  The
  number 7 means that the processing function can handle
  a max of 7 items in one call.
  If we get 5 items in the first 30 seconds, it will write out the 5 items
  after 30 seconds  This is to ensure that data is not kept too long before
  being processed.

Note: The BatchTimer class creates a single thread that loops over all the batching instances and processes them.
This is a design decision to ensure that we don't get a large number of threads even though there may be 100 API's using the batching technique.
This is probably not ideal, but it is better than having too many threads
overloading the system, so it should be adequate for an initial setup.
Changing this to have multiple threads running should ensure that
the number of threads does not grow arbitrarily large.
The BatchTask class loops over all the batches and processes 1 batch each.
For the batches that had data, it does it repeats until none of the batches
have data to be processed as a batch.
The max item size of the batch is configurable per type of data and endpoint.
Each batching type can also set the max time between batch writes so that if
some endpoint is not called for a while, it will still process the given data.


TODO Tasks

(1) Add tests
I like test driven development.
For this situation, it seemed easier to write the example code and test that.
The example code shows that multiple batch types are handled.

(2) Rework threading to create multiple threads and manage them
For a quick deliverable, limiting to one thread which loops over the batch types
ensures that the app will not have numerous threads running around overloading
the system as we add more types for batching.
It does mean that some of the batching would be slower since it goes over all the batches once per second and sees which batches have data to process.
a. The benefit is we ensure that the batching thread does not overload the system,
and makes for fast response times.
b. Disadvantage is that the batching could take longer.  However, this is
mitigated by at least ensuring that all batches are handled fairly.

(3) Fairness handling
Fairness is very complex.
If only one batch type has many batches, then we want to process more of those.
However, if other batches start producing data, then we need to handle them as well.
One part of fairness code tries to process batch types with more batches.
However when there are too many requests for one batch type, we go back
and check all the batch types to make sure they are not being starved (prevent starvation).
This is simple, but not perfect fairness code, and could probably be improved.

(4) Dynamic Configuration for batch size and interval
The library allows developers to pass in the batch type name, batch size
and interval, so the library itself is configurable.
It is simple to add in a config file and use those values for configuration
That will allow changing the batch size and interval dynamically for those
types which need that capability

(5) Handling data if the program crashed
The library does not save data to disk, so transactions could be lost
if there was a crash.  This will take some time to do right, so did not
work on it, but it is definitely important.  For companion, it is possible
that some use cases losing some data is not that big a deal, so would
alert product team and discuss priority here.  E.g. losing one hour of
dog habits may not significantly affect the value proposition, so it
would be wise to check with product on whether we need to address
this or it is fine to postpone for now.

(6) Thread handling
We are limiting to max one thread per batch type
Maybe some batch types can handle more than that, but we are not
taking that into consideration at this time.
If some batch types take long, they should not block other batch
types from being processed, so updated the code to process the
batches in their own threads.


https://git-scm.com/docs/git-bundle


Many systems are more efficient at processing batches than single items. For example, the MySQL manual suggests that in order to "optimize insert speed, combine many small operations into a single large operation." Similarly, uploading logs or metrics from a mobile device to a monitoring service is typically more efficient if done in batches.

On the other hand, it is often more convenient to process items one at a time. For example, if you're writing a web server with an endpoint that inserts items into a database, it'd be easier if you had an API that let you ask to have an item inserted and that call returns only when the item has been inserted. The fact that the insert is held for a bit and batched with other inserts can be hidden from the public API. Similarly, a mobile app updating metrics based on app interactions would be easier to write if the batching behavior was abstracted.

This coding challenge asks you to create a library that allows developers to achieve the best of both worlds. The API should be thread-safe and allow users to interact with the API as if processing single items. However, behind the scenes, the library should aggregate the items and process them in batches. If you're using a language that tends to encourage asynchronous code consider making the API asynchronous (e.g. a suspend function in Kotlin or an async function in C#).

The challenge is intentionally under-specified. We want to see how you'd think about the "best" way to do something like this. We'd like to see how you think about issues like how you can structure your code and API so that it can be used in as many circumstances as possible. Or how you try to ensure that the code is extensible, easy to understand, and easy to maintain. Specifically, we'd like you to imagine you are a Companion employee and have been asked to quickly create such a batching service for a specific current company need like batching database inserts. Given the realities of a startup, we can't spend too much time on it so you should spend no more than 3 hours building this system. However, the long-term velocity at a startup is more important than getting a single feature out quickly so we'd like to see how you think about the issues like extensibility, re-use, and maintainability.

You have 48 hours to send back your completed solution but you should not spend more than about 3 hours on it. You can write your solution in any strongly typed language that has "real threads" (e.g. not Python or TypeScript). Please email your completed solution as a git bundle. Feel free to email me if you have any questions.

Good luck!


