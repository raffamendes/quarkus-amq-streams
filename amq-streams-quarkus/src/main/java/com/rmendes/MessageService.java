package com.rmendes;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path; 
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.rmendes.generator.TemperatureGenerator;
import com.rmendes.model.Transaction;

@Path("/message")
public class MessageService {
	
	@Inject
	@Channel("simple-out")
	Emitter<String> simpleEmitter;
	
	@Inject
	@Channel("simple-message")
	Emitter<String> simpleEmitter2;
	
	@Inject
	@Channel("event")
	Emitter<Transaction> transactionEmitter;
	
	@Inject
	TemperatureGenerator generator;
	
	@GET 
	@Path("/simple/{message}")
	@Produces(MediaType.TEXT_PLAIN)
	public void sendSimpleMessage(@PathParam("message") String message){
		simpleEmitter.send(Message.of(message)
				.withAck(() -> {
					return CompletableFuture.completedFuture(null);
				})
				.withNack(Throwable -> {
					return CompletableFuture.completedFuture(null);
				}));
	}
	
	@GET
	@Path("/another/simple/{message}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response sendAnotherSimpleMessage(@PathParam("message") String message){
		simpleEmitter2.send(Message.of(message)
				.withAck(() -> {
					return CompletableFuture.completedFuture(null);
				})
				.withNack(Throwable -> {
					return CompletableFuture.completedFuture(null);
				}));
		return Response.accepted().build();
	}
	
	@POST
	@Path("/transaction")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response sendTransaction(Transaction t) {
		transactionEmitter.send(t);
		return Response.accepted().build();
	}
	

}
