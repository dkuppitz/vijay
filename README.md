vijay
=====

Answer to: https://groups.google.com/d/msg/aureliusgraphs/IDGIic_nNTQ/kjbwKNRFpZQJ

## compile

```sh
mvn clean package
```

## start

```sh
java -cp "target/vijay-1.0-SNAPSHOT.jar:target/lib/*" com.aureliusgraphs.mailinglist.App
```

## sample output

```

Benchmark started, be patient...

Mean time without ES: 480.37 ms
Mean time with ES: 431.97 ms

Results are identical.

{{type=tweet, tweetId=2000}={type=user, userId=11}, {type=tweet, tweetId=900}={type=user, userId=148}, {type=tweet, tweetId=901}={type=user, userId=21}, {type=tweet, tweetId=902}={type=user, userId=14...

```
