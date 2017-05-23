# Spring Data Redis Possible Bug

Spring-Data-Redis 1.8 and above can return NULL when this has never been set in a cache.

to test this run redis locally: i.e. 
```
docker run -p 6379:6379 redis
```

and run the application:
```
gradle run
```

If the dependency is changed to `org.springframework.data:spring-data-redis:1.7.10.RELEASE` the app will not exit.
