# springboot-resilience4j-demo

### How To Implement Fault Tolerance In Microservices Using Resilience4j?

### Things todo list:

1. Clone this repository: `git clone https://github.com/hendisantika/springboot-resilience4j.git`
2. Navigate to the folder: `cd springboot-resilience4j`
3. Run the application: `mvn clean spring-boot:run`

### What is Fault Tolerance in Microservices?

In a context of Microservices, Fault Tolerance is a technique of tolerating a fault. A Microservice that tolerates the
fault is known as Fault Tolerant. Moreover, a Microservice should be a fault tolerant in such a way that the entire
application runs smoothly. In order to implement this technique, the Resilience4j offers us a variety of modules based
on the type of fault we want to tolerate.

### Core modules of Resilience4j

resilience4j-circuitbreaker: Circuit breaking resilience4j-ratelimiter: Rate limiting resilience4j-bulkhead: Bulkheading
resilience4j-retry: Automatic retrying (sync and async)
resilience4j-cache: Result caching resilience4j-timelimiter: Timeout handling

### What is Rate Limiting?

Rate Limiter limits the number of requests for a given period. Let’s assume that we want to limit the number of requests
on a Rest API and fix it for a particular duration. There are various reasons to limit the number of requests than an
API can handle, such as protect the resources from spammers, minimize the overhead, meet a service level agreement and
many others. Undoubtedly, we can achieve this functionality with the help of annotation @RateLimiter provided by
Resilience4j without writing a code explicitly.

### How to implement Rate Limiting? : Rate Limiting Example

For example, we want to restrict only 2 requests per 5 seconds duration. In order to achieve this, let’s follow below
steps to write code and respective configurations.

### How to test the implemented RateLimiter?

1) Open the Browser and hit the URL : http://localhost:8080/getMessage
2) You should see the result “Message from getMessage() :Hello” on the browser.
3) Now let’s refresh the browser more than 2 times within 5 seconds period.
4) Once you refresh third time within 5 seconds, you should see the message “Too many requests : No further request will
   be accepted. Please try after sometime”
5) In console also you should see the logger message as ‘Rate limit has applied, So no further calls are getting
   accepted’
6) Now update limit-for-period=10 and limit-refresh-period=1s in application.xml. Then, After refreshing the browser
   multiple times you should see only success message as “Message from getMessage() :Hello” in the browser.

### What is Retry?

Suppose Microservice ‘A’ depends on another Microservice ‘B’. Let’s assume Microservice ‘B’ is a faulty service and its
success rate is only upto 50-60%. However, fault may be due to any reason, such as service is unavailable, buggy service
that sometimes responds and sometimes not, or an intermittent network failure etc. However, in this case, if
Microservice ‘A’ retries to send request 2 to 3 times, the chances of getting response increases. Obviously, we can
achieve this functionality with the help of annotation @Rerty provided by Resilience4j without writing a code
explicitly.

Here, we have to implement a Retry mechanism in Microservice ‘A’. We will call Microservice ‘A’ as Fault Tolerant as it
is participating in tolerating the fault. However, Retry will take place only on a failure not on a success. By default
retry happens 3 times. Moreover, we can configure how many times to retry as per our requirement.

### How to implement Retry? :Retry Example

We will develop a scenario where one Microservice will call another Microservice.

### How to test the implemented Retry?

1) Make the called Microservice down.
2) Open the browser and hit the URL : http://localhost:8080/getInvoice
3) You should see “getInvoice() call starts here” message 5 times in the console. It means it has tried 5 attempts.
3) Once 5 attempts completes, you should see the message “—RESPONSE FROM FALLBACK METHOD—” in the console. It indicates
   that the fallback method called.
4) Subsequently, You will see the “SERVICE IS DOWN, PLEASE TRY AFTER SOMETIME !!!” message in the browser. It indicates
   that a common message is getting shown to the user.
5) Now let’s make the called Microservice up. Hit the URl again to see the desired results.
6) If you are getting the desired results successfully, neither Microservice should attempt any retry nor fallback
   method should be called.

### What is Circuit Breaker ?

Circuit Breaker is a pattern in developing the Microservices based applications in order to tolerate any fault. As the
name suggests, ‘Breaking the Circuit’. Suppose a Microservice ‘A’ is internally calling another Microservice ‘B’ and ‘B’
has some fault. Needless to say, in Microservice Architecture ‘A’ might be dependent on other Microservices and the same
is true for Microservice ‘B’. In order to escape the multiple microservices from becoming erroneous as a result of
cascading effect, we stop calling the faulty Microservice ‘B’. Instead, we call a dummy method that is called a
‘Fallback Method’. Therefore, calling a fallback method instead of an actual service due to a fault is called breaking
the circuit. That’s why, we call this as a ‘Circuit Breaker’ Pattern. Moreover, there are generally three states of a
Circuit Breaker Pattern : Closed, Open, Half Open.

### Closed

When a Microservice calls the dependent Microservice continuously, then we call the Circuit is in Closed State.

### Open

When a MicroService doesn’t call the dependent Microservice, Instead, it calls the fallback method that is implemented
to tolerate the fault. We call this state as Open State. When a certain percentage of requests get failed, let’s say
90%, then we change the state from Closed to Open.

