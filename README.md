#Sentiment Demo
A REST service that delegates to a remote TCP server to handle requests. This is built with the [Spring Cloud Stream 
Binder Servlet](https://github.com/dsyer/spring-cloud-stream-binder-servlet) and the 
[spring-cloud-starter-stream-processor-tcp-client](https://github
.com/spring-cloud-stream-app-starters/tcp/tree/master/spring-cloud-starter-stream-processor-tcp-client).

This is configured to work with a Python [sentiment-analyzer](https://github.com/dturanski/sentiment-analyzer) that 
accepts a list of tweets as JSON and returns the text along with a score called `polarity`.

The sentiment analyzer only cares about the text, so if it is running and this app is connected to it, 
you can try posting a tweet:

```bash
$curl <host>:8080/stream -H'Content-type:application/json' -d '[[{"text":"v"},{"text":"tota"},{"text":"really great!!"},
{"text":"happy hay happy"}]]
[[{"text":"v"},{"text":"tota"},{"text":"really great!!"},{"text":"happy hay happy"}]]
```
This will return what you posted, indicating that the request was accepted.

Note that this JSON is a nested list containing a single list. A bit of impedance from using the binder servlet which
 does some list handling itself. In this case I need to pass the entire list as the request.

To see the result:

```bash
$ curl <host>:8080/stream/sentiments
["[{\"polarity\": 0.5, \"text\": \"v\"}, {\"polarity\": 0.5, \"text\": \"tota\"}, {\"polarity\": 0.67, \"text\": \"really great!!\"}, {\"polarity\": 0.9, \"text\": \"happy hay happy\"}]"]
```

Not pretty, but the binder servlet makes for a simple implementation and makes it easy to reuse the tcp client processer
which, it turns out, is not straightforward for this type of app. And I really want to test interoperability with the
 tcp client processor since eventually this will be deployed with Spring Cloud Dataflow.
 





