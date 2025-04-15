# 🎯 Live Odds

**Live Odds** is a clean, testable Java library designed to implement Live Football World Cup Score Board

### Versions:
- Java 17
- Maven 3.9.9
- JUnit 5.9.3

### Features:
- Start a new match, assuming initial score 0 – 0  
- Update score
- Finish match currently in progress and remove from the scoreboard. 
- Get a summary of matches in progress ordered by their total score and most recently start time (descending order)
- Pluggable `MatchStore` interface for adding custom store solutions. Default store solution is HashMap collection.

## 🚀 Getting Started
### Maven

1. Download library and run :
    ```bash
    mvn clean install
    ```
3. Add dependency to your project :
   ```xml
    <dependency>
      <groupId>com.example</groupId>
      <artifactId>live-odds</artifactId>
      <version>1.0.0</version>
    </dependency>
    ```

4. Use the service
   ```java
   ScoreboardService service = ScoreboardFactory.createDefaultScoreboard();
   
   service.startNewMatch("Spain", "Italy");
   service.startNewMatch("Germany", "France");
   
   service.updateMatch("Spain", "Italy", 3, 1);
   
   service.finishMatch("Germany", "France");
   
   List<String> summary = service.getSummary();   
   summary.forEach(System.out::println);
   ```
   You can make service use another store solution, in case default solution is not of your liking, by implementing interface `MatchStore` and using `ScoreboardFactory.createScoreboard(MatchStore store)`.
   ```java
   //CustomMatchStore.java
   public class CustomMatchStore implements MatchStore {...}
   
   //Main.java
   MatchStore store = new CustomMatchStore();
   ScoreboardService service = ScoreboardFactory.createScoreboard(store);   
   
   service.startNewMatch("Spain", "Italy");
   service.updateMatch("Spain", "Italy", 3, 1);
   List<String> summary = service.getSummary();
   
   summary.forEach(System.out::println);
   ```
## 📝 Notes
### Assumptions:
- Match uniqueness is based on home team + away team (case-insensitive)
- Team names are validated using ISO country list `Locale.getISOCountries()`.
- Scores can't be negative integers.
- Maximum number of ongoing matches is not enough for having significant difference in performance between using non-thread safe and thread-safe in-memory store solution for storing matches.
  So by default library is single-threaded but left open option of using another store solution. (ex. ConcurrencyHashMap, Redis and Hazelcast)