### Half-open

When a Microservice sends a percentage of requests to dependent Microservice and the rest of them to Fallback method. We
call this state as Half-open. During the open state, we can configure the wait duration. Once wait duration is over, the
Circuit Breaker will come in Half-open state. In this state Circuit Breaker checks if the dependent service is up. In
order to achieve this, it sends a certain percentage of requests to dependent service that we can configure. If it gets
a positive response from dependent service, it would switch to the closed state, otherwise it would again go back to the
Open State.

### When to use Circuit Breaker?

For example, if a Microservice ‘A’ depends up on Microservice ‘B’. For some reason, Microservice ‘B’ is experiencing an
error. Instead of repeatedly calling Microservice ‘B’, the Microservice ‘A’ should take a break (not calling) until
Microservice ‘B’ is completely or partially recovered. Using Circuit Breaker we can eliminate the flow of failures to
downstream/upstream. We can achieve this functionality easily with the help of annotation @CircuitBreaker without
writing a specific code.

### How to implement Circuit Breaker ? : Circuit Breaker Example

We will develop a scenario where one Microservice will call another Microservice.

1) ‘failure-rate-threshold=80‘ indicates that if 80% of requests are getting failed, open the circuit ie. Make the
   Circuit Breaker state as Open.
2) ‘sliding-window-size=10‘ indicates that if 80% of requests out of 10 (it means 8) are failing, open the circuit.
3) ‘sliding-window-type=COUNT_BASED‘ indicates that we are using COUNT_BASED sliding window. Another type is TIME_BASED.
4) ‘minimum-number-of-calls=5‘ indicates that we need at least 5 calls to calculate the failure rate threshold.
5) ‘automatic-transition-from-open-to-half-open-enabled=true‘ indicates that don’t switch directly from the open state
   to the closed state, consider the half-open state also.
6) ‘permitted-number-of-calls-in-half-open-state=4‘ indicates that when on half-open state, consider sending 4 requests.
   If 80% of them are failing, switch circuit breaker to open state.
7) ‘wait-duration-in-open-state=1s’ indicates the waiting time interval while switching from the open state to the
   closed state.

These attributes are the important part of an implementation of a Circuit Breaker. We can configure the values as per
our requirement and test the implemented functionality accordingly.

### What is Bulkhead?

In the context of the Fault Tolerance mechanism, if we want to limit the number of concurrent requests, we can use
Bulkhead as an aspect. Using Bulkhead, we can limit the number of concurrent requests within a particular period. Please
note the difference between Bulkhead and Rate Limiting. Rate Limiter never talks about concurrent requests, but Bulkhead
does. Rate Limiter talks about limiting number of requests within a particular period. Hence, using Bulkhead we can
limit the number of concurrent requests. We can achieve this functionality easily with the help of annotation @Bulkhead
without writing a specific code.

### How to implement Bulkhead ? : Bulkhead Example

For example, we want to limit only 5 concurrent requests. In order to achieve this, let’s follow below steps to write
code and respective configurations. ‘max-concurrent-calls=5’ indicates that if the number of concurrent calls exceed 5,
activate the fallback method.

‘max-wait-duration=0’ indicates that don’t wait for anything, show response immediately based on the configuration.

### What is Time Limiting or Timeout Handling?

Time Limiting is the process of setting a time limit for a Microservice to respond. Suppose Microservice ‘A’ sends a
request to Microservice ‘B’, it sets a time limit for the Microservice ‘B’ to respond. If Microservice ‘B’ doesn’t
respond within that time limit, then it will be considered that it has some fault. We can achieve this functionality
easily with the help of annotation @Timelimiter without writing a specific code.

### How to implement TimeLimiter ? :TimeLimiter Example

For example, we want to limit the duration of getting the response of a request. In order to achieve this, let’s follow
below steps to write code and respective configurations. ‘timeout-duration=1ms’ indicates that the maximum amount of
time a request can take to respond is 1 millisecond

‘cancel-running-future=false’ indicates that do not cancel the Running Completable Futures After TimeOut.

In order to test the functionality, Run the application as it is. You will get TimeOutException on the Browser. When you
change the value of timeout-duration=1s, you will receive “Executing Within the time Limit…” message in the browser.

### How to implement multiple Aspects/patterns in a single method?

If we are learning ‘How to implement Fault Tolerance in Microservices using Resilience4j?’, it becomes crucial to know
how to apply multiple aspects/patterns in a single service. Yes, undoubtedly we can apply multiple aspects in a single
method using separate annotations for each. The important point here is the order of their execution. Generally, we
follow the order as given below, which is the default order specified by Resilience4J:

1) Bulkhead
2) Time Limiter.
3) Rate Limiter.
4) Circuit Breaker
5) Retry

This repo is based on
this [article](https://javatechonline.com/how-to-implement-fault-tolerance-in-microservices-using-resilience4j/?fbclid=IwAR1hokLXmTh3BF2gc9GjD27NsX4_WlrVyb6IhUVteSul_nOnh0Wz4erGjos)
.
