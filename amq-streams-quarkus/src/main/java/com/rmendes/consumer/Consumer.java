package com.rmendes.consumer;

import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.rmendes.model.Transaction; 

@ApplicationScoped
public class Consumer {
	
	private static final Logger LOG = Logger.getLogger(Consumer.class);
	
	@Incoming("simple-in")
	public CompletionStage<Void> consume(Message<String> msg){
		LOG.info(msg.getPayload());
		return msg.ack(); 
	} 
	
//	@Incoming("topic-simple-message")
//	public CompletionStage<Void> consumeOriginal(Message<String> msg){
//		Log.info("------------------Cluster 1 topic topic-simple-message: "+msg.getPayload());
//		return msg.ack();
//	}
	
	@Incoming("event-reader")
	public CompletionStage<Void> consumeEvent(Message<Transaction> t){
		LOG.info(t.getPayload().getDestinyAccountNumber() + " - "+t.getPayload().getOriginAccountNumber()+" - "+t.getPayload().getValue()+" - "+ t.getPayload().getType());
		return t.ack();
	}
	
	

}
