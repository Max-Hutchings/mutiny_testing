package com.max.test;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("/hello")
public class GreetingResource {

    private static final Logger logger = Logger.getLogger(GreetingResource.class);

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }


    @GET
    @Path("/process-list-on-subscription")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<String>> processListRunOnSubscription(){

        Book book1 = new Book("The Lion King", "Jason", "The lion ran through the desert");
        Book book2 = new Book("Dark Side", "Steve", "You were my brother Aniken");
        Book book3 = new Book("Cars", "Lightning McQueen", "He Drove around a track");

        List<Book> books = List.of(book1, book2, book3);
        Multi<Book> bookMulti = getBookMulti(books);

        Uni<List<String>> successMessage = bookMulti
                .onItem().transformToUniAndMerge(this::processBookRunSubscriptionOn)
                .collect().asList();


        return successMessage;
    }

    private Multi<Book> getBookMulti(List<Book> books){
        logger.info("Getting book multi for " + books.size() + " books");
        return Multi.createFrom().iterable(books);
    }

    private Uni<String> processBookRunSubscriptionOn(Book book){
        return Uni.createFrom().item(() -> {
            logger.info("Starting processing of book: " + book.getTitle());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
            logger.info("Finished processing of book: " + book.getTitle());
            return "Completed for Book " + book.getTitle();
        }).runSubscriptionOn(executorService);
    }





    @GET
    @Path("/process-list-emit-on")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<String>> processListEmitOn(){
        Book book1 = new Book("The Lion King", "Jason", "The lion ran through the desert");
        Book book2 = new Book("Dark Side", "Steve", "You were my brother Aniken");
        Book book3 = new Book("Cars", "Lightning McQueen", "He Drove around a track");

        List<Book> books = List.of(book1, book2, book3);
        Multi<Book> bookMulti = getBookMulti(books);

        Uni<List<String>> successMessage = bookMulti.emitOn(executorService)
                .onItem().transformToUniAndMerge(this::processBook)
                .collect().asList();
        return successMessage;

    }

    private Uni<String> processBook(Book book){
        return Uni.createFrom().item(() -> {
            logger.info("Starting processing of book: " + book.getTitle());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
            logger.info("Finished processing of book: " + book.getTitle());
            return "Completed for Book " + book.getTitle();
        });
    }

}
