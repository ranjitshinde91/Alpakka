package slick;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.alpakka.slick.javadsl.Slick;
import akka.stream.alpakka.slick.javadsl.SlickRow;
import akka.stream.alpakka.slick.javadsl.SlickSession;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JdbcConnectionDemo {
    private ActorSystem system = ActorSystem.create("jdbcdemo");
    private Materializer materializer = ActorMaterializer.create(system);
    final SlickSession session = SlickSession.forConfig("slick-h2");


    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        JdbcConnectionDemo jdbcConnectionDemo = new JdbcConnectionDemo();
        jdbcConnectionDemo.droptable();
        jdbcConnectionDemo.createTable();
        jdbcConnectionDemo.inserUsers();
        jdbcConnectionDemo.getUsers();
    }

    private boolean createTable() {
        executeStatement(
                "CREATE TABLE ALPAKKA_SLICK_JAVADSL_TEST_USERS(ID INTEGER, NAME VARCHAR(50))"
        );
        return true;
    }

    private void getUsers() throws InterruptedException, ExecutionException, TimeoutException {

        final String selectAllUsers = "SELECT ID, NAME FROM ALPAKKA_SLICK_JAVADSL_TEST_USERS";
        final Source<User, NotUsed> slickSource =
                Slick.source(
                        session, selectAllUsers, (SlickRow row) -> new User(row.nextInt(), row.nextString()));

        final CompletionStage<List<User>> foundUsersFuture =
                slickSource.runWith(Sink.seq(), materializer);

        final Set<User> foundUsers = new HashSet<>(foundUsersFuture.toCompletableFuture().get(3, TimeUnit.SECONDS));

        System.out.println(foundUsers);
    }

    private void droptable() {
        executeStatement("DROP TABLE ALPAKKA_SLICK_JAVADSL_TEST_USERS");
    }

    private void inserUsers() throws InterruptedException, ExecutionException, TimeoutException {
        final Set<User> users =
                IntStream.range(0, 40)
                        .boxed()
                        .map((i) -> new User(i, "Name" + i))
                        .collect(Collectors.toSet());
        Source<User, NotUsed> usersSource = Source.from(users);

        final Sink<User, CompletionStage<Done>> slickSink = Slick.sink(session, user -> "INSERT INTO ALPAKKA_SLICK_JAVADSL_TEST_USERS VALUES ("
                                                                                        + user.id
                                                                                        + ", '"
                                                                                        + user.name
                                                                                        + "')");


        final CompletionStage<Done> insertionResultFuture =
                usersSource.runWith(slickSink, materializer);

        insertionResultFuture.toCompletableFuture().get(5, TimeUnit.SECONDS);
    }

    private void executeStatement(String statement) {
        try {
            Source.single(statement).runWith(Slick.sink(session), materializer)
                    .toCompletableFuture()
                    .get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
