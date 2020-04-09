# ktorGzipTest

Demonstrates a bug in ktor client - when using the gzip compression feature together 
with the logging feature (with `LogLevel.ALL`) the client fails with the 
`kotlinx.coroutines.JobCancellationException: Parent job is Completed` exception.

To reproduce the behavior run `log_all_with_gzip` test from GUI
or `gradlew testDebugUnitTest --tests *log_all_with_gzip` from a command line.

There are three tests:
1. `log_info_with_gzip`
2. `log_all_without_gzip`
3. `log_all_with_gzip`

The first two complete successfully, the third one fails.

There's even stranger behavior - if you run all tests in one run, they all complete succesfully!
