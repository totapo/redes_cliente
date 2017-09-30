package pattern;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

//observer pattern (thread safe)-> código retirado de https://www.techyourchance.com/thread-safe-observer-design-pattern-in-java/

public class MessageBox {
		
	    // Can use CopyOnWriteArraySet too
	    private final Set<Observer> mObservers = Collections.newSetFromMap(
	            new ConcurrentHashMap<Observer, Boolean>(0));
	     
	    /**
	     * This method adds a new Observer - it will be notified when Observable changes
	     */
	    public void registerObserver(Observer observer) {
	        if (observer == null) return;
	        mObservers.add(observer); // this is safe due to thread-safe Set
	    }
	     
	    /**
	     * This method removes an Observer - it will no longer be notified when Observable changes
	     */
	    public void unregisterObserver(Observer observer) {
	        if (observer != null) {
	            mObservers.remove(observer); // this is safe due to thread-safe Set
	        }
	    }
	 
	    /**
	     * This method notifies currently registered observers about Observable's change
	     */
	    private void notifyObservers() {
	        for (Observer observer : mObservers) { // this is safe due to thread-safe Set
	            observer.onObservableChanged();
	        }
	    }
	    
	    //nosso código
	    private ConcurrentLinkedQueue<String> mensagens; //guarda as mensagens recebidas
	    
	    public MessageBox(){
	    	mensagens = new ConcurrentLinkedQueue<String>();
	    }
	    
	    public void addMessage(String message){
	    	mensagens.add(message);
	    	notifyObservers();
	    }
	    
	    public String getFirsMessage(){
	    	return mensagens.poll();
	    }
}
